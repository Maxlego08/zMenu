package fr.maxlego08.menu.zcore.utils.toast;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.toast.ToastType;
import fr.maxlego08.menu.zcore.utils.toast.utils.ColorParser;
import fr.maxlego08.menu.zcore.utils.toast.versions.ServerVersion;
import fr.maxlego08.menu.zcore.utils.toast.versions.VersionType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

class AdvancementHandler {
    private final MenuPlugin plugin;

    AdvancementHandler(@NotNull MenuPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Shows Toast notification to specified players
     */
    void showToast(Collection<? extends Player> players, String icon, String message, ToastType style, Object modelData, String modelDataType, boolean glowing) {

        if (players.isEmpty()) return;

        NamespacedKey advancementKey = createAdvancement(icon, message, style, modelData, modelDataType, glowing);

        var scheduler = this.plugin.getScheduler();
        for (Player p : players) {
            scheduler.runLater(() -> {
                grantAdvancement(p, advancementKey);
                scheduler.runLater(() -> revokeAdvancement(p, advancementKey), 10);
            }, 1);
        }

        scheduler.runLater(() -> Bukkit.getUnsafe().removeAdvancement(advancementKey), 40);
    }

    /**
     * Shows the Toast notification to all players in a more optimal way
     * Creates a single advancement and shows it to all players
     */
    void showToastToAll(String icon, String message, ToastType style, Object modelData, String modelDataType, boolean glowing) {
       showToast(Bukkit.getOnlinePlayers(), icon, message, style, modelData, modelDataType, glowing);
    }

    /**
     * Creates an advancement for displaying a toast notification for Minecraft versions before 1.20.5
     * Uses NBT with integer CustomModelData (1.16-1.20.4)
     *
     * @param icon      The Minecraft item ID to use as the toast icon
     * @param message   The message to display in the toast
     * @param style     The advancement frame style (toast type)
     * @param modelData The custom model data as integer
     * @return The NamespacedKey of the created advancement
     */
    @NotNull
    private NamespacedKey legacyType(String icon, String message, ToastType style, Object modelData, boolean glowing, NamespacedKey advancementKey) {

        int modelDataInt = 0;
        if (modelData instanceof Integer) {
            modelDataInt = (Integer) modelData;
        } else if (modelData instanceof Float) {
            modelDataInt = ((Float) modelData).intValue();
        } else if (modelData instanceof String) {
            try {
                modelDataInt = Integer.parseInt(modelData.toString());
            } catch (NumberFormatException ignored) {
                // TODO: If parsing fails, modelDataInt remains 0
            }
        }

        String json = "{\n" + //
                " \"criteria\": {\n" + //
                " \"trigger\": {\n" + //
                " \"trigger\": \"minecraft:impossible\"\n" + //
                " }\n" + //
                " },\n" + //
                " \"display\": {\n" + //
                " \"icon\": {\n" + //
                " \"item\": \"minecraft:" + icon + "\",\n" + //
                " \"nbt\": \"{CustomModelData:" + modelDataInt + //
                (glowing ? ",Enchantments:[{lvl:1,id:\\\"minecraft:protection\\\"}]" : "") + "}\"\n" + //
                " },\n" + //
                " \"title\": " + message + ",\n" + //
                " \"description\": {\n" + //
                " \"text\": \"\"\n" + //
                " },\n" + //
                " \"background\": \"minecraft:textures/gui/advancements/backgrounds/adventure.png\",\n" + //
                " \"frame\": \"" + style.toString().toLowerCase() + "\",\n" + //
                " \"announce_to_chat\": false,\n" + //
                " \"show_toast\": true,\n" + //
                " \"hidden\": true\n" + //
                " }\n" + //
                "}";

        Bukkit.getUnsafe().loadAdvancement(advancementKey, json);
        return advancementKey;
    }

    /**
     * Creates an advancement for displaying a toast notification for Minecraft versions 1.20.5-1.21.3
     * Uses components system with integer CustomModelData
     *
     * @param icon      The Minecraft item ID to use as the toast icon
     * @param message   The message to display in the toast
     * @param style     The advancement frame style (toast type)
     * @param modelData The custom model data as integer
     * @return The NamespacedKey of the created advancement
     */
    @NotNull
    private NamespacedKey middleType(String icon, String message, ToastType style, Object modelData, boolean glowing, NamespacedKey advancementKey) {
        int modelDataInt = 0;
        if (modelData instanceof Integer) {
            modelDataInt = (Integer) modelData;
        } else if (modelData instanceof Float) {
            modelDataInt = ((Float) modelData).intValue();
        } else if (modelData instanceof String) {
            try {
                modelDataInt = Integer.parseInt(modelData.toString());
            } catch (NumberFormatException ignored) {
                // TODO: If parsing fails, modelDataInt remains 0
            }
        }

        String json = "{\n" + //
                " \"criteria\": {\n" + //
                "   \"trigger\": {\n" + //
                "     \"trigger\": \"minecraft:impossible\"\n" + //
                "   }\n" + //
                " },\n" + //
                " \"display\": {\n" + //
                "   \"icon\": {\n" + //
                "     \"id\": \"minecraft:" + icon + "\",\n" + //
                "     \"components\": {\n" + //
                "       \"minecraft:custom_model_data\": " + modelDataInt + //
                (glowing ? ",\n       \"minecraft:enchantments\": {\n" + //
                        "         \"levels\": {\n" + //
                        "           \"minecraft:protection\": 1\n" + //
                        "         }\n" + //
                        "       }" : "") + //
                "\n     },\n" + //
                "     \"count\": 1\n" + //
                "   },\n" + //
                "   \"title\": " + message + ",\n" + //
                "   \"description\": {\n" + //
                "     \"text\": \"\"\n" + //
                "   },\n" + //
                "   \"background\": \"minecraft:textures/gui/advancements/backgrounds/adventure.png\",\n" + //
                "   \"frame\": \"" + style.toString().toLowerCase() + "\",\n" + //
                "   \"announce_to_chat\": false,\n" + //
                "   \"show_toast\": true,\n" + //
                "   \"hidden\": true\n" + //
                " }\n" + //
                "}";

        Bukkit.getUnsafe().loadAdvancement(advancementKey, json);
        return advancementKey;
    }

    /**
     * Creates an advancement for displaying a toast notification for Minecraft versions 1.21.4+
     * Uses component system with string or float CustomModelData arrays
     *
     * @param icon          The Minecraft item ID to use as the toast icon
     * @param message       The message to display in the toast
     * @param style         The advancement frame style (toast type)
     * @param modelData     The custom model data (string or float)
     * @param modelDataType The type of model data ("string", "float", or "integer")
     * @return The NamespacedKey of the created advancement
     */
    @NotNull
    private NamespacedKey modernType(String icon, String message, ToastType style, Object modelData, String modelDataType, boolean glowing, NamespacedKey advancementKey) {
        String customModelData;
        if (modelDataType == null) {
            modelDataType = modelData instanceof String ? "string" : (modelData instanceof Float || modelData instanceof Double) ? "float" : "integer";
        }

        if ("float".equals(modelDataType) || "integer".equals(modelDataType)) {
            customModelData = "\"minecraft:custom_model_data\": {\n" + " \"floats\": [" + modelData + "]\n" + " }";
        } else {
            customModelData = "\"minecraft:custom_model_data\": {\n" + " \"strings\": [\n" + " \"" + modelData + "\"\n" + " ]\n" + " }";
        }

        String json = "{\n" + //
                " \"criteria\": {\n" + //
                " \"trigger\": {\n" + //
                " \"trigger\": \"minecraft:impossible\"\n" + //
                " }\n" + //
                " },\n" + //
                " \"display\": {\n" + //
                " \"icon\": {\n" + //
                " \"id\": \"minecraft:" + icon + "\",\n" + //
                " \"components\": {\n" + //
                " " + customModelData + //
                (glowing ? ",\n \"minecraft:enchantments\": {\n" + //
                        " \"minecraft:protection\": 1\n" + //
                        " }" : "") + "\n" + //
                " },\n" + //
                " \"count\": 1\n" + //
                " },\n" + //
                " \"title\": " + message + ",\n" + //
                " \"description\": {\n" + //
                " \"text\": \"\"\n" + //
                " },\n" + //
                " \"background\": \"minecraft:textures/gui/advancements/backgrounds/adventure.png\",\n" + //
                " \"frame\": \"" + style.toString().toLowerCase() + "\",\n" + //
                " \"announce_to_chat\": false,\n" + //
                " \"show_toast\": true,\n" + //
                " \"hidden\": true\n" + //
                " }\n" + //
                "}";

        Bukkit.getUnsafe().loadAdvancement(advancementKey, json);
        return advancementKey;
    }

    /**
     * Helper method to determine which advancement creation method to use based on server version
     *
     * @param icon          The Minecraft item ID to use as the toast icon
     * @param message       The message to display in the toast
     * @param style         The advancement frame style (toast type)
     * @param modelData     The custom model data (can be int, float, or String)
     * @param modelDataType The type of model data ("string", "float", or "integer")
     * @return The NamespacedKey of the created advancement
     */
    @NotNull
    private NamespacedKey createAdvancement(String icon, String message, ToastType style, Object modelData, String modelDataType, boolean glowing) {
        String serverVersion = plugin.getServer().getVersion();

        List<Map<String, Object>> msgList = ColorParser.process(message);
        String json = ColorParser.formatToJsonString(msgList);
        json = json.replace("|", "\n");
        icon = icon.toLowerCase().replace("İ", "I").replace("ı", "i");

        UUID randomUUID = UUID.randomUUID();
        NamespacedKey advancementKey = new NamespacedKey(plugin, "anelib_" + randomUUID);

        VersionType versionType = ServerVersion.getVersionType(serverVersion);

        return switch (versionType) {
            case LEGACY -> {
                // 1.16 - 1.20.4: NBT format with integer CustomModelData
                if (modelData == null) {
                    modelData = 0;
                }
                yield legacyType(icon, json, style, modelData, glowing, advancementKey);
            }
            case MIDDLE -> {
                // 1.20.5 - 1.21.3: Components format with integer CustomModelData
                if (modelData == null) {
                    modelData = 0;
                }
                yield middleType(icon, json, style, modelData, glowing, advancementKey);
            }
            default -> {
                // 1.21.4+: Components format with floats/strings arrays
                if (modelData == null) {
                    modelData = "anemys";
                    modelDataType = "string";
                }
                yield modernType(icon, json, style, modelData, modelDataType, glowing, advancementKey);
            }
        };
    }

    /**
     * Add advancement to the player
     */
    private void grantAdvancement(Player p, NamespacedKey advancementKey) {
        p.getAdvancementProgress(Objects.requireNonNull(Bukkit.getAdvancement(advancementKey))).awardCriteria("trigger");
    }

    /**
     * Revoke advancement from the player
     */
    private void revokeAdvancement(Player p, NamespacedKey advancementKey) {
        p.getAdvancementProgress(Objects.requireNonNull(Bukkit.getAdvancement(advancementKey))).revokeCriteria("trigger");
    }
}