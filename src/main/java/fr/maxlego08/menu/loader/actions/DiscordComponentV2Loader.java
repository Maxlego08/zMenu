package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.annotations.AutoActionLoader;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.DiscordComponentAction;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.discord.DiscordConfigurationComponent;
import fr.maxlego08.menu.zcore.utils.discord.DiscordWebhookChecker;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.List;

@AutoActionLoader
public class DiscordComponentV2Loader extends ActionLoader {

    public DiscordComponentV2Loader() {
        super("discord component", "discord_component", "discord webhook component", "discordwebhookcomponent");
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        String webhookUrl = accessor.getString("webhook");
        String avatarUrl = accessor.getString("avatar_url", null);
        String username = accessor.getString("username", null);
        List<?> json = accessor.getList("component");
        if (webhookUrl == null || webhookUrl.isBlank()) {
            Logger.info("Impossible to load discord action at " + path + " in " + file.getAbsolutePath()
                    + ", no webhook was given.", Logger.LogType.ERROR);
            return null;
        }

        DiscordWebhookChecker.verifyAsync(webhookUrl, file.getAbsolutePath());

        DiscordConfigurationComponent config = new DiscordConfigurationComponent(webhookUrl, avatarUrl, username, json);
        return new DiscordComponentAction(config);
    }
}
