package fr.maxlego08.menu;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.configuration.Feature;
import fr.maxlego08.menu.api.mechanic.MechanicFactory;
import fr.maxlego08.menu.api.registry.KeyedRegistry;
import fr.maxlego08.menu.api.registry.Registries;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class DisabledFeatureTest {

    private static ZMenuPlugin plugin;

    @BeforeAll
    public static void init() throws IOException {
        ServerMock server = MockBukkit.mock();
        File dataFolder = server.getPluginManager().createTemporaryDirectory("zMenu-" + pluginVersion());
        Files.writeString(new File(dataFolder, "config.yml").toPath(), "features:\n  custom-items: false\n");
        plugin = MockBukkit.load(ZMenuPlugin.class, true);
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    private static String pluginVersion() throws IOException {
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(ZMenuPlugin.class.getClassLoader().getResourceAsStream("paper-plugin.yml")), StandardCharsets.UTF_8)) {
            return YamlConfiguration.loadConfiguration(reader).getString("version");
        }
    }

    @Test
    public void zMenuStillEnables() {
        assertTrue(plugin.isEnabled());
        assertNotNull(plugin.getCommandMenu());
        assertTrue(new File(plugin.getDataFolder(), "inventories/basic_inventory.yml").exists());
    }

    @Test
    public void disabledCustomItemsRegisterNothing() {
        assertFalse(plugin.isFeatureEnabled(Feature.CUSTOM_ITEMS));
        assertInstanceOf(DisabledItemManager.class, plugin.getItemManager());
        KeyedRegistry<MechanicFactory<?>> mechanics = plugin.getInventoryManager().getRegistries().registry(Registries.MECHANIC);
        assertFalse(mechanics.contains(ZMenuKeys.parse("itemjoin")));
        assertFalse(mechanics.contains(ZMenuKeys.parse("on-click")));
        assertFalse(FeatureTest.hasItemUpdaterListener(plugin));
        assertFalse(new File(plugin.getDataFolder(), "items").exists());
    }

    @Test
    public void disabledItemManagerIgnoresAddons() {
        assertTrue(plugin.getItemManager().getItemIds().isEmpty());
        assertFalse(plugin.getItemManager().isCustomItem("example"));
        assertTrue(plugin.getItemManager().getMechanicFactory("itemjoin").isEmpty());
    }
}
