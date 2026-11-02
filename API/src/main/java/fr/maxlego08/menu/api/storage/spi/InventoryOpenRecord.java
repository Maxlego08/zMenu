package fr.maxlego08.menu.api.storage.spi;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * One inventory a player opened.
 *
 * @param playerId            the player
 * @param plugin              the plugin owning the inventory
 * @param inventory           the inventory file name
 * @param page                the page opened
 * @param previousInventories the file names of the inventories opened before it, oldest first
 */
@ApiStatus.Experimental
public record InventoryOpenRecord(@NotNull UUID playerId, @NotNull String plugin, @NotNull String inventory, int page,
                                  @NotNull List<String> previousInventories) {
}
