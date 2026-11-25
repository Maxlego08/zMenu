package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.BundleContentsComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyBundleContentsComponent;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BundleContents;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoComponentLoader
public class BundleContentsItemComponentLoader extends AbstractMenuItemStackListComponentLoaderBase {

    public BundleContentsItemComponentLoader(MenuPlugin plugin){
        super("bundle-contents", plugin);
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        path = this.normalizePath(path);

        List<Map<?, ?>> mapList = configuration.getMapList(path);
        List<MenuItemStack> contents = this.loadItemStackList(mapList, file);
        if (contents.isEmpty()) return null;
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new BundleContentsComponent(contents)
                : new LegacyBundleContentsComponent(contents);
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
        BundleContents bundleContents = itemStack.getData(DataComponentTypes.BUNDLE_CONTENTS);
        if (bundleContents == null || bundleContents.contents().isEmpty()) return null;

        List<MenuItemStack> contents = this.convertItemStackList(bundleContents.contents());
        return contents == null ? null : new BundleContentsComponent(contents);
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof BundleMeta bundleMeta) || !bundleMeta.hasItems()) return null;

        List<MenuItemStack> contents = this.convertItemStackListFromItemMeta(bundleMeta.getItems());
        return contents == null ? null : new LegacyBundleContentsComponent(contents);
    }
}
