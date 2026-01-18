package fr.maxlego08.menu.loader.components.paper;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.paper.IntangibleProjectileComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class PaperIntangibleProjectileItemComponentLoader extends ItemComponentLoader {

    public PaperIntangibleProjectileItemComponentLoader(){
        super("intangible_projectile");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        return new IntangibleProjectileComponent();
    }
}
