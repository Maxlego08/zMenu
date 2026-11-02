package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;

/**
 * The {@code visible} key of a canvas element: a value that gives {@code true}, {@code yes} or {@code 1}, or
 * a comparison of two values.
 *
 * <pre>
 * visible: "%canvas_badge%"                 # true, yes or 1
 * visible: "%canvas_tab% == shop"           # == and != compare text (or numbers when both sides are)
 * visible: "%player_level% >= 10"           # &gt;, &gt;=, &lt;, &lt;= compare numbers
 * visible: "%canvas_tags% has pvp"          # has / !has: a comma list holds the value
 * visible: "%canvas_search% contains sw"    # contains / !contains: a text holds another, any case
 * </pre>
 * The operator is found in the value as written, so a placeholder result holding {@code >} cannot change the
 * comparison.
 *
 * @param raw      The value as written.
 * @param left     The left side, or the whole value when there is no operator.
 * @param operator The operator, or null.
 * @param right    The right side, or null.
 */
public record VisibleCondition(@NotNull String raw, @NotNull String left, @Nullable String operator, @Nullable String right) {

    private static final List<String> OPERATORS = List.of("==", "!=", ">=", "<=", ">", "<", " !has ", " has ", " !contains ", " contains ");

    /**
     * @param raw The value as written.
     * @return The condition.
     */
    public static @NotNull VisibleCondition parse(@NotNull String raw) {
        int best = -1;
        String found = null;
        for (String operator : OPERATORS) {
            int index = raw.indexOf(operator);
            if (index > 0 && (best < 0 || index < best)) {
                best = index;
                found = operator;
            }
        }
        if (found == null) return new VisibleCondition(raw, raw.trim(), null, null);
        return new VisibleCondition(raw, raw.substring(0, best).trim(), found.trim(), raw.substring(best + found.length()).trim());
    }

    /**
     * @param context The player.
     * @return True if the element is drawn for the player.
     */
    public boolean test(@NotNull CanvasRenderContext context) {
        return this.test(text -> context.resolve(text).trim());
    }

    /**
     * @param resolver Resolves the placeholders of one side.
     * @return True if the condition holds.
     */
    public boolean test(@NotNull UnaryOperator<String> resolver) {
        String value = resolver.apply(this.left);
        if (this.operator == null || this.right == null) {
            return switch (value.toLowerCase(Locale.ROOT)) {
                case "true", "yes", "1" -> true;
                default -> false;
            };
        }
        String other = resolver.apply(this.right);
        Double leftNumber = number(value);
        Double rightNumber = number(other);
        boolean numbers = leftNumber != null && rightNumber != null;
        return switch (this.operator) {
            case "==" -> numbers ? leftNumber.doubleValue() == rightNumber.doubleValue() : value.equals(other);
            case "!=" -> numbers ? leftNumber.doubleValue() != rightNumber.doubleValue() : !value.equals(other);
            case ">=" -> numbers && leftNumber >= rightNumber;
            case "<=" -> numbers && leftNumber <= rightNumber;
            case ">" -> numbers && leftNumber > rightNumber;
            case "<" -> numbers && leftNumber < rightNumber;
            case "has" -> hasItem(value, other);
            case "!has" -> !hasItem(value, other);
            case "contains" -> value.toLowerCase(Locale.ROOT).contains(other.toLowerCase(Locale.ROOT));
            case "!contains" -> !value.toLowerCase(Locale.ROOT).contains(other.toLowerCase(Locale.ROOT));
            default -> false;
        };
    }

    /**
     * @param list A comma list, such as {@code a,b,c}.
     * @param item An item.
     * @return True if the list holds the item.
     */
    public static boolean hasItem(@NotNull String list, @NotNull String item) {
        for (String part : list.split(",")) {
            if (part.trim().equals(item)) return true;
        }
        return false;
    }

    private static @Nullable Double number(@NotNull String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    /**
     * @return True if the condition holds placeholders.
     */
    public boolean isPlaceholder() {
        return this.raw.indexOf('%') >= 0;
    }
}
