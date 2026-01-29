package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import fr.maxlego08.menu.zcore.utils.discord.DiscordConfigurationComponent;
import fr.maxlego08.menu.zcore.utils.discord.DiscordWebhookComponent;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class DiscordComponentAction extends ActionHelper {

    private final DiscordConfigurationComponent configuration;

    public DiscordComponentAction(DiscordConfigurationComponent configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        var scheduler = inventory.getPlugin().getScheduler();
        DiscordWebhookComponent discordWebhook = new DiscordWebhookComponent(configuration.webhookUrl());
        configuration.apply(text -> text == null ? null : player == null ? text : papi(placeholders.parse(text), player), discordWebhook);

        scheduler.runAsync(w -> {
            try {
                discordWebhook.execute();
            } catch (Exception exception) {
                if (Configuration.enableDebug) {
                    exception.printStackTrace();
                }
            }
        });
    }
}
