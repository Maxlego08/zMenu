package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.Animation;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayback;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
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
    private final Key animationKey;
    private final Animation inlineAnimation;
    private final AnimationPlayback playback;
    private final Boolean freeze;
    private final boolean stop;

    public AnimationAction(@NotNull MenuPlugin plugin, @Nullable Key animationKey, @Nullable Animation inlineAnimation,
                           @NotNull AnimationPlayback playback, @Nullable Boolean freeze, boolean stop) {
        this.plugin = plugin;
        this.animationKey = animationKey;
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
        } else if (this.animationKey != null) {
            boolean ownFile = ZMenuKeys.NAMESPACE.equals(this.animationKey.namespace());
            map.put("file", ownFile ? this.animationKey.value() : this.animationKey.asString());
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
            Logger.info("The animation " + this.animationKey.asString() + " does not exist, it cannot be played.", Logger.LogType.ERROR);
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
        if (this.animationKey == null) return null;

        return this.plugin.getInventoryManager().getAnimationManager().getAnimation(this.animationKey).orElse(null);
    }
}
