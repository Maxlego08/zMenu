package fr.maxlego08.menu.hooks.dialogs.loader.builder;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.hooks.ComponentMeta;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    protected List<DialogInput> getDialogInputs(Player player, List<InputButton> inputButtons) {
        return buildDialogs(
                player,
                inputButtons,
                InputButton::getInputType,
                DialogBuilderClass::getDialogInputBuilder,
                (builder, button) -> builder.build(player, button)
        );
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

    protected DialogBase.Builder createDialogBase(String dialogName, String externalTitle, boolean canCloseWithEscape, boolean canPauseGame, String afterAction){
        DialogBase.Builder dialogBuilder = DialogBase.builder(toComponent(dialogName))
                .externalTitle(toComponent(externalTitle))
                .canCloseWithEscape(canCloseWithEscape)
                .pause(canPauseGame)
                .afterAction(DialogBase.DialogAfterAction.valueOf(afterAction));
        return dialogBuilder;
    }

    public ComponentMeta getPaperComponent() {
        return paperComponent;
    }

    public Component toComponent(String text) {
        return paperComponent.getComponent(text);
    }
}