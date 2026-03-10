package org.byauth.manager;

import com.google.gson.JsonObject;
import org.byauth.EnsDaire;
import org.byauth.service.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WebHookManager implements Service {

    private final EnsDaire plugin;
    private final HttpClient client = HttpClient.newHttpClient();
    private String webhookUrl;

    public WebHookManager(EnsDaire plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {
        this.webhookUrl = plugin.getConfig().getString("discord.webhook-url");
    }

    public void sendEmbed(String title, String description, int color) {
        if (webhookUrl == null || webhookUrl.isBlank()) return;

        JsonObject embed = new JsonObject();
        embed.addProperty("title", title);
        embed.addProperty("description", description);
        embed.addProperty("color", color);

        JsonObject body = new JsonObject();
        body.add("embeds", com.google.gson.JsonParser.parseString("[" + embed.toString() + "]"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhookUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public void terminate() {}
}
