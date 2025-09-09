package fr.maxlego08.menu.hooks.bedrock;

import fr.maxlego08.menu.api.BedrockInventory;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.bedrock.BedrockButton;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.enums.bedrock.BedrockType;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ZBedrockInventory implements BedrockInventory {

    private final MenuPlugin menuPlugin;
    private final String fileName;
    private File file;

    private final String name;
    private final String content;
    private BedrockType bedrockType = BedrockType.SIMPLE;
    private List<BedrockButton> bedrockButtons = new ArrayList<>();
    private List<InputButton> inputButtons = new ArrayList<>();

    private final List<Requirement> actions = new ArrayList<>();
    private Requirement openRequirement;

    public ZBedrockInventory(MenuPlugin plugin, String fileName, String name, String content) {
        this.menuPlugin = plugin;
        this.name = name;
        this.fileName = fileName.endsWith(".yml") ? fileName.replace(".yml", "") : fileName;
        this.content = content;
    }

    @Override
    public String getName(Player player) {
        return this.menuPlugin.parse(player, this.name);
    }

    @Override
    public String getContent(Player player) {
        return this.menuPlugin.parse(player, this.content);
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public MenuPlugin getPlugin() {
        return menuPlugin;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public void setBedrockType(BedrockType bedrockType) {
        this.bedrockType = bedrockType;
    }

    @Override
    public BedrockType getBedrockType() {
        return bedrockType;
    }

    @Override
    public void setBedrockButtons(List<BedrockButton> bedrockButtons) {
        this.bedrockButtons = bedrockButtons;
    }

    @Override
    public List<BedrockButton> getBedrockButtons() {
        return this.bedrockButtons;
    }

    @Override
    public List<BedrockButton> getBedrockButtons(Player player) {
        return filterByViewRequirement(this.bedrockButtons, player);
    }

    @Override
    public void setInputButtons(List<InputButton> inputButtons) {
        this.inputButtons = inputButtons != null ? inputButtons : new ArrayList<>();
    }

    @Override
    public List<InputButton> getInputButtons() {
        return this.inputButtons;
    }

    @Override
    public List<InputButton> getInputButtons(Player player) {
        return filterByViewRequirement(this.inputButtons, player);
    }

    @Override
    public void setOpenRequirement(Requirement openRequirement) {
        this.openRequirement = openRequirement;
    }

    @Override
    public Requirement getOpenRequirement() {
        return this.openRequirement;
    }

    @Override
    public boolean hasOpenRequirement(Player player) {
        return checkRequirement(openRequirement, player);
    }

    @Override
    public void setRequirements(List<Requirement> actions) {
        this.actions.addAll(actions);
    }

    @Override
    public List<Requirement> getRequirements() {
        return actions;
    }

    @SuppressWarnings("unchecked")
    protected <T extends Button> List<T> filterByViewRequirement(List<T> buttons, Player player) {
        List<T> visibleButtons = new ArrayList<>();
        for (T button : buttons) {
            Button masterParent = button.getMasterParentButton();
            if (button.getClass().isInstance(masterParent)) {
                T visible = getFirstVisibleButtonRecursive((T) masterParent, player);
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
            boolean hasPermission = button.checkPermission(player, menuPlugin.getInventoryManager().getFakeInventory(), new Placeholders());
            if (!hasPermission) {
                if (button.hasElseButton()) {
                    return getFirstVisibleButtonRecursive((T) button.getElseButton(), player);
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
    protected boolean checkRequirement(Requirement requirement, Player player) {
        if (requirement == null) return true;
        InventoryEngine fakeInventory = menuPlugin.getInventoryManager().getFakeInventory();
        Placeholders placeholder = new Placeholders();
        return requirement.execute(player, null, fakeInventory, placeholder);
    }
}
