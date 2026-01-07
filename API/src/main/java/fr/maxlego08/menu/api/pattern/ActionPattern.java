package fr.maxlego08.menu.api.pattern;

import fr.maxlego08.menu.api.requirement.Action;

import java.util.List;

public interface ActionPattern {
    String name();

    List<Action> actions();
    List<Action> denyActions();
}
