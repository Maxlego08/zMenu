package fr.maxlego08.menu.hooks.bedrock;

import fr.robie.minimessageflow.Template;
import fr.robie.minimessageflow.TextTemplate;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.animation.TitleAnimation;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.enums.bedrock.BedrockType;
import fr.maxlego08.menu.api.inventory.bedrock.BedrockInventory;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.ConditionalName;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.ClearInvType;
import fr.maxlego08.menu.api.utils.InventoryReplacement;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.cumulus.response.FormResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBedrockInventory<B extends FormBuilder<B, F, R>, F extends Form, R extends FormResponse> implements BedrockInventory<B, F, R> {

    protected final MenuPlugin menuPlugin;
    private final String fileName;
    private File file;

    private final Template name;
    private final TextTemplate nameText;
    private final Map<ConditionalName, Template> conditionalTemplates = new ConcurrentHashMap<>();
    private final Map<ConditionalName, TextTemplate> conditionalTexts = new ConcurrentHashMap<>();
    private final BedrockType bedrockType;

    private final List<Requirement> actions = new ArrayList<>();
    private final List<ConditionalName> conditionalNames = new ArrayList<>();
    private @Nullable TextTemplate targetPlayerName;
    private Requirement openRequirement;
    private InventoryReplacement inventoryReplacement;

    private List<Action> openActions = new ArrayList<>();
    private List<Action> closeActions = new ArrayList<>();


    public AbstractBedrockInventory(@NotNull MenuPlugin plugin, @NotNull String fileName, @NotNull String name, @NotNull BedrockType bedrockType) {
        this.menuPlugin = plugin;
        this.name = TextService.get().flow().compile(name);
        this.nameText = TextService.get().flow().compileText(name);
        this.fileName = fileName.endsWith(".yml") ? fileName.replace(".yml", "") : fileName;
        this.bedrockType = bedrockType;
    }

    @Override
    public String getName() {
        return this.name.source();
    }

    @Override
    public String getName(@NotNull Player player, InventoryEngine inventoryDefault, Placeholders placeholders) {
        ConditionalName chosen = this.chosenName(player, inventoryDefault, placeholders);
        TextTemplate text = chosen == null ? this.nameText
                : this.conditionalTexts.computeIfAbsent(chosen, conditional -> TextService.get().flow().compileText(conditional.name()));
        return text.render(placeholders.values(), player);
    }

    /**
     * The conditional name of highest priority the player has, or null for the inventory's own name.
     */
    private @Nullable ConditionalName chosenName(@NotNull Player player, InventoryEngine inventoryDefault, Placeholders placeholders) {
        if (!this.conditionalNames.isEmpty()) {
            Optional<ConditionalName> optional = this.conditionalNames.stream().filter(conditionalName -> conditionalName.hasPermission(player, null, inventoryDefault, placeholders)).max(Comparator.comparingInt(ConditionalName::priority));
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        return null;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public MenuPlugin getPlugin() {
        return this.menuPlugin;
    }

    @Override
    public File getFile() {
        return this.file;
    }
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public @NotNull BedrockType getBedrockType() {
        return this.bedrockType;
    }

    public void setOpenRequirement(Requirement openRequirement) {
        this.openRequirement = openRequirement;
    }

    @Override
    public Requirement getOpenRequirement() {
        return this.openRequirement;
    }

    public void setInventoryReplacement(InventoryReplacement replacement) {
        this.inventoryReplacement = replacement;
    }

    @Override
    public InventoryReplacement getInventoryReplacement() {
        return this.inventoryReplacement;
    }

    @Override
    public List<ConditionalName> getConditionalNames() {
        return this.conditionalNames;
    }

    @Override
    public @Nullable TextTemplate getTargetPlayerNameTemplate() {
        return this.targetPlayerName;
    }

    @Override
    public String getTargetPlayerNamePlaceholder() {
        return TextService.source(this.targetPlayerName);
    }

    @Override
    public void setTitleAnimation(TitleAnimation load) {
        // Not supported
    }

    /**
     * Get the title animation
     * @return null (not supported)
     */
    @Override
    public TitleAnimation getTitleAnimation() {
        return null;
    }

    @Override
    public List<Action> getOpenActions() {
        return this.openActions;
    }

    public void setOpenActions(List<Action> openActions) {
        this.openActions = openActions;
    }

    @Override
    public List<Action> getCloseActions() {
        return this.closeActions;
    }

    public void setCloseActions(List<Action> closeActions) {
        this.closeActions = closeActions;
    }

    @Override
    public ClearInvType getClearInvType() {
        return null;
    }

    @Override
    public boolean isClickLimiterEnabled() {
        return false;
    }

    protected Placeholders createPlaceholders(Player player) {
        Placeholders placeholders = new Placeholders();
        placeholders.register("player", player.getName());
        return placeholders;
    }

    protected String getLegacyTitle(Player player, InventoryEngine inventoryEngine, Placeholders placeholders) {
        ConditionalName chosen = this.chosenName(player, inventoryEngine, placeholders);
        Template title = chosen == null ? this.name
                : this.conditionalTemplates.computeIfAbsent(chosen, conditional -> TextService.get().flow().compile(conditional.name()));
        return TextService.legacy(title, placeholders, player);
    }

    protected <C, T extends Button> List<C> renderButtons(
            List<T> buttons,
            Player player,
            Placeholders placeholders,
            java.util.function.BiConsumer<T, fr.maxlego08.menu.api.context.BedrockRenderContext<C>> renderLogic) {
        return this.renderButtons(buttons, player, placeholders, null, renderLogic);
    }

    protected <C, T extends Button> List<C> renderButtons(
            List<T> buttons,
            Player player,
            Placeholders placeholders,
            List<T> expandedButtons,
            java.util.function.BiConsumer<T, fr.maxlego08.menu.api.context.BedrockRenderContext<C>> renderLogic) {
        List<C> result = new ArrayList<>();
        fr.maxlego08.menu.api.context.BedrockRenderContext<C> context = new fr.maxlego08.menu.api.context.BedrockRenderContext<>(result, player, this, this.menuPlugin.getMetaUpdater(), placeholders, this.menuPlugin);

        for (T button : buttons) {
            int beforeSize = result.size();
            if (expandedButtons != null && button instanceof fr.maxlego08.menu.hooks.bedrock.button.BedrockDynamicButton dynamic) {
                dynamic.onRender(context, expandedButton -> expandedButtons.add((T) expandedButton));
            } else {
                renderLogic.accept(button, context);
                int afterSize = result.size();

                if (expandedButtons != null) {
                    for (int i = 0; i < (afterSize - beforeSize); i++) {
                        expandedButtons.add(button);
                    }
                }
            }
        }
        return result;
    }

    public void setTargetPlayerNamePlaceholder(String targetPlaceholder) {
        this.targetPlayerName = TextService.text(targetPlaceholder);
    }

    public void setRequirements(List<Requirement> actions) {
        this.actions.addAll(actions);
    }

    @Override
    public List<Requirement> getRequirements() {
        return this.actions;
    }

    @SuppressWarnings("unchecked")
    protected <T extends Button> List<T> filterByViewRequirement(List<T> buttons, Player player, InventoryEngine inventoryEngine, Placeholders placeholders) {
        List<T> visibleButtons = new ArrayList<>();
        for (T button : buttons) {
            Button masterParent = button.getMasterParentButton();
            if (button.getClass().isInstance(masterParent)) {
                T visible = this.getFirstVisibleButtonRecursive((T) masterParent, player, placeholders, inventoryEngine);
                if (visible != null) {
                    visibleButtons.add(visible);
                }
            }
        }
        return visibleButtons;
    }

    @SuppressWarnings("unchecked")
    private <T extends Button> T getFirstVisibleButtonRecursive(T button, Player player, Placeholders placeholders, InventoryEngine inventoryEngine) {
        if (button.hasPermission()) {
            boolean hasPermission = button.checkPermission(player, inventoryEngine, placeholders);
            if (!hasPermission) {
                if (button.hasElseButton()) {
                    return this.getFirstVisibleButtonRecursive((T) button.getElseButton(), player, placeholders, inventoryEngine);
                } else {
                    return null;
                }
            } else {
                return button;
            }
        } else {
            return button;
        }
    }
}
