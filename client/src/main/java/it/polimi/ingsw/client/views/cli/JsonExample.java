package it.polimi.ingsw.client.views.cli;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class JsonExample {
    public static void main(String[] args) throws FileNotFoundException {
        var parser = new JsonParser();
        var json = parser.parse(new JsonReader(new FileReader(new File(GameCli.class.getResource("strings.json").getFile())))).getAsJsonObject();
        var string = json.get("cli").getAsJsonObject().get("select_action").getAsString();
        //T consigliio di usare un metodo del genere che riduce il codice quando hai tanti oggetti
        var string1 = getObject(json, "cli", "actions", "move_action").get("select_square").getAsString();
    }

    public static JsonObject getObject(JsonObject root, @NotNull String... args) {
        for (var arg : args) root = root.get(arg).getAsJsonObject();
        return root;
    }
}
