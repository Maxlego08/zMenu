package fr.maxlego08.menu;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.test.DataComponentTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.HashSet;
import java.util.Set;

public class DataComponentTest extends DataComponentTestBase {

    private static MenuPlugin menuPlugin;
    private ZComponentsManager componentsManager;

    @BeforeAll
    public static void init() {
        MockBukkit.mock();
        menuPlugin = MockBukkit.load(ZMenuPlugin.class, true); // MockBukkit server
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }


    @BeforeEach
    public void setup() {
        this.componentsManager = new ZComponentsManager();
    }

    @Test
    public void testAllKnownComponents() {
        componentsManager.initializeDefaultComponents(menuPlugin);

        Set<String> registered = new HashSet<>(componentsManager.getRegisteredComponentNames());

//         checkComponents(registered);
    }
}
