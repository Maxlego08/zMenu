package fr.maxlego08.menu.hooks.dialogs.canvas;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CanvasSettingsTest {

    private static CanvasSettings.RequirePack requirePack(String yaml) throws Exception {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.loadFromString(yaml);
        return CanvasSettings.read(configuration).requirePack();
    }

    @Test
    public void requirePackIsAutoByDefault() throws Exception {
        assertEquals(CanvasSettings.RequirePack.AUTO, requirePack(""));
        assertEquals(CanvasSettings.RequirePack.AUTO, requirePack("dialog-canvas:\n  require-pack: maybe\n"));
    }

    @Test
    public void autoTrustsPackPluginsUnlessAFailureWasReported() {
        CanvasSettings.RequirePack auto = CanvasSettings.RequirePack.AUTO;
        assertTrue(auto.showsCanvas(null, false));
        assertTrue(auto.showsCanvas(true, false));
        assertFalse(auto.showsCanvas(false, false));
        assertFalse(auto.showsCanvas(null, true));
        assertTrue(auto.showsCanvas(true, true));
        assertFalse(auto.showsCanvas(false, true));
    }

    @Test
    public void trueNeedsALoadedPackAndFalseNeverFallsBack() {
        assertFalse(CanvasSettings.RequirePack.TRUE.showsCanvas(null, false));
        assertTrue(CanvasSettings.RequirePack.TRUE.showsCanvas(true, false));
        assertTrue(CanvasSettings.RequirePack.FALSE.showsCanvas(false, true));
    }

    @Test
    public void requirePackReadsBooleansAndNames() throws Exception {
        assertEquals(CanvasSettings.RequirePack.TRUE, requirePack("dialog-canvas:\n  require-pack: true\n"));
        assertEquals(CanvasSettings.RequirePack.FALSE, requirePack("dialog-canvas:\n  require-pack: false\n"));
        assertEquals(CanvasSettings.RequirePack.AUTO, requirePack("dialog-canvas:\n  require-pack: Auto\n"));
    }
}
