package fr.maxlego08.menu;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.configuration.Feature;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.listener.ItemUpdaterListener;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class FeatureTest {

    private static ZMenuPlugin plugin;

    @BeforeAll
    public static void init() {
        MockBukkit.mock();
        plugin = MockBukkit.load(ZMenuPlugin.class, true);
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    static boolean hasItemUpdaterListener(ZMenuPlugin plugin) {
        return plugin.getListenerAdapters().stream().anyMatch(ItemUpdaterListener.class::isInstance);
    }

    @Test
    public void customItemsAreEnabledByDefault() {
        assertTrue(plugin.isEnabled());
        assertTrue(plugin.isFeatureEnabled(Feature.CUSTOM_ITEMS));
        assertInstanceOf(ZItemManager.class, plugin.getItemManager());
        assertTrue(plugin.getInventoryManager().getRegistries().registry(Registries.MECHANIC).contains(ZMenuKeys.parse("itemjoin")));
        assertTrue(hasItemUpdaterListener(plugin));
        assertTrue(new File(plugin.getDataFolder(), "items/default-items.yml").exists());
    }
}
