package fr.maxlego08.menu.api.utils.version;

import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MinecraftVersion implements Comparable<MinecraftVersion> {
    private static final Map<String, MinecraftVersion> PARSE_CACHE = new ConcurrentHashMap<>();

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
            try {
                int major = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
                int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
                return new MinecraftVersion(major, minor, patch);
            } catch (NumberFormatException e) {
                Logger.info("Could not parse Minecraft value '" + raw + "'. Version-gated classes will be skipped. (" + e.getMessage() + ")", Logger.LogType.WARNING);
                return new MinecraftVersion(0, 0, 0);
            }
        });
    }

    /**
     * Returns {@code true} if this value is greater than or equal to {@code other}.
     */
    public boolean isAtLeast(MinecraftVersion other) {
        return compareTo(other) >= 0;
    }

    /**
     * Returns {@code true} if this value is less than or equal to {@code other}.
     */
    public boolean isAtMost(MinecraftVersion other) {
        return compareTo(other) <= 0;
    }

    public boolean isBefore(MinecraftVersion other) {
        return compareTo(other) < 0;
    }

    public boolean isAfter(MinecraftVersion other) {
        return compareTo(other) > 0;
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
        return 31 * (31 * major + minor) + patch;
    }

    @Override
    public String toString() {
        return major + "." + minor + (patch != 0 ? "." + patch : "");
    }
}
