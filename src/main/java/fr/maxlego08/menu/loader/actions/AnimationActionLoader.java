package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.animation.slot.Animation;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayback;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import fr.maxlego08.menu.api.annotations.AutoActionLoader;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.loader.SlotAnimationSettingsLoader;
import fr.maxlego08.menu.requirement.actions.AnimationAction;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.Map;

/**
 * Loads the action playing an animation on the inventory the player is looking at.
 *
 * <pre>
 * actions:
 *   - type: animation
 *     file: pulse
 *     mode: OVERLAY
 * </pre>
 */
@AutoActionLoader
public class AnimationActionLoader extends ActionLoader {

    private final MenuPlugin plugin;

    public AnimationActionLoader(MenuPlugin plugin) {
        super("animation", "play animation", "play_animation");
        this.plugin = plugin;
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        AnimationPlayback playback = AnimationPlayback.parse(accessor.getString("mode", null), AnimationPlayback.OVERLAY);
        Boolean freeze = accessor.getObject("freeze") == null ? null : accessor.getBoolean("freeze");

        if (accessor.getBoolean("stop", false)) {
            return new AnimationAction(this.plugin, null, null, playback, freeze, true);
        }

        String animationName = accessor.getString("file", null);
        if (animationName != null) {
            return new AnimationAction(this.plugin, animationName, null, playback, freeze, false);
        }

        Animation inline = this.loadInline(accessor, file);
        if (inline == null) {
            Logger.info("An animation action needs a file, an animation block, or stop: true, in " + file.getAbsolutePath(), Logger.LogType.ERROR);
            return null;
        }
        return new AnimationAction(this.plugin, null, inline, playback, freeze, false);
    }

    /**
     * Loads an animation written inside the action itself, for a one off that does not deserve a
     * file of its own.
     */
    private Animation loadInline(@NonNull TypedMapAccessor accessor, @NonNull File file) {
        Object value = accessor.getObject("animation");
        if (!(value instanceof Map<?, ?> map) || map.isEmpty()) return null;

        YamlConfiguration configuration = new YamlConfiguration();
        configuration.createSection("animation", map);

        SlotAnimationSettings settings = new SlotAnimationSettingsLoader(this.plugin).load(configuration, "animation.", file);
        return settings == null ? null : new Animation("inline", settings);
    }
}
