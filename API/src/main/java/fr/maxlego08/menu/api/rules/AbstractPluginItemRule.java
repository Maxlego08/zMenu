package fr.maxlego08.menu.api.rules;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    /**
     * Returns the type of the rule loader that builds this rule, written as the {@code type} of the serialized rule.
     * Not abstract, so rules from other plugins keep compiling; those simply cannot be serialized until they override it.
     *
     * @return the rule type
     * @throws UnsupportedOperationException If this rule does not say its type.
     */
    @NotNull
    protected String getType() {
        throw new UnsupportedOperationException("The rule " + this.getClass().getName() + " cannot be serialized: it does not override getType()");
    }

    @Override
    public boolean matches(@NotNull ItemRuleContext context) {
        ItemStack itemStack = context.getItemStack();
        if (itemStack == null) return false;

        String resolvedId = this.resolveId(itemStack);
        if (resolvedId == null) return false;

        String id = this.ignoreCase ? resolvedId.toLowerCase(Locale.ROOT) : resolvedId;

        if (this.itemIds.contains(id)) return true;

        for (Pattern pattern : this.patterns) {
            if (pattern.matcher(id).matches()) return true;
        }

        return false;
    }

    @Override
    public boolean isValid() {
        return !this.itemIds.isEmpty() || !this.patterns.isEmpty();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        List<String> items = new ArrayList<>(this.itemIds);
        for (Pattern pattern : this.patterns) {
            items.add(patternToWildcard(pattern));
        }

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", this.getType());
        map.put("items", items);
        if (!this.ignoreCase) map.put("ignore-case", false);
        return map;
    }

    private static Pattern wildcardToPattern(String wildcard, boolean ignoreCase) {
        String normalized = ignoreCase ? wildcard.toLowerCase(Locale.ROOT) : wildcard;
        String regex = normalized.replace(".", "\\.").replace("*", ".*");
        return Pattern.compile("^" + regex + "$");
    }

    /**
     * Reverses {@link #wildcardToPattern(String, boolean)}: strips the anchors, turns {@code .*} back into {@code *}
     * and {@code \.} back into {@code .}.
     */
    private static String patternToWildcard(Pattern pattern) {
        String regex = pattern.pattern();
        regex = regex.substring(1, regex.length() - 1);
        StringBuilder wildcard = new StringBuilder(regex.length());
        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            if (c == '\\' && i + 1 < regex.length() && regex.charAt(i + 1) == '.') {
                wildcard.append('.');
                i++;
            } else if (c == '.' && i + 1 < regex.length() && regex.charAt(i + 1) == '*') {
                wildcard.append('*');
                i++;
            } else {
                wildcard.append(c);
            }
        }
        return wildcard.toString();
    }
}