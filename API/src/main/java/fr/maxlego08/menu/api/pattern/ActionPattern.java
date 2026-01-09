package fr.maxlego08.menu.api.pattern;

import fr.maxlego08.menu.api.requirement.Action;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ActionPattern {
    @NotNull
    String name();

    @NotNull
    List<Action> actions();
    @NotNull
    List<Action> denyActions();
}
