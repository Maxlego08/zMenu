package fr.maxlego08.menu.api.button.buttons;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.utils.SwitchCaseButton;

import java.util.List;

public abstract class SwitchButton extends Button {

    public abstract String getPlaceholder();

    public abstract List<SwitchCaseButton> getButtons();

}
