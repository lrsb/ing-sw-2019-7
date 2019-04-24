package it.polimi.ingsw.server.network;

import com.google.gson.Gson;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;
import it.polimi.ingsw.Server;
import it.polimi.ingsw.common.models.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
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

    @Override
    protected WebSocket openWebSocket(IHTTPSession ihttpSession) {
        return new AdrenalineWebSocket(ihttpSession);
    }

    private static @NotNull Response newJsonResponse(@Nullable Object object) {
        return object != null ? NanoHTTPD.newFixedLengthResponse(new Gson().toJson(object)) : newFixedLengthResponse(Response.Status.UNAUTHORIZED, NanoHTTPD.MIME_PLAINTEXT, null);
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
                        return newJsonResponse(Server.controller.createRoom(token, session.getParameters().get("name").get(0)));
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
        } catch (Exception e) {
            e.printStackTrace();
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
                        Server.controller.addGameListener(getHandshakeRequest().getHeaders().get("auth-token"), game -> {
                            try {
                                sendFrame(new WebSocketFrame(WebSocketFrame.OpCode.Text, true, new Gson().toJson(game)));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        executorService.submit(() -> {
                            while (isOpen()) try {
                                sendFrame(new WebSocketFrame(WebSocketFrame.OpCode.Ping, true, ""));
                                Thread.sleep(50000);
                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                        break;
                    case "/roomUpdate":
                        Server.controller.addRoomListener(getHandshakeRequest().getHeaders().get("auth-token"), update -> {
                            try {
                                sendFrame(new WebSocketFrame(WebSocketFrame.OpCode.Text, true, new Gson().toJson(update)));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        executorService.submit(() -> {
                            while (isOpen()) try {
                                sendFrame(new WebSocketFrame(WebSocketFrame.OpCode.Ping, true, ""));
                                Thread.sleep(50000);
                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                        break;
                    default:
                        close(WebSocketFrame.CloseCode.UnsupportedData, "Endpoint not valid!", true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                        Server.controller.removeGameListener(getHandshakeRequest().getHeaders().get("auth-token"));
                        break;
                    case "/roomUpdate":
                        Server.controller.removeRoomListener(getHandshakeRequest().getHeaders().get("auth-token"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}