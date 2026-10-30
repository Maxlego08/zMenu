package fr.maxlego08.menu.api.rules.loader;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.rules.Rule;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface RuleLoader extends Keyed {

    @NotNull String getType();

    @NotNull
    default List<@NotNull String> getAliases() {
        return Collections.emptyList();
    }

    /**
     * The key of this rule type in {@link fr.maxlego08.menu.api.registry.Registries#RULE}, read from
     * {@link #getType()} like a {@code type} written in a file.
     *
     * @return the key
     */
    @Override
    default @NotNull Key key() {
        return ZMenuKeys.parse(this.getType());
    }

    /**
     * {@link #getAliases()} read like the key.
     *
     * @return the other names, as keys
     */
    default @NotNull List<Key> aliases() {
        return this.getAliases().stream().map(ZMenuKeys::parse).toList();
    }

    @Nullable
    Rule load(@NotNull Map<String, Object> configuration);
}
