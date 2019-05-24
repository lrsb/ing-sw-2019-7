package it.polimi.ingsw.server.network;

import com.google.gson.Gson;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;
import it.polimi.ingsw.Server;
import it.polimi.ingsw.common.models.Action;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.network.exceptions.UserRemoteException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerRestImpl extends NanoWSD {
    private static final @NotNull ExecutorService executorService = Executors.newCachedThreadPool();

    public ServerRestImpl() throws IOException {
        super(Integer.parseInt(System.getProperty("server.port")));
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
                    if (method == Method.POST)
                        return newJsonResponse(Server.controller.createRoom(token, session.getParameters().get("name").get(0),
                                Integer.parseInt(session.getParameters().get("timeout").get(0)), Game.Type.valueOf(session.getParameters().get("gameType").get(0))));
                    break;
                case "/startGame":
                    if (method == Method.POST)
                        return newJsonResponse(Server.controller.startGame(token, UUID.fromString(session.getParameters().get("uuid").get(0))));
                    break;
                case "/doAction":
                    if (method == Method.POST)
                        return newJsonResponse(Server.controller.doAction(token, new Gson().fromJson(new InputStreamReader(session.getInputStream()), Action.class)));
                    break;
                default:
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Not found!!");
            }
        } catch (UserRemoteException e) {
            return newFixedLengthResponse(Response.Status.UNAUTHORIZED, NanoHTTPD.MIME_PLAINTEXT, e.getMessage());
        } catch (RemoteException e) {
            return newFixedLengthResponse(Response.Status.NOT_ACCEPTABLE, NanoHTTPD.MIME_PLAINTEXT, e.getMessage());
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
                switch (getHandshakeRequest().getUri()) {
                    case "/gameUpdate":
                        Server.controller.addGameListener(getHandshakeRequest().getHeaders().get("auth-token"),
                                UUID.fromString(getHandshakeRequest().getParameters().get("uuid").get(0)), game -> {
                            try {
                                sendFrame(new WebSocketFrame(WebSocketFrame.OpCode.Text, true, new Gson().toJson(game)));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        schedulePing();
                        break;
                    case "/roomUpdate":
                        Server.controller.addRoomListener(getHandshakeRequest().getHeaders().get("auth-token"),
                                UUID.fromString(getHandshakeRequest().getParameters().get("uuid").get(0)), update -> {
                            try {
                                sendFrame(new WebSocketFrame(WebSocketFrame.OpCode.Text, true, new Gson().toJson(update)));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        schedulePing();
                        break;
                    default:
                        close(WebSocketFrame.CloseCode.UnsupportedData, "Endpoint not valid!", true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void schedulePing() {
            executorService.submit(() -> {
                while (isOpen()) try {
                    sendFrame(new WebSocketFrame(WebSocketFrame.OpCode.Ping, true, ""));
                    Thread.sleep(30000);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
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
        }

        @Override
        protected void onPong(WebSocketFrame pong) {
        }

        @Override
        protected void onException(IOException exception) {
            close();
        }

        @Override
        protected void debugFrameReceived(WebSocketFrame frame) {
        }

        @Override
        protected void debugFrameSent(WebSocketFrame frame) {
        }

        private void close() {
            try {
                switch (getHandshakeRequest().getUri()) {
                    case "/gameUpdate":
                        Server.controller.removeGameListener(getHandshakeRequest().getHeaders().get("auth-token"),
                                UUID.fromString(getHandshakeRequest().getParameters().get("uuid").get(0)));
                        break;
                    case "/roomUpdate":
                        Server.controller.removeRoomListener(getHandshakeRequest().getHeaders().get("auth-token"),
                                UUID.fromString(getHandshakeRequest().getParameters().get("uuid").get(0)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}