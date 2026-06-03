package fr.maxlego08.menu.rules;

import fr.maxlego08.menu.api.rules.ItemRuleContext;
import fr.maxlego08.menu.api.rules.Rule;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModelDataRule implements Rule {
    private final Set<Integer> modelDataValues;

    public ModelDataRule(@NotNull Set<@NotNull Integer> modelDataValues) {
        this.modelDataValues = modelDataValues;
    }

    @Override
    public boolean matches(@NotNull ItemRuleContext context) {
        if (!context.hasCustomModelData()) return false;
        int modelData = context.getCustomModelData();
        return this.modelDataValues.contains(modelData);
    }

    @Override
    public boolean isValid() {
        return !this.modelDataValues.isEmpty();
    }
}
