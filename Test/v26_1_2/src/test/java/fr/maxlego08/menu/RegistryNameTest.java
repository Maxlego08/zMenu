package fr.maxlego08.menu;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.command.CommandArgumentValidator;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.mechanic.Mechanic;
import fr.maxlego08.menu.api.mechanic.MechanicFactory;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.loader.RuleLoader;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.common.utils.cache.YamlFileCache;
import fr.maxlego08.menu.registry.ZRuleLoaderRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Names that were accepted before the registries, but that a Paper key cannot hold as they are
 * (upper case, spaces, symbols, accents, a namespace with a space), must still register and still be
 * found by the same name, through the old register methods and through the registries.
 */
class RegistryNameTest {

    private static ZMenuPlugin plugin;
    private static Plugin addon;
    private static int counter;

    private InventoryManager inventoryManager;
    private ButtonManager buttonManager;
    private final List<File> created = new ArrayList<>();

    @BeforeAll
    static void init() {
        MockBukkit.mock();
        plugin = MockBukkit.load(ZMenuPlugin.class, true);
        addon = MockBukkit.createMockPlugin("NameAddon");
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unmock();
    }

    @BeforeEach
    void setup() {
        this.inventoryManager = plugin.getInventoryManager();
        this.buttonManager = plugin.getButtonManager();
    }

    @AfterEach
    void cleanup() {
        this.created.forEach(File::delete);
        this.inventoryManager.getRegistries().writable(Registries.BUTTON).unregisterAll(addon.getName());
    }

    private static String unique(String name) {
        return name + (counter++);
    }

    private static List<String> spellings(String name) {
        return List.of(name, name.toUpperCase(Locale.ROOT), name.toLowerCase(Locale.ROOT));
    }

    private static class TestButton extends Button {
    }

    private static ButtonLoader buttonLoader(String name, Button button) {
        return new ButtonLoader(addon, name) {
            @Override
            public Button load(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull DefaultButtonValue defaultButtonValue) {
                return button;
            }
        };
    }

