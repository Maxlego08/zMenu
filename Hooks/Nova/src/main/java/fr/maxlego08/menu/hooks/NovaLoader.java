package fr.maxlego08.menu.hooks;

import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.nova.api.Nova;
import xyz.xenondevs.nova.api.block.NovaBlockRegistry;
import xyz.xenondevs.nova.api.item.NovaItemRegistry;

import java.util.Objects;

public class NovaLoader extends MaterialLoader {
    public NovaLoader() {
        super("nova");
    }

    @Override
    public ItemStack load(Player player, YamlConfiguration configuration, String path, String materialString) {
        NovaBlockRegistry blockRegistry = Nova.getNova().getBlockRegistry();
        NovaItemRegistry itemRegistry = Nova.getNova().getItemRegistry();
        return blockRegistry.getOrNull(materialString) == null ? itemRegistry.get(materialString).createItemStack() : Objects.requireNonNull(blockRegistry.get(materialString).getItem()).createItemStack();
    }
}
