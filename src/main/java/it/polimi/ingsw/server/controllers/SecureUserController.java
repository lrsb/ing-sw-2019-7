package it.polimi.ingsw.server.controllers;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import it.polimi.ingsw.Server;
import it.polimi.ingsw.common.models.User;
import org.bson.Document;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;

import static com.mongodb.client.model.Filters.eq;

@SuppressWarnings("ConstantConditions")
class SecureUserController {
    private static final @NotNull SecureRandom random = new SecureRandom();
    private static final @NotNull char[] symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789:".toCharArray();
    private static final @NotNull char[] buf = new char[256];

    private static final @NotNull MongoCollection<Document> users = Server.mongoDatabase.getCollection("users");

    @Contract(pure = true)
    private SecureUserController() {
    }

    @Contract(pure = true, value = "null -> null")
    static @Nullable User getUser(@Nullable String token) {
        if (token != null) try {
            return new Gson().fromJson(users.find(eq("token", token)).first().toJson(), User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Contract("null, _ -> null; !null, null -> null")
    static @Nullable String createUser(@Nullable String nickname, @Nullable String password) {
        if (nickname != null && password != null) synchronized (users) {
            try {
                if (users.find(eq("nickname", nickname)).iterator().hasNext()) return null;
                var user = new SecureUser(nickname, password);
                user.setToken(nextToken());
                users.insertOne(Document.parse(new Gson().toJson(user)));
                return user.getToken();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Contract("null, _ -> null; !null, null -> null")
    static @Nullable String authUser(@Nullable String nickname, @Nullable String password) {
        if (nickname != null && password != null) try {
            var user = new Gson().fromJson(users.find(eq("nickname", nickname)).first().toJson(), SecureUser.class);
            if (!user.getPassword().equals(password)) return null;
            user.setToken(nextToken());
            users.replaceOne(eq("uuid", user.getUuid().toString()), Document.parse(new Gson().toJson(user)));
            return user.getToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Contract(" -> new")
    private static @NotNull String nextToken() {
        for (var idx = 0; idx < buf.length; idx++) buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

    private static class SecureUser extends User {
        private @Nullable String token;
        private @NotNull String password;

        private SecureUser(@NotNull String nickname, @NotNull String password) {
            super(nickname);
            this.password = password;
        }

        @Contract(pure = true)
        private @Nullable String getToken() {
            return token;
        }

        private void setToken(@NotNull String token) {
            this.token = token;
        }

        @Contract(pure = true)
        private @NotNull String getPassword() {
            return password;
        }
    }
}