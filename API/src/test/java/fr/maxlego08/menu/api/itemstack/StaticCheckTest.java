package fr.maxlego08.menu.api.itemstack;

import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableItemDamageFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StaticCheckTest {

    @Test
    @DisplayName("A value, or an expression without placeholders, is static; one with a placeholder is not")
    void resolvables() {
        assertTrue(ResolvableString.of("diamond").isStatic());
        assertTrue(ResolvableString.ofExpression("50% off").isStatic());
        assertFalse(ResolvableString.ofExpression("%player_name%").isStatic());
        assertTrue(ResolvableFloat.of(1.5f).isStatic());
        assertFalse(ResolvableFloat.of("%multiplier%").isStatic());
    }

    @Test
    @DisplayName("A composite resolvable is static only when all its parts are")
    void composite() {
        assertTrue(new ResolvableItemDamageFunction(ResolvableFloat.of(1f), ResolvableFloat.of(2f), ResolvableFloat.of(3f)).isStatic());
        assertFalse(new ResolvableItemDamageFunction(ResolvableFloat.of(1f), ResolvableFloat.of("%base%"), ResolvableFloat.of(3f)).isStatic());
    }

    @Test
    @DisplayName("Lists and maps are static when every element is")
    void collections() {
        assertTrue(StaticCheck.isStatic(List.of(ResolvableString.of("a"), "fixed")));
        assertFalse(StaticCheck.isStatic(Map.of("key", ResolvableString.ofExpression("%value%"))));
        assertTrue(StaticCheck.isStatic(null));
    }
}
