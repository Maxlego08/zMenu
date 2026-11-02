package fr.maxlego08.menu.hooks.dialogs.canvas.pack;

import fr.maxlego08.menu.hooks.dialogs.canvas.ZCanvasBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Hides the white outline the client draws around a dialog canvas after a click on it.
 * <p>
 * The client draws that outline in code, not with a texture: {@code FocusableTextWidget} draws four 1 pixel
 * rectangles of opaque white around the body (4 pixels outside its text) through the {@code gui} pipeline.
 * This patches the client's own {@code core/gui.vsh} and {@code core/gui.fsh} so the fragment shader knows the
 * size of the rectangle it draws: each vertex passes its position and the square of its position
 * (interpolated), and its position again as a flat value, which every fragment of a triangle gets from the
 * same corner {@code k}. A triangle of an axis-aligned rectangle has two x values, {@code k} and another one
 * {@code o}; interpolating gives {@code p = k + t(o - k)} and {@code p² = k² + t(o² - k²)}, so
 * {@code o = (p² - k²) / (p - k) - k} and the width is {@code |o - k|}, whatever the order of the vertices
 * (the same for y). An opaque pure white pixel is then thrown away only when its rectangle is a 1 pixel line
 * as long as the outline of one of the canvases: the body width across ({@link #across(int)}),
 * {@code lines * 9 + 6} down. Other white of the client's screens stays, unless it is a line of exactly such
 * a length.
 * <p>
 * The shaders come from the client of the server's version, patched when the pack is generated, and replace
 * the same shaders of any other pack.
 */
public final class FocusOutlineShader {

    /**
     * Where the vertex shader goes in the pack.
     */
    public static final String VERTEX_PATH = "assets/minecraft/shaders/core/gui.vsh";

    /**
     * Where the fragment shader goes in the pack.
     */
    public static final String FRAGMENT_PATH = "assets/minecraft/shaders/core/gui.fsh";

    private static final int PADDING = 4;
    private static final int LINE_HEIGHT = 9;
    private static final int WIDTH_STEPS = 4;
    private static final int EXTRA_LINES = 3;
    private static final String CENTER = "512.0";

    private static final String VERTEX_OUT = "out vec4 vertexColor;";
    private static final String VERTEX_SET = "vertexColor = Color;";
    private static final String FRAGMENT_IN = "in vec4 vertexColor;";
    private static final String FRAGMENT_COLOR = "vec4 color = vertexColor;";

    private FocusOutlineShader() {
    }

    /**
     * @param width The width of a canvas.
     * @return The lengths the outline's lines across can have: the body width, which grows by steps when
     * something is drawn past the right edge (see {@link ZCanvasBuilder#bodyWidth(int, int)}).
     */
    public static @NotNull List<Integer> across(int width) {
        List<Integer> lengths = new ArrayList<>();
        for (int steps = 0; steps < WIDTH_STEPS; steps++) {
            int length = ZCanvasBuilder.bodyWidth(width, steps);
            if (!lengths.contains(length)) lengths.add(length);
        }
        return lengths;
    }

    /**
     * @param height The height of a canvas.
     * @return The lengths the outline's lines down can have: its lines of 9 pixels and the padding, minus the
     * two pixels of the lines across. The client counts the lines again at the width of the longest one,
     * so a line going past it would add an empty line at the bottom; the builder prevents it, and up to
     * {@value #EXTRA_LINES} such lines are still accepted in case another cause appears.
     */
    public static @NotNull List<Integer> down(int height) {
        int lines = (int) Math.ceil(height / (double) LINE_HEIGHT);
        List<Integer> lengths = new ArrayList<>();
        for (int extra = 0; extra <= EXTRA_LINES; extra++) lengths.add((lines + extra) * LINE_HEIGHT + 2 * PADDING - 2);
        return lengths;
    }

    /**
     * Patches the client's shaders.
     *
     * @param vertex   The client's {@code core/gui.vsh}.
     * @param fragment The client's {@code core/gui.fsh}.
     * @param canvases The sizes of the canvases, as {@code [width, height]}.
     * @param debug    True to paint instead of hiding: red for the lines that would be hidden, green for
     *                 other white 1 pixel lines, blue for other white rectangles.
     * @return The shaders by path in the pack, or an empty result when there is no canvas or the client's
     * shaders are not the ones this was written for.
     */
    public static @NotNull Optional<Map<String, String>> patch(@NotNull String vertex, @NotNull String fragment, @NotNull Collection<int[]> canvases,
                                                              boolean debug) {
        if (canvases.isEmpty() || !once(vertex, VERTEX_OUT) || !once(vertex, VERTEX_SET) || !once(fragment, FRAGMENT_IN) || !once(fragment, FRAGMENT_COLOR)
                || !fragment.contains("fragColor")) {
            return Optional.empty();
        }
        TreeSet<Integer> across = new TreeSet<>();
        TreeSet<Integer> down = new TreeSet<>();
        for (int[] canvas : canvases) {
            across.addAll(across(canvas[0]));
            down.addAll(down(canvas[1]));
        }

        String patchedVertex = vertex
                .replace(VERTEX_OUT, VERTEX_OUT + "\nnoperspective out vec2 zmenuPosition;\nnoperspective out vec2 zmenuSquare;\nflat out vec2 zmenuCorner;")
                .replace(VERTEX_SET, VERTEX_SET + "\n    zmenuPosition = Position.xy - vec2(" + CENTER + ");"
                        + "\n    zmenuSquare = zmenuPosition * zmenuPosition;"
                        + "\n    zmenuCorner = zmenuPosition;");

        String function = "\nnoperspective in vec2 zmenuPosition;\nnoperspective in vec2 zmenuSquare;\nflat in vec2 zmenuCorner;\n\n"
                + "const float ZMENU_ACROSS[" + across.size() + "] = float[](" + list(across) + ");\n"
                + "const float ZMENU_DOWN[" + down.size() + "] = float[](" + list(down) + ");\n\n"
                + "float zmenuExtent(float position, float square, float corner) {\n"
                + "    float delta = position - corner;\n"
                + "    if (abs(delta) < 1e-4) return -1.0;\n"
                + "    return abs((square - corner * corner) / delta - 2.0 * corner);\n"
                + "}\n\n"
                + "bool zmenuOutline(vec2 size) {\n"
                + "    if (size.y > 0.0 && size.y < 1.5) {\n"
                + "        for (int i = 0; i < " + across.size() + "; i++) if (abs(size.x - ZMENU_ACROSS[i]) < 0.75) return true;\n"
                + "    }\n"
                + "    if (size.x > 0.0 && size.x < 1.5) {\n"
                + "        for (int i = 0; i < " + down.size() + "; i++) if (abs(size.y - ZMENU_DOWN[i]) < 0.75) return true;\n"
                + "    }\n"
                + "    return false;\n"
                + "}";
        String check = "\n    vec2 zmenuSize = vec2(zmenuExtent(zmenuPosition.x, zmenuSquare.x, zmenuCorner.x),"
                + " zmenuExtent(zmenuPosition.y, zmenuSquare.y, zmenuCorner.y));"
                + (debug
                ? "\n    if (color == vec4(1.0)) {"
                + "\n        bool zmenuThin = (zmenuSize.x > 0.0 && zmenuSize.x < 1.5) || (zmenuSize.y > 0.0 && zmenuSize.y < 1.5);"
                + "\n        color = zmenuOutline(zmenuSize) ? vec4(1.0, 0.0, 0.0, 1.0) : zmenuThin ? vec4(0.0, 1.0, 0.0, 1.0) : vec4(0.0, 0.0, 1.0, 1.0);"
                + "\n    }"
                : "\n    if (color == vec4(1.0) && zmenuOutline(zmenuSize)) {\n        discard;\n    }");
        String patchedFragment = fragment
                .replace(FRAGMENT_IN, FRAGMENT_IN + function)
                .replace(FRAGMENT_COLOR, FRAGMENT_COLOR + check);

        Map<String, String> shaders = new TreeMap<>();
        shaders.put(VERTEX_PATH, patchedVertex);
        shaders.put(FRAGMENT_PATH, patchedFragment);
        return Optional.of(shaders);
    }

    private static boolean once(@NotNull String source, @NotNull String anchor) {
        int index = source.indexOf(anchor);
        return index >= 0 && source.indexOf(anchor, index + 1) < 0;
    }

    private static @NotNull String list(@NotNull TreeSet<Integer> values) {
        return values.stream().map(value -> String.format(Locale.ROOT, "%d.0", value)).collect(Collectors.joining(", "));
    }
}
