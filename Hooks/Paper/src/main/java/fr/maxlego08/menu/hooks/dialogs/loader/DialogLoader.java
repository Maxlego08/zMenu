package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.hooks.dialogs.ZDialogInventory;
import fr.maxlego08.menu.hooks.dialogs.ZDialogManager;
import fr.maxlego08.menu.hooks.dialogs.ZDialogs;
import fr.maxlego08.menu.hooks.dialogs.buttons.BodyButton;
import fr.maxlego08.menu.hooks.dialogs.buttons.InputButton;
import fr.maxlego08.menu.hooks.dialogs.enums.DialogType;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.BodyLoader;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.DialogActionIntLoader;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.InputLoader;
import io.papermc.paper.registry.data.dialog.DialogBase;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

public class DialogLoader implements Loader<ZDialogs> {
    private final Plugin plugin;
    private final ZDialogManager manager;

    public DialogLoader(Plugin plugin, ZDialogManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public ZDialogs load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {
        File file = (File) objects[0];

        String name = configuration.getString("name", "");
        String externalTitle = configuration.getString("external-title", "");

        ZDialogInventory dialogInventory = new ZDialogInventory(plugin, name, file.getName(), externalTitle);

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
            DialogBase.DialogAfterAction afterAction = DialogBase.DialogAfterAction.valueOf(afterActionString.toUpperCase());
            dialogInventory.setAfterAction(afterAction);
        } catch (IllegalArgumentException e) {
            throw new InventoryException("Invalid after action: " + afterActionString);
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
    private List<BodyButton> loadBodyButtons(YamlConfiguration configuration, File file) throws InventoryException {
        List<BodyButton> bodyButtons = new ArrayList<>();

        ConfigurationSection bodySection = configuration.getConfigurationSection("body");

        if (bodySection == null) {
            plugin.getLogger().warning("No body section found in configuration");
            return bodyButtons;
        }

        for (String bodyKey : bodySection.getKeys(false)) {
            String path = "body." + bodyKey;

            String type = configuration.getString(path + ".type");

            if (type == null) {
                plugin.getLogger().warning("No type found for body element: " + bodyKey);
                continue;
            }

            Optional<BodyLoader> optionalLoader = manager.getBodyLoader(type.toLowerCase());

            if (optionalLoader.isPresent()) {
                BodyLoader loader = optionalLoader.get();

                BodyButton button = loader.load(path, file, configuration);
                button.setPlugin((MenuPlugin) plugin);
                button.setBodyType(loader.getBodyType());
                bodyButtons.add(button);
            } else {
                plugin.getLogger().warning("No body loader found for type: " + type);
            }
        }
        return bodyButtons;
    }
    private List<InputButton> loadInputButtons(YamlConfiguration configuration, File file) throws InventoryException {
        List<InputButton> inputButtons = new ArrayList<>();

        ConfigurationSection inputSection = configuration.getConfigurationSection("inputs");

        if (inputSection == null) {
            plugin.getLogger().warning("No inputs section found in configuration");
            return inputButtons;
        }

        for (String inputKey : inputSection.getKeys(false)) {
            String path = "inputs." + inputKey;

            String type = configuration.getString(path + ".type");
            if (type == null) {
                plugin.getLogger().warning("No type found for input element: " + inputKey);
                continue;
            }

            Optional<InputLoader> optionalInputType = manager.getInputLoader(type.toLowerCase());
            if (optionalInputType.isPresent()) {
                InputLoader loader = optionalInputType.get();

                InputButton button = loader.load(path, file, configuration);
                button.setPlugin((MenuPlugin) plugin);
                button.setInputType(loader.getInputType());
                button.setKey(inputKey);

                inputButtons.add(button);

            } else {
                plugin.getLogger().warning("No input loader found for type: " + type);
            }

        }
        return inputButtons;
    }

    @Override
    public void save(ZDialogs object, YamlConfiguration configuration, String path, File file, Object... objects) {
        //TODO: Implement save logic if needed
    }

    private void loadSpecificItems(DialogType dialogType, YamlConfiguration configuration, ZDialogInventory dialogInventory, File file) {
        switch (dialogType) {
            case NOTICE -> {
                List<Map<?, ?>> actions = configuration.getMapList("actions");
                for (Map<?, ?> action : actions) {
                    TypedMapAccessor accessor = new TypedMapAccessor((Map<String, Object>) action);
                    String actionType = accessor.getString("type");
                    Optional<DialogActionIntLoader> actionLoader = manager.getDialogAction(actionType);
                    if (actionLoader.isPresent()) {
                        DialogActionIntLoader actionIntLoader = actionLoader.get();
                        dialogInventory.addAction(actionIntLoader.load("actions", accessor, file));
                    } else {
                        plugin.getLogger().warning("No action loader found for type: " + actionType);
                    }
                }
                String noticeText = configuration.getString("notice.text","");
                String noticeTooltip = configuration.getString("notice.tooltip","");
                int noticeWidth = configuration.getInt("notice.width", 200);
                dialogInventory.setLabel(noticeText);
                dialogInventory.setLabelTooltip(noticeTooltip);
                dialogInventory.setLabelWidth(noticeWidth);

            }
            case CONFIRMATION -> {
                String yesText = configuration.getString("confirmation.yes-text");
                plugin.getLogger().log(Level.INFO, "Yes text: " + yesText);
                String yesTooltip = configuration.getString("confirmation.yes-tooltip");
                plugin.getLogger().log(Level.INFO, "Yes tooltip: " + yesTooltip);
                int yesWidth = configuration.getInt("confirmation.yes-width", 100);
                plugin.getLogger().log(Level.INFO, "Yes width: " + yesWidth);
                String noText = configuration.getString("confirmation.no-text");
                plugin.getLogger().log(Level.INFO, "No text: " + noText);
                String noTooltip = configuration.getString("confirmation.no-tooltip");
                plugin.getLogger().log(Level.INFO, "No tooltip: " + noTooltip);
                int noWidth = configuration.getInt("confirmation.no-width", 100);
                plugin.getLogger().log(Level.INFO, "No width: " + noWidth);
                dialogInventory.setYesText(yesText);
                dialogInventory.setNoText(noText);
                dialogInventory.setYesTooltip(yesTooltip);
                dialogInventory.setNoTooltip(noTooltip);
                dialogInventory.setYesWidth(yesWidth);
                dialogInventory.setNoWidth(noWidth);
                List<Map<?, ?>> yesActions = configuration.getMapList("yes-actions");
                for (Map<?, ?> yesAction : yesActions) {
                    TypedMapAccessor accessor = new TypedMapAccessor((Map<String, Object>) yesAction);
                    String yesActionType = accessor.getString("type");
                    Optional<DialogActionIntLoader> actionLoader = manager.getDialogAction(yesActionType);
                    if (actionLoader.isPresent()) {
                        DialogActionIntLoader actionIntLoader = actionLoader.get();
                        dialogInventory.addYesAction(actionIntLoader.load("yes-actions", accessor, file));
                    } else {
                        plugin.getLogger().warning("No action loader found for type: " + yesActionType);
                    }
                }
                List<Map<?, ?>> noActions = configuration.getMapList("no-actions");
                for (Map<?, ?> noAction : noActions) {
                    TypedMapAccessor accessor = new TypedMapAccessor((Map<String, Object>) noAction);
                    String noActionType = accessor.getString("type");
                    Optional<DialogActionIntLoader> actionLoader = manager.getDialogAction(noActionType);

                    if (actionLoader.isPresent()) {
                        DialogActionIntLoader actionIntLoader = actionLoader.get();
                        dialogInventory.addNoAction(actionIntLoader.load("no-actions", accessor, file));
                    } else {
                        plugin.getLogger().warning("No action loader found for type: " + noActionType);
                    }
                }
            }
        }
    }


}