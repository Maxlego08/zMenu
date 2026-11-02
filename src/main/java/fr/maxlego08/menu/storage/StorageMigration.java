package fr.maxlego08.menu.storage;

import fr.maxlego08.menu.api.storage.spi.StorageCapability;
import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import fr.maxlego08.menu.api.storage.spi.VariableMutation;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableScope;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Copies every zMenu data from one storage to another, for {@code /zm storage migrate}: the
 * variables of every scope, the saved inventories of this server, the visited worlds and the
 * played animations.
 *
 * <p>Values are written over the ones of the target with the same key; nothing is deleted. The
 * opened-inventory log is not copied. Runs on the calling thread and returns once everything is
 * stored in the target.</p>
 */
public final class StorageMigration {

    private static final int BATCH = 500;

    private final StorageProvider source;
    private final StorageProvider target;
    private final Consumer<String> progress;

    /**
     * @param progress receives a line of text after each step
     */
    public StorageMigration(@NotNull StorageProvider source, @NotNull StorageProvider target, @NotNull Consumer<String> progress) {
        this.source = source;
        this.target = target;
        this.progress = progress;
    }

    /**
     * @throws IllegalStateException when the source cannot list its content
     */
    public @NotNull Result run() {
        if (!this.source.capabilities().contains(StorageCapability.EXPORT)) {
            throw new IllegalStateException("The storage " + this.source.key().asString() + " cannot list its data, so it cannot be copied");
        }

        int variables = 0;
        for (VariableScope scope : VariableScope.values()) {
            int copied = this.copyScope(scope);
            variables += copied;
            this.progress.accept("Copied " + copied + " " + scope.id() + " variables.");
        }

        List<CompletableFuture<Void>> writes = new ArrayList<>();

        Map<UUID, String> inventories = this.source.savedInventories().loadAll().join();
        inventories.forEach((playerId, inventory) -> writes.add(this.target.savedInventories().save(playerId, inventory)));
        this.progress.accept("Copied " + inventories.size() + " saved inventories.");

        int worlds = 0;
        for (UUID playerId : this.source.worldVisits().players().join()) {
            for (String world : this.source.worldVisits().load(playerId).join()) {
                writes.add(this.target.worldVisits().markVisited(playerId, world));
                worlds++;
            }
        }
        this.progress.accept("Copied " + worlds + " visited worlds.");

        int animations = 0;
        for (UUID playerId : this.source.animations().players().join()) {
            for (Map.Entry<String, Long> entry : this.source.animations().load(playerId).join().entrySet()) {
                writes.add(this.target.animations().markPlayed(playerId, entry.getKey(), entry.getValue()));
                animations++;
            }
        }
        this.progress.accept("Copied " + animations + " played animations.");

        CompletableFuture.allOf(writes.toArray(new CompletableFuture[0])).join();
        return new Result(variables, inventories.size(), worlds, animations);
    }

    private int copyScope(VariableScope scope) {
        Set<String> owners = this.source.variables().owners(scope).join();
        List<VariableMutation> batch = new ArrayList<>(BATCH);
        int copied = 0;
        for (String owner : owners) {
            for (Variable variable : this.source.variables().loadOwner(scope, owner).join()) {
                batch.add(new VariableMutation.Set(variable));
                copied++;
                if (batch.size() >= BATCH) {
                    this.target.variables().apply(new ArrayList<>(batch)).join();
                    batch.clear();
                }
            }
        }
        if (!batch.isEmpty()) this.target.variables().apply(batch).join();
        return copied;
    }

    /**
     * How much was copied.
     */
    public record Result(int variables, int inventories, int worlds, int animations) {
    }
}
