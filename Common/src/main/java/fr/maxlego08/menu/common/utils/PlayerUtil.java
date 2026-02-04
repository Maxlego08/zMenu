package fr.maxlego08.menu.common.utils;

import fr.maxlego08.menu.api.utils.SimpleCache;
import org.bukkit.Bukkit;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;

public class PlayerUtil {
    private static final Pattern MINECRAFT_USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");

    public static boolean isValidMinecraftUsername(@NotNull String username) {
        return MINECRAFT_USERNAME_PATTERN.matcher(username).matches();
    }

    public static class Profile {
        private static final UUID RANDOM_UUID = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4"); // We reuse the same "random" UUID all the time
        private static final PlayerProfile profile = Bukkit.createPlayerProfile(RANDOM_UUID);
        private static final SimpleCache<String, PlayerProfile> profileSimpleCache = new SimpleCache<>();

        @Nullable
        public static PlayerProfile getProfileFromUrl(@NotNull String url) {
            return profileSimpleCache.get(url, () -> {
                PlayerTextures textures = profile.getTextures();
                URL urlObject;
                try {
                    urlObject = SkinUrlDecoder.extractSkinUrl(url).toURL(); // The URL to the skin, for example: https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a
                } catch (URISyntaxException | MalformedURLException exception) {
                    exception.printStackTrace();
                    return null;
                }
                textures.setSkin(urlObject); // Set the skin of the player profile to the URL
                profile.setTextures(textures); // Set the textures back to the profile
                return profile.clone();
            });
        }

    }
}
