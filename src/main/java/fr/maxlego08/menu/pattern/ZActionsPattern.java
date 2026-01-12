package fr.maxlego08.menu.pattern;

import fr.maxlego08.menu.api.pattern.ActionPattern;
import fr.maxlego08.menu.api.requirement.Action;

import java.util.List;

public record ZActionsPattern(String name, List<Action> actions, List<Action> denyActions) implements ActionPattern {
}
