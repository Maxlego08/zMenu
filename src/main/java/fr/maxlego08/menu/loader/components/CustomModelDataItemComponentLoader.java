package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.CustomModelDataComponent;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomModelDataItemComponentLoader extends ItemComponentLoader {

    public CustomModelDataItemComponentLoader(){
        super("custom_model_data");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
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

    private Color parseColor(Object obj) {
        if (obj instanceof Float[] floatsArray && (floatsArray.length == 3 || floatsArray.length == 4)) {
            int r = (int) (floatsArray[0] * 255);
            int g = (int) (floatsArray[1] * 255);
            int b = (int) (floatsArray[2] * 255);

            if (floatsArray.length == 3) { // RGB
                return Color.fromRGB(r, g, b);
            } else { // RGBA
                int a = (int) (floatsArray[3] * 255);
                return Color.fromARGB(a, r, g, b);
            }
        }

        if (obj instanceof Double[] doublesArray && (doublesArray.length == 3 || doublesArray.length == 4)) {
            int r = (int) (doublesArray[0] * 255);
            int g = (int) (doublesArray[1] * 255);
            int b = (int) (doublesArray[2] * 255);

            if (doublesArray.length == 3) { // RGB
                return Color.fromRGB(r, g, b);
            } else { // RGBA
                int a = (int) (doublesArray[3] * 255);
                return Color.fromARGB(a, r, g, b);
            }
        }

        if (obj instanceof List<?> list && (list.size() == 3 || list.size() == 4)) {
            try {
                int r = (int) (((Number) list.get(0)).doubleValue() * 255);
                int g = (int) (((Number) list.get(1)).doubleValue() * 255);
                int b = (int) (((Number) list.get(2)).doubleValue() * 255);

                if (list.size() == 3) { // RGB
                    return Color.fromRGB(r, g, b);
                } else { // RGBA
                    int a = (int) (((Number) list.get(3)).doubleValue() * 255);
                    return Color.fromARGB(a, r, g, b);
                }
            } catch (Exception ignored) {
            }
        }

        if (obj instanceof Number number) {
            int rgb = number.intValue();
            return Color.fromRGB(rgb);
        }

        if (obj instanceof String str) {
            try {
                String cleaned = str.trim()
                        .replace("#", "")
                        .replace("0x", "")
                        .replace("0X", "");

                int rgb = Integer.parseInt(cleaned, 16);

                if (cleaned.length() == 8) {
                    return Color.fromARGB(
                            (rgb >> 24) & 0xFF, // Alpha
                            (rgb >> 16) & 0xFF, // Red
                            (rgb >> 8) & 0xFF,  // Green
                            rgb & 0xFF          // Blue
                    );
                } else if (cleaned.length() == 6) {
                    return Color.fromRGB(rgb);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return null;
    }
}
