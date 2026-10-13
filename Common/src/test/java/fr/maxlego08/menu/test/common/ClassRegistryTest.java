package fr.maxlego08.menu.test.common;

import fr.maxlego08.menu.api.loader.ClassRegistry;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A hook whose plugin is missing or broken fails while its loader is being constructed. Those
 * failures must stay contained here, and must be reported for what they are, so that one bad hook
 * neither aborts the scan nor gets logged as a missing constructor.
 */
class ClassRegistryTest {

    public static class Working implements Runnable {
        @Override
        public void run() {
        }
    }

    public static class ThrowsException implements Runnable {
        public ThrowsException() {
            throw new NullPointerException("the plugin API is null");
        }

        @Override
        public void run() {
        }
    }

    public static class ThrowsError implements Runnable {
        public ThrowsError() {
            throw new NoClassDefFoundError("com/example/Missing");
        }

        @Override
        public void run() {
        }
    }

    public static class NoUsableConstructor implements Runnable {
        public NoUsableConstructor(String unsupported) {
        }

        @Override
        public void run() {
        }
    }

    private final List<Runnable> registered = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    private ClassRegistry<Runnable, Plugin> registry() {
        return ClassRegistry.of(Runnable.class, this.registered::add)
                .tryNoArgsConstructor()
                .errorLogger(this.errors::add);
    }

    @Test
    void aWorkingClassIsRegistered() {
        assertTrue(this.registry().load(null, Working.class));
        assertEquals(1, this.registered.size());
        assertTrue(this.errors.isEmpty(), "a successful load must not log an error: " + this.errors);
    }

    @Test
    void aConstructorThrowingAnExceptionIsContained() {
        assertFalse(this.registry().load(null, ThrowsException.class));
        assertTrue(this.registered.isEmpty());
        assertEquals(1, this.errors.size());
        assertTrue(this.errors.getFirst().contains("constructor threw"), "the real cause must be reported, not a missing constructor: " + this.errors.getFirst());
        assertTrue(this.errors.getFirst().contains("the plugin API is null"), "the message of the failure must be kept: " + this.errors.getFirst());
    }

    @Test
    void aConstructorThrowingAnErrorIsContained() {
        assertFalse(this.registry().load(null, ThrowsError.class));
        assertTrue(this.registered.isEmpty());
        assertEquals(1, this.errors.size());
        assertTrue(this.errors.getFirst().contains("NoClassDefFoundError"), "the linkage failure must be named: " + this.errors.getFirst());
    }

    @Test
    void aMissingConstructorIsStillReportedAsSuch() {
        assertFalse(this.registry().load(null, NoUsableConstructor.class));
        assertTrue(this.registered.isEmpty());
        assertEquals(1, this.errors.size());
        assertTrue(this.errors.getFirst().contains("Could not find a valid constructor"), "a genuinely missing constructor keeps its own message: " + this.errors.getFirst());
    }
}
