package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.pagination.PaginationManager;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.ResetPaginationAction;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ResetPaginationLoader extends ActionLoader {
    private final PaginationManager paginationManager;

    public ResetPaginationLoader(PaginationManager paginationManager) {
        super("reset-pagination");
        this.paginationManager = paginationManager;
    }

    @Override
    public @Nullable Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        String typeStr = accessor.getString("type", "context").toLowerCase();
        
        try {
            ResetPaginationAction.ResetType resetType = ResetPaginationAction.ResetType.valueOf(typeStr.toUpperCase());

            if (resetType == ResetPaginationAction.ResetType.CONTEXT) {
                List<String> contextIds = loadContextIds(accessor);
                if (contextIds.isEmpty()) {
                    return null;
                }
                return new ResetPaginationAction(this.paginationManager, resetType, contextIds);
            } else if (resetType == ResetPaginationAction.ResetType.ALL) {
                return new ResetPaginationAction(this.paginationManager, resetType, null);
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
        
        return null;
    }

    private List<String> loadContextIds(@NonNull TypedMapAccessor accessor) {
        List<String> contextIds = new ArrayList<>();
        
        List<String> contextIdList = accessor.getStringList("context-ids");
        if (!contextIdList.isEmpty()) {
            contextIds.addAll(contextIdList);
        }
        
        if (contextIds.isEmpty()) {
            String contextId = accessor.getString("context-id");
            if (contextId != null && !contextId.isEmpty()) {
                contextIds.add(contextId);
            }
        }
        
        return contextIds;
    }
}

