package fr.maxlego08.menu.hooks.dialogs.loader.builder;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.hooks.ComponentMeta;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class DialogBuilderManager {
    protected final MenuPlugin menuPlugin;
    protected final ComponentMeta paperComponent;

    public DialogBuilderManager(MenuPlugin menuPlugin) {
        this.menuPlugin = menuPlugin;
        this.paperComponent = (ComponentMeta) menuPlugin.getMetaUpdater();
    }

    protected <B, T, TYPE, BUILDER> List<T> buildDialogs(
            Player player,
            List<B> buttons,
            Function<B, TYPE> typeExtractor,
            Function<TYPE, Optional<BUILDER>> builderResolver,
            BiFunction<BUILDER, B, T> builderExecutor
    ) {
        List<T> results = new java.util.ArrayList<>(buttons.size());
        for (B button : buttons) {
            TYPE type = typeExtractor.apply(button);
            if (type == null) {
                continue;
            }
            Optional<BUILDER> builderOptional = builderResolver.apply(type);
            if (builderOptional.isPresent()) {
                T value = builderExecutor.apply(builderOptional.get(), button);
                if (value != null) {
                    results.add(value);
                }
            }
        }
        return results;
    }

    public ComponentMeta getPaperComponent() {
        return this.paperComponent;
    }

}