package it.polimi.ingsw.server.controllers;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import it.polimi.ingsw.Server;
import it.polimi.ingsw.common.models.User;
import it.polimi.ingsw.common.models.wrappers.Opt;
import org.bson.Document;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.rmi.RemoteException;
import java.security.SecureRandom;

import static com.mongodb.client.model.Filters.eq;

class SecureUserController {
    private static final @NotNull MongoCollection<Document> users = Server.mongoDatabase.getCollection("users");

    @Contract(pure = true)
    private SecureUserController() {
    }

    @Contract("null -> fail")
    static @NotNull User getUser(@Nullable String token) throws RemoteException {
        if (token != null && !token.isEmpty()) try {
            var user = users.find(eq("token", token));
            if (user.iterator().hasNext()) throw new Exception();
            return new Gson().fromJson(Opt.of(user.first()).e(Document::toJson).get(""), User.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("User not exists!");
        }
        else throw new RemoteException("The token!!!");
    }

    @Contract("null, _ -> fail; !null, null -> fail")
    static @NotNull String createUser(@Nullable String nickname, @Nullable String password) throws RemoteException {
        if (nickname != null && password != null) synchronized (users) {
            try {
                if (users.find(eq("nickname", nickname)).iterator().hasNext())
                    throw new RemoteException("User already exists!");
                var user = new SecureUser(nickname, password);
                users.insertOne(Document.parse(new Gson().toJson(user)));
                return user.getToken();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RemoteException("User not exists!");
            }
        }
        else throw new RemoteException("Incomplete credentials!");
    }

    @Contract("null, _ -> fail; !null, null -> fail")
    static @NotNull String authUser(@Nullable String nickname, @Nullable String password) throws RemoteException {
        if (nickname != null && password != null) try {
            var user = new Gson().fromJson(Opt.of(users.find(eq("nickname", nickname)).first()).e(Document::toJson).get(""), SecureUser.class);
            if (user == null || !user.getPassword().equals(password))
                throw new RemoteException("Wrong username and/or password!");
            user.nextToken();
            users.replaceOne(eq("uuid", user.getUuid().toString()), Document.parse(new Gson().toJson(user)));
            return user.getToken();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("User not exists!");
        }
        else throw new RemoteException("Incomplete credentials!");
    }

    private static class SecureUser extends User {
        private static final @NotNull SecureRandom random = new SecureRandom();
        private static final @NotNull char[] symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789:".toCharArray();
        private static final @NotNull char[] buf = new char[256];

        private @NotNull String token;
        private @NotNull String password;

        private SecureUser(@NotNull String nickname, @NotNull String password) {
            super(nickname);
            this.password = password;
            this.token = nextTokenImpl();
        }

        @Contract(" -> new")
        private static @NotNull String nextTokenImpl() {
            for (var idx = 0; idx < buf.length; idx++) buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }

        @Contract(pure = true)
        private @NotNull String getToken() {
            return token;
        }

        @Contract(pure = true)
        private @NotNull String getPassword() {
            return password;
        }

        private void nextToken() {
            this.token = nextTokenImpl();
        }
    }
}