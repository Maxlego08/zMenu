package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.UnbreakableComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyUnbreakableComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
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
public class UnbreakableItemComponentLoader extends ItemComponentLoader {

    public UnbreakableItemComponentLoader(){
        super("unbreakable");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        ResolvableBoolean unbreakable = this.asResolvableBoolean(configuration, path);
        unbreakable = unbreakable != null ? unbreakable : ResolvableBoolean.of(true);
        return MinecraftVersion.isServerAtLeast("1.21.5")
                ? new UnbreakableComponent(unbreakable)
                : new LegacyUnbreakableComponent(unbreakable);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        if (!MinecraftVersion.isServerAtLeast("1.21.5")) return null;
        return itemStack.hasData(DataComponentTypes.UNBREAKABLE) ? new UnbreakableComponent(ResolvableBoolean.of(true)) : null;
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || !itemMeta.isUnbreakable()) return null;
        return new LegacyUnbreakableComponent(ResolvableBoolean.of(true));
    }
}
