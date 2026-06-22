package fr.maxlego08.menu.loader.components.paper;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableProfileResolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableUUID;
import fr.maxlego08.menu.itemstack.components.paper.PaperProfileComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaperProfileComponentLoader extends ItemComponentLoader {

    public PaperProfileComponentLoader() {
        super("profile");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) {
            String name = configuration.getString(this.normalizePath(path));
            ResolvableString resolvedName = ResolvableString.autoOrNull(name);
            return resolvedName != null ? new PaperProfileComponent(new ResolvableProfileResolvable(resolvedName)) : null;
        }

        ResolvableUUID uuid = ResolvableUUID.autoOrNull(componentSection.getString("id"));
        ResolvableString name = ResolvableString.autoOrNull(componentSection.getString("name"));
        List<ResolvableProfileResolvable.ProfilePropertyEntry> properties = this.loadProperties(componentSection.getMapList("properties"));
        ResolvableNamespacedKey texture = ResolvableNamespacedKey.autoOrNull(componentSection.getString("texture"));
        ResolvableNamespacedKey cape = ResolvableNamespacedKey.autoOrNull(componentSection.getString("cape"));
        ResolvableNamespacedKey elytra = ResolvableNamespacedKey.autoOrNull(componentSection.getString("elytra"));
        ResolvableEnum<PlayerTextures.SkinModel> model = ResolvableEnum.autoOrNull(PlayerTextures.SkinModel.class, componentSection.getString("model"));

        ResolvableProfileResolvable resolvable = new ResolvableProfileResolvable(name, uuid, properties, texture, cape, elytra, model);
        return new PaperProfileComponent(resolvable);
    }

    private @Nullable List<ResolvableProfileResolvable.ProfilePropertyEntry> loadProperties(@NotNull List<Map<?, ?>> rawProperties) {
        if (rawProperties.isEmpty()) return null;

        List<ResolvableProfileResolvable.ProfilePropertyEntry> properties = new ArrayList<>(rawProperties.size());
        for (Map<?, ?> raw : rawProperties) {
            ResolvableString name = ResolvableString.autoOrNull((String) raw.get("name"));
            ResolvableString value = ResolvableString.autoOrNull((String) raw.get("value"));
            ResolvableString signature = ResolvableString.autoOrNull((String) raw.get("signature"));
            if (name != null && value != null) {
                properties.add(new ResolvableProfileResolvable.ProfilePropertyEntry(name, value, signature));
            }
        }
        return properties;
    }
}
