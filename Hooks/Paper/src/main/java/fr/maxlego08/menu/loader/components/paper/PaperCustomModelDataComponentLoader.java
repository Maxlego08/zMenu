package fr.maxlego08.menu.loader.components.paper;

import fr.maxlego08.menu.api.annotations.ComponentLoader;
import fr.maxlego08.menu.api.annotations.PaperOnly;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.itemstack.components.paper.PaperCustomModelDataComponent;
import fr.maxlego08.menu.loader.components.spigot.SpigotCustomModelDataItemComponentLoader;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

@ComponentLoader
@SinceVersion("1.20.5")
@PaperOnly
public class PaperCustomModelDataComponentLoader extends SpigotCustomModelDataItemComponentLoader {

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        List<Float> floats = getFloats(componentSection);

        List<Boolean> booleans = getBooleans(componentSection);

        List<String> strings = getStrings(componentSection);

        List<Color> colorList = getColors(componentSection);

        if (colorList.isEmpty() && booleans.isEmpty() && floats.isEmpty() && strings.isEmpty()) {
            return null;
        }

        CustomModelData.Builder builder = CustomModelData.customModelData();
        if (!colorList.isEmpty()) {
            builder.addColors(colorList);
        }
        if (!booleans.isEmpty()) {
            builder.addFlags(booleans);
        }
        if (!floats.isEmpty()) {
            builder.addFloats(floats);
        }
        if (!strings.isEmpty()) {
            builder.addStrings(strings);
        }
        return new PaperCustomModelDataComponent(builder.build());
    }
}
