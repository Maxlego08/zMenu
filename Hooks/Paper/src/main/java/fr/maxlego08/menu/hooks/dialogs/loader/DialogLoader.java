package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.InventoryOption;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogType;
import fr.maxlego08.menu.api.exceptions.InventoryButtonException;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.InventoryReplacement;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.hooks.dialogs.ZDialogManager;
import fr.maxlego08.menu.hooks.dialogs.inventory.AbstractDialogInventory;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.BiConsumer;

public class DialogLoader implements Loader<AbstractDialogInventory> {
    private final MenuPlugin menuPlugin;
    private final ZDialogManager manager;

    public DialogLoader(MenuPlugin menuPlugin, ZDialogManager manager) {
        this.menuPlugin = menuPlugin;
        this.manager = manager;
    }

    @Override
    public AbstractDialogInventory load(@NonNull YamlConfiguration configuration, @NonNull String path, Object... objects) throws InventoryException {
        File file = (File) objects[0];

        String name = configuration.getString("name", "");
        String externalTitle = configuration.getString("external-title", "");

        String typeString = configuration.getString("type", "NOTICE");
        DialogType dialogType;
        try {
            dialogType = DialogType.valueOf(typeString.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new InventoryException("Invalid dialog type: " + typeString);
        }


        Optional<DialogInventoryTypeLoader<?>> dialogInventoryTypeLoader = DialogInventoryTypeRegistry.getInstance().get(dialogType);
        if (dialogInventoryTypeLoader.isEmpty()) {
            throw new InventoryException("No loader found for dialog type: " + dialogType);
        }
        AbstractDialogInventory dialogInventory = dialogInventoryTypeLoader.get().load(this.menuPlugin, file, configuration, name, externalTitle);

        boolean canCloseWithEscape = configuration.getBoolean("can-close-with-escape", true);
        dialogInventory.setCanCloseWithEscape(canCloseWithEscape);

        boolean pause = configuration.getBoolean("pause", false);
        dialogInventory.setPause(pause);

        String afterActionString = configuration.getString("after-action", "CLOSE");
        try {
            dialogInventory.setAfterAction(afterActionString.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new InventoryException("Invalid after action: " + afterActionString);
        }
        if (configuration.isConfigurationSection("open-requirement")){
            try {
                Requirement openRequirement = this.loadRequirement(configuration, "open-requirement.", file);
                dialogInventory.setOpenRequirement(openRequirement);
            } catch (InventoryException e) {
                Logger.info("Failed to load open requirement: " + e.getMessage(), Logger.LogType.WARNING);
            }
        }
        if (configuration.isConfigurationSection("inventory-replacement")){
            String replacementName = configuration.getString("inventory-replacement.name", "");
            String replacementPlugin = configuration.getString("inventory-replacement.plugin", "zMenu");
            List<Integer> replacementPages = configuration.getIntegerList("inventory-replacement.pages");
            InventoryReplacement inventoryReplacement = new InventoryReplacement(replacementName, replacementPlugin, replacementPages);
            dialogInventory.setInventoryReplacement(inventoryReplacement);
        }

        List<BodyButton> bodyButtons = this.loadButtons(configuration, file, "body", BodyButton.class, null);
        dialogInventory.setBodyButtons(bodyButtons);

        List<InputButton> inputButtons = this.loadButtons(configuration, file, "inputs", InputButton.class, InputButton::setKey);
        dialogInventory.setInputButtons(inputButtons);

        dialogInventory.setFile(file);
        dialogInventory.setTargetPlayerNamePlaceholder(configuration.getString(path + "target-player-name-placeholder", configuration.getString(path + "target_player_name_placeholder", "%player_name%")));

        List<InventoryOption> inventoryOptions = this.menuPlugin.getInventoryManager().getInventoryOptions().entrySet().stream().flatMap(entry -> entry.getValue().stream().map(inventoryOption -> this.createInstance(entry.getKey(), inventoryOption))).filter(Objects::nonNull).toList();
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
                T typedButton = this.getButtonType(button, buttonClass, path, file);

                if (postProcess != null) {
                    Button current = button.getMasterParentButton();
                    while (current != null) {
                        if (buttonClass.isInstance(current)) {
                            postProcess.accept(buttonClass.cast(current), key);
                        }
                        current = current.getElseButton();
                    }
                }

                buttons.add(typedButton);
            } catch (Exception exception) {
                Logger.info(exception.getMessage(), Logger.LogType.ERROR);
            }
        }
        return buttons;
    }

    @Override
    public void save(AbstractDialogInventory object, @NonNull YamlConfiguration configuration, @NonNull String path, File file, Object... objects) {
        //TODO: Implement save logic if needed
    }

    protected Requirement loadRequirement(YamlConfiguration configuration, String path, File file) throws InventoryException {
        return this.menuPlugin.getButtonManager().loadRequirement(configuration, path, file);
    }

    @SuppressWarnings("unchecked")
    protected <T extends Button> T getButtonType(Button button, Class<T> verifClass, String path, File file) throws InventoryButtonException {
        if (verifClass.isInstance(button)) {
            if (button.getElseButton() != null){
                return this.getButtonType(button.getElseButton(), verifClass, path, file);
            }
            return (T) button;
        } else {
            throw new InventoryButtonException("The type " + button.getName() + " for the button " + path + " is not valid for this section" + file.getAbsolutePath());
        }
    }
}