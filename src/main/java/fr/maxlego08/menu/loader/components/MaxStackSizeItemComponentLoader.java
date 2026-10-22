package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.MaxStackSizeComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyMaxStackSizeComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
public class MaxStackSizeItemComponentLoader extends ItemComponentLoader {
    private final MenuPlugin plugin;

    public MaxStackSizeItemComponentLoader(MenuPlugin plugin){
        super("max-stack-size");
        this.plugin = plugin;
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        ResolvableInt maxStackSize = this.asResolvableInt(configuration, path);
        if (maxStackSize != null) {
            return MinecraftVersion.isServerAtLeast("1.21.3")
                    ? new MaxStackSizeComponent(maxStackSize)
                    : new LegacyMaxStackSizeComponent(maxStackSize);
        }
        return null;
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        Integer maxStackSize = itemStack.getData(DataComponentTypes.MAX_STACK_SIZE);
        return maxStackSize == null ? null : new MaxStackSizeComponent(ResolvableInt.of(maxStackSize));
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || !itemMeta.hasMaxStackSize()) return null;
        return new LegacyMaxStackSizeComponent(ResolvableInt.of(itemMeta.getMaxStackSize()));
    }
}
