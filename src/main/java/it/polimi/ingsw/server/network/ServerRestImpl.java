package it.polimi.ingsw.server.network;

import com.google.gson.Gson;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;
import it.polimi.ingsw.Server;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.network.GameListener;
import it.polimi.ingsw.common.network.RoomListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerRestImpl extends NanoWSD {
    public ServerRestImpl() throws IOException {
        super(Integer.parseInt(System.getProperty("server.port")));
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    private static @NotNull Response newJsonResponse(@Nullable Object object) {
        return object != null ? NanoHTTPD.newFixedLengthResponse(new Gson().toJson(object)) : newFixedLengthResponse(Response.Status.UNAUTHORIZED, NanoHTTPD.MIME_PLAINTEXT, null);
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession ihttpSession) {
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
                case "/doMove":
                    if (method == Method.POST)
                        return newJsonResponse(Server.controller.doMove(token, session.getParameters().get("move").get(0)));
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
                        Server.controller.addGameListener(getHandshakeRequest().getHeaders().get("auth-token"), new GameListener() {
                            @Override
                            public void onGameUpdate(Game game) {
                                try {
                                    sendFrame(new WebSocketFrame(WebSocketFrame.OpCode.Text, true, new Gson().toJson(game)));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void disconnected() {
                            }
                        });
                        break;
                    case "/roomUpdate":
                        Server.controller.addRoomListener(getHandshakeRequest().getHeaders().get("auth-token"), new RoomListener() {
                            @Override
                            public void onRoomUpdate(@NotNull Room update) {
                                try {
                                    sendFrame(new WebSocketFrame(WebSocketFrame.OpCode.Text, true, new Gson().toJson(update)));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void disconnected() {
                            }
                        });
                        break;
                    default:
                        close(WebSocketFrame.CloseCode.UnsupportedData, "endpoint not valid!", true);
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