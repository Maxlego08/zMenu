package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.enums.dialog.DialogType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EscapeActionCheckTest {

    private static Optional<String> check(String yaml, DialogType type) throws Exception {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.loadFromString(yaml);
        return EscapeActionCheck.refreshOnEscape(configuration, type);
    }

    @Test
    public void aNoticeWhoseButtonRefreshesIsFound() throws Exception {
        String yaml = "actions:\n  1:\n    success:\n      - type: canvas-set\n        key: a\n        value: b\n      - type: canvas-refresh\n";
        assertEquals(Optional.of("notice"), check(yaml, DialogType.NOTICE));
        assertEquals(Optional.empty(), check("can-close-with-escape: false\n" + yaml, DialogType.NOTICE));
    }

    @Test
    public void theExitButtonIsCheckedButNotTheOtherButtons() throws Exception {
        String refreshInButton = """
                multi-actions:
                  preview:
                    actions:
                      1:
                        success:
                          - type: canvas-refresh
                exit-button:
                  text: Close
                """;
        assertEquals(Optional.empty(), check(refreshInButton, DialogType.MULTI_ACTION));

        String refreshInExit = "exit-button:\n  text: Close\n  actions:\n    1:\n      success:\n        - type: canvas_refresh\n";
        assertEquals(Optional.of("exit-button"), check(refreshInExit, DialogType.MULTI_ACTION));
    }

    @Test
    public void theNoButtonOfAConfirmationIsChecked() throws Exception {
        String yaml = """
                confirmation:
                  yes-text: Yes
                  no-text: No
                yes-actions:
                  1:
                    success:
                      - type: canvas-refresh
                no-actions:
                  1:
                    success:
                      - type: message
                        messages: [bye]
                """;
        assertEquals(Optional.empty(), check(yaml, DialogType.CONFIRMATION));
        assertEquals(Optional.of("confirmation.no"), check(yaml.replace("type: message", "type: canvas-refresh"), DialogType.CONFIRMATION));
    }
}
