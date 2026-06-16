package fr.maxlego08.menu.api.rules.loader;

import fr.maxlego08.menu.api.rules.Rule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface RuleLoader {

    @NotNull String getType();

    @NotNull
    default List<@NotNull String> getAliases() {
        return Collections.emptyList();
    }

    @Nullable
    Rule load(@NotNull Map<String, Object> configuration);
}
