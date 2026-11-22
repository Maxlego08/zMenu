package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.CustomNameComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyCustomNameComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableComponent;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoComponentLoader
public class CustomNameItemComponentLoader extends ItemComponentLoader {
    private final MetaUpdater metaUpdater;

    public CustomNameItemComponentLoader(MenuPlugin plugin){
        super("custom-name");
        this.metaUpdater = plugin.getMetaUpdater();
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        path = this.normalizePath(path);
        String customName = configuration.getString(path);
        if (this.metaUpdater instanceof PaperMetaUpdater paperMetaUpdater && MinecraftVersion.isServerAtLeast("1.21.3")) {
            ResolvableComponent resolvableComponent = ResolvableComponent.autoOrNull(customName, paperMetaUpdater);
            return resolvableComponent != null ? new CustomNameComponent(resolvableComponent) : null;
        }
        ResolvableString resolvableString = ResolvableString.autoOrNull(customName);
        return resolvableString != null ? new LegacyCustomNameComponent(resolvableString) : null;
    }

    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        Component customName = itemStack.getData(DataComponentTypes.CUSTOM_NAME);
        if (customName == null) return null;
        if (this.metaUpdater instanceof PaperMetaUpdater paperMetaUpdater) {
            ResolvableComponent component = toResolvableComponent(customName, paperMetaUpdater);
            return component == null ? null : new CustomNameComponent(component);
        }
        String text = toLegacyText(customName);
        return text == null ? null : new LegacyCustomNameComponent(ResolvableString.of(text));
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        Component customName = itemMeta == null || !itemMeta.hasDisplayName() ? null : itemMeta.displayName();
        String text = customName == null ? null : toLegacyText(customName);
        return text == null ? null : new LegacyCustomNameComponent(ResolvableString.of(text));
    }
}
