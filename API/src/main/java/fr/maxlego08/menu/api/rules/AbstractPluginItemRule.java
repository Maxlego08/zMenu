package fr.maxlego08.menu.api.rules;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public abstract class AbstractPluginItemRule implements Rule {

    private final List<String> itemIds;
    private final List<Pattern> patterns;
    private final boolean ignoreCase;

    protected AbstractPluginItemRule(@NotNull List<String> itemIds, boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        this.itemIds = itemIds.stream()
                .filter(id -> !id.contains("*"))
                .map(id -> ignoreCase ? id.toLowerCase(Locale.ROOT) : id)
                .toList();
        this.patterns = itemIds.stream()
                .filter(id -> id.contains("*"))
                .map(id -> wildcardToPattern(id, ignoreCase))
                .toList();
    }

    /**
     * Resolves the plugin-specific string ID for the given ItemStack.
     *
     * @param itemStack the item to resolve
     * @return the plugin ID, or {@code null} if the item is not recognized by the plugin
     */
    @Nullable
    protected abstract String resolveId(@NotNull ItemStack itemStack);

    @Override
    public boolean matches(@NotNull ItemRuleContext context) {
        ItemStack itemStack = context.getItemStack();
        if (itemStack == null) return false;

        String resolvedId = resolveId(itemStack);
        if (resolvedId == null) return false;

        String id = ignoreCase ? resolvedId.toLowerCase(Locale.ROOT) : resolvedId;

        if (itemIds.contains(id)) return true;

        for (Pattern pattern : patterns) {
            if (pattern.matcher(id).matches()) return true;
        }

        return false;
    }

    @Override
    public boolean isValid() {
        return !this.itemIds.isEmpty() || !this.patterns.isEmpty();
    }

    private static Pattern wildcardToPattern(String wildcard, boolean ignoreCase) {
        String normalized = ignoreCase ? wildcard.toLowerCase(Locale.ROOT) : wildcard;
        String regex = normalized.replace(".", "\\.").replace("*", ".*");
        return Pattern.compile("^" + regex + "$");
    }
}