package fr.maxlego08.menu.serialization;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.animation.slot.settings.ButtonAnimationSettings;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import fr.maxlego08.menu.api.animation.slot.settings.idle.ButtonIdleAnimation;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.inventory.ContainerInventory;
import fr.maxlego08.menu.loader.SlotAnimationSettingsLoader;
import fr.maxlego08.menu.test.serialization.ObjectGraphComparator;
import fr.maxlego08.menu.test.serialization.SerializationRoundTrip;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every animation of the example menus must survive serialize then load: the opening and closing
 * animations, and the animation and idle animation of every button.
 */
class SlotAnimationSerializationTest {

    private static ZMenuPlugin plugin;

    @TempDir
    Path directory;

    private final List<String> failures = new ArrayList<>();
    private final Map<String, Integer> checked = new TreeMap<>();
    private int fileIndex;

    @BeforeAll
    static void init() {
        MockBukkit.mock().addSimpleWorld("world");
        plugin = MockBukkit.load(ZMenuPlugin.class, true);
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unmock();
    }

    private File nextFile() {
        return this.directory.resolve("animation-" + this.fileIndex++ + ".yml").toFile();
    }

    private YamlConfiguration writeAndReread(Consumer<ConfigurationSection> writer) throws IOException {
        YamlConfiguration configuration = new YamlConfiguration();
        writer.accept(configuration.createSection("button"));
        File file = this.nextFile();
        configuration.save(file);
        return YamlConfiguration.loadConfiguration(file);
    }

    private static ObjectGraphComparator comparator() {
        return new ObjectGraphComparator().ignoreField("ZMenuItemStack.filePath").ignoreField("ZMenuItemStack.path");
    }

    private void compare(String where, Object original, Object reloaded) {
        comparator().compare(original, reloaded).forEach(difference -> this.failures.add(where + ": " + difference));
    }

    private void checkSettings(String where, SlotAnimationSettings settings, File origin, SlotAnimationSettingsLoader loader) {
        if (settings == null) return;
        this.checked.merge(where.substring(where.lastIndexOf(' ') + 1), 1, Integer::sum);
        try {
            YamlConfiguration reread = SerializationRoundTrip.writeAndReread(settings, "animation", this.nextFile());
            SlotAnimationSettings reloaded = loader.load(reread, "animation.", origin);
            if (reloaded == null) {
                this.failures.add(where + ": its serialized form " + reread.saveToString() + " does not load");
                return;
            }
            this.compare(where, settings, reloaded);
        } catch (IOException | RuntimeException exception) {
            this.failures.add(where + ": " + exception);
        }
    }

    private void checkButton(String where, Button button, File origin, SlotAnimationSettingsLoader loader) {
        ButtonAnimationSettings settings = button.getAnimationSettings();
        try {
            YamlConfiguration reread = this.writeAndReread(section -> settings.serialize(section, button.allowsTemporaryItems()));
            this.compare(where + " animation", settings, loader.loadButton(reread, "button.", button.allowsTemporaryItems()));
            if (!settings.equals(ButtonAnimationSettings.DEFAULT) && !settings.equals(ButtonAnimationSettings.NOT_COVERABLE)) {
                this.checked.merge("button-animation", 1, Integer::sum);
            }
        } catch (IOException | RuntimeException exception) {
            this.failures.add(where + " animation: " + exception);
        }

        ButtonIdleAnimation idle = button.getIdleAnimation();
        if (idle == null) return;
        this.checked.merge("idle-animation", 1, Integer::sum);
        try {
            YamlConfiguration reread = this.writeAndReread(section -> idle.serialize(section.createSection("idle-animation")));
            ButtonIdleAnimation reloaded = loader.loadIdle(reread, "button.", origin);
            if (reloaded == null) {
                this.failures.add(where + " idle: its serialized form " + reread.saveToString() + " does not load");
                return;
            }
            this.compare(where + " idle", idle, reloaded);
        } catch (IOException | RuntimeException exception) {
            this.failures.add(where + " idle: " + exception);
        }
    }

    @Test
    void everyAnimationOfTheExampleMenusSurvivesARoundTrip() {
        SlotAnimationSettingsLoader loader = new SlotAnimationSettingsLoader(plugin);

        for (Inventory inventory : plugin.getInventoryManager().getInventories(plugin)) {
            String name = inventory.getFileName();
            if (inventory instanceof ContainerInventory container) {
                this.checkSettings(name + " open", container.getSlotAnimation(), inventory.getFile(), loader);
                this.checkSettings(name + " close", container.getCloseAnimation(), inventory.getFile(), loader);
            }
            for (Button button : inventory.getButtons()) {
                this.checkButton(name + " " + button.getName(), button, inventory.getFile(), loader);
            }
        }

        assertEquals(List.of(), this.failures, String.join("\n", this.failures));
        for (String kind : List.of("open", "close", "button-animation", "idle-animation")) {
            assertTrue(this.checked.getOrDefault(kind, 0) > 0, "no example menu has a " + kind + " to check: " + this.checked);
        }
    }

    @Test
    void anAnimationOnlyNamingAFileIsWrittenBackAsThatFile() throws IOException {
        SlotAnimationSettingsLoader loader = new SlotAnimationSettingsLoader(plugin);
        YamlConfiguration menu = new YamlConfiguration();
        menu.set("animation.file", "pulse");

        SlotAnimationSettings settings = loader.load(menu, "animation.", this.nextFile());
        YamlConfiguration reread = SerializationRoundTrip.writeAndReread(settings, "animation", this.nextFile());

        assertEquals(Map.of("file", "pulse"), reread.getConfigurationSection("animation").getValues(false));
    }

    @Test
    void anAnimationOverridingItsFileIsWrittenInFull() throws IOException {
        SlotAnimationSettingsLoader loader = new SlotAnimationSettingsLoader(plugin);
        YamlConfiguration menu = new YamlConfiguration();
        menu.set("animation.file", "pulse");
        menu.set("animation.chance", 40);

        SlotAnimationSettings settings = loader.load(menu, "animation.", this.nextFile());
        YamlConfiguration reread = SerializationRoundTrip.writeAndReread(settings, "animation", this.nextFile());

        assertTrue(reread.contains("animation.type"), reread.saveToString());
        assertTrue(!reread.contains("animation.file"), reread.saveToString());
        assertEquals(40.0, reread.getDouble("animation.chance"));
        assertEquals(List.of(), comparator().compare(settings, loader.load(reread, "animation.", this.nextFile())));
    }
}
