package fr.maxlego08.menu.rules;

import fr.maxlego08.menu.api.rules.ItemRuleContext;
import fr.maxlego08.menu.api.rules.Rule;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModelDataRangeRule implements Rule {
    private final int min;
    private final int max;

    public ModelDataRangeRule(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean matches(@NotNull ItemRuleContext context) {
        if (!context.hasCustomModelData()) return false;
        int modelData = context.getCustomModelData();
        return modelData >= this.min && modelData <= this.max;
    }

    @Override
    public boolean isValid() {
        return this.min <= this.max;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> range = new LinkedHashMap<>();
        if (this.min != 0) range.put("min", this.min);
        if (this.max != Integer.MAX_VALUE) range.put("max", this.max);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", "custom-model-data");
        map.put("ranges", List.of(range));
        return map;
    }
}
