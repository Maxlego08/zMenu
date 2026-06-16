package fr.maxlego08.menu.api.utils.version;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PluginVersion implements Comparable<PluginVersion> {
    private static final Map<String, PluginVersion> PARSE_CACHE = new ConcurrentHashMap<>();

    private final int[] segments;

    private PluginVersion(int[] segments) {
        this.segments = stripLeadingZeros(segments);
    }

    /**
     * Strips leading zero segments: [0, 61, 2] → [61, 2].
     * Keeps at least one segment to represent version "0".
     */
    private static int[] stripLeadingZeros(int[] segments) {
        int start = 0;
        while (start < segments.length - 1 && segments[start] == 0) {
            start++;
        }
        return start == 0 ? segments : Arrays.copyOfRange(segments, start, segments.length);
    }

    @NotNull
    public static PluginVersion parse(@Nullable String rawVersion) {
        if (rawVersion == null || rawVersion.isBlank()) {
            return new PluginVersion(new int[]{0});
        }
        return PARSE_CACHE.computeIfAbsent(rawVersion, raw -> {
            String clean = raw.contains("-") ? raw.substring(0, raw.indexOf('-')) : raw;
            String[] parts = clean.split("\\.");
            int[] segments = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i].replaceAll("[^0-9]", "");
                try {
                    segments[i] = part.isEmpty() ? 0 : Integer.parseInt(part);
                } catch (NumberFormatException e) {
                    segments[i] = 0;
                }
            }
            return new PluginVersion(segments);
        });
    }

    @Override
    public int compareTo(@NotNull PluginVersion other) {
        int length = Math.max(this.segments.length, other.segments.length);
        for (int i = 0; i < length; i++) {
            if (i >= this.segments.length) return -1;
            if (i >= other.segments.length) return 1;
            if (this.segments[i] != other.segments[i]) {
                return Integer.compare(this.segments[i], other.segments[i]);
            }
        }
        return 0;
    }

    public boolean isNewerThan(@NotNull PluginVersion other) { return compareTo(other) > 0; }
    public boolean isOlderThan(@NotNull PluginVersion other) { return compareTo(other) < 0; }
    public boolean isSameAs(@NotNull PluginVersion other) { return compareTo(other) == 0; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PluginVersion other)) return false;
        return Arrays.equals(this.segments, other.segments);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(segments);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < segments.length; i++) {
            if (i > 0) builder.append('.');
            builder.append(segments[i]);
        }
        return builder.toString();
    }
}