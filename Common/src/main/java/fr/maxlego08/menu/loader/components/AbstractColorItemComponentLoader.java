package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public abstract class AbstractColorItemComponentLoader extends ItemComponentLoader {

    public AbstractColorItemComponentLoader(@NotNull String componentName) {
        super(componentName);
    }


    @Nullable
    protected Color parseColor(@NotNull Object obj) {
        switch (obj) {
            case Float[] floatsArray when (floatsArray.length == 3 || floatsArray.length == 4) -> {
                return getColor(floatsArray[0] * 255, floatsArray[1] * 255, floatsArray[2] * 255, floatsArray.length == 3, floatsArray[3] * 255);
            }
            case Double[] doublesArray when (doublesArray.length == 3 || doublesArray.length == 4) -> {
                return getColor(doublesArray[0] * 255, doublesArray[1] * 255, doublesArray[2] * 255, doublesArray.length == 3, doublesArray[3] * 255);
            }
            case List<?> list when (list.size() == 3 || list.size() == 4) -> {
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
            default -> {
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

    @NonNull
    private Color getColor(double v, double v2, double v3, boolean b2, double v4) {
        int r = (int) (v);
        int g = (int) (v2);
        int b = (int) (v3);

        if (b2) { // RGB
            return Color.fromRGB(r, g, b);
        } else { // RGBA
            int a = (int) (v4);
            return Color.fromARGB(a, r, g, b);
        }
    }
}
