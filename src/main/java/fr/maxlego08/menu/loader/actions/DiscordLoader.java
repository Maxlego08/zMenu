package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.DiscordAction;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.discord.DiscordConfiguration;
import fr.maxlego08.menu.zcore.utils.discord.DiscordEmbedConfiguration;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordLoader extends ActionLoader {

    private static final Map<String, Boolean> webhookUrlCache = new HashMap<>();

    public DiscordLoader() {
        super("discord", "send discord", "discord webhook", "discordwebhook");
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {

        String webhookUrl = accessor.getString("webhook");
        String avatarUrl = accessor.getString("avatar", null);
        String message = accessor.getString("message", null);
        String username = accessor.getString("username", null);
        List<Map<?, ?>> values = new ArrayList<>();
        if (accessor.contains("embeds")) {
            values = (List<Map<?, ?>>) accessor.getObject("embeds");
        }

        if (checkWebhookExists(webhookUrl)) {
            DiscordConfiguration config = new DiscordConfiguration(webhookUrl, avatarUrl, message, username, DiscordEmbedConfiguration.convertToEmbedObjects(values));
            return new DiscordAction(config);
        } else {
            Logger.info("Impossible to load discord action, webhook does not exists: " + webhookUrl);
        }

        return null;
    }

    private boolean checkWebhookExists(String webhookUrl) {
        if (webhookUrlCache.containsKey(webhookUrl)) {
            return webhookUrlCache.get(webhookUrl);
        }

        try {
            URL url = new URI(webhookUrl).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();

            webhookUrlCache.put(webhookUrl, responseCode == 200);
            return responseCode == 200;
        } catch (Exception exception) {
            exception.printStackTrace();
            webhookUrlCache.put(webhookUrl, false);
            return false;
        }
    }
}
