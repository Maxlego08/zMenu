package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.OfflinePlayerCache;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableUUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public final class ResolvablePlayerProfile implements Resolvable<PlayerProfile> {

    private final @Nullable ResolvableUUID uuid;
    private final @Nullable Resolvable<String> name;
    private final @Nullable Resolvable<String> textureUrl;
    private final @Nullable Resolvable<String> capeUrl;
    private final @Nullable ResolvableEnum<PlayerTextures.SkinModel> skinModel;

    public ResolvablePlayerProfile(
            @Nullable ResolvableUUID uuid,
            @Nullable Resolvable<String> name,
            @Nullable Resolvable<String> textureUrl,
            @Nullable Resolvable<String> capeUrl,
            @Nullable ResolvableEnum<PlayerTextures.SkinModel> skinModel
    ) {
        super();
        this.uuid = uuid;
        this.name = name;
        this.textureUrl = textureUrl;
        this.capeUrl = capeUrl;
        this.skinModel = skinModel;
    }

    @Override
    public @Nullable PlayerProfile resolve(@NotNull BuildContext context) {
        UUID uuidVal = Resolvable.resolve(context, this.uuid);
        String name = Resolvable.resolve(context, this.name);
        PlayerProfile profile = null;

        if (uuidVal != null) {
            try {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuidVal);
                profile = offlinePlayer.getPlayerProfile();
            } catch (Exception ignored) {
            }
        } else if (name != null) {
            OfflinePlayer offlinePlayer = OfflinePlayerCache.get(name);
            profile = offlinePlayer.getPlayerProfile();
        }

        if (profile == null) return null;

        String textureUrl = Resolvable.resolve(context, this.textureUrl);
        if (textureUrl != null) {
            try {
                URL url = new URL(textureUrl);
                PlayerTextures textures = profile.getTextures();
                PlayerTextures.SkinModel model = Resolvable.resolve(context, this.skinModel);
                textures.setSkin(url, model);
                profile.setTextures(textures);
            } catch (MalformedURLException ignored) {
            }
        }

        String capeUrl = Resolvable.resolve(context, this.capeUrl);
        if (capeUrl != null) {
            try {
                URL url = new URL(capeUrl);
                PlayerTextures textures = profile.getTextures();
                textures.setCape(url);
                profile.setTextures(textures);
            } catch (MalformedURLException ignored) {
            }
        }

        return profile;
    }
}
