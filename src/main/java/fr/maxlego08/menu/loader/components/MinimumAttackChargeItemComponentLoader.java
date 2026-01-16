package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.MinimumAttackChargeComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class MinimumAttackChargeItemComponentLoader extends ItemComponentLoader {

    public MinimumAttackChargeItemComponentLoader(){
        super("minimum_attack_charge");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        float charge = (float) configuration.getDouble(path, -1);
        return charge < 0 ? null : new MinimumAttackChargeComponent(charge);
    }
}
