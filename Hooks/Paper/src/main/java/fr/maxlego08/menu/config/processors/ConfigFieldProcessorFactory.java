package fr.maxlego08.menu.config.processors;

import fr.maxlego08.menu.config.processors.type.BooleanFieldProcessor;
import fr.maxlego08.menu.config.processors.type.NumberRangeFieldProcessor;
import fr.maxlego08.menu.config.processors.type.TextFieldProcessor;

public class ConfigFieldProcessorFactory {

    public ConfigFieldProcessor createBooleanProcessor() {
        return new BooleanFieldProcessor();
    }

    public ConfigFieldProcessor createNumberRangeProcessor() {
        return new NumberRangeFieldProcessor();
    }

    public ConfigFieldProcessor createTextProcessor() {
        return new TextFieldProcessor();
    }

}