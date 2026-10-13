package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.discord.DiscordConfiguration;
import fr.maxlego08.menu.zcore.utils.discord.DiscordEmbedConfiguration;
import fr.maxlego08.menu.zcore.utils.discord.DiscordWebhook;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DiscordAction extends ActionHelper {

    private final DiscordConfiguration configuration;

    public DiscordAction(DiscordConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {

        var scheduler = inventory.getPlugin().getScheduler();
        DiscordWebhook discordWebhook = new DiscordWebhook(this.configuration.webhookUrl());
        this.configuration.apply(text -> text == null ? null : player == null ? text : this.papi(placeholders.parse(text), player), discordWebhook);

        scheduler.runAsync(w -> {
            try {
                discordWebhook.execute();
            } catch (Exception exception) {
                if (Configuration.enableDebug) {
                    Logger.error(exception);
                }
            }
        });
    }

    @Override
    protected void serializeProperties(@NonNull Map<String, Object> map) {
        map.put("type", "discord");
        map.put("webhook", this.configuration.webhookUrl());
        if (this.configuration.avatarUrl() != null) map.put("avatar", this.configuration.avatarUrl());
        if (this.configuration.content() != null) map.put("message", this.configuration.content());
        if (this.configuration.username() != null) map.put("username", this.configuration.username());
        List<DiscordEmbedConfiguration> embeds = this.configuration.embeds();
        if (embeds != null && !embeds.isEmpty()) {
            List<Map<String, Object>> serializedEmbeds = new ArrayList<>(embeds.size());
            for (DiscordEmbedConfiguration embed : embeds) {
                serializedEmbeds.add(embed.serialize());
            }
            map.put("embeds", serializedEmbeds);
        }
    }
}
