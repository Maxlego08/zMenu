package fr.maxlego08.menu.pagination;

import fr.maxlego08.menu.api.pagination.PaginationManager;
import fr.maxlego08.menu.api.pagination.PaginationState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ZPaginationManager implements PaginationManager {
    private final Map<UUID, Map<String, PaginationState>> paginationStates = new ConcurrentHashMap<>();

    @Override
    public @NotNull PaginationState getOrCreateState(@NotNull UUID playerId, @NotNull String contextId) {
        return paginationStates
                .computeIfAbsent(playerId, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(contextId, k -> new PaginationState());
    }

    @Override
    public int getPage(@NotNull UUID playerId, @NotNull String contextId) {
        PaginationState state = getState(playerId, contextId);
        return state != null ? state.getCurrentPage() : 0;
    }

    @Override
    public void setPage(@NotNull UUID playerId, @NotNull String contextId, int page) {
        getOrCreateState(playerId, contextId).setCurrentPage(page);
    }

    @Override
    public void nextPage(@NotNull UUID playerId, @NotNull String contextId) {
        getOrCreateState(playerId, contextId).nextPage();
    }

    @Override
    public void previousPage(@NotNull UUID playerId, @NotNull String contextId) {
        getOrCreateState(playerId, contextId).previousPage();
    }

    @Override
    public void reset(@NotNull UUID playerId, @NotNull String contextId) {
        removeState(playerId, contextId);
    }

    @Override
    public @Nullable PaginationState getState(@NotNull UUID playerId, @NotNull String contextId) {
        Map<String, PaginationState> playerStates = paginationStates.get(playerId);
        return playerStates != null ? playerStates.get(contextId) : null;
    }

    @Override
    public void removeState(@NotNull UUID playerId, @NotNull String contextId) {
        Map<String, PaginationState> playerStates = paginationStates.get(playerId);
        if (playerStates != null) playerStates.remove(contextId);
    }

    @Override
    public void removePlayerStates(@NotNull UUID playerId) {
        paginationStates.remove(playerId);
    }

    @Override
    public void clearAll() {
        paginationStates.clear();
    }
}