package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.animation.slot.Animation;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayback;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Map;

/**
 * Plays an animation on the inventory the player is looking at.
 */
public class AnimationAction extends Action {

    private final MenuPlugin plugin;
    private final String animationName;
    private final Animation inlineAnimation;
    private final AnimationPlayback playback;
    private final Boolean freeze;
    private final boolean stop;

    public AnimationAction(@NotNull MenuPlugin plugin, @Nullable String animationName, @Nullable Animation inlineAnimation,
                           @NotNull AnimationPlayback playback, @Nullable Boolean freeze, boolean stop) {
        this.plugin = plugin;
        this.animationName = animationName;
        this.inlineAnimation = inlineAnimation;
        this.playback = playback;
        this.freeze = freeze;
        this.stop = stop;
    }

    @Override
    protected void serializeProperties(@NonNull Map<String, Object> map) {
        map.put("type", "animation");
        if (this.stop) {
            map.put("stop", true);
        } else if (this.animationName != null) {
            map.put("file", this.animationName);
        } else if (this.inlineAnimation != null) {
            map.put("animation", this.inlineAnimation.settings().serializeToMap());
        }
        if (this.playback != AnimationPlayback.OVERLAY) map.put("mode", this.playback.name());
        if (this.freeze != null) map.put("freeze", this.freeze);
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        if (this.stop) {
            inventory.stopAnimation();
            return;
        }

        Animation animation = this.resolve();
        if (animation == null) {
            Logger.info("The animation " + this.animationName + " does not exist, it cannot be played.", Logger.LogType.ERROR);
            return;
        }

        if (this.freeze == null) {
            inventory.playAnimation(animation, this.playback);
        } else {
            inventory.playAnimation(animation, this.playback, this.freeze);
        }
    }

    /**
     * A named animation is resolved when the action runs rather than when it is loaded, so that
     * reloading the plugin is enough to change it.
     */
    @Nullable
    private Animation resolve() {
        if (this.inlineAnimation != null) return this.inlineAnimation;
        if (this.animationName == null) return null;

        return this.plugin.getInventoryManager().getAnimationManager().getAnimation(this.animationName).orElse(null);
    }
}
