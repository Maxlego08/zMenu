package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.CustomModelDataComponent;
import fr.maxlego08.menu.loader.components.AbstractColorItemComponentLoader;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpigotCustomModelDataItemComponentLoader extends AbstractColorItemComponentLoader {

    public SpigotCustomModelDataItemComponentLoader(){
        super("custom-model-data");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        Optional<List<Float>> floatList;
        List<Float> floats = componentSection.getFloatList("floats");
        floatList = floats.isEmpty() ? Optional.empty() : Optional.of(floats);

        Optional<List<Boolean>> booleanList;
        List<Boolean> booleans = componentSection.getBooleanList("flags");
        booleanList = booleans.isEmpty() ? Optional.empty() : Optional.of(booleans);

        Optional<List<String>> stringList;
        List<String> strings = componentSection.getStringList("strings");
        stringList = strings.isEmpty() ? Optional.empty() : Optional.of(strings);

        Optional<List<Color>> colorList;
        List<?> colorsList = componentSection.getList("colors");
        if (colorsList == null || colorsList.isEmpty()) {
            colorList = Optional.empty();
        } else {
            List<Color> colors = new ArrayList<>();
            for (Object obj : colorsList) {
                Color color = parseColor(obj);
                if (color != null) {
                    colors.add(color);
                }
            }
            colorList = colors.isEmpty() ? Optional.empty() : Optional.of(colors);
        }

        if (colorList.isEmpty() && booleanList.isEmpty() && floatList.isEmpty() && stringList.isEmpty()) {
            return null;
        }

        return new CustomModelDataComponent(
            colorList,
            booleanList,
            floatList,
            stringList
        );
    }


}
