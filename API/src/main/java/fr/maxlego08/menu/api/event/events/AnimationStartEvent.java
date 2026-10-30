package fr.maxlego08.menu.api.event.events;

import fr.maxlego08.menu.api.animation.slot.Animation;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayback;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.event.CancelledMenuEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Called before an animation starts playing on an inventory.
 *
 * <p>Cancelling it stops the animation from playing at all; the inventory is displayed complete, as
 * if it had none.</p>
 */
public class AnimationStartEvent extends CancelledMenuEvent {

    private final Player player;
    private final InventoryEngine inventory;
    private final Animation animation;
    private final AnimationPlayback playback;
    private boolean freeze;

    public AnimationStartEvent(@NotNull Player player, @NotNull InventoryEngine inventory,
                               @NotNull Animation animation, @NotNull AnimationPlayback playback, boolean freeze) {
        this.player = player;
        this.inventory = inventory;
        this.animation = animation;
        this.playback = playback;
        this.freeze = freeze;
    }

    @Contract(pure = true)
    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @Contract(pure = true)
    @NotNull
    public InventoryEngine getInventory() {
        return this.inventory;
    }

    @Contract(pure = true)
    @NotNull
    public Animation getAnimation() {
        return this.animation;
    }

    @Contract(pure = true)
    @NotNull
    public AnimationPlayback getPlayback() {
        return this.playback;
    }

    /**
     * @return whether the inventory will ignore clicks while the animation plays
     */
    @Contract(pure = true)
    public boolean isFreeze() {
        return this.freeze;
    }

    /**
     * Changes whether the inventory ignores clicks while the animation plays.
     *
     * @param freeze true to freeze it
     */
    public void setFreeze(boolean freeze) {
        this.freeze = freeze;
    }
}
