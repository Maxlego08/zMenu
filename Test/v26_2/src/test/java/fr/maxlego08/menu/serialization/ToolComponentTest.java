package fr.maxlego08.menu.serialization;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.context.ZBuildContext;
import fr.maxlego08.menu.api.itemstack.components.ToolComponent;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableToolRule;
import fr.maxlego08.menu.api.utils.resolvable.paper.TagKeySetResolvable;
import fr.maxlego08.menu.api.utils.resolvable.paper.TypedKeySetResolvable;
import fr.maxlego08.menu.loader.MenuItemStackLoader;
import fr.maxlego08.menu.test.serialization.ObjectGraphComparator;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import net.kyori.adventure.util.TriState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The tool component follows the Minecraft format (minecraft.wiki, Data component format, tool), with kebab-case keys.
 */
class ToolComponentTest {

    private static ZMenuPlugin plugin;

    @TempDir
    Path directory;

    @BeforeAll
    static void init() {
        MockBukkit.mock();
        plugin = MockBukkit.load(ZMenuPlugin.class, true);
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unmock();
    }

    private MenuItemStack load(String toolYaml) throws Exception {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.loadFromString("item:\n  material: DIAMOND_PICKAXE\n  skip-first-cache: true\n  components:\n    tool:\n      " + toolYaml.replace("\n", "\n      "));
        return new MenuItemStackLoader(plugin.getInventoryManager()).load(configuration, "item.", this.directory.resolve("item.yml").toFile());
    }

    private ToolComponent tool(String toolYaml) throws Exception {
        return (ToolComponent) this.load(toolYaml).getItemComponents().iterator().next();
    }

    @Test
    void omittedSpeedAndCorrectForDropsStayUnset() throws Exception {
        ToolComponent tool = this.tool("""
                rules:
                  - blocks: "minecraft:obsidian"
                """);

        ResolvableToolRule rule = tool.getRules().getFirst();
        assertNull(rule.speed(), "an unset speed must not override the tool's speed");
        assertNull(rule.correctForDrops(), "an unset correct-for-drops must stay unset");
    }

    @Test
    void aTagWithoutItsHashIsStillATag() throws Exception {
        ToolComponent tool = this.tool("""
                rules:
                  - blocks: "minecraft:logs"
                """);

        assertInstanceOf(TagKeySetResolvable.class, tool.getRules().getFirst().blocks());
        assertEquals("#minecraft:logs", tool.getRules().getFirst().blocks().serialize());
    }

    @Test
    void aTagInAListBecomesItsOwnRuleInPlace() throws Exception {
        ToolComponent tool = this.tool("""
                rules:
                  - blocks:
                      - "minecraft:dirt"
                      - "#minecraft:logs"
                      - "minecraft:gravel"
                    speed: 3.0
                """);

        List<ResolvableToolRule> rules = tool.getRules();
        assertEquals(3, rules.size());
        assertInstanceOf(TypedKeySetResolvable.class, rules.get(0).blocks());
        assertInstanceOf(TagKeySetResolvable.class, rules.get(1).blocks());
        assertInstanceOf(TypedKeySetResolvable.class, rules.get(2).blocks());
        assertEquals(List.of("minecraft:dirt"), rules.get(0).blocks().serialize());
        assertEquals(List.of("minecraft:gravel"), rules.get(2).blocks().serialize());
        for (ResolvableToolRule rule : rules) {
            assertEquals(3.0f, rule.speed().getResolvedValue());
        }
    }

    @Test
    void minecraftSnakeCaseKeysLoadTheSameTool() throws Exception {
        String kebabCase = """
                default-mining-speed: 1.5
                damage-per-block: 2
                can-destroy-blocks-in-creative: false
                rules:
                  - blocks: "#minecraft:mineable/pickaxe"
                    speed: 6.0
                    correct-for-drops: true
                """;
        String snakeCase = kebabCase.replace("default-mining-speed", "default_mining_speed").replace("damage-per-block", "damage_per_block")
                .replace("can-destroy-blocks-in-creative", "can_destroy_blocks_in_creative").replace("correct-for-drops", "correct_for_drops");

        assertEquals(List.of(), new ObjectGraphComparator().compare(this.load(kebabCase), this.load(snakeCase)));
    }

    @Test
    void negativeDamagePerBlockIsRefused() throws Exception {
        ToolComponent tool = this.tool("damage-per-block: -3");

        assertEquals(1, tool.getDamagePerBlock().getResolvedValue());
    }

    @Test
    void theBuiltItemHasTheRulesInOrder() throws Exception {
        MenuItemStack item = this.load("""
                rules:
                  - blocks: "minecraft:obsidian"
                    speed: 2.5
                  - blocks:
                      - "minecraft:dirt"
                      - "minecraft:gravel"
                    correct-for-drops: true
                """);

        // Applied directly: building the whole item would hide an error of the component
        ItemStack itemStack = ItemStack.of(org.bukkit.Material.DIAMOND_PICKAXE);
        item.getItemComponents().iterator().next().apply(new ZBuildContext.Builder().build(), itemStack, null);
        Tool tool = itemStack.getData(DataComponentTypes.TOOL);

        assertNotNull(tool, "the built item must have the tool component");
        assertEquals(2, tool.rules().size());
        assertEquals(2.5f, tool.rules().get(0).speed());
        assertEquals(TriState.NOT_SET, tool.rules().get(0).correctForDrops());
        assertNull(tool.rules().get(1).speed());
        assertEquals(TriState.TRUE, tool.rules().get(1).correctForDrops());
    }
}
