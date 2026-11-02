package fr.maxlego08.menu.hooks.dialogs.canvas.pack;

import com.google.gson.JsonObject;
import fr.maxlego08.menu.hooks.dialogs.canvas.CanvasTestSupport;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpaceFontTest {

    @Test
    public void zeroIsEmpty() {
        assertEquals("", SpaceFont.encode(0));
        assertEquals("", SpaceFont.encode(0.1));
    }

    @Test
    public void everyWholeMoveAddsUp() {
        for (int pixels = -3000; pixels <= 3000; pixels++) {
            double sum = CanvasTestSupport.netAdvance(Component.text(SpaceFont.encode(pixels)), Map.of());
            assertEquals(pixels, sum, "move of " + pixels);
        }
    }

    @Test
    public void quarterMovesAddUp() {
        for (double pixels = -20; pixels <= 20; pixels += 0.25) {
            double sum = CanvasTestSupport.netAdvance(Component.text(SpaceFont.encode(pixels)), Map.of());
            assertEquals(pixels, sum, 1e-9, "move of " + pixels);
        }
    }

    @Test
    public void movesStayShort() {
        assertTrue(SpaceFont.encode(1023).codePointCount(0, SpaceFont.encode(1023).length()) <= 10);
        assertTrue(SpaceFont.encode(-1016).codePointCount(0, SpaceFont.encode(-1016).length()) <= 10);
    }

    @Test
    public void providerHoldsEveryCharacter() {
        JsonObject advances = SpaceFont.provider().getAsJsonObject("advances");
        String encoded = SpaceFont.encode(2047.75) + SpaceFont.encode(-2047.75);
        encoded.codePoints().forEach(codepoint -> assertTrue(advances.has(Character.toString(codepoint)), "missing " + Integer.toHexString(codepoint)));
        assertEquals(26, advances.size());
    }
}
