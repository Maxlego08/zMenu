package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.SulfurCubeContentComponent;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoComponentLoader
@SinceVersion("26.2")
public class SulfurCubeContentItemComponentLoader extends AbstractMenuItemStackListComponentLoaderBase {

    public SulfurCubeContentItemComponentLoader(MenuPlugin plugin) {
        super("sulfur-cube-content", plugin);
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        if (componentSection == null) return null;
        Map<String, Object> values = componentSection.getStringRouteMappedValues(true);
        MenuItemStack menuItemStack = this.loadItemStack(values, file);
        return menuItemStack == null ? null : new SulfurCubeContentComponent(menuItemStack);
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
        ItemStack absorbedItem = SulfurCubeContentComponent.getAbsorbedItem(itemStack);
        if (absorbedItem == null || absorbedItem.isEmpty()) return null;

        MenuItemStack menuItemStack = this.convertItemStack(absorbedItem);
        return menuItemStack == null ? null : new SulfurCubeContentComponent(menuItemStack);
    }
}
