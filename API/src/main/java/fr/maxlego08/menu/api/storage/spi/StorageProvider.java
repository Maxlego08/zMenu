package fr.maxlego08.menu.api.storage.spi;

import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * A place where zMenu keeps its data: a SQL database, an in-memory store, or a backend an addon
 * adds.
 *
 * <p>Providers are registered in {@link fr.maxlego08.menu.api.registry.Registries#STORAGE_PROVIDER}
 * and chosen by their key with {@code storage-type} in {@code config.yml}. An addon registers its
 * provider in {@code onLoad}, since zMenu opens its storage while it enables.</p>
 *
 * <p>Every store method returns at once and does its work in the background. Writes are applied
 * in the order they are called, and the future of a write completes once that write and every
 * write called before it are stored.</p>
 */
@ApiStatus.Experimental
public interface StorageProvider extends Keyed {

    /**
     * @return what this provider can do
     */
    @NotNull
    Set<StorageCapability> capabilities();

    /**
     * Connects to the backend and prepares it (tables, indexes...). Called once, when zMenu
     * enables.
     *
     * @param context what the provider needs from zMenu
     * @return completes when the stores can be used, or fails if the backend cannot be reached
     */
    @NotNull
    CompletableFuture<Void> open(@NotNull StorageContext context);

    /**
     * Stores what is still queued and releases the backend. Called once, when zMenu disables.
     *
     * @return completes when every queued write is stored and the backend is released
     */
    @NotNull
    CompletableFuture<Void> close();

    @NotNull
    PlayerDataStore playerData();

    @NotNull
    SavedInventoryStore savedInventories();

    @NotNull
    WorldVisitStore worldVisits();

    @NotNull
    AnimationStore animations();

    /**
     * @return the log of opened inventories, or empty when this provider does not keep one
     */
    @NotNull
    Optional<InventoryLogStore> inventoryLogs();
}
