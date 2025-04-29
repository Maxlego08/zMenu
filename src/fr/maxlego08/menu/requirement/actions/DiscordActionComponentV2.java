package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.scheduler.ZScheduler;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.zcore.utils.discord.DiscordConfigurationComponentV2;
import fr.maxlego08.menu.zcore.utils.discord.DiscordWebhookComponentV2;
import org.bukkit.entity.Player;

public class DiscordActionComponentV2 extends Action {

    private final DiscordConfigurationComponentV2 configuration;

    public DiscordActionComponentV2(DiscordConfigurationComponentV2 configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void execute(Player player, Button button, InventoryDefault inventory, Placeholders placeholders) {
        ZScheduler scheduler = inventory.getPlugin().getScheduler();
        DiscordWebhookComponentV2 discordWebhook = new DiscordWebhookComponentV2(configuration.getWebhookUrl());
        configuration.apply(text -> text == null ? null : player == null ? text : papi(placeholders.parse(text), player, false), discordWebhook);

        scheduler.runTaskAsynchronously(() -> {
            try {
                discordWebhook.execute();
            } catch (Exception exception) {
                if (Config.enableDebug) {
                    exception.printStackTrace();
                }
            }
        });
    }
}
