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

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DataComponentTest extends DataComponentTestBase {

    private static MenuPlugin menuPlugin;
    private ZComponentsManager componentsManager;

    @BeforeAll
    static void init() {
        MockBukkit.mock();
        menuPlugin = MockBukkit.load(ZMenuPlugin.class, true); // MockBukkit server
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unmock();
    }


    @BeforeEach
    void setup() {
        this.componentsManager = new ZComponentsManager();
    }

    @Test
    void testAllKnownComponents() {
        this.componentsManager.initializeDefaultComponents(menuPlugin);

        Set<String> registered = new HashSet<>(this.componentsManager.getRegisteredComponentNames());

        checkComponents(registered);
    }

    @Test
    void testRegistrationWithDuplicateNameThrowsException() {
        ItemComponentLoader first = new ItemComponentLoader("duplicate-test") {
            @Override
            public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
                return null;
            }
        };

        this.componentsManager.registerComponent(first);

        ItemComponentLoader second = new ItemComponentLoader("duplicate-test") {
            @Override
            public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
                return null;
            }
        };

        assertThrows(ItemComponentAlreadyRegisterException.class, () -> this.componentsManager.registerComponent(second));
    }

    @Test
    void testRegistrationWithDistinctNamesSucceeds() {
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

        this.componentsManager.registerComponent(first);
        this.componentsManager.registerComponent(second);

        assertSame(first, this.componentsManager.getLoader("distinct-a").orElseThrow());
        assertSame(second, this.componentsManager.getLoader("distinct-b").orElseThrow());
    }
}
