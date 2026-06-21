package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.annotations.SpigotOnly;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.CustomModelDataComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
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
        List<ResolvableFloat> floats = this.getFloats(componentSection);

        List<ResolvableBoolean> booleans = this.getBooleans(componentSection);

        List<ResolvableString> strings = this.getStrings(componentSection);

        List<ResolvableColor> colorList = this.getColors(componentSection);

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

    protected @NotNull List<ResolvableFloat> getFloats(@NotNull ConfigurationSection section) {
        List<ResolvableFloat> resolvables = new ArrayList<>();
        List<?> list = section.getList("floats");
        if (list != null) {
            for (Object obj : list) {
                if (obj instanceof Number number) resolvables.add(ResolvableFloat.of(number.floatValue()));
                else if (obj instanceof String expr) resolvables.add(ResolvableFloat.of(expr));
            }
        }
        return resolvables;
    }

    protected @NotNull List<ResolvableBoolean> getBooleans(@NotNull ConfigurationSection section) {
        List<ResolvableBoolean> resolvables = new ArrayList<>();
        List<?> list = section.getList("flags");
        if (list != null) {
            for (Object obj : list) {
                if (obj instanceof Boolean bool) resolvables.add(ResolvableBoolean.of(bool));
                else if (obj instanceof String expr) resolvables.add(ResolvableBoolean.of(expr));
            }
        }
        return resolvables;
    }

    protected @NotNull List<ResolvableString> getStrings(@NotNull ConfigurationSection section) {
        List<ResolvableString> resolvables = new ArrayList<>();
        List<String> list = section.getStringList("strings");
        for (String s : list) {
            resolvables.add(ResolvableString.ofExpression(s));
        }
        return resolvables;
    }

    protected @NotNull List<ResolvableColor> getColors(@NotNull ConfigurationSection section) {
        List<ResolvableColor> colors = new ArrayList<>();
        List<?> colorsList = section.getList("colors");
        if (colorsList != null) {
            for (Object obj : colorsList) {
                ResolvableColor color = ResolvableColor.of(obj);
                if (color != null) {
                    colors.add(color);
                }
            }
        }
        return colors;
    }

}
