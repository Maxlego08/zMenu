package fr.maxlego08.menu.registry;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import fr.maxlego08.menu.api.animation.slot.order.SequentialSlotAnimation;
import fr.maxlego08.menu.api.loader.animation.slot.SlotAnimationLoader;
import fr.maxlego08.menu.api.registry.KeyedRegistry;
import fr.maxlego08.menu.api.registry.RegistrationException;
import fr.maxlego08.menu.api.registry.Registries;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SimpleRegistryTest {

    private static SlotAnimationLoader factory(String name) {
        return new SlotAnimationLoader(ZMenuKeys.key(name)) {
            @Override
            public @NotNull SlotAnimation create(@NotNull Map<String, Object> options) {
                return new SequentialSlotAnimation();
            }
        };
    }

    private static SimpleRegistry<SlotAnimationLoader> newRegistry() {
        return new SimpleRegistry<>(Registries.SLOT_ANIMATION);
    }

    @Test
    void parseDefaultsToZMenuNamespace() {
        assertEquals(Key.key("zmenu", "from_center"), ZMenuKeys.parse(" From-Center "));
        assertEquals(Key.key("myplugin", "spin"), ZMenuKeys.parse("MyPlugin:spin"));
    }

    @Test
    void fileKeyKeepsDashesAndReplacesWhatAKeyCannotHold() {
        assertEquals(Key.key("zmenu", "my_shop"), ZMenuKeys.fileKey("zMenu", "My Shop"));
        assertEquals(Key.key("zmenu", "my-shop"), ZMenuKeys.fileKey("zMenu", "my-shop"));
        assertEquals(Key.key("my_plugin", "caf_"), ZMenuKeys.fileKey("My Plugin", "café"));
    }

    @Test
    void inventoryReferencesDefaultToZMenuAndPlainNamesMatchAnyNamespace() {
        assertEquals(Key.key("zmenu", "shop"), ZMenuKeys.inventoryKey("Shop"));
        assertEquals(Key.key("myplugin", "my_shop"), ZMenuKeys.inventoryKey("MyPlugin:My Shop"));

        assertTrue(ZMenuKeys.matches(Key.key("myplugin", "shop"), "Shop"), "a plain name matches any plugin");
        assertTrue(ZMenuKeys.matches(Key.key("myplugin", "shop"), "myplugin:shop"));
        assertFalse(ZMenuKeys.matches(Key.key("myplugin", "shop"), "zmenu:shop"), "a full key must match exactly");
    }

    @Test
    void removeDropsTheEntryAndItsAliases() {
        SimpleRegistry<SlotAnimationLoader> registry = newRegistry();
        registry.register("zmenu", factory("sequential"));
        registry.alias("zmenu", ZMenuKeys.key("rows"), ZMenuKeys.key("sequential"));

        assertTrue(registry.remove(ZMenuKeys.key("sequential")));

        assertFalse(registry.contains(ZMenuKeys.key("rows")));
        assertTrue(registry.owner(ZMenuKeys.key("rows")).isEmpty());
        assertEquals(0, registry.size());
        assertFalse(registry.remove(ZMenuKeys.key("sequential")));
    }

    @Test
    void copyIsWritableAndIndependent() {
        SimpleRegistry<SlotAnimationLoader> registry = newRegistry();
        registry.register("zmenu", factory("spiral"));
        registry.freeze();

        SimpleRegistry<SlotAnimationLoader> copy = registry.copy();
        copy.register("zmenu", factory("wave"));

        assertTrue(copy.contains(ZMenuKeys.key("spiral")));
        assertFalse(registry.contains(ZMenuKeys.key("wave")));
        assertEquals("zmenu", copy.owner(ZMenuKeys.key("spiral")).orElseThrow());
    }

    @Test
    void registersAndReadsByKeyAndOwner() {
        SimpleRegistry<SlotAnimationLoader> registry = newRegistry();
        SlotAnimationLoader spiral = registry.register("zmenu", factory("spiral"));

        assertSame(spiral, registry.get(ZMenuKeys.key("spiral")).orElseThrow());
        assertEquals("zmenu", registry.owner(ZMenuKeys.key("spiral")).orElseThrow());
        assertTrue(registry.containsValue(spiral));
        assertEquals(1, registry.size());
    }

    @Test
    void duplicateKeyNamesBothOwners() {
        SimpleRegistry<SlotAnimationLoader> registry = newRegistry();
        registry.register("first", factory("spiral"));

        RegistrationException exception = assertThrows(RegistrationException.class, () -> registry.register("second", factory("spiral")));

        assertTrue(exception.getMessage().contains("first"));
        assertTrue(exception.getMessage().contains("second"));
    }

    @Test
    void aliasResolvesToTheEntryWithoutDuplicatingIt() {
        SimpleRegistry<SlotAnimationLoader> registry = newRegistry();
        SlotAnimationLoader sequential = registry.register("zmenu", factory("sequential"));
        registry.alias("zmenu", ZMenuKeys.key("rows"), ZMenuKeys.key("sequential"));

        assertSame(sequential, registry.get(ZMenuKeys.key("rows")).orElseThrow());
        assertEquals(1, registry.size());
        assertThrows(RegistrationException.class, () -> registry.register("other", factory("rows")));
        assertThrows(RegistrationException.class, () -> registry.alias("zmenu", ZMenuKeys.key("x"), ZMenuKeys.key("missing")));
    }

    @Test
    void frozenRegistryRejectsRegistrationButStillReads() {
        SimpleRegistry<SlotAnimationLoader> registry = newRegistry();
        registry.register("zmenu", factory("spiral"));
        registry.freeze();

        assertThrows(RegistrationException.class, () -> registry.register("late", factory("wave")));
        assertFalse(registry.contains(ZMenuKeys.key("wave")));
        assertTrue(registry.get(ZMenuKeys.key("spiral")).isPresent());
    }

    @Test
    void aNewManagerHasEmptyRegistriesUntilThePluginFillsThem() {
        KeyedRegistry<SlotAnimationLoader> registry = new ZRegistryManager().registry(Registries.SLOT_ANIMATION);

        assertEquals(0, registry.size());
    }
}
