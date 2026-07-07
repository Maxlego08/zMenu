package fr.maxlego08.menu.api.utils.version;

import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public final class MinecraftVersion implements Comparable<MinecraftVersion> {
    private static final Map<String, MinecraftVersion> PARSE_CACHE = new ConcurrentHashMap<>();
    private static final Pattern NUMERIC = Pattern.compile("\\d+");

    private static MinecraftVersion currentVersion;

    private final int major;
    private final int minor;
    private final int patch;

    private MinecraftVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public static MinecraftVersion getCurrentVersion() {
        if (currentVersion == null) {
            currentVersion = parse(Bukkit.getBukkitVersion());
        }
        return currentVersion;
    }

    @NotNull
    public static MinecraftVersion parse(@Nullable String rawVersion) {
        if (rawVersion == null || rawVersion.isBlank()) {
            return new MinecraftVersion(0, 0, 0);
        }
        return PARSE_CACHE.computeIfAbsent(rawVersion, raw -> {
            String clean = raw.contains("-") ? raw.substring(0, raw.indexOf('-')) : raw;
            String[] parts = clean.split("\\.");

            int[] numbers = new int[3];
            int index = 0;

            for (String part : parts) {
                if (index >= 3) break;
                if (!NUMERIC.matcher(part).matches()) {
                    continue;
                }
                try {
                    numbers[index++] = Integer.parseInt(part);
                } catch (NumberFormatException e) {
                    Logger.info("Could not parse numeric segment '" + part + "' in Minecraft value '" + raw + "'. (" + e.getMessage() + ")", Logger.LogType.WARNING);
                }
            }

            if (index == 0) {
                Logger.info("Could not parse Minecraft value '" + raw + "'. Version-gated classes will be skipped.", Logger.LogType.WARNING);
            }

            return new MinecraftVersion(numbers[0], numbers[1], numbers[2]);
        });
    }

    /**
     * Returns {@code true} if this value is greater than or equal to {@code other}.
     */
    public boolean isAtLeast(MinecraftVersion other) {
        return this.compareTo(other) >= 0;
    }

    /**
     * Returns {@code true} if this value is less than or equal to {@code other}.
     */
    public boolean isAtMost(MinecraftVersion other) {
        return this.compareTo(other) <= 0;
    }

    public boolean isBefore(MinecraftVersion other) {
        return this.compareTo(other) < 0;
    }

    public boolean isAfter(MinecraftVersion other) {
        return this.compareTo(other) > 0;
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public int getPatch() {
        return this.patch;
    }

    @Override
    public int compareTo(MinecraftVersion other) {
        if (this.major != other.major) {
            return Integer.compare(this.major, other.major);
        }
        if (this.minor != other.minor) {
            return Integer.compare(this.minor, other.minor);
        }
        return Integer.compare(this.patch, other.patch);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MinecraftVersion other)) return false;
        return this.major == other.major && this.minor == other.minor && this.patch == other.patch;
    }

    @Override
    public int hashCode() {
        return 31 * (31 * this.major + this.minor) + this.patch;
    }

    @Override
    public String toString() {
        return this.major + "." + this.minor + (this.patch != 0 ? "." + this.patch : "");
    }
}