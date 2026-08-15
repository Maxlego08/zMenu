package fr.maxlego08.menu.api.utils.version;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Maps a Minecraft protocol number to the Minecraft version that introduced it.
 * <p>
 * Protocol numbers are the values sent by the client during the handshake, as exposed by
 * {@code Player#getProtocolVersion()} on Paper. Versions sharing a protocol are collapsed
 * onto the lowest one of the range ({@code 772} is {@code 1.21.7-1.21.8}, so it maps to
 * {@code 1.21.7}), which is the conservative choice for a minimum-version check.
 */
public final class ProtocolVersions {
    private static final NavigableMap<Integer, MinecraftVersion> VERSIONS = new TreeMap<>();

    static {
        register(4, "1.7.2");
        register(5, "1.7.6");
        register(47, "1.8");
        register(107, "1.9");
        register(108, "1.9.1");
        register(109, "1.9.2");
        register(110, "1.9.3");
        register(210, "1.10");
        register(315, "1.11");
        register(316, "1.11.1");
        register(335, "1.12");
        register(338, "1.12.1");
        register(340, "1.12.2");
        register(393, "1.13");
        register(401, "1.13.1");
        register(404, "1.13.2");
        register(477, "1.14");
        register(480, "1.14.1");
        register(485, "1.14.2");
        register(490, "1.14.3");
        register(498, "1.14.4");
        register(573, "1.15");
        register(575, "1.15.1");
        register(578, "1.15.2");
        register(735, "1.16");
        register(736, "1.16.1");
        register(751, "1.16.2");
        register(753, "1.16.3");
        register(754, "1.16.4");
        register(755, "1.17");
        register(756, "1.17.1");
        register(757, "1.18");
        register(758, "1.18.2");
        register(759, "1.19");
        register(760, "1.19.1");
        register(761, "1.19.3");
        register(762, "1.19.4");
        register(763, "1.20");
        register(764, "1.20.2");
        register(765, "1.20.3");
        register(766, "1.20.5");
        register(767, "1.21");
        register(768, "1.21.2");
        register(769, "1.21.4");
        register(770, "1.21.5");
        register(771, "1.21.6");
        register(772, "1.21.7");
        register(773, "1.21.9");
        register(774, "1.21.11");
        register(775, "26.1");
        register(776, "26.2");
    }

    private ProtocolVersions() {
    }

    private static void register(int protocol, String version) {
        VERSIONS.put(protocol, MinecraftVersion.parse(version));
    }

    public static Optional<MinecraftVersion> fromProtocol(int protocol) {
        if (protocol < 0) return Optional.empty();

        Map.Entry<Integer, MinecraftVersion> entry = VERSIONS.floorEntry(protocol);
        if (entry == null) {
            return Optional.of(VERSIONS.firstEntry().getValue());
        }
        return Optional.of(entry.getValue());
    }
}
