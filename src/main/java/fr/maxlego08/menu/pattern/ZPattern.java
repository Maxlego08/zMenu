package fr.maxlego08.menu.pattern;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.pattern.Pattern;

import java.util.List;

public record ZPattern(String name, List<Button> buttons, int inventorySize, boolean enableMultiPage) implements Pattern {

}
