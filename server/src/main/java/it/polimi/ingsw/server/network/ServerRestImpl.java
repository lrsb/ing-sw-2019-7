package it.polimi.ingsw.server.network;

import com.google.gson.Gson;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;
import it.polimi.ingsw.common.models.Action;
import it.polimi.ingsw.common.models.Message;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.network.exceptions.UserRemoteException;
import it.polimi.ingsw.server.Server;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerRestImpl extends NanoWSD {
    private static final @NotNull Logger logger = Logger.getLogger("ServerRestImpl");
    private static final @NotNull ExecutorService executorService = Executors.newCachedThreadPool();

    public ServerRestImpl(int port) throws IOException {
        super(port);
        start(Integer.MAX_VALUE, false);
    }

    private static @NotNull Response newJsonResponse(@NotNull Object object) {
        return NanoHTTPD.newFixedLengthResponse(new Gson().toJson(object));
    }

    @Override
    protected WebSocket openWebSocket(@NotNull IHTTPSession ihttpSession) {
        return new AdrenalineWebSocket(ihttpSession);
    }

    @Override
    public Response serveHttp(@NotNull IHTTPSession session) {
        Logger.getLogger("rest").log(Level.INFO, "request: {0}", session.getUri());
        try {
            var token = session.getHeaders().get("auth-token");
            var method = session.getMethod();
            switch (session.getUri()) {
                case "":
                case "/":
                    return newFixedLengthResponse("Running!");
                case "/authUser":
                    if (method == Method.POST)
                        return newJsonResponse(Server.controller.authUser(session.getParameters().get("nickname").get(0), session.getParameters().get("password").get(0)));
                    break;
                case "/createUser":
                    if (method == Method.POST)
                        return newJsonResponse(Server.controller.createUser(session.getParameters().get("nickname").get(0), session.getParameters().get("password").get(0)));
                    break;
                case "/getActiveGame":
                    if (method == Method.GET)
                        return newJsonResponse(Server.controller.getActiveGame(token));
                    break;
                case "/getRooms":
                    if (method == Method.GET) return newJsonResponse(Server.controller.getRooms(token));
                    break;
                case "/joinRoom":
                    if (method == Method.POST)
                        return newJsonResponse(Server.controller.joinRoom(token, UUID.fromString(session.getParameters().get("uuid").get(0))));
                    break;
                case "/createRoom":
                    if (method == Method.POST) {
                        var map = new HashMap<String, String>();
                        session.parseBody(map);
                        return newJsonResponse(Server.controller.createRoom(token, new Gson().fromJson(map.get("postData"), Room.class)));
                    }
                    break;
                case "/quitRoom":
                    if (method == Method.POST) {
                        Server.controller.quitRoom(token, UUID.fromString(session.getParameters().get("uuid").get(0)));
                        return newJsonResponse("ok");
                    } else break;
                case "/startGame":
                    if (method == Method.POST) {
                        Server.controller.startGame(token, UUID.fromString(session.getParameters().get("uuid").get(0)));
                        return newJsonResponse("ok");
                    }
                    break;
                case "/quitGame":
                    if (method == Method.POST) {
                        Server.controller.quitGame(token, UUID.fromString(session.getParameters().get("uuid").get(0)));
                        return newJsonResponse("ok");
                    } else break;
                case "/doAction":
                    if (method == Method.POST) {
                        var map = new HashMap<String, String>();
                        session.parseBody(map);
                        return newJsonResponse(Server.controller.doAction(token, new Gson().fromJson(map.get("postData"), Action.class)));
                    }
                    break;
                case "/sendMessage":
                    if (method == Method.POST) {
                        var map = new HashMap<String, String>();
                        session.parseBody(map);
                        Server.controller.sendMessage(token, new Gson().fromJson(map.get("postData"), Message.class));
                        return newJsonResponse("ok");
                    } else break;
                default:
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Not found!!");
            }
        } catch (UserRemoteException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            return newFixedLengthResponse(Response.Status.UNAUTHORIZED, NanoHTTPD.MIME_PLAINTEXT, e.getMessage());
        } catch (RemoteException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            return newFixedLengthResponse(Response.Status.NOT_ACCEPTABLE, NanoHTTPD.MIME_PLAINTEXT, e.getMessage());
        } catch (Exception e) {
            if (!(e instanceof SocketException)) logger.log(Level.WARNING, e.getMessage(), e);
            return newFixedLengthResponse(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "Something wrong happened!");
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Not found!!");
    }

    private class AdrenalineWebSocket extends WebSocket {
        private AdrenalineWebSocket(IHTTPSession handshakeRequest) {
            super(handshakeRequest);
        }

        @Override
        protected void onOpen() {
            try {
                if (getHandshakeRequest().getUri().equals("/update")) {
                    Server.controller.addListener(getHandshakeRequest().getHeaders().get("auth-token"), (object) -> {
                        try {
                            sendFrame(new WebSocketFrame(WebSocketFrame.OpCode.Text, true,
                                    new Gson().toJson(List.of(object.getClass().getCanonicalName(), new Gson().toJson(object)))));
                        } catch (IOException e) {
                            logger.log(Level.WARNING, e.getMessage(), e);
                        }
                    });
                    schedulePing();
                } else close(WebSocketFrame.CloseCode.UnsupportedData, "Endpoint not valid!", true);
            } catch (Exception e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }

        private void schedulePing() {
            executorService.submit(() -> {
                while (isOpen()) try {
                    sendFrame(new WebSocketFrame(WebSocketFrame.OpCode.Ping, true, ""));
                    Thread.sleep(30000);
                } catch (IOException | InterruptedException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                    Thread.currentThread().interrupt();
                }
            });
        }

        @Override
        protected void onClose(WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
            close();
        }

        @Override
        protected void onMessage(@NotNull WebSocketFrame message) {
            //not important
        }

        @Override
        protected void onPong(WebSocketFrame pong) {
            //not important
        }

        @Override
        protected void onException(IOException exception) {
            close();
        }

        @Override
        protected void debugFrameReceived(WebSocketFrame frame) {
            //not important
        }

        @Override
        protected void debugFrameSent(WebSocketFrame frame) {
            //not important
        }

        private void close() {
            try {
                if (getHandshakeRequest().getUri().equals("/removeUpdate")) {
                    Server.controller.removeListener(getHandshakeRequest().getHeaders().get("auth-token"));
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
}