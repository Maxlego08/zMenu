package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.DiscordComponentAction;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.discord.DiscordConfigurationComponent;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DiscordComponentV2Loader extends ActionLoader {

    private static final Map<String, Boolean> webhookUrlCache = new HashMap<>();

    public DiscordComponentV2Loader() {
        super("discord component", "discord_component", "discord webhook component", "discordwebhookcomponent");
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        String webhookUrl = accessor.getString("webhook");
        String avatarUrl = accessor.getString("avatar_url", null);
        String username = accessor.getString("username", null);
        List<?> json = accessor.getList("component");
        if (checkWebhookExists(webhookUrl)) {
            DiscordConfigurationComponent config = new DiscordConfigurationComponent(webhookUrl, avatarUrl, username, json);
            return new DiscordComponentAction(config);
        } else {
            Logger.info("Impossible to load discord action, webhook does not exists: " + webhookUrl, Logger.LogType.ERROR);
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
            boolean exists = (responseCode == 200);
            webhookUrlCache.put(webhookUrl, exists);
            return exists;
        } catch (Exception e) {
            webhookUrlCache.put(webhookUrl, false);
            return false;
        }
    }
}
