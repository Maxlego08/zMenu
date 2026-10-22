package fr.maxlego08.menu.loader.components;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.ProfileComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyProfileComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableProfileResolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableUUID;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

@AutoComponentLoader
public class ProfileItemComponentLoader extends ItemComponentLoader {

    public ProfileItemComponentLoader() {
        super("profile");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) {
            String name = configuration.getString(this.normalizePath(path));
            ResolvableString resolvedName = ResolvableString.autoOrNull(name);
            return resolvedName != null ? this.createComponent(new ResolvableProfileResolvable(resolvedName)) : null;
        }

        ResolvableUUID uuid = ResolvableUUID.autoOrNull(componentSection.getString("id"));
        ResolvableString name = ResolvableString.autoOrNull(componentSection.getString("name"));
        List<ResolvableProfileResolvable.ProfilePropertyEntry> properties = this.loadProperties(componentSection.getMapList("properties"));
        ResolvableNamespacedKey texture = ResolvableNamespacedKey.autoOrNull(componentSection.getString("texture"));
        ResolvableNamespacedKey cape = ResolvableNamespacedKey.autoOrNull(componentSection.getString("cape"));
        ResolvableNamespacedKey elytra = ResolvableNamespacedKey.autoOrNull(componentSection.getString("elytra"));
        ResolvableEnum<PlayerTextures.SkinModel> model = ResolvableEnum.autoOrNull(PlayerTextures.SkinModel.class, componentSection.getString("model"));

        ResolvableProfileResolvable resolvable = new ResolvableProfileResolvable(name, uuid, properties, texture, cape, elytra, model);
        return this.createComponent(resolvable);
    }

    private @NotNull ItemComponent createComponent(@NotNull ResolvableProfileResolvable resolvable) {
        return MinecraftVersion.isServerAtLeast("1.21.9")
                ? new ProfileComponent(resolvable)
                : new LegacyProfileComponent(resolvable);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        if (!MinecraftVersion.isServerAtLeast("1.21.9")) return null;
        ResolvableProfile profile = itemStack.getData(DataComponentTypes.PROFILE);
        if (profile == null) return null;

        String name = profile.name();
        if (name != null && Resolvable.isExpression(name)) return null;
        UUID uuid = profile.uuid();

        List<ResolvableProfileResolvable.ProfilePropertyEntry> properties = new ArrayList<>(profile.properties().size());
        for (ProfileProperty property : profile.properties()) {
            String signature = property.getSignature();
            if (Resolvable.isExpression(property.getName()) || Resolvable.isExpression(property.getValue()) || (signature != null && Resolvable.isExpression(signature))) {
                return null;
            }
            properties.add(new ResolvableProfileResolvable.ProfilePropertyEntry(
                    ResolvableString.of(property.getName()),
                    ResolvableString.of(property.getValue()),
                    signature == null ? null : ResolvableString.of(signature)
            ));
        }

        ResolvableProfile.SkinPatch skinPatch = profile.skinPatch();
        Key body = skinPatch.body();
        Key cape = skinPatch.cape();
        Key elytra = skinPatch.elytra();
        PlayerTextures.SkinModel model = skinPatch.model();

        return new ProfileComponent(new ResolvableProfileResolvable(
                name == null ? null : ResolvableString.of(name),
                uuid == null ? null : ResolvableUUID.of(uuid),
                properties.isEmpty() ? null : properties,
                toResolvableKey(body),
                toResolvableKey(cape),
                toResolvableKey(elytra),
                model == null ? null : ResolvableEnum.of(PlayerTextures.SkinModel.class, model)
        ));
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof SkullMeta skullMeta)) return null;
        PlayerProfile profile = skullMeta.getPlayerProfile();
        if (profile == null) return null;

        String name = profile.getName();
        UUID uuid = profile.getId();
        if (name == null && uuid == null) return null;
        if (name != null && Resolvable.isExpression(name)) return null;

        List<ResolvableProfileResolvable.ProfilePropertyEntry> properties = new ArrayList<>(profile.getProperties().size());
        for (ProfileProperty property : profile.getProperties()) {
            String signature = property.getSignature();
            if (Resolvable.isExpression(property.getName()) || Resolvable.isExpression(property.getValue()) || (signature != null && Resolvable.isExpression(signature))) {
                return null;
            }
            properties.add(new ResolvableProfileResolvable.ProfilePropertyEntry(
                    ResolvableString.of(property.getName()),
                    ResolvableString.of(property.getValue()),
                    signature == null ? null : ResolvableString.of(signature)
            ));
        }

        return new LegacyProfileComponent(new ResolvableProfileResolvable(
                name == null ? null : ResolvableString.of(name),
                uuid == null ? null : ResolvableUUID.of(uuid),
                properties.isEmpty() ? null : properties,
                null,
                null,
                null,
                null
        ));
    }

    private static @Nullable ResolvableNamespacedKey toResolvableKey(@Nullable Key key) {
        return key == null ? null : ResolvableNamespacedKey.of(Objects.requireNonNull(NamespacedKey.fromString(key.asString())));
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
