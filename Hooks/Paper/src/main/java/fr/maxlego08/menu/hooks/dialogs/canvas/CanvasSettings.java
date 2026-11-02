package fr.maxlego08.menu.hooks.dialogs.canvas;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * The {@code dialog-canvas} section of {@code config.yml}.
 *
 * @param target                The pack target: {@code auto}, or the name of a target.
 * @param autoRebuild           True to run the pack plugin's rebuild command when zMenu's files changed.
 * @param requirePack           When to show the fallback: {@code auto}, {@code true} or {@code false}.
 * @param downloadVanillaAssets True to download the fonts and textures of the vanilla client.
 * @param clickLifetimeSeconds  How long the click areas of an opened canvas keep working.
 * @param standaloneUrl         Where the server owner uploads {@code zmenu-pack.zip}, empty when unused.
 * @param standaloneRequired    True to kick players who decline the standalone pack.
 * @param standalonePrompt      The MiniMessage text shown when the standalone pack is offered.
 * @param focusOutline          Whether a shader in the pack hides the white outline a click draws.
 */
public record CanvasSettings(@NotNull String target, boolean autoRebuild, @NotNull RequirePack requirePack, boolean downloadVanillaAssets,
                             long clickLifetimeSeconds, @NotNull String standaloneUrl, boolean standaloneRequired, @NotNull String standalonePrompt,
                             @NotNull FocusOutline focusOutline) {

    public static final String AUTO = "auto";

    /**
     * {@code dialog-canvas.hide-focus-outline}.
     */
    public enum FocusOutline {
        /**
         * The client draws its outline.
         */
        OFF,
        /**
         * A shader hides the outline of canvases.
         */
        ON,
        /**
         * A shader paints what it would hide in red, other white 1 pixel lines in green and other white
         * rectangles in blue, to see why an outline stays.
         */
        DEBUG;

        static @NotNull FocusOutline read(@NotNull ConfigurationSection configuration) {
            return switch (String.valueOf(configuration.get("dialog-canvas.hide-focus-outline", false)).trim().toLowerCase(Locale.ROOT)) {
                case "true" -> ON;
                case "debug" -> DEBUG;
                default -> OFF;
            };
        }
    }

    /**
     * When a player gets the fallback of a canvas instead of the canvas.
     */
    public enum RequirePack {
        /**
         * zMenu sends the pack itself ({@code standalone}): fallback until the pack is reported loaded. A pack
         * plugin, or {@code server.properties}, sends its pack during the configuration phase, where Bukkit
         * reports nothing: fallback only after a pack failure was reported.
         */
        AUTO,
        /**
         * Fallback until a pack is reported loaded.
         */
        TRUE,
        /**
         * Never the fallback.
         */
        FALSE;

        /**
         * @param loaded      True if a pack was reported loaded, false if one was declined or failed, null when
         *                    nothing was reported.
         * @param sentByZMenu True if zMenu sends the pack itself ({@code standalone}).
         * @return True if the player is shown the canvas.
         */
        public boolean showsCanvas(@Nullable Boolean loaded, boolean sentByZMenu) {
            return switch (this) {
                case FALSE -> true;
                case TRUE -> Boolean.TRUE.equals(loaded);
                case AUTO -> sentByZMenu ? Boolean.TRUE.equals(loaded) : !Boolean.FALSE.equals(loaded);
            };
        }

        static @NotNull RequirePack read(@NotNull ConfigurationSection configuration) {
            Object value = configuration.get("dialog-canvas.require-pack", "auto");
            String text = String.valueOf(value).trim().toUpperCase(Locale.ROOT);
            try {
                return valueOf(text);
            } catch (IllegalArgumentException exception) {
                return AUTO;
            }
        }
    }

    /**
     * @param configuration The content of {@code config.yml}.
     * @return The settings, with defaults for missing keys.
     */
    public static @NotNull CanvasSettings read(@NotNull ConfigurationSection configuration) {
        return new CanvasSettings(
                configuration.getString("dialog-canvas.resource-pack.target", AUTO),
                configuration.getBoolean("dialog-canvas.resource-pack.auto-rebuild", false),
                RequirePack.read(configuration),
                configuration.getBoolean("dialog-canvas.download-vanilla-assets", true),
                Math.max(1, configuration.getLong("dialog-canvas.click-lifetime-seconds", 600)),
                configuration.getString("dialog-canvas.resource-pack.standalone.url", ""),
                configuration.getBoolean("dialog-canvas.resource-pack.standalone.required", false),
                configuration.getString("dialog-canvas.resource-pack.standalone.prompt", ""),
                FocusOutline.read(configuration)
        );
    }
}
