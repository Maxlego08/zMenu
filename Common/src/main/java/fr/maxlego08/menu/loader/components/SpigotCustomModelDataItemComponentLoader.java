package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.annotations.SpigotOnly;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.CustomModelDataComponent;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@AutoComponentLoader
@SinceVersion("1.20.5")
@SpigotOnly
public class SpigotCustomModelDataItemComponentLoader extends AbstractColorItemComponentLoader {

    public SpigotCustomModelDataItemComponentLoader(){
        super("custom-model-data");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        List<Float> floats = this.getFloats(componentSection);

        List<Boolean> booleans = this.getBooleans(componentSection);

        List<String> strings = this.getStrings(componentSection);

        List<Color> colorList = this.getColors(componentSection);

        if (colorList.isEmpty() && booleans.isEmpty() && floats.isEmpty() && strings.isEmpty()) {
            return null;
        }

        return new CustomModelDataComponent(
            colorList,
            booleans,
            floats,
            strings
        );
    }

    protected @NotNull List<Float> getFloats(@NotNull ConfigurationSection section) {
        return section.getFloatList("floats");
    }

    protected @NotNull List<Boolean> getBooleans(@NotNull ConfigurationSection section) {
        return section.getBooleanList("flags");
    }

    protected @NotNull List<String> getStrings(@NotNull ConfigurationSection section) {
        return section.getStringList("strings");
    }

    protected @NotNull List<Color> getColors(@NotNull ConfigurationSection section) {
        List<Color> colors = new ArrayList<>();
        List<?> colorsList = section.getList("colors");
        if (colorsList != null) {
            for (Object obj : colorsList) {
                Color color = this.parseColor(obj);
                if (color != null) {
                    colors.add(color);
                }
            }
        }
        return colors;
    }

}
