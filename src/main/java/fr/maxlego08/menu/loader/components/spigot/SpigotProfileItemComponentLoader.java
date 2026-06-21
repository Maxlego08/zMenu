package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.ProfileComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvablePlayerProfile;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableUUID;
import fr.maxlego08.menu.common.utils.PlayerUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@AutoComponentLoader
@SinceVersion("1.21.9")
public class SpigotProfileItemComponentLoader extends ItemComponentLoader {

    public SpigotProfileItemComponentLoader(){
        super("profile");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) {
            return this.loadSimplePath(configuration, path);
        }
        return this.loadComponentSection(componentSection);
    }

    private @Nullable ItemComponent loadSimplePath(@NotNull YamlConfiguration configuration, @NotNull String path) {
        path = this.normalizePath(path);
        String value = configuration.getString(path);
        if (value == null) return null;

        if (isUrl(value)) {
            PlayerProfile profile = PlayerUtil.Profile.getProfileFromUrl(value);
            return profile != null ? new ProfileComponent(profile) : null;
        }

        try {
            UUID uuid = UUID.fromString(value);
            ResolvableUUID resolvableUuid = ResolvableUUID.of(uuid);
            return new ProfileComponent(new ResolvablePlayerProfile(resolvableUuid, null, null, null, null));
        } catch (IllegalArgumentException e) {
            Resolvable<String> name = ResolvableString.auto(value);
            return new ProfileComponent(new ResolvablePlayerProfile(null, name, null, null, null));
        }
    }

    private @Nullable ItemComponent loadComponentSection(@NotNull ConfigurationSection componentSection) {
        ResolvableUUID uuid = null;
        try {
            String uuidString = componentSection.getString("id");
            if (uuidString != null) {
                uuid = ResolvableUUID.auto(uuidString);
            }
        } catch (IllegalArgumentException ignored) {
        }

        Resolvable<String> name = null;
        String nameString = componentSection.getString("name");
        if (nameString != null) {
            name = ResolvableString.auto(nameString);
        }

        Resolvable<String> textureUrl = null;
        String textureString = componentSection.getString("texture");
        if (textureString != null) {
            textureUrl = ResolvableString.auto(textureString);
        }

        Resolvable<String> capeUrl = null;
        String capeString = componentSection.getString("cape");
        if (capeString != null) {
            capeUrl = ResolvableString.auto(capeString);
        }

        ResolvableEnum<PlayerTextures.SkinModel> skinModel = null;
        String modelString = componentSection.getString("model");
        if (modelString != null) {
            skinModel = ResolvableEnum.auto(PlayerTextures.SkinModel.class, modelString);
        }

        if (uuid == null && name == null) return null;

        return new ProfileComponent(new ResolvablePlayerProfile(uuid, name, textureUrl, capeUrl, skinModel));
    }

    private static boolean isUrl(@NotNull String value) {
        try {
            new URL(value);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
