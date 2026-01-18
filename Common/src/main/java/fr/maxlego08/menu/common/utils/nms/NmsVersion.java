package fr.maxlego08.menu.common.utils.nms;

import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum NmsVersion {

    V_1_8_8(188),
    V_1_9(190),
    V_1_10(1100),
    V_1_11(1110),
    V_1_12(1120),
    V_1_12_2(1122),
    V_1_13(1130),
    V_1_13_1(1131),
    V_1_13_2(1132),
    V_1_14(1140),
    V_1_14_1(1141),
    V_1_14_2(1142),
    V_1_14_3(1143),
    V_1_14_4(1144),
    V_1_15(1150),
    V_1_15_1(1151),
    V_1_15_2(1152),
    V_1_16(1160),
    V_1_16_1(1161),
    V_1_16_2(1162),
    V_1_16_3(1163),
    V_1_16_4(1164),
    V_1_16_5(1165),
    V_1_17(1170),
    V_1_17_1(1171),
    V_1_17_2(1172),
    V_1_18(1180),
    V_1_18_1(1181),
    V_1_18_2(1182),
    V_1_19(1190),
    V_1_19_1(1191),
    V_1_19_2(1192),
    V_1_20(1200),
    V_1_20_1(1201),
    V_1_20_2(1202),
    V_1_20_3(1203),
    V_1_20_4(1204),
    V_1_20_5(1205),
    V_1_20_6(1206),
    V_1_21(1210),
    V_1_21_1(1211),
    V_1_21_2(1212),
    V_1_21_3(1213),
    V_1_21_4(1214),
    V_1_21_5(1215),
    V_1_21_6(1216),
    V_1_21_7(1217),
    V_1_21_8(1218),
    V_1_21_9(1219),
    V_1_21_10(12110),
    V_1_21_11(12111),

    UNKNOWN(Integer.MAX_VALUE)

    ;

    public static final NmsVersion nmsVersion = getNmsVersion();
    private final int version;

    NmsVersion(int version) {
        this.version = version;
    }

    /**
     * Gets the current version of the Bukkit server.
     *
     * @return The NmsVersion instance corresponding to the current version.
     */
    public static NmsVersion getCurrentVersion() {
        return nmsVersion;
    }

    private static NmsVersion getNmsVersion(){
        Matcher matcher = Pattern.compile("(?<version>\\d+\\.\\d+)(?<patch>\\.\\d+)?").matcher(Bukkit.getBukkitVersion());
        int currentVersion = matcher.find() ? Integer.parseInt(matcher.group("version").replace(".", "") + (matcher.group("patch") != null ? matcher.group("patch").replace(".", "") : "0")) : 0;

        NmsVersion highestSupportedVersionEnum = V_1_8_8;
        for (NmsVersion value : values()) {
            if (value != UNKNOWN && value.version > highestSupportedVersionEnum.version) {
                highestSupportedVersionEnum = value;
            }
        }

        if (currentVersion > highestSupportedVersionEnum.version) {
            Logger.info(String.format(
                "Running Minecraft %s (newer than highest supported version %s). " +
                "Please report this version to help us add support. " +
                "Check for plugin updates if you experience issues.",
                currentVersion,
                highestSupportedVersionEnum.name()
            ), Logger.LogType.WARNING);
            return UNKNOWN;
        }

        NmsVersion closest = V_1_12_2;
        int smallestDifference = Integer.MAX_VALUE;
        for (NmsVersion value : values()) {
            if (value == UNKNOWN) continue;
            int difference = Math.abs(value.version - currentVersion);
            if (difference < smallestDifference) {
                smallestDifference = difference;
                closest = value;
            }
        }
        return closest;
    }

    /**
     * Checks if the current version supports PlayerProfiles.
     *
     * @return True if PlayerProfiles are supported, else False.
     */
    public boolean hasPlayerProfiles() {
        return version >= NmsVersion.V_1_18_1.version;
    }

    /**
     * Checks if the current version uses obfuscated names.
     *
     * @return True if names are obfuscated, else False.
     */
    public boolean hasObfuscatedNames() {
        return version >= NmsVersion.V_1_17.version;
    }

    /**
     * Checks if the current version supports components.
     *
     * @param isPaper True if the server uses Paper, else False.
     * @return True if components are supported, else False.
     */
    public boolean isComponent(boolean isPaper) {
        return isPaper && version >= NmsVersion.V_1_16_5.version;
    }

    /**
     * Checks if the current version is a legacy item version.
     *
     * @return True if the version is legacy, else False.
     */
    public boolean isItemLegacy() {
        return version < NmsVersion.V_1_13.version;
    }

    /**
     * Checks if the current version supports PersistentDataContainer.
     *
     * @return True if PersistentDataContainer is supported, else False.
     */
    public boolean isPdcVersion() {
        return version >= NmsVersion.V_1_14.version;
    }

    /**
     * Checks if the current version is a legacy version for Skull owners.
     *
     * @return True if the version is legacy, else False.
     */
    public boolean isSkullOwnerLegacy() {
        return version <= NmsVersion.V_1_12.version;
    }

    /**
     * Checks if the current version supports CustomModelData.
     *
     * @return True if CustomModelData is supported, else False.
     */
    public boolean isCustomModelData() {
        return version >= NmsVersion.V_1_14.version;
    }

    /**
     * Checks if the current version is a hexadecimal version.
     *
     * @return True if the version is hexadecimal, else False.
     */
    public boolean isHexVersion() {
        return version >= NmsVersion.V_1_16.version;
    }

    /**
     * Checks if the current version is an Attribute version.
     *
     * @return True if the version is Attribute, else False.
     */
    public boolean isAttributeVersion() {
        return version != NmsVersion.V_1_8_8.version;
    }

    /**
     * Gets the version number associated with the enumeration.
     *
     * @return The version number.
     */
    public int getVersion() {
        return version;
    }

    public boolean isAttributItemStack() {
        return version >= NmsVersion.V_1_20_5.version;
    }

    public boolean isOneHand() {
        return version == NmsVersion.V_1_8_8.version;
    }

    public boolean isBarrel() {
        return version >= V_1_14.version;
    }

    public boolean isShulker() {
        return version >= V_1_9.version;
    }

    public boolean isNewMaterial() {
        return version >= V_1_13.version;
    }

    public boolean isNewNBTVersion() {
        return version >= V_1_18.version;
    }

    public boolean isNewHeadApi() {
        return version >= V_1_20.version;
    }

    public boolean isNewNMSVersion() {
        return version >= V_1_17.version;
    }

    public boolean isNewItemStackAPI() {
        return version >= V_1_21.version;
    }

    public boolean isNewItemModelAPI() {
        return version >= V_1_21_4.version;
    }

    public boolean isDialogsVersion() {
        return version >= V_1_21_7.version;
    }

    public boolean is1_21_2OrNewer() {
        return version >= V_1_21_2.version;
    }

    public boolean is1_21_5OrNewer() {
        return version >= V_1_21_5.version;
    }

    public boolean is1_21_9OrNewer() {
        return version >= V_1_21_9.version;
    }

    public boolean is1_21_11OrNewer() {
        return version >= V_1_21_11.version;
    }
}
