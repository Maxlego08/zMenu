package fr.maxlego08.menu.hooks.dialogs.canvas;

import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class EscapePlaceholdersTest {

    private static final Map<String, String> VALUES = Map.of(
            "%player_name%", "<red><click:run_command:/op me>Steve",
            "%rank%", "VIP");

    private static final UnaryOperator<String> RESOLVER = placeholder -> VALUES.getOrDefault(placeholder, placeholder);

    @Test
    public void theTagsOfAPlaceholderResultAreShownAsText() {
        String text = CanvasRenderContext.resolveEscaped("<gold>Hello %player_name%", RESOLVER);
        Component component = MiniMessage.miniMessage().deserialize(text);

        assertEquals("Hello <red><click:run_command:/op me>Steve", PlainTextComponentSerializer.plainText().serialize(component));
        assertFalse(hasClick(component));
    }

    private static boolean hasClick(Component component) {
        return component.clickEvent() != null || component.children().stream().anyMatch(EscapePlaceholdersTest::hasClick);
    }

    @Test
    public void theWrittenTagsUnknownPlaceholdersAndPercentSignsAreKept() {
        assertEquals("<gold>VIP %unknown% 50% off", CanvasRenderContext.resolveEscaped("<gold>%rank% %unknown% 50% off", RESOLVER));
    }
}
