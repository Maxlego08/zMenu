package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.LoreComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyLoreComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableComponent;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@AutoComponentLoader
public class LoreItemComponentLoader extends ItemComponentLoader {
    private final MetaUpdater metaUpdater;

    public LoreItemComponentLoader(MenuPlugin menuPlugin){
        super("lore");
        this.metaUpdater = menuPlugin.getMetaUpdater();
    }

    @Override
    public @Nullable ItemComponent load(
            @NotNull MenuItemStackContext context,
            @NotNull File file,
            @NotNull YamlConfiguration configuration,
            @NotNull String path,
            @Nullable ConfigurationSection componentSection
    ) {

        List<String> lines = this.readLore(configuration, this.normalizePath(path));

        if (lines.isEmpty()) {
            return null;
        }

        return this.metaUpdater instanceof PaperMetaUpdater paperMetaUpdater && MinecraftVersion.isServerAtLeast("1.21.3")
                ? this.loadPaperComponent(lines, paperMetaUpdater)
                : this.loadLegacyComponent(lines);
    }

    private @NotNull List<String> readLore(
            @NotNull YamlConfiguration configuration,
            @NotNull String path
    ) {

        Object value = configuration.get(path);

        if (value instanceof String str) {
            return List.of(str);
        }

        if (value instanceof List<?> list) {
            return list.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }

        return List.of();
    }

    private @NotNull LoreComponent loadPaperComponent(@NotNull List<String> lines, PaperMetaUpdater paperMetaUpdater) {
        return new LoreComponent(this.mapLore(lines, (s -> ResolvableComponent.auto(s, paperMetaUpdater))));
    }

    private @NotNull LegacyLoreComponent loadLegacyComponent(@NotNull List<String> lines) {
        return new LegacyLoreComponent(lines.stream().map(ResolvableString::auto).toList());
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        ItemLore lore = itemStack.getData(DataComponentTypes.LORE);
        if (lore == null) return null;

        if (this.metaUpdater instanceof PaperMetaUpdater paperMetaUpdater) {
            List<ResolvableComponent> lines = new ArrayList<>(lore.lines().size());
            for (Component line : lore.lines()) {
                ResolvableComponent resolvableLine = toResolvableComponent(line, paperMetaUpdater);
                if (resolvableLine == null) return null;
                lines.add(resolvableLine);
            }
            return new LoreComponent(lines);
        }

        return this.toLegacyLore(lore.lines());
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || !itemMeta.hasLore()) return null;
        List<Component> lore = itemMeta.lore();
        return lore == null || lore.isEmpty() ? null : this.toLegacyLore(lore);
    }

    private @Nullable LegacyLoreComponent toLegacyLore(@NotNull List<Component> lore) {
        List<ResolvableString> lines = new ArrayList<>(lore.size());
        for (Component line : lore) {
            String text = toLegacyText(line);
            if (text == null) return null;
            lines.add(ResolvableString.of(text));
        }
        return new LegacyLoreComponent(lines);
    }

    private <T> List<T> mapLore(List<String> lines, Function<String, T> mapper) {
        return lines.stream().map(mapper).toList();
    }
}
