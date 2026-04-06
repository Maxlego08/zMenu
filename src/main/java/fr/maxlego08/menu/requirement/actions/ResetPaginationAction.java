package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.pagination.PaginationManager;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ResetPaginationAction extends Action {
    public enum ResetType {
        /**
         * Reset a specific pagination context by ID
         */
        CONTEXT,
        /**
         * Reset all pagination contexts for the player
         */
        ALL
    }

    private final PaginationManager paginationManager;
    private final ResetType resetType;
    private final List<@NotNull String> contextIds;

    public ResetPaginationAction(@NotNull PaginationManager paginationManager, @NotNull ResetType resetType, @Nullable List<@NotNull String> contextIds) {
        this.paginationManager = paginationManager;
        this.resetType = resetType;
        this.contextIds = contextIds != null ? new ArrayList<>(contextIds) : new ArrayList<>();
    }

    @Override
    public void execute(@NotNull Player player, @Nullable Button button, @NotNull InventoryEngine inventoryEngine, @NotNull Placeholders placeholders) {
        switch (this.resetType) {
            case CONTEXT -> {
                for (String contextId : this.contextIds) {
                    if (!contextId.isEmpty()) {
                        this.paginationManager.reset(player.getUniqueId(), inventoryEngine.getPlugin().parse(player, placeholders.parse(contextId)));
                    }
                }
            }
            case ALL -> this.paginationManager.removePlayerStates(player.getUniqueId());
        }
    }

    @NotNull
    public ResetType getResetType() {
        return this.resetType;
    }

    @NotNull
    public List<String> getContextIds() {
        return new ArrayList<>(this.contextIds);
    }

    @Nullable
    public String getContextId() {
        return this.contextIds.isEmpty() ? null : this.contextIds.getFirst();
    }

    @Override
    public String toString() {
        return "ResetPaginationAction{" +
                "resetType=" + this.resetType +
                ", contextIds=" + this.contextIds +
                '}';
    }
}

