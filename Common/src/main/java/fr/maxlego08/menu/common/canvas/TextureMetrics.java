package fr.maxlego08.menu.common.canvas;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Measures and edits textures the way the client reads them for bitmap fonts.
 */
public final class TextureMetrics {

    private TextureMetrics() {
    }

    /**
     * Finds the width the client uses for a bitmap cell: up to its rightmost column holding a pixel that
     * is not fully transparent.
     *
     * @param image  The texture.
     * @param x      The left edge of the cell.
     * @param y      The top edge of the cell.
     * @param width  The width of the cell.
     * @param height The height of the cell.
     * @return The ink width, 0 for an empty cell.
     */
    public static int inkWidth(@NotNull BufferedImage image, int x, int y, int width, int height) {
        if (!image.getColorModel().hasAlpha()) return width;
        for (int column = width - 1; column >= 0; column--) {
            for (int row = 0; row < height; row++) {
                int px = x + column;
                int py = y + row;
                if (px >= image.getWidth() || py >= image.getHeight()) continue;
                if ((image.getRGB(px, py) >>> 24) != 0) return column + 1;
            }
        }
        return 0;
    }

    /**
     * Copies a part of a texture into a new image with an alpha channel.
     *
     * @param image  The texture.
     * @param x      The left edge.
     * @param y      The top edge.
     * @param width  The width.
     * @param height The height, which may go past the texture: the rest is transparent.
     * @return The copy.
     */
    public static @NotNull BufferedImage copy(@NotNull BufferedImage image, int x, int y, int width, int height) {
        BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                int px = x + column;
                int py = y + row;
                if (px < image.getWidth() && py < image.getHeight()) copy.setRGB(column, row, image.getRGB(px, py));
            }
        }
        return copy;
    }

    /**
     * Makes the client measure an image by its full width, by giving its top-right pixel the smallest
     * alpha that is not fully transparent when it is fully transparent. The client does not draw a pixel
     * that faint.
     *
     * @param image The image, changed in place.
     */
    public static void markFullWidth(@NotNull BufferedImage image) {
        int x = image.getWidth() - 1;
        int argb = image.getRGB(x, 0);
        if ((argb >>> 24) == 0) image.setRGB(x, 0, 0x01000000);
    }

    /**
     * @param bytes The bytes of a PNG.
     * @return The image, or null if the bytes are not an image.
     */
    public static @Nullable BufferedImage read(byte @NotNull [] bytes) {
        try {
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException exception) {
            return null;
        }
    }

    /**
     * @param image The image.
     * @return The image as PNG bytes.
     */
    public static byte @NotNull [] png(@NotNull BufferedImage image) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, "png", output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot write a PNG in memory", exception);
        }
    }
}
