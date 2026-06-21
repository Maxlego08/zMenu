package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.PiercingWeaponComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableSound;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.11")
public class SpigotPiercingWeaponItemComponentLoader extends ItemComponentLoader {

    public SpigotPiercingWeaponItemComponentLoader(){
        super("piercing-weapon");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        ResolvableBoolean dealsKnockback = this.asResolvableBoolean(componentSection, "deals-knockback", true);
        ResolvableBoolean dismounts = this.asResolvableBoolean(componentSection, "dismounts", false);
        ResolvableSound sound = this.asResolvableSound(componentSection, "sound");
        ResolvableSound hitSound = this.asResolvableSound(componentSection, "hit-sound");
        return new PiercingWeaponComponent(dealsKnockback, dismounts, sound, hitSound);
    }
}
