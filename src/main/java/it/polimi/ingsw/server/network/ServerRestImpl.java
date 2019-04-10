package it.polimi.ingsw.server.network;

import fi.iki.elonen.NanoHTTPD;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ServerRestImpl extends NanoHTTPD {
    public ServerRestImpl() throws IOException {
        super(Integer.parseInt(System.getProperty("server.port")));
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public Response serve(@NotNull IHTTPSession session) {
        var msg = "<html><body><h1>Hello server</h1>\n";
        var parms = session.getParameters();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }
        return newFixedLengthResponse(msg + "</body></html>\n");
    }
}