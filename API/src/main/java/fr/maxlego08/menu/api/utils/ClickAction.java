package fr.maxlego08.menu.api.utils;

import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum ClickAction {
    LEFT_CLICK(Action.LEFT_CLICK_BLOCK, Action.LEFT_CLICK_AIR),
    RIGHT_CLICK(Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR),
    LEFT_CLICK_BLOCK(Action.LEFT_CLICK_BLOCK),
    RIGHT_CLICK_BLOCK(Action.RIGHT_CLICK_BLOCK),
    LEFT_CLICK_AIR(Action.LEFT_CLICK_AIR),
    RIGHT_CLICK_AIR(Action.RIGHT_CLICK_AIR),
    PHYSICAL(Action.PHYSICAL)
    ;
    private final List<Action> actions;

    ClickAction(Action ... actions) {
        this.actions = List.of(actions);
    }

    @NotNull
    public List<Action> asActions() {
        return this.actions;
    }
}
