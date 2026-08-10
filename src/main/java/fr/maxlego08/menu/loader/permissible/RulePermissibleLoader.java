package fr.maxlego08.menu.loader.permissible;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.annotations.AutoPermissibleLoader;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.enums.ItemSource;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.registry.ZRuleLoaderRegistry;
import fr.maxlego08.menu.requirement.permissible.ZRulePermissible;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@AutoPermissibleLoader
public class RulePermissibleLoader extends PermissibleLoader {

    private final ButtonManager buttonManager;

    public RulePermissibleLoader(ButtonManager buttonManager) {
        super("item-rule");
        this.buttonManager = buttonManager;
    }

    @Override
    public Permissible load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        Object ruleObject = accessor.getObject("rule");
        if (!(ruleObject instanceof Map<?, ?> ruleRawMap)) return null;

        @SuppressWarnings("unchecked")
        Map<String, Object> ruleMap = (Map<String, Object>) ruleRawMap;
        Rule rule = ZRuleLoaderRegistry.getInstance().loadRule(ruleMap);
        if (rule == null) return null;

        String sourceStr = accessor.getString("item-source", "HAND");
        ItemSource itemSource;
        try {
            itemSource = ItemSource.valueOf(sourceStr.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            if (Configuration.enableDebug)
                Logger.info(
                        "Item source '" + sourceStr + "' is not valid. Valid values are: " +
                                Arrays.stream(ItemSource.values())
                                        .map(Enum::name)
                                        .collect(Collectors.joining(", "))
                );
            return null;
        }

        int amount = accessor.getInt("amount", 1);

        List<Action> denyActions = this.loadAction(this.buttonManager, accessor, "deny", path, file);
        List<Action> successActions = this.loadAction(this.buttonManager, accessor, "success", path, file);

        return new ZRulePermissible(rule, itemSource, amount, denyActions, successActions);
    }
}
