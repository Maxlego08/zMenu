package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.DialogInventory;
import fr.maxlego08.menu.api.InventoryOption;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.enums.DialogType;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.enums.dialog.DialogType;
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
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.BiConsumer;

public class DialogLoader implements Loader<DialogInventory> {
    private final MenuPlugin menuPlugin;
    private final ZDialogManager manager;

    public DialogLoader(MenuPlugin menuPlugin, ZDialogManager manager) {
        this.menuPlugin = menuPlugin;
        this.manager = manager;
    }

    @Override
    public DialogInventory load(@NonNull YamlConfiguration configuration, @NonNull String path, Object... objects) throws InventoryException {
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

        List<BodyButton> bodyButtons = loadButtons(configuration, file, "body", BodyButton.class, null);
        dialogInventory.setBodyButtons(bodyButtons);

        List<InputButton> inputButtons = loadButtons(configuration, file, "inputs", InputButton.class, InputButton::setKey);
        dialogInventory.setInputButtons(inputButtons);

        dialogInventory.setFile(file);
        dialogInventory.setTargetPlayerNamePlaceholder(configuration.getString(path + "target-player-name-placeholder", configuration.getString(path + "target_player_name_placeholder", "%player_name%")));

        List<InventoryOption> inventoryOptions = this.menuPlugin.getInventoryManager().getInventoryOptions().entrySet().stream().flatMap(entry -> entry.getValue().stream().map(inventoryOption -> createInstance(entry.getKey(), inventoryOption))).filter(Objects::nonNull).toList();
        for (InventoryOption inventoryOption : inventoryOptions) {
            inventoryOption.loadInventory(dialogInventory, file, configuration, this.menuPlugin.getInventoryManager(), this.menuPlugin.getButtonManager());
        }
        return dialogInventory;
    }

    /**
     * Loads InventoryOption
     */
    private InventoryOption createInstance(Plugin plugin, Class<? extends InventoryOption> aClass) {
        try {
            Constructor<? extends InventoryOption> constructor = aClass.getConstructor(Plugin.class);
            return constructor.newInstance(plugin);
        } catch (NoSuchMethodException ignored) {
            try {
                return aClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                return null;
            }
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Loads buttons from the configuration
     */
    private <T extends Button> List<T> loadButtons(
            YamlConfiguration configuration,
            File file,
            String sectionKey,
            Class<T> buttonClass,
            BiConsumer<T, String> postProcess
    ) {
        List<T> buttons = new ArrayList<>();

        ConfigurationSection section = configuration.getConfigurationSection(sectionKey);

        if (section == null) {
            return buttons;
        }

        Loader<Button> loader = this.menuPlugin.getButtonManager()
                .getLoaderButton(this.menuPlugin, file, 54, new HashMap<>());

        for (String key : section.getKeys(false)) {
            String path = sectionKey + "." + key + ".";
            try {
                Button button = loader.load(configuration, path, key);
                T typedButton = getButtonType(button, buttonClass, path, file);

                if (postProcess != null) {
                    postProcess.accept(typedButton, key);
                }

                buttons.add(typedButton);
            } catch (Exception exception) {
                Logger.info(exception.getMessage(), Logger.LogType.ERROR);
            }
        }
        return buttons;
    }

    @Override
    public void save(DialogInventory object, @NonNull YamlConfiguration configuration, @NonNull String path, File file, Object... objects) {
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
                    if (Configuration.enableDebug){
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