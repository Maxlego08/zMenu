package fr.maxlego08.menu;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.exceptions.ItemComponentAlreadyRegisterException;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.test.DataComponentTestBase;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataComponentTest extends DataComponentTestBase {

    private static MenuPlugin menuPlugin;
    private ZComponentsManager componentsManager;

    @BeforeAll
    public static void init() {
        MockBukkit.mock();
        menuPlugin = MockBukkit.load(ZMenuPlugin.class, true); // MockBukkit server
    }

    @AfterAll
    public static void tearDown() {
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

        checkComponents(registered);
    }

    @Test
    public void testRegistrationWithDuplicateNameThrowsException() {
        ItemComponentLoader first = new ItemComponentLoader("duplicate-test") {
            @Override
            public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
                return null;
            }
        };

        componentsManager.registerComponent(first);

        ItemComponentLoader second = new ItemComponentLoader("duplicate-test") {
            @Override
            public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
                return null;
            }
        };

        assertThrows(ItemComponentAlreadyRegisterException.class, () -> componentsManager.registerComponent(second));
    }

    @Test
    public void testRegistrationWithDistinctNamesSucceeds() {
        ItemComponentLoader first = new ItemComponentLoader("distinct-a") {
            @Override
            public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
                return null;
            }
        };

        ItemComponentLoader second = new ItemComponentLoader("distinct-b") {
            @Override
            public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
                return null;
            }
        };

        componentsManager.registerComponent(first);
        componentsManager.registerComponent(second);
    }
}
