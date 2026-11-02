package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.exceptions.DialogException;
import fr.maxlego08.menu.hooks.dialogs.action.ZStaticOpenUrlDialogAction;
import fr.maxlego08.menu.hooks.dialogs.canvas.DialogActionRegistries;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClickValueTest {

    private static final MenuPlugin PLUGIN = DialogActionRegistries.plugin(DialogActionRegistries.create());

    private static ConfigurationSection element(String yaml) throws InvalidConfigurationException {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.loadFromString(yaml);
        return configuration.getConfigurationSection("docs");
    }

    private static ClickValue read(String yaml) throws InvalidConfigurationException {
        return ClickValue.read(PLUGIN, element(yaml), "docs", "test.yml");
    }

    @Test
    public void anElementWithoutClickHasNone() throws InvalidConfigurationException {
        assertNull(read("docs:\n  type: button\n"));
    }

    @Test
    public void aStaticClickIsLoadedLikeADialogButtonAndWrittenBack() throws InvalidConfigurationException {
        ClickValue click = read("""
                docs:
                  type: button
                  click:
                    type: static
                    static-type: OPEN_URL
                    url: "https://docs.groupez.dev"
                """);
        assertNotNull(click);
        assertInstanceOf(ZStaticOpenUrlDialogAction.class, click.action());

        YamlConfiguration written = new YamlConfiguration();
        click.serialize(written);
        assertEquals("static", written.getString("click.type"));
        assertEquals("OPEN_URL", written.getString("click.static-type"));
        assertEquals("https://docs.groupez.dev", written.getString("click.url"));
    }

    @Test
    public void clickCannotBeMixedWithZMenuActions() {
        DialogException exception = assertThrows(DialogException.class, () -> read("""
                docs:
                  type: button
                  refresh: true
                  click:
                    type: static
                    static-type: COPY_TO_CLIPBOARD
                    text: hello
                """));
        assertTrue(exception.getMessage().contains("refresh"), exception.getMessage());
    }

    @Test
    public void customClickUnknownTypesAndMissingValuesAreRefused() {
        assertThrows(DialogException.class, () -> read("docs:\n  click:\n    type: custom-click\n"));
        assertThrows(DialogException.class, () -> read("docs:\n  click:\n    type: nope\n"));
        assertThrows(DialogException.class, () -> read("docs:\n  click:\n    type: static\n    static-type: OPEN_URL\n"));
        assertThrows(DialogException.class, () -> read("docs:\n  click: https://docs.groupez.dev\n"));
    }
}
