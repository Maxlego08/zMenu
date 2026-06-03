package fr.maxlego08.menu.rules;

import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.RuleContext;
import org.jetbrains.annotations.NotNull;

public class ModelDataRangeRule implements Rule {
    private final int min;
    private final int max;

    public ModelDataRangeRule(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean matches(@NotNull RuleContext context) {
        if (!context.hasCustomModelData()) return false;
        int modelData = context.getCustomModelData();
        return modelData >= this.min && modelData <= this.max;
    }

    @Override
    public boolean isValid() {
        return this.min <= this.max;
    }
}
