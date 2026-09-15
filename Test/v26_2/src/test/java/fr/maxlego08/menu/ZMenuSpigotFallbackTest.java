package fr.maxlego08.menu;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the descriptor Spigot falls back to. The plugin must enable there instead of being
 * rejected, and must tell whoever administrates the server to install Paper.
 */
class ZMenuSpigotFallbackTest {

    private static final String WARNING_PERMISSION = "zmenu.paper.warning";

    private ServerMock server;
    private ZMenuSpigotFallback plugin;

    @BeforeEach
    void setUp() {
        this.server = MockBukkit.mock();
        this.plugin = MockBukkit.load(ZMenuSpigotFallback.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void administratorsAreWarnedOnJoin() {
        PlayerMock player = new PlayerMock(this.server, "Admin");
        player.addAttachment(this.plugin, WARNING_PERMISSION, true);
        this.server.addPlayer(player);

        this.server.getScheduler().performTicks(60L);

        String message = this.readMessages(player);
        assertNotNull(message, "a player holding " + WARNING_PERMISSION + " must be warned on join");
        assertTrue(message.contains("zMenu"), "the warning must name the plugin: " + message);
        assertTrue(message.contains("papermc.io"), "the warning must link Paper: " + message);
    }

    @Test
    void regularPlayersAreNotWarnedOnJoin() {
        PlayerMock player = new PlayerMock(this.server, "Player");
        this.server.addPlayer(player);

        this.server.getScheduler().performTicks(60L);

        assertNull(this.readMessages(player), "a player without the permission must not be warned");
    }

    private String readMessages(PlayerMock player) {
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = player.nextMessage()) != null) {
            builder.append(line).append('\n');
        }
        return builder.isEmpty() ? null : builder.toString();
    }
}
