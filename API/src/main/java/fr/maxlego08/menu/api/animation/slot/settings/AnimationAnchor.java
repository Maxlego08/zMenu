package fr.maxlego08.menu.api.animation.slot.settings;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

/**
 * A point in time inside an animation, written in whichever unit suits the author.
 *
 * <table>
 *   <caption>Accepted forms</caption>
 *   <tr><td>{@code 250}</td><td>250 milliseconds after the animation started</td></tr>
 *   <tr><td>{@code group:2} or {@code group:2.start}</td><td>the moment group 2 starts</td></tr>
 *   <tr><td>{@code group:2.end}</td><td>the moment group 2 is over</td></tr>
 *   <tr><td>{@code start}</td><td>the beginning of the animation</td></tr>
 *   <tr><td>{@code end}</td><td>the end of the last group</td></tr>
 * </table>
 *
 * <p>An anchor is parsed when the inventory is loaded, and resolved to a time when the animation is
 * compiled, because the boundaries of the groups are only known then.</p>
 *
 * @param type   the kind of anchor
 * @param millis the time, only used by {@link Type#MILLIS}
 * @param group  the group, only used by {@link Type#GROUP_START} and {@link Type#GROUP_END}
 */
public record AnimationAnchor(@NotNull Type type, long millis, int group) {

    public enum Type {START, END, MILLIS, GROUP_START, GROUP_END}

    /**
     * The beginning of the animation.
     */
    public static final AnimationAnchor START = new AnimationAnchor(Type.START, 0, 0);

    /**
     * The end of the last group.
     */
    public static final AnimationAnchor END = new AnimationAnchor(Type.END, 0, 0);

    /**
     * An anchor at a fixed time.
     *
     * @param millis the time in milliseconds
     * @return the anchor
     */
    @Contract(pure = true)
    @NotNull
    public static AnimationAnchor at(long millis) {
        return new AnimationAnchor(Type.MILLIS, Math.max(0, millis), 0);
    }

    /**
     * An anchor on the start of a group.
     *
     * @param group the group number
     * @return the anchor
     */
    @Contract(pure = true)
    @NotNull
    public static AnimationAnchor groupStart(int group) {
        return new AnimationAnchor(Type.GROUP_START, 0, group);
    }

    /**
     * An anchor on the end of a group.
     *
     * @param group the group number
     * @return the anchor
     */
    @Contract(pure = true)
    @NotNull
    public static AnimationAnchor groupEnd(int group) {
        return new AnimationAnchor(Type.GROUP_END, 0, group);
    }

    /**
     * Gets the compact form of this anchor, the way {@link #parseValue(Object)} reads it back.
     *
     * @return a number of milliseconds, or the text of the anchor
     */
    @Contract(pure = true)
    @NotNull
    public Object serialize() {
        return switch (this.type) {
            case START -> "start";
            case END -> "end";
            case MILLIS -> this.millis;
            case GROUP_START -> "group:" + this.group;
            case GROUP_END -> "group:" + this.group + ".end";
        };
    }

    /**
     * Parses an anchor written in a configuration file, in either of its two forms.
     *
     * <p>The compact form is a single value, parsed by {@link #parse(String)}. The explicit form is a
     * section, given here as its values:</p>
     *
     * <pre>
     * show:
     *   group: 2
     *   at: end      # start by default
     *
     * show:
     *   at: 250      # anything the compact form accepts
     * </pre>
     *
     * @param value the value to parse: a section as a map, or anything else for the compact form
     * @return the anchor, or null if the value is not a valid anchor
     */
    @Contract(pure = true)
    @Nullable
    public static AnimationAnchor parseValue(@Nullable Object value) {
        if (value == null) return null;

        if (value instanceof Map<?, ?> map) {
            Object group = map.get("group");
            Object at = map.get("at");

            if (group != null) {
                int groupNumber;
                try {
                    groupNumber = group instanceof Number number ? number.intValue() : Integer.parseInt(group.toString().trim());
                } catch (NumberFormatException exception) {
                    return null;
                }

                if (at == null) return groupStart(groupNumber);
                String boundary = at.toString().trim().toLowerCase(Locale.ROOT);
                if (boundary.equals("start")) return groupStart(groupNumber);
                if (boundary.equals("end")) return groupEnd(groupNumber);
                return null;
            }

            return at == null ? null : parse(at.toString());
        }

        return parse(value.toString());
    }

    /**
     * Parses an anchor written in its compact form.
     *
     * @param value the value to parse
     * @return the anchor, or null if the value is not a valid anchor
     */
    @Contract(pure = true)
    @Nullable
    public static AnimationAnchor parse(@Nullable String value) {
        if (value == null) return null;

        String trimmed = value.trim().toLowerCase(Locale.ROOT);
        if (trimmed.isEmpty()) return null;

        if (trimmed.equals("start")) return START;
        if (trimmed.equals("end")) return END;

        if (trimmed.startsWith("group:")) {
            String rest = trimmed.substring("group:".length());

            boolean end = false;
            if (rest.endsWith(".end")) {
                end = true;
                rest = rest.substring(0, rest.length() - ".end".length());
            } else if (rest.endsWith(".start")) {
                rest = rest.substring(0, rest.length() - ".start".length());
            }

            try {
                int group = Integer.parseInt(rest.trim());
                return end ? groupEnd(group) : groupStart(group);
            } catch (NumberFormatException exception) {
                return null;
            }
        }

        try {
            return at(Long.parseLong(trimmed));
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
