package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.DialogInventory;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.enums.DialogType;
import fr.maxlego08.menu.api.exceptions.InventoryButtonException;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.api.utils.dialogs.record.ActionButtonRecord;
import fr.maxlego08.menu.hooks.dialogs.ZDialogInventory;
import fr.maxlego08.menu.hooks.dialogs.ZDialogManager;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class DialogLoader implements Loader<DialogInventory> {
    private final MenuPlugin menuPlugin;
    private final ZDialogManager manager;

    public DialogLoader(MenuPlugin menuPlugin, ZDialogManager manager) {
        this.menuPlugin = menuPlugin;
        this.manager = manager;
    }

    @Override
    public DialogInventory load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {
        File file = (File) objects[0];

        String name = configuration.getString("name", "");
        String externalTitle = configuration.getString("external-title", "");

        ZDialogInventory dialogInventory = new ZDialogInventory(menuPlugin, name, file.getName(), externalTitle);

        boolean canCloseWithEscape = configuration.getBoolean("can-close-with-escape", true);
        dialogInventory.setCanCloseWithEscape(canCloseWithEscape);

        boolean pause = configuration.getBoolean("pause", false);
        dialogInventory.setPause(pause);

        String typeString = configuration.getString("type", "NOTICE");
        DialogType dialogType;
        try {
            dialogType = DialogType.valueOf(typeString.toUpperCase());
            dialogInventory.setDialogType(dialogType);
        } catch (IllegalArgumentException e) {
            throw new InventoryException("Invalid dialog type: " + typeString);
        }

        String afterActionString = configuration.getString("after-action", "CLOSE");
        try {
            dialogInventory.setAfterAction(afterActionString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InventoryException("Invalid after action: " + afterActionString);
        }
        if (configuration.isConfigurationSection("open-requirement")){
            try {
                Requirement openRequirement = loadRequirement(configuration, "open-requirement.", file);
                dialogInventory.setOpenRequirement(openRequirement);
            } catch (InventoryException e) {
                Logger.info("Failed to load open requirement: " + e.getMessage(), Logger.LogType.WARNING);
            }
        }

        loadSpecificItems(dialogType, configuration, dialogInventory, file);

        List<BodyButton> bodyButtons = loadBodyButtons(configuration, file);
        dialogInventory.setBodyButtons(bodyButtons);

        List<InputButton> inputButtons = loadInputButtons(configuration, file);
        dialogInventory.setInputButtons(inputButtons);

        dialogInventory.setFile(file);

        return dialogInventory;
    }

    /**
     * Loads body buttons from the configuration
     */
    private List<BodyButton> loadBodyButtons(YamlConfiguration configuration, File file) {
        List<BodyButton> bodyButtons = new ArrayList<>();

        ConfigurationSection bodySection = configuration.getConfigurationSection("body");

        if (bodySection == null) {
            return bodyButtons;
        }

        Loader<Button> loader = this.menuPlugin.getButtonManager().getLoaderButton(this.menuPlugin, file, 54, new HashMap<>());
        for (String bodyKey : bodySection.getKeys(false)) {
            String path = "body." + bodyKey + ".";
            try {
                Button button = loader.load(configuration, path, bodyKey);
                BodyButton bodyButton = getButtonType(button, BodyButton.class, path, file);
                bodyButtons.add(bodyButton);
            } catch (Exception exception) {
                Logger.info(exception.getMessage(), Logger.LogType.ERROR);
            }
        }
        return bodyButtons;
    }
    private List<InputButton> loadInputButtons(YamlConfiguration configuration, File file) {
        List<InputButton> inputButtons = new ArrayList<>();

        ConfigurationSection inputSection = configuration.getConfigurationSection("inputs");

        if (inputSection == null) {
            return inputButtons;
        }

        Loader<Button> loader = this.menuPlugin.getButtonManager().getLoaderButton(this.menuPlugin, file, 54, new HashMap<>());
        for (String inputKey : inputSection.getKeys(false)) {
            String path = "inputs." + inputKey + ".";
            try {
                Button button = loader.load(configuration, path, inputKey);
                InputButton inputButton = getButtonType(button, InputButton.class, path, file);
                inputButton.setKey(inputKey);
                inputButtons.add(inputButton);
            } catch (Exception exception) {
                Logger.info(exception.getMessage(), Logger.LogType.ERROR);
            }
        }
        return inputButtons;
    }

    @Override
    public void save(DialogInventory object, YamlConfiguration configuration, String path, File file, Object... objects) {
        //TODO: Implement save logic if needed
    }

    private void loadSpecificItems(DialogType dialogType, YamlConfiguration configuration, ZDialogInventory dialogInventory, File file) throws InventoryException {
        switch (dialogType) {
            case NOTICE -> {
                dialogInventory.addAction(loadRequirements(configuration, "actions", file));
                String noticeText = configuration.getString("notice.text","");
                String noticeTooltip = configuration.getString("notice.tooltip","");
                int noticeWidth = configuration.getInt("notice.width", 200);
                dialogInventory.setLabel(noticeText);
                dialogInventory.setLabelTooltip(noticeTooltip);
                dialogInventory.setLabelWidth(noticeWidth);

            }
            case CONFIRMATION -> {
                String yesText = configuration.getString("confirmation.yes-text");
                String yesTooltip = configuration.getString("confirmation.yes-tooltip");
                int yesWidth = configuration.getInt("confirmation.yes-width", 100);
                String noText = configuration.getString("confirmation.no-text");
                String noTooltip = configuration.getString("confirmation.no-tooltip");
                int noWidth = configuration.getInt("confirmation.no-width", 100);
                dialogInventory.setYesText(yesText);
                dialogInventory.setNoText(noText);
                dialogInventory.setYesTooltip(yesTooltip);
                dialogInventory.setNoTooltip(noTooltip);
                dialogInventory.setYesWidth(yesWidth);
                dialogInventory.setNoWidth(noWidth);
                dialogInventory.addYesAction(loadRequirements(configuration, "yes-actions", file));
                dialogInventory.addNoAction(loadRequirements(configuration, "no-actions", file));
            }
            case MULTI_ACTION -> {
                int numberOfColumns = configuration.getInt("number-of-columns", 3);
                ConfigurationSection multiSection = configuration.getConfigurationSection("multi-actions");
                if (multiSection == null) {
                    return;
                }
                Set<String> keys = multiSection.getKeys(false);
                if (keys.isEmpty()) {
                    if (Config.enableDebug){
                        Logger.info("A minimum of one action button is required for multi-action dialogs.", Logger.LogType.WARNING);
                    }
                    return;
                }
                for (String key : keys) {
                    String path = "multi-actions."+key;
                    String text = configuration.getString(path + ".text", "");
                    String tooltip = configuration.getString(path + ".tooltip", "");
                    int width = configuration.getInt(path + ".width", 100);
                    List<Requirement> requirement = loadRequirements(configuration, path+".actions", file);
                    ActionButtonRecord record = new ActionButtonRecord(text, tooltip, width, requirement);
                    dialogInventory.addActionButton(record);
                }
                dialogInventory.setNumberOfColumns(numberOfColumns);
            }
            case SERVER_LINKS -> {
                String text = configuration.getString("server-links.text", "");
                String tooltip = configuration.getString("server-links.tooltip", "");
                int width = configuration.getInt("server-links.width", 100);
                List<Requirement> requirement = loadRequirements(configuration, "server-links.actions", file);
                int numberOfColumns = configuration.getInt("server-links.number-of-columns", 1);
                ActionButtonRecord record = new ActionButtonRecord(text, tooltip, width, requirement);
                dialogInventory.setActionButtonServerLink(record);
                dialogInventory.setNumberOfColumns(numberOfColumns);
            }
        }
    }

    protected List<Requirement> loadRequirements(YamlConfiguration configuration, String path, File file) throws InventoryException {
        return menuPlugin.getButtonManager().loadRequirements(configuration, path, file);
    }
    protected Requirement loadRequirement(YamlConfiguration configuration, String path, File file) throws InventoryException {
        return menuPlugin.getButtonManager().loadRequirement(configuration, path, file);
    }

    @SuppressWarnings("unchecked")
    protected <T extends Button> T getButtonType(Button button, Class<T> verifClass, String path, File file) throws InventoryButtonException {
        if (verifClass.isInstance(button)) {
            if (button.getElseButton() != null){
                return getButtonType(button.getElseButton(), verifClass, path, file);
            }
            return (T) button;
        } else {
            throw new InventoryButtonException("The type " + button.getName() + " for the button " + path + " is not valid for this section" + file.getAbsolutePath());
        }
    }
}