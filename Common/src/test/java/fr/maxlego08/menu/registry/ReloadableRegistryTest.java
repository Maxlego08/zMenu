package fr.maxlego08.menu.registry;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.registry.KeyedRegistry;
import fr.maxlego08.menu.api.registry.RegistrationException;
import fr.maxlego08.menu.api.registry.RegistryType;
import fr.maxlego08.menu.api.registry.reload.ReloadReport;
import fr.maxlego08.menu.api.registry.reload.ReloadableRegistry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReloadableRegistryTest {

    record Menu(@NotNull Key key, @NotNull String opens) implements Keyed {
    }

    private static final RegistryType<Menu> MENUS = RegistryType.createReloadable(ZMenuKeys.key("test_menu"), Menu.class);

    private ZRegistryManager manager;
    private final AtomicReference<List<Menu>> files = new AtomicReference<>(List.of());

    private static Menu menu(String name, String opens) {
        return new Menu(ZMenuKeys.key(name), opens);
    }

    private ReloadableRegistry<Menu> menus() {
        return (ReloadableRegistry<Menu>) this.manager.registry(MENUS);
    }

    @BeforeEach
    void setUp() {
        this.manager = new ZRegistryManager();
        this.manager.create(MENUS);
        this.manager.provider(MENUS, "files", context -> this.files.get().forEach(context::register));
    }

    @Test
    void startsEmptyAtGenerationZero() {
        assertEquals(0, this.menus().generation());
        assertEquals(0, this.menus().size());
    }

    @Test
    void reloadPublishesTheSnapshotAndBumpsTheGeneration() {
        this.files.set(List.of(menu("a", ""), menu("b", "")));

        ReloadReport report = this.manager.reload(MENUS);

        assertTrue(report.applied());
        assertEquals(1, this.menus().generation());
        assertEquals(2, this.menus().size());
        assertEquals("files", this.menus().owner(ZMenuKeys.key("a")).orElseThrow());
    }

    @Test
    void aSecondReloadReplacesTheFirstSnapshot() {
        this.files.set(List.of(menu("a", "")));
        this.manager.reload(MENUS);
        this.files.set(List.of(menu("b", "")));
        this.manager.reload(MENUS);

        assertFalse(this.menus().contains(ZMenuKeys.key("a")));
        assertTrue(this.menus().contains(ZMenuKeys.key("b")));
        assertEquals(2, this.menus().generation());
    }

    @Test
    void anErrorRejectsTheSnapshotAndKeepsThePreviousOne() {
        this.files.set(List.of(menu("a", "")));
        this.manager.reload(MENUS);
        this.files.set(List.of(menu("b", ""), menu("b", "")));

        ReloadReport report = this.manager.reload(MENUS);

        assertFalse(report.applied());
        assertTrue(report.hasErrors());
        assertEquals(ZMenuKeys.key("b"), report.errors().get(0).subject());
        assertTrue(this.menus().contains(ZMenuKeys.key("a")));
        assertFalse(this.menus().contains(ZMenuKeys.key("b")));
        assertEquals(1, this.menus().generation());
    }

    @Test
    void aThrowingProviderRejectsTheSnapshot() {
        this.manager.provider(MENUS, "broken", context -> {
            throw new IllegalStateException("bad yaml");
        });
        this.files.set(List.of(menu("a", "")));

        ReloadReport report = this.manager.reload(MENUS);

        assertFalse(report.applied());
        assertTrue(report.format().stream().anyMatch(line -> line.contains("broken") && line.contains("bad yaml")));
        assertEquals(0, this.menus().size());
    }

    @Test
    void warningsDoNotRejectTheSnapshot() {
        this.manager.provider(MENUS, "lint", context -> context.warn("looks odd"));
        this.files.set(List.of(menu("a", "")));

        ReloadReport report = this.manager.reload(MENUS);

        assertTrue(report.applied());
        assertEquals(1, report.warnings().size());
        assertTrue(this.menus().contains(ZMenuKeys.key("a")));
    }

    @Test
    void aValidatorSeesTheWholeNewSnapshotAndCanRejectIt() {
        this.manager.validator(MENUS, "links", (snapshot, registries, reporter) -> {
            for (Menu menu : snapshot) {
                if (!menu.opens().isEmpty() && !registries.registry(MENUS).contains(ZMenuKeys.key(menu.opens()))) {
                    reporter.error(menu.key(), "opens " + menu.opens() + ", which does not exist");
                }
            }
        });

        this.files.set(List.of(menu("a", "b"), menu("b", "")));
        assertTrue(this.manager.reload(MENUS).applied());

        this.files.set(List.of(menu("a", "missing")));
        ReloadReport report = this.manager.reload(MENUS);

        assertFalse(report.applied());
        assertTrue(this.menus().contains(ZMenuKeys.key("b")), "the previous snapshot stays live");
    }

    @Test
    void providersSeeTheSnapshotUnderConstruction() {
        List<Integer> seen = new ArrayList<>();
        this.manager.provider(MENUS, "later", context -> seen.add(context.registries().registry(MENUS).size()));
        this.files.set(List.of(menu("a", ""), menu("b", "")));

        this.manager.reload(MENUS);

        assertEquals(List.of(2), seen);
    }

    @Test
    void readersKeepAConsistentSnapshotAcrossAReload() {
        this.files.set(List.of(menu("a", "")));
        this.manager.reload(MENUS);
        List<Menu> held = this.menus().values();

        this.files.set(List.of(menu("b", "")));
        this.manager.reload(MENUS);

        assertEquals(1, held.size());
        assertEquals(ZMenuKeys.key("a"), held.get(0).key());
    }

    @Test
    void reloadAllReloadsOnlyReloadableRegistries() {
        this.files.set(List.of(menu("a", "")));

        List<ReloadReport> reports = this.manager.reloadAll();
        List<RegistryType<?>> reloaded = reports.stream().<RegistryType<?>>map(ReloadReport::type).toList();
        long reloadable = this.manager.types().stream().filter(RegistryType::isReloadable).count();

        assertEquals(reloadable, reports.size(), "one report per reloadable registry");
        assertTrue(reloaded.contains(MENUS));
        assertFalse(reloaded.contains(fr.maxlego08.menu.api.registry.Registries.SLOT_ANIMATION), "a static registry is not reloaded");
    }

    @Test
    void staticAndReloadableRegistriesAreNotInterchangeable() {
        assertThrows(IllegalArgumentException.class, () -> this.manager.writable(MENUS));
        assertThrows(IllegalArgumentException.class, () -> this.manager.reload(fr.maxlego08.menu.api.registry.Registries.SLOT_ANIMATION));
        assertThrows(IllegalArgumentException.class, () -> this.manager.provider(fr.maxlego08.menu.api.registry.Registries.SLOT_ANIMATION, "x", context -> {
        }));
    }

    @Test
    void freezingStaticRegistriesLeavesReloadableOnesReloadable() {
        this.manager.freezeStatic();
        this.files.set(List.of(menu("a", "")));

        assertTrue(this.manager.reload(MENUS).applied());
    }

    @Test
    void aProviderCanKeepThePreviousEntryOfABrokenFile() {
        this.files.set(List.of(menu("a", "v1"), menu("b", "")));
        this.manager.reload(MENUS);

        ZRegistryManager manager = new ZRegistryManager();
        manager.create(MENUS);
        AtomicReference<Boolean> broken = new AtomicReference<>(false);
        manager.provider(MENUS, "files", context -> {
            context.register(menu("b", ""));
            if (broken.get()) {
                context.warn(ZMenuKeys.key("a"), "cannot read a, previous version kept");
                context.previous().get(ZMenuKeys.key("a")).ifPresent(context::register);
            } else {
                context.register(menu("a", "v1"));
            }
        });
        manager.reload(MENUS);
        Menu first = manager.registry(MENUS).getOrThrow(ZMenuKeys.key("a"));

        broken.set(true);
        ReloadReport report = manager.reload(MENUS);

        assertTrue(report.applied(), "a warning does not reject the snapshot");
        assertEquals(1, report.warnings().size());
        assertTrue(first == manager.registry(MENUS).getOrThrow(ZMenuKeys.key("a")), "the very same entry is kept");
    }

    @Test
    void onPublishRunsOnlyWhenTheSnapshotIsPublished() {
        List<String> fired = new ArrayList<>();
        this.manager.provider(MENUS, "events", context -> {
            context.onPublish(() -> fired.add("loaded"));
            if (this.files.get().isEmpty()) context.error("nothing to load");
        });

        this.files.set(List.of());
        this.manager.reload(MENUS);
        assertEquals(List.of(), fired);

        this.files.set(List.of(menu("a", "")));
        this.manager.reload(MENUS);
        assertEquals(List.of("loaded"), fired);
    }

    @Test
    void updateChangesTheLiveSnapshotWithoutRunningProviders() {
        this.files.set(List.of(menu("a", ""), menu("b", "")));
        this.manager.reload(MENUS);
        this.files.set(List.of());

        ReloadReport report = this.manager.update(MENUS, "single", editor -> {
            editor.remove(ZMenuKeys.key("a"));
            editor.register(menu("c", ""));
        });

        assertTrue(report.applied());
        assertEquals(2, this.menus().generation());
        assertFalse(this.menus().contains(ZMenuKeys.key("a")));
        assertTrue(this.menus().contains(ZMenuKeys.key("b")));
        assertEquals("single", this.menus().owner(ZMenuKeys.key("c")).orElseThrow());
    }

    @Test
    void aRejectedUpdateLeavesTheLiveSnapshotUntouched() {
        this.files.set(List.of(menu("a", "")));
        this.manager.reload(MENUS);

        ReloadReport report = this.manager.update(MENUS, "single", editor -> {
            editor.remove(ZMenuKeys.key("a"));
            editor.register(menu("b", ""));
            editor.register(menu("b", ""));
        });

        assertFalse(report.applied());
        assertTrue(this.menus().contains(ZMenuKeys.key("a")));
        assertFalse(this.menus().contains(ZMenuKeys.key("b")));
    }

    @Test
    void creatingTheSameRegistryTwiceFails() {
        assertThrows(RegistrationException.class, () -> this.manager.create(MENUS));
    }
}
