package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.BreakSoundComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.5")
public final class BreakSoundItemComponentLoader extends ItemComponentLoader {

    public BreakSoundItemComponentLoader() {
        super("break-sound");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        ResolvableNamespacedKey resolvableNamespacedKey = ResolvableNamespacedKey.autoOrNull(configuration.getString(path));
        if (resolvableNamespacedKey == null) return null;
        return new BreakSoundComponent(resolvableNamespacedKey);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        Key breakSound = itemStack.getData(DataComponentTypes.BREAK_SOUND);
        if (breakSound == null) return null;
        NamespacedKey key = NamespacedKey.fromString(breakSound.asString());
        return key == null ? null : new BreakSoundComponent(ResolvableNamespacedKey.of(key));
    }
}
