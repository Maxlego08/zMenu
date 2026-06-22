package fr.maxlego08.menu.loader.components.paper;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.PaperOnly;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistry;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.itemstack.components.paper.PaperTooltipDisplayComponent;
import io.papermc.paper.datacomponent.DataComponentType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Objects;

@AutoComponentLoader
@SinceVersion("1.21.5")
@PaperOnly
public class PaperTooltipDisplayItemComponentLoader extends ItemComponentLoader {

    public PaperTooltipDisplayItemComponentLoader(){
        super("tooltip_display");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        ResolvableBoolean hideTooltip = ResolvableBoolean.auto(componentSection.getString("hide_tooltip"), false);
        List<ResolvableRegistryEntry<DataComponentType>> hiddenComponentEntries = componentSection.getStringList("hidden_components").stream()
                .map(component -> ResolvableRegistry.autoOrNull(component, DataComponentType.class))
                .filter(Objects::nonNull)
                .toList();


        return new PaperTooltipDisplayComponent(hideTooltip, hiddenComponentEntries);
    }
}
