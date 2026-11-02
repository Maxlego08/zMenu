package fr.maxlego08.menu.hooks.dialogs.canvas.pack;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FocusOutlineShaderTest {

    private static final String GUI_26_2 = """
            #version 330

            layout(std140) uniform DynamicTransforms {
                mat4 ModelViewMat;
                vec4 ColorModulator;
                vec3 ModelOffset;
                mat4 TextureMat;
            };

            in vec4 vertexColor;

            out vec4 fragColor;

            void main() {
                vec4 color = vertexColor;
                if (color.a == 0.0) {
                    discard;
                }
                fragColor = color * ColorModulator;
            }
            """;

    private static final String VERTEX_26_2 = """
            #version 330

            in vec3 Position;
            in vec4 Color;

            out vec4 vertexColor;

            void main() {
                gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

                vertexColor = Color;
            }
            """;

    @Test
    public void theOutlineIsTheBodyWidthAcrossAndLinesTimes9Plus6Down() {
        assertEquals(List.of(270, 286, 302, 318), FocusOutlineShader.across(260));
        assertEquals(List.of(1024), FocusOutlineShader.across(1014));
        assertEquals(List.of(132, 141, 150, 159), FocusOutlineShader.down(120));
        assertEquals(List.of(150, 159, 168, 177), FocusOutlineShader.down(140));
    }

    @Test
    public void onlyWhiteLinesOfTheCanvasOutlinesAreThrownAway() {
        Map<String, String> shaders = FocusOutlineShader.patch(VERTEX_26_2, GUI_26_2, List.of(new int[]{240, 120}, new int[]{260, 140}), false)
                .orElseThrow();
        String vertex = shaders.get(FocusOutlineShader.VERTEX_PATH);
        String fragment = shaders.get(FocusOutlineShader.FRAGMENT_PATH);

        assertTrue(vertex.contains("flat out vec2 zmenuCorner;"), vertex);
        assertTrue(vertex.contains("zmenuSquare = zmenuPosition * zmenuPosition;"), vertex);
        assertFalse(vertex.contains("gl_VertexID"), vertex);
        assertTrue(fragment.contains("const float ZMENU_ACROSS[8] = float[](250.0, 266.0, 270.0, 282.0, 286.0, 298.0, 302.0, 318.0);"), fragment);
        assertTrue(fragment.contains("const float ZMENU_DOWN[6] = float[](132.0, 141.0, 150.0, 159.0, 168.0, 177.0);"), fragment);
        assertFalse(fragment.contains("dFdx"), fragment);
        int check = fragment.indexOf("if (color == vec4(1.0) && zmenuOutline(zmenuSize))");
        assertTrue(check > fragment.indexOf("vec4 color = vertexColor;") && check < fragment.indexOf("if (color.a == 0.0)"), fragment);
    }

    /**
     * The extent the fragment shader finds, computed with 32 bit floats like the GPU: the corner {@code k}
     * comes flat, position and square are interpolated at {@code t}.
     */
    private static float extent(float k, float other, float t) {
        float position = k + t * (other - k);
        float square = k * k + t * (other * other - k * k);
        float delta = position - k;
        return Math.abs((square - k * k) / delta - 2.0f * k);
    }

    @Test
    public void theExtentOfARectangleIsFoundFromAnyCornerWithFloats() {
        for (float x : new float[]{-512f, -100f, 0f, 37f, 300f, 700f, 1400f}) {
            float k = x - 512f;
            for (float t : new float[]{0.0625f, 0.125f, 0.25f, 0.5f, 0.875f, 0.9375f}) {
                assertEquals(1f, extent(k, k + 1f, t), 0.3f, "a 1 pixel line at x " + x + ", t " + t);
                assertEquals(1f, extent(k + 1f, k, t), 0.3f, "the same line from its other corner");
                assertEquals(270f, extent(k, k + 270f, t), 0.5f, "a 270 pixel line at x " + x + ", t " + t);
            }
        }
    }

    @Test
    public void theDebugShaderPaintsInsteadOfHiding() {
        String fragment = FocusOutlineShader.patch(VERTEX_26_2, GUI_26_2, List.<int[]>of(new int[]{260, 140}), true).orElseThrow()
                .get(FocusOutlineShader.FRAGMENT_PATH);
        assertTrue(fragment.contains("vec4(1.0, 0.0, 0.0, 1.0)"), fragment);
        assertFalse(fragment.contains("zmenuOutline(zmenuSize)) {\n        discard;"), fragment);
    }

    @Test
    public void otherShadersOrNoCanvasLeaveTheClientAlone() {
        assertTrue(FocusOutlineShader.patch(VERTEX_26_2, "void main() { fragColor = vec4(1.0); }", List.<int[]>of(new int[]{1, 1}), false).isEmpty());
        assertTrue(FocusOutlineShader.patch(VERTEX_26_2, GUI_26_2, List.of(), false).isEmpty());
    }
}
