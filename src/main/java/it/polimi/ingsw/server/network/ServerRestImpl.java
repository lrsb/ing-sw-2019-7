package it.polimi.ingsw.server.network;

import com.google.gson.Gson;
import fi.iki.elonen.NanoHTTPD;
import it.polimi.ingsw.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerRestImpl extends NanoHTTPD {
    public ServerRestImpl() throws IOException {
        super(Integer.parseInt(System.getProperty("server.port")));
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    private static @NotNull Response newJsonResponse(@Nullable Object object) {
        return object != null ? NanoHTTPD.newFixedLengthResponse(new Gson().toJson(object)) : newFixedLengthResponse(Response.Status.UNAUTHORIZED, NanoHTTPD.MIME_PLAINTEXT, null);
    }

    @Override
    public Response serve(@NotNull IHTTPSession session) {
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
                case "/gameUpdate":
                    if (method == Method.GET)
                        return newJsonResponse(Server.controller.waitGameUpdate(token, UUID.fromString(session.getParameters().get("uuid").get(0))));
                    break;
                case "/roomUpdate":
                    if (method == Method.GET)
                        return newJsonResponse(Server.controller.waitRoomUpdate(token, UUID.fromString(session.getParameters().get("uuid").get(0))));
                    break;
                default:
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Not found!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Not found!!");
    }
}