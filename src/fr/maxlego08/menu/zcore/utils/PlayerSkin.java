package fr.maxlego08.menu.zcore.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

/**
 * Based on
 * https://www.spigotmc.org/threads/how-to-get-a-players-texture.244966/
 */
public class PlayerSkin {

    private static final Map<String, String> textures = new HashMap<String, String>();
    private static ExecutorService pool = Executors.newCachedThreadPool();
    private static String gameProfileMethodName;

    public static String getTexture(Player player) {
        if (textures.containsKey(player.getName())) {
            return textures.get(player.getName());
        }
        String[] textures = getFromPlayer(player);
        try {
            String texture = textures[0];
            PlayerSkin.textures.put(player.getName(), texture);
            return texture;
        } catch (Exception ignored) {
        }
        return null;
    }

    public static String getTexture(String name) {

        if (textures.containsKey(name)) {
            return textures.get(name);
        }

        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            return getTexture(player);
        }

        pool.execute(() -> {
            String[] textures = getFromName(name);
            try {
                String texture = textures[0];
                PlayerSkin.textures.put(name, texture);
            } catch (Exception ignored) {
            }
        });
        return null;
    }

    public static String[] getFromPlayer(Player playerBukkit) {
        GameProfile profile = getProfile(playerBukkit);
            if (profile.getProperties().get("textures").iterator().hasNext()) {
                Property property = profile.getProperties().get("textures").iterator().next();
                String texture = property.getValue();
                String signature = property.getSignature();
                return new String[]{texture, signature};
            } else {
                return getFromName(playerBukkit.getName());
            }
    }

    @SuppressWarnings("deprecation")
    public static String[] getFromName(String name) {
        try {
            URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
            String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();

            URL url_1 = new URL(
                    "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
            JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties")
                    .getAsJsonArray().get(0).getAsJsonObject();
            String texture = textureProperty.get("value").getAsString();
            String signature = textureProperty.get("signature").getAsString();

            return new String[]{texture, signature};
        } catch (IOException e) {
            /*
             * System.err.
             * println("Could not get skin data from session servers!");
             * e.printStackTrace();
             */
            return null;
        }
    }

    public static GameProfile getProfile(Player player) {

        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            return (GameProfile) entityPlayer.getClass().getMethod(getMethodName(entityPlayer)).invoke(entityPlayer);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                 | SecurityException ignored) {
        }

        return null;
    }

    public static String getMethodName(Object entityPlayer) {
		/*double version = NMSUtils.getNMSVersion();
		if (version == 1.18) {
			return "fq";
		} else if (version == 1.19) {
			return "fy";
		}*/
        if (gameProfileMethodName == null) {
            for (Method method : entityPlayer.getClass().getMethods()) {
                if (method.getParameterCount() == 0 && method.getReturnType().toString().contains("com.mojang.authlib.GameProfile")) {
                    gameProfileMethodName = method.getName();
                }
            }
        }
        return gameProfileMethodName != null ? gameProfileMethodName : "getProfile";
    }

}