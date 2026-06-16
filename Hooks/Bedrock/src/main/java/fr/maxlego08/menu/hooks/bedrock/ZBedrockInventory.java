package fr.maxlego08.menu.hooks.bedrock;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.animation.TitleAnimation;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.bedrock.BedrockButton;
import fr.maxlego08.menu.api.button.buttons.bedrock.inputs.BedrockInputButton;
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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ZBedrockInventory implements BedrockInventory {

    private final MenuPlugin menuPlugin;
    private final String fileName;
    private File file;

    private final String name;
    private final String content;
    private BedrockType bedrockType = BedrockType.SIMPLE;
    private List<BedrockButton> bedrockButtons = new ArrayList<>();
    private List<BedrockInputButton> inputButtons = new ArrayList<>();

    private final List<Requirement> actions = new ArrayList<>();
    private final List<ConditionalName> conditionalNames = new ArrayList<>();
    private String targetPlayerNamePlaceholder;
    private Requirement openRequirement;
    private InventoryReplacement inventoryReplacement;

    private List<Action> openActions = new ArrayList<>();
    private List<Action> closeActions = new ArrayList<>();


    public ZBedrockInventory(MenuPlugin plugin, String fileName, String name, String content) {
        this.menuPlugin = plugin;
        this.name = name;
        this.fileName = fileName.endsWith(".yml") ? fileName.replace(".yml", "") : fileName;
        this.content = content;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getName(@NotNull Player player, InventoryEngine inventoryDefault, Placeholders placeholders) {
        if (!this.conditionalNames.isEmpty()) {
            Optional<ConditionalName> optional = this.conditionalNames.stream().filter(conditionalName -> conditionalName.hasPermission(player, null, inventoryDefault, placeholders)).max(Comparator.comparingInt(ConditionalName::priority));

            if (optional.isPresent()) {
                ConditionalName conditionalName = optional.get();
                return this.menuPlugin.parse(player, placeholders.parse(conditionalName.name()));
            }
        }
        return this.menuPlugin.parse(player, placeholders.parse(this.name));
    }

    @Override
    public String getContent(Player player) {
        return this.menuPlugin.parse(player, this.content);
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

    public void setBedrockType(BedrockType bedrockType) {
        this.bedrockType = bedrockType;
    }

    @Override
    public BedrockType getBedrockType() {
        return this.bedrockType;
    }

    public void setBedrockButtons(List<BedrockButton> bedrockButtons) {
        this.bedrockButtons = bedrockButtons;
    }

    @Override
    public List<BedrockButton> getBedrockButtons() {
        return this.bedrockButtons;
    }

    @Override
    public List<BedrockButton> getBedrockButtons(Player player) {
        return this.filterByViewRequirement(this.bedrockButtons, player);
    }

    public void setInputButtons(List<BedrockInputButton> inputButtons) {
        this.inputButtons = inputButtons != null ? inputButtons : new ArrayList<>();
    }

    @Override
    public List<BedrockInputButton> getInputButtons() {
        return this.inputButtons;
    }

    @Override
    public List<BedrockInputButton> getInputButtons(Player player) {
        return this.filterByViewRequirement(this.inputButtons, player);
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
    public String getTargetPlayerNamePlaceholder() {
        return this.targetPlayerNamePlaceholder;
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

    public void setTargetPlayerNamePlaceholder(String targetPlaceholder) {
        this.targetPlayerNamePlaceholder = targetPlaceholder;
    }

    public void setRequirements(List<Requirement> actions) {
        this.actions.addAll(actions);
    }

    @Override
    public List<Requirement> getRequirements() {
        return this.actions;
    }

    @SuppressWarnings("unchecked")
    protected <T extends Button> List<T> filterByViewRequirement(List<T> buttons, Player player) {
        List<T> visibleButtons = new ArrayList<>();
        for (T button : buttons) {
            Button masterParent = button.getMasterParentButton();
            if (button.getClass().isInstance(masterParent)) {
                T visible = this.getFirstVisibleButtonRecursive((T) masterParent, player);
                if (visible != null) {
                    visibleButtons.add(visible);
                }
            }
        }
        return visibleButtons;
    }

    @SuppressWarnings("unchecked")
    private <T extends Button> T getFirstVisibleButtonRecursive(T button, Player player) {
        if (button.hasPermission()) {
            boolean hasPermission = button.checkPermission(player, this.menuPlugin.getInventoryManager().getFakeInventory(), new Placeholders());
            if (!hasPermission) {
                if (button.hasElseButton()) {
                    return this.getFirstVisibleButtonRecursive((T) button.getElseButton(), player);
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
