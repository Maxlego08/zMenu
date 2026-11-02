package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

public class VisibleConditionTest {

    private static final Map<String, String> VALUES = Map.of("%tab%", "shop", "%level%", "12", "%flag%", "yes", "%evil%", "a > b");
    private static final UnaryOperator<String> RESOLVER = text -> {
        String result = text;
        for (Map.Entry<String, String> entry : VALUES.entrySet()) result = result.replace(entry.getKey(), entry.getValue());
        return result;
    };

    private static boolean test(String raw) {
        return VisibleCondition.parse(raw).test(RESOLVER);
    }

    @Test
    public void aValueIsTrueForTrueYesOrOne() {
        assertTrue(test("%flag%"));
        assertTrue(test("1"));
        assertFalse(test("%tab%"));
        assertFalse(test(""));
    }

    @Test
    public void comparesTextAndNumbers() {
        assertTrue(test("%tab% == shop"));
        assertFalse(test("%tab% != shop"));
        assertTrue(test("%level% >= 10"));
        assertFalse(test("%level% < 10"));
        assertTrue(test("%level%==12.0"));
        assertFalse(test("%tab% > 3"));
        assertTrue(test("%tab%/%flag% == shop/yes"));
    }

    @Test
    public void listsAndTextsCanBeSearched() {
        UnaryOperator<String> resolver = text -> text.replace("%tags%", "pvp,shop").replace("%search%", "Diamond Sword");
        assertTrue(VisibleCondition.parse("%tags% has pvp").test(resolver));
        assertFalse(VisibleCondition.parse("%tags% has pv").test(resolver));
        assertTrue(VisibleCondition.parse("%tags% !has build").test(resolver));
        assertTrue(VisibleCondition.parse("%search% contains sword").test(resolver));
        assertFalse(VisibleCondition.parse("%search% !contains SWORD").test(resolver));
        assertEquals("has", VisibleCondition.parse("%tags% has pvp").operator());
    }

    @Test
    public void theOperatorIsReadFromTheTextAsWritten() {
        VisibleCondition condition = VisibleCondition.parse("%evil% == x");
        assertEquals("==", condition.operator());
        assertFalse(condition.test(RESOLVER));
        assertTrue(test("%evil% == a > b"));
    }
}
