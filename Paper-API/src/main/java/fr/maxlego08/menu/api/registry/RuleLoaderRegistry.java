package fr.maxlego08.menu.api.registry;

import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.loader.RuleLoader;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public abstract class RuleLoaderRegistry extends Registry<String, RuleLoader>{

    protected RuleLoaderRegistry() {

    }

    public void register(@NonNull RuleLoader value) {
        register(null, value);
    }

    @Override
    public void register(@Nullable String key, @NonNull RuleLoader value) {
        String type = value.getType().toLowerCase(Locale.ROOT);
        super.register(type, value);
        for (String alias : value.getAliases()) {
            super.register(alias.toLowerCase(Locale.ROOT), value);
        }
    }

    @Nullable
    public Rule loadRule(Map<String, Object> configuration) {
        String type = (String) configuration.get("type");
        if (type == null) {
            return null;
        }
        Optional<RuleLoader> ruleLoader = this.get(type);
        if (ruleLoader.isEmpty()) {
            Logger.info("Unknown rule type: " + type, "Available types: " + this.getAllKeys());
            return null;
        }
        return ruleLoader.get().load(configuration);
    }

    @Override
    public Optional<RuleLoader> get(@NonNull String key) {
        return super.get(key.toLowerCase(Locale.ROOT));
    }
}
