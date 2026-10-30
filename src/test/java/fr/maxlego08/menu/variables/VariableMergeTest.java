package fr.maxlego08.menu.variables;

import fr.maxlego08.menu.api.storage.spi.VariableMutation;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * How a change merges with the one already waiting for the same variable.
 */
class VariableMergeTest {

    private static final VariableRef REF = VariableRef.global("coins");

    private static VariableMutation.Increment increment(String delta) {
        return new VariableMutation.Increment(REF, new BigDecimal(delta), 0);
    }

    @Test
    void incrementsAddUp() {
        VariableMutation merged = ZVariableService.merge(increment("2"), increment("3.5"));
        assertEquals(0, new BigDecimal("5.5").compareTo(((VariableMutation.Increment) merged).delta()));
    }

    @Test
    void anIncrementAfterASetChangesTheSetValue() {
        VariableMutation merged = ZVariableService.merge(new VariableMutation.Set(Variable.of(REF, "10", 123L)), increment("-4"));
        Variable variable = ((VariableMutation.Set) merged).variable();
        assertEquals("6", variable.text());
        assertEquals(123L, variable.expiresAt());
    }

    @Test
    void anIncrementAfterADeleteStartsFromZero() {
        VariableMutation merged = ZVariableService.merge(new VariableMutation.Delete(REF), increment("7"));
        assertEquals("7", ((VariableMutation.Set) merged).variable().text());
    }

    @Test
    void aSetOrADeleteReplacesWhatWaits() {
        VariableMutation set = new VariableMutation.Set(Variable.of(REF, "abc", 0));
        VariableMutation delete = new VariableMutation.Delete(REF);
        assertEquals(set, ZVariableService.merge(increment("1"), set));
        assertEquals(delete, ZVariableService.merge(set, delete));
    }

    @Test
    void numbersAreReadAndFormatted() {
        assertEquals("15", Variable.of(REF, "15.000", 0).text());
        assertEquals("100", Variable.of(REF, "1E+2", 0).text());
        assertEquals("2.5", Variable.of(REF, "2,5", 0).text());
        assertEquals("abc", Variable.of(REF, "abc", 0).text());
    }
}
