package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.exceptions.DialogException;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

public class PositionValueTest {

    @Test
    public void aNumberIsFixed() {
        YamlDocument section = YamlSections.empty();
        section.set("y", 12);
        PositionValue value = PositionValue.readY(section, "y", "test", "body");
        assertEquals(12, value.fixed());
        assertEquals(List.of(12), value.all());
        assertEquals(List.of(15), value.all(3));
    }

    @Test
    public void aPlaceholderYNeedsItsValues() {
        YamlDocument section = YamlSections.empty();
        section.set("y", "%fish_y%");
        assertThrows(DialogException.class, () -> PositionValue.readY(section, "y", "test", "body"));

        section.set("y-values", "0-3");
        PositionValue range = PositionValue.readY(section, "y", "test", "body");
        assertNull(range.fixed());
        assertEquals(List.of(0, 1, 2, 3), range.all());

        section.set("y-values", List.of(20, 0, 10));
        assertEquals(List.of(0, 10, 20), PositionValue.readY(section, "y", "test", "body").all());
    }

    @Test
    public void aPlaceholderXNeedsNoValues() {
        YamlDocument section = YamlSections.empty();
        section.set("x", "%fish_x%");
        PositionValue value = PositionValue.readX(section, "x", "test", "body");
        assertNull(value.fixed());
        assertEquals(List.of(), value.all());
    }

    @Test
    public void negativeYIsRejected() {
        YamlDocument section = YamlSections.empty();
        section.set("y", -1);
        assertThrows(DialogException.class, () -> PositionValue.readY(section, "y", "test", "body"));
    }

    @Test
    public void writesBackWhatItRead() {
        YamlDocument section = YamlSections.empty();
        section.set("y", "%fish_y%");
        section.set("y-values", "4-8");
        YamlDocument written = YamlSections.empty();
        PositionValue.readY(section, "y", "test", "body").serialize(written, "y");
        assertEquals("%fish_y%", written.getString("y"));
        assertEquals("4-8", written.getString("y-values"));
    }
}
