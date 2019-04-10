package it.polimi.ingsw.server.network;

import com.google.gson.Gson;
import fi.iki.elonen.NanoHTTPD;
import it.polimi.ingsw.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;

public class ServerRestImpl extends NanoHTTPD {
    public ServerRestImpl() throws IOException {
        super(Integer.parseInt(System.getProperty("server.port")));
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    private static final @NotNull String DEF_RESPONSE = "<!doctype html>\n" +
            "<head>\n" +
            "  <meta charset=\"utf-8\">\n" +
            "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\">\n" +
            "  <title></title>\n" +
            "  <meta name=\"description\" content=\"\">\n" +
            "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
            "</head>\n" +
            "<body>\n" +
            "  <p>Hello world!</p>\n" +
            "</body>\n" +
            "</html>";

    private static @NotNull Response newJsonResponse(@Nullable Object object) {
        return object != null ? NanoHTTPD.newFixedLengthResponse(new Gson().toJson(object)) : newFixedLengthResponse(Response.Status.UNAUTHORIZED, NanoHTTPD.MIME_PLAINTEXT, null);
    }

    @Override
    public Response serve(@NotNull IHTTPSession session) {
        try {
            var token = session.getHeaders().get("auth-token");
            var method = session.getMethod();
            switch (session.getUri()) {
                case "":
                case "/":
                    newFixedLengthResponse(DEF_RESPONSE);
                    break;
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
                    newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, null);
    }
}