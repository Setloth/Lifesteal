package page.echology.lifesteal;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.bukkit.plugin.java.JavaPlugin;
import page.echology.lifesteal.utility.Utils;

import java.util.function.Consumer;

public record API(JavaPlugin plugin) {

    public void version(final Consumer<String> consumer) {

        HttpResponse<JsonNode> res = Unirest.get("https://api.github.com/repos/Echological/Lifesteal-MH/releases/latest")
                .asJson();

        JsonNode node = res.getBody();

        JSONObject obj = node.getObject();

        if (!obj.has("tag_name")) {
            Utils.warn("Failed to fetch current plugin version, this probably isn't a problem. But you should check for updates.");
            return;
        }
        String v = obj.get("tag_name").toString();

        consumer.accept(v);

    }


}