package fr.maxlego08.menu.hooks.bedrock;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.hooks.bedrock.loader.builder.BedrockBuilderClass;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractBedrockInventoryManager {
    protected final MenuPlugin menuPlugin;

    public AbstractBedrockInventoryManager(MenuPlugin menuPlugin) {
        this.menuPlugin = menuPlugin;
    }

    protected List<Component> getInputComponents(Player player, List<InputButton> inputButtons) {
        return buildComponents(
                player,
                inputButtons,
                InputButton::getInputType,
                BedrockBuilderClass::getDialogInputBuilder,
                (builder, button) -> builder.build(player, button)
        );
    }

    protected <B, T, TYPE, BUILDER> List<T> buildComponents(
            Player player,
            List<B> buttons,
            Function<B, TYPE> typeExtractor,
            Function<TYPE, Optional<BUILDER>> builderResolver,
            BiFunction<BUILDER, B, T> builderExecutor
    ) {
        return buttons.stream()
                .map(button -> {
                    TYPE type = typeExtractor.apply(button);
                    if (type == null) return null;

                    return builderResolver.apply(type)
                            .map(builder -> builderExecutor.apply(builder, button))
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();
    }
}