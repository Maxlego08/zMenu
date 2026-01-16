package fr.maxlego08.menu.loader.components;

import fr.maxlego08.common.utils.PlayerUtil;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.OfflinePlayerCache;
import fr.maxlego08.menu.itemstack.components.ProfileComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URL;
import java.util.UUID;

public class ProfileItemComponentLoader extends ItemComponentLoader {

    public ProfileItemComponentLoader(){
        super("profile");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) {
            path = normalizePath(path);
            String name = configuration.getString(path);
            if (name == null) return null;
            try {
                UUID uuid = UUID.fromString(name);
                PlayerProfile playerProfile = Bukkit.getServer().getOfflinePlayer(uuid).getPlayerProfile();
                return new ProfileComponent(playerProfile);
            } catch (IllegalArgumentException e) {
                if (PlayerUtil.isValidMinecraftUsername(name)){
                    OfflinePlayer offlinePlayer = OfflinePlayerCache.get(name);
                    PlayerProfile playerProfile = offlinePlayer.getPlayerProfile();
                    return new ProfileComponent(playerProfile);
                } else {
                    @Nullable PlayerProfile playerProfile = PlayerUtil.Profile.getProfileFromUrl(name);
                    if (playerProfile != null){
                        return new ProfileComponent(playerProfile);
                    }
                }
            }
            return null;
        }
        UUID uuid = null;
        try {
            String uuidString = componentSection.getString("id");
            if (uuidString != null){
                uuid = UUID.fromString(uuidString);
            }
        } catch (IllegalArgumentException ignored){}
        String name = componentSection.getString("name");
        PlayerProfile playerProfile = null;
        if (uuid != null){
            OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(uuid);
            playerProfile = offlinePlayer.getPlayerProfile();
        } else if (name != null){
            if (PlayerUtil.isValidMinecraftUsername(name)){
                OfflinePlayer offlinePlayer = OfflinePlayerCache.get(name);
                playerProfile = offlinePlayer.getPlayerProfile();
            }
        }
        if (playerProfile == null) return null;
        String textureUrl = componentSection.getString("texture");
        PlayerTextures.SkinModel skinModel = PlayerTextures.SkinModel.CLASSIC;
        String skinModelString = componentSection.getString("model");
        if (skinModelString != null && skinModelString.equalsIgnoreCase("slim")){
            skinModel = PlayerTextures.SkinModel.SLIM;
        }
        if (textureUrl != null){
            URL urlObject;
            try {
                urlObject = new URL(textureUrl);
            } catch (Exception exception) {
                return new ProfileComponent(playerProfile);
            }
            PlayerTextures textures = playerProfile.getTextures();
            textures.setSkin(urlObject, skinModel);
            playerProfile.setTextures(textures);
        }
        String capeUrl = componentSection.getString("cape");
        if (capeUrl != null){
            URL urlObject;
            try {
                urlObject = new URL(capeUrl);
            } catch (Exception exception) {
                return new ProfileComponent(playerProfile);
            }
            PlayerTextures textures = playerProfile.getTextures();
            textures.setCape(urlObject);
            playerProfile.setTextures(textures);
        }
        return new ProfileComponent(playerProfile);
    }
}
