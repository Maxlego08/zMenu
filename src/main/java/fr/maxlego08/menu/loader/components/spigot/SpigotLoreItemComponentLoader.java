package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.LoreComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableComponent;
import fr.maxlego08.menu.itemstack.components.paper.PaperLoreComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.function.Function;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class SpigotLoreItemComponentLoader extends ItemComponentLoader {
    private final MetaUpdater metaUpdater;

    public SpigotLoreItemComponentLoader(MenuPlugin menuPlugin){
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

        return this.metaUpdater instanceof PaperMetaUpdater paperMetaUpdater ? this.loadPaperComponent(lines, paperMetaUpdater) : this.loadSpigotComponent(lines);
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

    private @NotNull PaperLoreComponent loadPaperComponent(@NotNull List<String> lines, PaperMetaUpdater paperMetaUpdater) {
        return new PaperLoreComponent(this.mapLore(lines, (s -> ResolvableComponent.auto(s, paperMetaUpdater))));
    }

    private @NotNull LoreComponent loadSpigotComponent(@NotNull List<String> lines) {
        return new LoreComponent(lines.stream().map(ResolvableString::auto).toList());
    }

    private <T> List<T> mapLore(List<String> lines, Function<String, T> mapper) {
        return lines.stream().map(mapper).toList();
    }
}
