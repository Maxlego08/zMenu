package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.CustomModelDataComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyCustomModelDataComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.Color;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

@AutoComponentLoader
public class CustomModelDataItemComponentLoader extends AbstractColorItemComponentLoader {

    public CustomModelDataItemComponentLoader(){
        super("custom-model-data");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        if (componentSection == null) return null;
        List<ResolvableFloat> floats = this.getFloats(componentSection);

        List<ResolvableBoolean> booleans = this.getBooleans(componentSection);

        List<ResolvableString> strings = this.getStrings(componentSection);

        List<ResolvableColor> colorList = this.getColors(componentSection);

        if (colorList.isEmpty() && booleans.isEmpty() && floats.isEmpty() && strings.isEmpty()) {
            return null;
        }

        return MinecraftVersion.isServerAtLeast("1.21.4")
                ? new CustomModelDataComponent(floats, booleans, strings, colorList)
                : new LegacyCustomModelDataComponent(floats, booleans, strings, colorList);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        if (!MinecraftVersion.isServerAtLeast("1.21.4")) return null;
        CustomModelData customModelData = itemStack.getData(DataComponentTypes.CUSTOM_MODEL_DATA);
        if (customModelData == null) return null;
        if (customModelData.floats().isEmpty() && customModelData.flags().isEmpty() && customModelData.strings().isEmpty() && customModelData.colors().isEmpty()) {
            return null;
        }

        List<ResolvableFloat> floats = new ArrayList<>(customModelData.floats().size());
        for (Float value : customModelData.floats()) floats.add(ResolvableFloat.of(value));

        List<ResolvableBoolean> flags = new ArrayList<>(customModelData.flags().size());
        for (Boolean value : customModelData.flags()) flags.add(ResolvableBoolean.of(value));

        List<ResolvableString> strings = new ArrayList<>(customModelData.strings().size());
        for (String value : customModelData.strings()) {
            if (value.indexOf('%') != -1) return null;
            strings.add(ResolvableString.ofExpression(value));
        }

        List<ResolvableColor> colors = new ArrayList<>(customModelData.colors().size());
        for (Color value : customModelData.colors()) colors.add(ResolvableColor.of(value));

        return new CustomModelDataComponent(floats, flags, strings, colors);
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || !itemMeta.hasCustomModelData()) return null;
        int customModelData = itemMeta.getCustomModelData();
        // The component applies its first float cast to an int
        float value = customModelData;
        if ((int) value != customModelData) return null;
        return new LegacyCustomModelDataComponent(List.of(ResolvableFloat.of(value)), List.of(), List.of(), List.of());
    }

    protected @NotNull List<ResolvableFloat> getFloats(@NotNull Section section) {
        List<ResolvableFloat> resolvables = new ArrayList<>();
        List<?> list = YamlSections.getList(section, "floats");
        if (list != null) {
            for (Object obj : list) {
                if (obj instanceof Number number) resolvables.add(ResolvableFloat.of(number.floatValue()));
                else if (obj instanceof String expr) resolvables.add(ResolvableFloat.of(expr));
            }
        }
        return resolvables;
    }

    protected @NotNull List<ResolvableBoolean> getBooleans(@NotNull Section section) {
        List<ResolvableBoolean> resolvables = new ArrayList<>();
        List<?> list = YamlSections.getList(section, "flags");
        if (list != null) {
            for (Object obj : list) {
                if (obj instanceof Boolean bool) resolvables.add(ResolvableBoolean.of(bool));
                else if (obj instanceof String expr) resolvables.add(ResolvableBoolean.of(expr));
            }
        }
        return resolvables;
    }

    protected @NotNull List<ResolvableString> getStrings(@NotNull Section section) {
        List<ResolvableString> resolvables = new ArrayList<>();
        List<String> list = YamlSections.getStringList(section, "strings");
        for (String s : list) {
            resolvables.add(ResolvableString.ofExpression(s));
        }
        return resolvables;
    }

    protected @NotNull List<ResolvableColor> getColors(@NotNull Section section) {
        List<ResolvableColor> colors = new ArrayList<>();
        List<?> colorsList = YamlSections.getList(section, "colors");
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
