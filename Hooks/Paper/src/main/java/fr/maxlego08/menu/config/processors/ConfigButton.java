package fr.maxlego08.menu.config.processors;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.configuration.annotation.ConfigOption;

public class ConfigButton extends InputButton {

    public ConfigButton(ConfigOption configOption) {
        super(configOption.type());
        this.setLabel(configOption.label());
        this.setLabelVisible(configOption.labelVisible());
        this.setKey(configOption.key());
    }
}
