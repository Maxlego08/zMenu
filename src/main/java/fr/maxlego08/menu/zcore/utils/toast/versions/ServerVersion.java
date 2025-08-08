package fr.maxlego08.menu.zcore.utils.toast.versions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerVersion {

    public static VersionType getVersionType(String serverVersion) {
        String version = extractVersionNumber(serverVersion);

        if (isBetween(version, "1.16", "1.20.4")) {
            return VersionType.LEGACY;
        }

        if (isBetween(version, "1.20.5", "1.21.3")) {
            return VersionType.MIDDLE;
        }

        return VersionType.MODERN;
    }

    private static String extractVersionNumber(String fullVersion) {
        Pattern pattern = Pattern.compile("(\\d+\\.\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(fullVersion);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "0.0.0";
    }

    private static boolean isBetween(String version, String min, String max) {
        return compareVersion(version, min) >= 0 && compareVersion(version, max) <= 0;
    }

    private static int compareVersion(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        int length = Math.max(parts1.length, parts2.length);

        for (int i = 0; i < length; i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            if (num1 != num2) {
                return Integer.compare(num1, num2);
            }
        }
        return 0;
    }
}