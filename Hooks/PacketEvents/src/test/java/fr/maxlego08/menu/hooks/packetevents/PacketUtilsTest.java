package fr.maxlego08.menu.hooks.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PacketUtilsTest {

    private static final class CapturingLogger extends Logger {
        private final List<String> messages = new ArrayList<>();

        private CapturingLogger() {
            super("zMenu-Test");
        }

        @Override
        public void log(@NotNull String message, @NotNull LogType type, @NotNull Object... args) {
            this.messages.add(message);
        }
    }

    private CapturingLogger logger;

    @BeforeEach
    void setUp() {
        PacketEvents.setAPI(null);
        this.logger = new CapturingLogger();
    }

    @AfterEach
    void tearDown() {
        PacketEvents.setAPI(null);
    }

    @Test
    void theHookCanBeBuiltWithoutTheApi() {
        assertDoesNotThrow(() -> new PacketUtils(null), "constructing the hook must never touch the packetevents API");
    }

    @Test
    void theHookIsNotReadyBeforeItIsEnabled() {
        assertFalse(new PacketUtils(null).isReady());
    }

    @Test
    void enablingWithoutTheApiIsReportedAndLeavesTheHookUnused() {
        PacketUtils packetUtils = new PacketUtils(null);

        assertDoesNotThrow(packetUtils::onEnable, "a missing packetevents API must not fail zMenu's enable");
        assertFalse(packetUtils.isReady(), "the hook must not claim to be usable without the API");
        assertTrue(this.logger.messages.stream().anyMatch(message -> message.contains("packetevents API is not available")), "the administrator must be told why packet features are off: " + this.logger.messages);
    }

    @Test
    void disablingWithoutTheApiDoesNothing() {
        PacketUtils packetUtils = new PacketUtils(null);
        packetUtils.onEnable();

        assertDoesNotThrow(packetUtils::onDisable);
    }

    @Test
    void editingATitleWithoutTheApiIsIgnored() {
        PacketUtils packetUtils = new PacketUtils(null);

        assertDoesNotThrow(() -> packetUtils.editInventoryTitleName(null, net.kyori.adventure.text.Component.text("title")));
    }
}
