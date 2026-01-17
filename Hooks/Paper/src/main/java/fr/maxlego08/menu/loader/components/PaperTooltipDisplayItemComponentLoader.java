package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.paper.components.PaperTooltipDisplayComponent;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class PaperTooltipDisplayItemComponentLoader extends ItemComponentLoader {

    public PaperTooltipDisplayItemComponentLoader(){
        super("tooltip_display");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        boolean hideTooltip = componentSection.getBoolean("hide_tooltip", false);
        List<String> hiddenComponents = componentSection.getStringList("hidden_components");
        TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay();
        builder.hideTooltip(hideTooltip);
        for (String hiddenComponent : hiddenComponents) {
            NamespacedKey key = NamespacedKey.fromString(hiddenComponent);
            if (key != null) {
                try {
                    DataComponentType dataComponentType = Registry.DATA_COMPONENT_TYPE.getOrThrow(key);
                    builder.addHiddenComponents(dataComponentType);
                } catch (Exception ignored) {
                }
            }
        }
        return new PaperTooltipDisplayComponent(builder.build());
    }
}
