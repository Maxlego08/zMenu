package fr.maxlego08.menu.api.context;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.inventory.bedrock.BedrockInventory;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;

import java.util.List;

public class BedrockRenderContext extends DialogRenderContext<Component, BedrockInventory, MetaUpdater, MenuPlugin> {

    public BedrockRenderContext(List<Component> content, Player player, BedrockInventory inventory, MetaUpdater metaUpdater, Placeholders placeholders, MenuPlugin plugin) {
        super(content, player, inventory, metaUpdater, placeholders, plugin);
    }
}
