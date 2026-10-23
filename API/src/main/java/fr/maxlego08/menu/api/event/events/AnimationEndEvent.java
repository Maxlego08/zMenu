package fr.maxlego08.menu.api.event.events;

import fr.maxlego08.menu.api.animation.slot.Animation;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayback;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.event.MenuEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Called once an animation is over, whether it played to its end or was finished early because
 * another one started, the inventory closed or the server stopped.
 */
public class AnimationEndEvent extends MenuEvent {

    private final Player player;
    private final InventoryEngine inventory;
    private final Animation animation;
    private final AnimationPlayback playback;
    private final boolean completed;

    public AnimationEndEvent(@NotNull Player player, @NotNull InventoryEngine inventory,
                             @NotNull Animation animation, @NotNull AnimationPlayback playback, boolean completed) {
        this.player = player;
        this.inventory = inventory;
        this.animation = animation;
        this.playback = playback;
        this.completed = completed;
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
     * Returns whether every step was played in time, rather than the rest being applied at once.
     *
     * @return true if the animation reached its end on its own
     */
    @Contract(pure = true)
    public boolean isCompleted() {
        return this.completed;
    }
}
