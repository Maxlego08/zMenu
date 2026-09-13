package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.annotations.AutoActionLoader;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.DiscordAction;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.discord.DiscordConfiguration;
import fr.maxlego08.menu.zcore.utils.discord.DiscordEmbedConfiguration;
import fr.maxlego08.menu.zcore.utils.discord.DiscordWebhookChecker;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoActionLoader
public class DiscordLoader extends ActionLoader {

    public DiscordLoader() {
        super("discord", "send discord", "discord webhook", "discordwebhook");
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {

        String webhookUrl = accessor.getString("webhook");
        String avatarUrl = accessor.getString("avatar", null);
        String message = accessor.getString("message", null);
        String username = accessor.getString("username", null);
        List<Map<?, ?>> values = new ArrayList<>();
        if (accessor.contains("embeds")) {
            values = (List<Map<?, ?>>) accessor.getObject("embeds");
        }

        if (webhookUrl == null || webhookUrl.isBlank()) {
            Logger.info("Impossible to load discord action at " + path + " in " + file.getAbsolutePath()
                    + ", no webhook was given.", Logger.LogType.ERROR);
            return null;
        }

        DiscordWebhookChecker.verifyAsync(webhookUrl, file.getAbsolutePath());

        DiscordConfiguration config = new DiscordConfiguration(webhookUrl, avatarUrl, message, username, DiscordEmbedConfiguration.convertToEmbedObjects(values));
        return new DiscordAction(config);
    }
}
