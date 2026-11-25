package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.dialog.canvas.CanvasValue;
import fr.maxlego08.menu.api.dialog.canvas.HAlign;
import fr.maxlego08.menu.api.exceptions.DialogException;
import net.kyori.adventure.key.Key;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

public class CanvasValueTest {

    @Test
    public void numbersAreReadAndChecked() {
        YamlDocument section = YamlSections.empty();
        section.set("width", 120);
        CanvasValue<Integer> width = CanvasValue.number(section, "width", 200, 1, 1014, "test");
        assertFalse(width.isPlaceholder());
        assertEquals(120, width.fixed());

        section.set("width", 2000);
        assertThrows(DialogException.class, () -> CanvasValue.number(section, "width", 200, 1, 1014, "test"));
        section.set("width", "big");
        assertThrows(DialogException.class, () -> CanvasValue.number(section, "width", 200, 1, 1014, "test"));
    }

    @Test
    public void aPlaceholderIsKeptForLater() {
        YamlDocument section = YamlSections.empty();
        section.set("width", "%my_width%");
        CanvasValue<Integer> width = CanvasValue.number(section, "width", 200, 1, 1014, "test");
        assertTrue(width.isPlaceholder());
        assertNull(width.fixed());
        assertEquals(200, width.fixedOrFallback());
    }

    @Test
    public void aMissingKeyGivesItsDefault() {
        CanvasValue<Boolean> hat = CanvasValue.bool(YamlSections.empty(), "hat", true, "test");
        assertEquals(Boolean.TRUE, hat.fixed());
    }

    @Test
    public void booleansEnumsAndKeysAreRead() {
        YamlDocument section = YamlSections.empty();
        section.set("hat", "no");
        section.set("align", "Center");
        section.set("atlas", "blocks");
        section.set("sprite", "myplugin:icons/star");
        assertEquals(Boolean.FALSE, CanvasValue.bool(section, "hat", true, "test").fixed());
        assertEquals(HAlign.CENTER, CanvasValue.enumeration(section, "align", HAlign.class, HAlign.LEFT, "test").fixed());
        assertEquals(Key.key("minecraft", "blocks"), CanvasValue.key(section, "atlas", Key.key("minecraft", "items"), "test").fixed());
        assertEquals(Key.key("myplugin", "icons/star"), CanvasValue.key(section, "sprite", Key.key("minecraft", "missingno"), "test").fixed());

        section.set("align", "middle");
        assertThrows(DialogException.class, () -> CanvasValue.enumeration(section, "align", HAlign.class, HAlign.LEFT, "test"));
    }

    @Test
    public void writesBackWhatItRead() {
        YamlDocument section = YamlSections.empty();
        section.set("align", "center");
        section.set("width", "%my_width%");
        YamlDocument written = YamlSections.empty();
        CanvasValue.enumeration(section, "align", HAlign.class, HAlign.LEFT, "test").serialize(written, "align");
        CanvasValue.number(section, "width", 200, 1, 1014, "test").serialize(written, "width");
        assertEquals("center", written.getString("align"));
        assertEquals("%my_width%", written.getString("width"));
    }
}