    @ParameterizedTest
    @ValueSource(strings = {"Shop+Buy", "Café Menu", "my.addon!button", "addon:Fancy Thing!", "Addon Plugin:thing", "a:b:c"})
    @SuppressWarnings("removal")
    void aButtonRegisteredTheOldWayIsFoundByItsName(String base) {
        String name = unique(base);
        ButtonLoader loader = buttonLoader(name, new TestButton());

        assertDoesNotThrow(() -> this.buttonManager.register(loader));
        for (String spelling : spellings(name)) {
            assertSame(loader, this.buttonManager.getLoader(spelling).orElse(null), spelling);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Shop+Buy", "Café Menu", "my.addon!button", "addon:Fancy Thing!", "Addon Plugin:thing", "a:b:c"})
    void aButtonRegisteredInItsRegistryIsUsedByAMenu(String base) throws IOException {
        String name = unique(base);
        TestButton button = new TestButton();
        ButtonLoader loader = buttonLoader(name, button);

        assertDoesNotThrow(() -> this.inventoryManager.getRegistries().writable(Registries.BUTTON).register(addon, loader));

        String fileName = "registry_name_" + counter;
        File file = new File(plugin.getDataFolder(), "inventories/" + fileName + ".yml");
        file.getParentFile().mkdirs();
        Files.writeString(file.toPath(), "size: 9\nname: \"Names\"\nitems:\n  odd:\n    type: \"" + name + "\"\n    slot: 0\n    item:\n      material: STONE\n");
        this.created.add(file);
        YamlFileCache.clearCache();
        this.inventoryManager.loadInventories();

        Inventory inventory = this.inventoryManager.findInventory(fileName).orElseThrow();
        assertTrue(inventory.getButtons().contains(button), "the menu uses the button named " + name);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Shop+Buy", "Café Menu", "my.addon!button", "addon:Fancy Thing!", "Addon Plugin:thing", "a:b:c"})
    @SuppressWarnings("removal")
    void anActionRegisteredTheOldWayIsFoundByItsNameAndAliases(String base) {
        String name = unique(base);
        String alias = unique(base + " alias");
        ActionLoader loader = new ActionLoader(name, alias) {
            @Override
            public Action load(@NotNull String path, @NotNull TypedMapAccessor accessor, @NotNull File file) {
                return null;
            }
        };

        assertDoesNotThrow(() -> this.buttonManager.registerAction(loader));
        for (String spelling : spellings(name)) {
            assertSame(loader, this.buttonManager.getActionLoader(spelling).orElse(null), spelling);
        }
        assertSame(loader, this.buttonManager.getActionLoader(alias).orElse(null), alias);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Shop+Buy", "Café Menu", "my.addon!button", "addon:Fancy Thing!", "Addon Plugin:thing", "a:b:c"})
    @SuppressWarnings("removal")
    void aRequirementRegisteredTheOldWayIsFoundByItsName(String base) {
        String name = unique(base);
        PermissibleLoader loader = new PermissibleLoader(name) {
            @Override
            public Permissible load(@NotNull String path, @NotNull TypedMapAccessor accessor, @NotNull File file) {
                return null;
            }
        };

        assertDoesNotThrow(() -> this.buttonManager.registerPermissible(loader));
        for (String spelling : spellings(name)) {
            assertSame(loader, this.buttonManager.getPermission(spelling).orElse(null), spelling);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Shop+Buy", "Café Menu", "my.addon!button", "addon:Fancy Thing!", "Addon Plugin:thing", "a:b:c"})
    @SuppressWarnings("removal")
    void aMaterialLoaderRegisteredTheOldWayIsFoundByItsName(String base) {
        String name = unique(base);
        MaterialLoader loader = new MaterialLoader(name) {
            @Override
            public ItemStack load(@NotNull Player player, @Nullable YamlConfiguration configuration, @NotNull String path, @NotNull String materialString) {
                return null;
            }
        };

        assertTrue(assertDoesNotThrow(() -> this.inventoryManager.registerMaterialLoader(loader)));
        for (String spelling : spellings(name)) {
            assertSame(loader, this.inventoryManager.getMaterialLoader(spelling).orElse(null), spelling);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Shop+Buy", "Café Menu", "my.addon!button", "addon:Fancy Thing!", "Addon Plugin:thing", "a:b:c"})
    @SuppressWarnings("removal")
    void anItemComparisonRegisteredTheOldWayIsFoundByItsName(String base) {
        String name = unique(base);
        ItemStackSimilar similar = new ItemStackSimilar() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public boolean isSimilar(@NotNull ItemStack itemStackA, @NotNull ItemStack itemStackB) {
                return true;
            }
        };

        assertDoesNotThrow(() -> this.inventoryManager.registerItemStackVerification(similar));
        for (String spelling : spellings(name)) {
            assertSame(similar, plugin.getItemStackService().getVerification(spelling).orElse(null), spelling);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Shop+Buy", "Café Menu", "my.addon!button", "addon:Fancy Thing!", "Addon Plugin:thing", "a:b:c"})
    @SuppressWarnings("removal")
    void aCommandArgumentTypeRegisteredTheOldWayIsFoundByItsName(String base) {
        String name = unique(base);
        CommandArgumentValidator validator = new CommandArgumentValidator() {
            @Override
            public boolean isValid(String value) {
                return true;
            }

            @Override
            public Message getErrorMessage() {
                return null;
            }

            @Override
            public String getType() {
                return name;
            }
        };

        assertDoesNotThrow(() -> plugin.getCommandManager().registerArgumentValidator(validator));
        for (String spelling : spellings(name)) {
            assertSame(validator, plugin.getCommandManager().getArgumentValidator(spelling).orElse(null), spelling);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Shop+Buy", "Café Menu", "my.addon!button", "addon:Fancy Thing!", "Addon Plugin:thing", "a:b:c"})
    @SuppressWarnings("removal")
    void aMechanicRegisteredTheOldWayIsFoundByItsName(String base) {
        String name = unique(base);
        MechanicFactory<?> factory = new MechanicFactory<Mechanic<?>>(plugin, name) {
            @Override
            public Mechanic<?> parse(MenuPlugin plugin, String itemId, ConfigurationSection mechanicSection, YamlConfiguration configurationFile, File file, String path) {
                return null;
            }
        };

        assertDoesNotThrow(() -> plugin.getItemManager().registerMechanicFactory(factory));
        for (String spelling : spellings(name)) {
            assertSame(factory, plugin.getItemManager().getMechanicFactory(spelling).orElse(null), spelling);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Shop+Buy", "Café Menu", "my.addon!button", "addon:Fancy Thing!", "Addon Plugin:thing", "a:b:c"})
    void aRuleRegisteredInItsRegistryIsReadByItsType(String base) {
        String name = unique(base);
        Rule rule = context -> true;
        RuleLoader loader = new RuleLoader() {
            @Override
            public @NotNull String getType() {
                return name;
            }

            @Override
            public Rule load(@NotNull Map<String, Object> configuration) {
                return rule;
            }
        };

        assertDoesNotThrow(() -> this.inventoryManager.getRegistries().writable(Registries.RULE).register(addon, loader));
        for (String spelling : spellings(name)) {
            assertSame(rule, ZRuleLoaderRegistry.loadRule(Map.of("type", spelling)), spelling);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Shop+Buy", "Café Menu", "my.addon!button", "addon:Fancy Thing!", "Addon Plugin:thing", "a:b:c"})
    void everyNameIsAKey(String name) {
        assertDoesNotThrow(() -> ZMenuKeys.parse(name));
        assertTrue(ZMenuKeys.tryParse(name) != null, name);
    }
}
