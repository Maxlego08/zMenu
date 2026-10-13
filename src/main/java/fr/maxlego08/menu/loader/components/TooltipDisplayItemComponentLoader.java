package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.TooltipDisplayComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistry;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AutoComponentLoader
@SinceVersion("1.21.5")
public class TooltipDisplayItemComponentLoader extends ItemComponentLoader {

    public TooltipDisplayItemComponentLoader(){
        super("tooltip-display");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        ResolvableBoolean hideTooltip = ResolvableBoolean.auto(componentSection.getString("hide-tooltip"), false);
        List<ResolvableRegistryEntry<DataComponentType>> hiddenComponentEntries = componentSection.getStringList("hidden-components").stream()
                .map(component -> ResolvableRegistry.autoOrNull(component, RegistryKey.DATA_COMPONENT_TYPE))
                .filter(Objects::nonNull)
                .toList();


        return new TooltipDisplayComponent(hideTooltip, hiddenComponentEntries);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        TooltipDisplay tooltipDisplay = itemStack.getData(DataComponentTypes.TOOLTIP_DISPLAY);
        if (tooltipDisplay == null) return null;

        List<ResolvableRegistryEntry<DataComponentType>> hiddenComponents = new ArrayList<>(tooltipDisplay.hiddenComponents().size());
        for (DataComponentType type : tooltipDisplay.hiddenComponents()) {
            ResolvableRegistryEntry<DataComponentType> entry = ResolvableRegistry.ofRegisteredOrNull(type, RegistryKey.DATA_COMPONENT_TYPE);
            if (entry == null) return null;
            hiddenComponents.add(entry);
        }
        return new TooltipDisplayComponent(ResolvableBoolean.of(tooltipDisplay.hideTooltip()), hiddenComponents);
    }
}
