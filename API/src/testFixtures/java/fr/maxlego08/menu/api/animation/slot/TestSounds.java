package fr.maxlego08.menu.api.animation.slot;

import fr.maxlego08.menu.api.sound.SoundOption;

import java.lang.reflect.Proxy;

/**
 * A {@link SoundOption} stand-in for the compiler tests, which only ever carry it around.
 */
public final class TestSounds {

    public static final SoundOption SILENT = named("silent");

    public static SoundOption other() {
        return named("other");
    }

    public static SoundOption named(String name) {
        return (SoundOption) Proxy.newProxyInstance(
                TestSounds.class.getClassLoader(),
                new Class<?>[]{SoundOption.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "toString" -> name;
                    case "hashCode" -> name.hashCode();
                    case "equals" -> proxy == args[0];
                    default -> null;
                });
    }

    private TestSounds() {
    }
}
