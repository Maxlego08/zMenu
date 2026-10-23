package fr.maxlego08.menu.registry;

import fr.maxlego08.menu.api.registry.KeyedRegistry;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.registry.RegistrationException;
import fr.maxlego08.menu.api.registry.RegistryAccess;
import fr.maxlego08.menu.api.registry.RegistryManager;
import fr.maxlego08.menu.api.registry.RegistryType;
import fr.maxlego08.menu.api.registry.reload.ReloadReport;
import fr.maxlego08.menu.api.registry.reload.ReloadReporter;
import fr.maxlego08.menu.api.registry.reload.ReloadValidator;
import fr.maxlego08.menu.api.registry.reload.ReloadableProvider;
import fr.maxlego08.menu.api.registry.WritableRegistry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class ZRegistryManager implements RegistryManager {

    private final Map<Key, KeyedRegistry<?>> registries = new ConcurrentHashMap<>();
    private final Map<Key, RegistryType<?>> types = new ConcurrentHashMap<>();
    private final List<RegistryType<?>> creationOrder = new CopyOnWriteArrayList<>();
    private final Map<Key, List<Provider<?>>> providers = new LinkedHashMap<>();
    private final Map<Key, List<Validator<?>>> validators = new LinkedHashMap<>();

    public ZRegistryManager() {
        this.create(Registries.SLOT_ANIMATION);
        this.create(Registries.INVENTORY);
        this.create(Registries.ANIMATION);
        this.create(Registries.PERMISSIBLE);
        this.create(Registries.ACTION);
        this.create(Registries.BUTTON);
        this.create(Registries.RULE);
        this.create(Registries.ITEM_VERIFICATION);
        this.create(Registries.MATERIAL_LOADER);
        this.create(Registries.ITEM_COMPONENT);
        this.create(Registries.COMMAND_ARGUMENT_VALIDATOR);
    }

    /**
     * Creates an empty registry, static or reloadable depending on the type.
     *
     * @throws RegistrationException if a registry with this key exists
     */
    public synchronized <T extends Keyed> void create(@NotNull RegistryType<T> type) {
        if (this.types.containsKey(type.key())) {
            throw new RegistrationException("Registry " + type + " already exists");
        }
        this.registries.put(type.key(), type.isReloadable() ? new SnapshotRegistry<>(type) : new SimpleRegistry<>(type));
        this.types.put(type.key(), type);
        this.creationOrder.add(type);
    }

    /**
     * Stops every static registry from accepting entries. Done once the server has loaded.
     * Reloadable registries are not affected, they change only through {@link #reload}, and open
     * registries keep accepting entries.
     */
    public synchronized void freezeStatic() {
        for (RegistryType<?> type : this.creationOrder) {
            if (type.kind() == RegistryType.Kind.STATIC && this.registries.get(type.key()) instanceof SimpleRegistry<?> simple) {
                simple.freeze();
            }
        }
    }

    /**
     * Calls {@code listener} with every key and alias later registered in a static or open registry,
     * whichever way it is registered.
     *
     * @param type     the registry
     * @param listener the listener
     */
    public <T extends Keyed> void onRegister(@NotNull RegistryType<T> type, @NotNull Consumer<Key> listener) {
        this.simple(type).addListener(listener);
    }

    /**
     * Removes what an owner registered in every open registry, when the plugin disables.
     *
     * @param owner the name the entries were registered with
     * @return how many entries and aliases were removed
     */
    public synchronized int unregisterOpen(@NotNull String owner) {
        int removed = 0;
        for (RegistryType<?> type : this.creationOrder) {
            if (type.isOpen() && this.registries.get(type.key()) instanceof SimpleRegistry<?> simple) {
                removed += simple.unregisterAll(owner);
            }
        }
        return removed;
    }

    private void checkExists(RegistryType<?> type) {
        RegistryType<?> known = this.types.get(type.key());
        if (known == null) throw new IllegalArgumentException("No registry " + type);
        if (!known.equals(type)) throw new IllegalArgumentException("Registry " + type + " exists with a different definition: " + known);
    }

    @SuppressWarnings("unchecked")
    private <T extends Keyed> SimpleRegistry<T> simple(RegistryType<T> type) {
        this.checkExists(type);
        if (this.registries.get(type.key()) instanceof SimpleRegistry<?> simple) return (SimpleRegistry<T>) simple;
        throw new IllegalArgumentException(type + " is reloadable, contribute to it with provider(...)");
    }

    @SuppressWarnings("unchecked")
    private <T extends Keyed> SnapshotRegistry<T> snapshot(RegistryType<T> type) {
        this.checkExists(type);
        if (this.registries.get(type.key()) instanceof SnapshotRegistry<?> snapshot) return (SnapshotRegistry<T>) snapshot;
        throw new IllegalArgumentException(type + " is not a reloadable registry");
    }

    @Override
    public synchronized <T extends Keyed> @NotNull WritableRegistry<T> writable(@NotNull RegistryType<T> type) {
        return this.simple(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Keyed> @NotNull KeyedRegistry<T> registry(@NotNull RegistryType<T> type) {
        this.checkExists(type);
        return (KeyedRegistry<T>) this.registries.get(type.key());
    }

    @Override
    public @NotNull Optional<KeyedRegistry<?>> find(@NotNull Key registryKey) {
        return Optional.ofNullable(this.registries.get(registryKey));
    }

    @Override
    public @NotNull Collection<RegistryType<?>> types() {
        return List.copyOf(this.creationOrder);
    }

    @Override
    public synchronized <T extends Keyed> void provider(@NotNull RegistryType<T> type, @NotNull String owner, @NotNull ReloadableProvider<T> provider) {
        this.snapshot(type);
        this.providers.computeIfAbsent(type.key(), key -> new ArrayList<>()).add(new Provider<>(owner, provider));
    }

    @Override
    public synchronized <T extends Keyed> void validator(@NotNull RegistryType<T> type, @NotNull String owner, @NotNull ReloadValidator<T> validator) {
        this.snapshot(type);
        this.validators.computeIfAbsent(type.key(), key -> new ArrayList<>()).add(new Validator<>(owner, validator));
    }

    @Override
    public synchronized @NotNull ReloadReport reload(@NotNull RegistryType<?> type) {
        return this.reloadTyped(type);
    }

    @Override
    public synchronized @NotNull List<ReloadReport> reloadAll() {
        List<ReloadReport> reports = new ArrayList<>();
        for (RegistryType<?> type : this.creationOrder) {
            if (type.isReloadable()) reports.add(this.reloadTyped(type));
        }
        return reports;
    }

    @SuppressWarnings("unchecked")
    private <T extends Keyed> ReloadReport reloadTyped(RegistryType<T> type) {
        SnapshotRegistry<T> live = this.snapshot(type);
        Build<T> build = new Build<>(live, new SimpleRegistry<>(type));

        for (Provider<?> raw : this.providers.getOrDefault(type.key(), List.of())) {
            Provider<T> provider = (Provider<T>) raw;
            try {
                provider.provider.provide(build.context(provider.owner));
            } catch (RuntimeException exception) {
                build.issue(ReloadReport.Severity.ERROR, provider.owner, null, "provider failed: " + exception);
            }
        }
        return this.publish(type, build);
    }

    /**
     * Changes the live snapshot of a reloadable registry without running its providers: the
     * entries stay unless {@code editor} removes them. Validators still run, and an error keeps
     * the live snapshot unchanged.
     *
     * @param type   the reloadable registry
     * @param owner  the name shown in the report and given to new entries
     * @param editor what to change
     * @return what was found
     * @throws IllegalArgumentException if no such reloadable registry exists
     */
    public synchronized <T extends Keyed> @NotNull ReloadReport update(@NotNull RegistryType<T> type, @NotNull String owner, @NotNull Consumer<SnapshotEditor<T>> editor) {
        SnapshotRegistry<T> live = this.snapshot(type);
        Build<T> build = new Build<>(live, live.snapshot().copy());
        try {
            editor.accept(build.context(owner));
        } catch (RuntimeException exception) {
            build.issue(ReloadReport.Severity.ERROR, owner, null, "update failed: " + exception);
        }
        return this.publish(type, build);
    }

    @SuppressWarnings("unchecked")
    private <T extends Keyed> ReloadReport publish(RegistryType<T> type, Build<T> build) {
        for (Validator<?> raw : this.validators.getOrDefault(type.key(), List.of())) {
            Validator<T> validator = (Validator<T>) raw;
            ReloadReporter reporter = (severity, subject, message) -> build.issue(severity, validator.owner, subject, message);
            try {
                validator.validator.validate(build.next, build.view, reporter);
            } catch (RuntimeException exception) {
                build.issue(ReloadReport.Severity.ERROR, validator.owner, null, "validator failed: " + exception);
            }
        }

        boolean rejected = build.issues.stream().anyMatch(issue -> issue.severity() == ReloadReport.Severity.ERROR);
        if (!rejected) {
            build.live.swap(build.next);
            for (PublishAction action : build.onPublish) {
                try {
                    action.action.run();
                } catch (RuntimeException exception) {
                    build.issue(ReloadReport.Severity.WARNING, action.owner, null, "action after publishing failed: " + exception);
                }
            }
        }
        return new ReloadReport(type, List.copyOf(build.issues), !rejected, build.live.generation());
    }

    /**
     * @return these registries, where {@code type} reads from {@code snapshot} instead
     */
    private <T extends Keyed> RegistryAccess viewWith(RegistryType<T> type, SimpleRegistry<T> snapshot) {
        ZRegistryManager self = this;
        return new RegistryAccess() {
            @Override
            @SuppressWarnings("unchecked")
            public <U extends Keyed> @NotNull KeyedRegistry<U> registry(@NotNull RegistryType<U> requested) {
                return requested.equals(type) ? (KeyedRegistry<U>) snapshot : self.registry(requested);
            }

            @Override
            public @NotNull Optional<KeyedRegistry<?>> find(@NotNull Key registryKey) {
                return registryKey.equals(type.key()) ? Optional.of(snapshot) : self.find(registryKey);
            }

            @Override
            public @NotNull Collection<RegistryType<?>> types() {
                return self.types();
            }
        };
    }

    private record Provider<T extends Keyed>(String owner, ReloadableProvider<T> provider) {
    }

    private record Validator<T extends Keyed>(String owner, ReloadValidator<T> validator) {
    }

    private record PublishAction(String owner, Runnable action) {
    }

    /**
     * A snapshot being built, with what was reported and what to run once it is published.
     */
    private final class Build<T extends Keyed> {

        private final SnapshotRegistry<T> live;
        private final SimpleRegistry<T> next;
        private final RegistryAccess view;
        private final List<ReloadReport.Issue> issues = new ArrayList<>();
        private final List<PublishAction> onPublish = new ArrayList<>();

        private Build(SnapshotRegistry<T> live, SimpleRegistry<T> next) {
            this.live = live;
            this.next = next;
            this.view = ZRegistryManager.this.viewWith(live.type(), next);
        }

        private void issue(ReloadReport.Severity severity, String owner, @Nullable Key subject, String message) {
            this.issues.add(new ReloadReport.Issue(severity, owner, subject, message));
        }

        private SnapshotEditor<T> context(String owner) {
            Build<T> build = this;
            return new SnapshotEditor<>() {
                @Override
                public @NotNull String owner() {
                    return owner;
                }

                @Override
                public @NotNull RegistryAccess registries() {
                    return build.view;
                }

                @Override
                public <V extends T> @NotNull V register(@NotNull V value) {
                    try {
                        return build.next.register(owner, value);
                    } catch (RegistrationException exception) {
                        this.report(ReloadReport.Severity.ERROR, value.key(), exception.getMessage());
                        return value;
                    }
                }

                @Override
                public boolean remove(@NotNull Key key) {
                    return build.next.unregister(key);
                }

                @Override
                public @NotNull KeyedRegistry<T> previous() {
                    return build.live;
                }

                @Override
                public void onPublish(@NotNull Runnable action) {
                    build.onPublish.add(new PublishAction(owner, action));
                }

                @Override
                public void report(@NotNull ReloadReport.Severity severity, @Nullable Key subject, @NotNull String message) {
                    build.issue(severity, owner, subject, message);
                }
            };
        }
    }
}
