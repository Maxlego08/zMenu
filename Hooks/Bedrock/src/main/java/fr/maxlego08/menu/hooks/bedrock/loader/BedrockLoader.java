package fr.maxlego08.menu.hooks.bedrock.loader;

import fr.maxlego08.menu.api.BedrockInventory;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.bedrock.BedrockButton;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.bedrock.BedrockType;
import fr.maxlego08.menu.api.exceptions.InventoryButtonException;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.hooks.bedrock.ZBedrockInventory;
import fr.maxlego08.menu.hooks.bedrock.ZBedrockManager;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

public class BedrockLoader implements Loader<BedrockInventory> {
    private final MenuPlugin menuPlugin;
    private final ZBedrockManager manager;

    public BedrockLoader(MenuPlugin menuPlugin, ZBedrockManager manager) {
        this.menuPlugin = menuPlugin;
        this.manager = manager;
    }

    @Override
    public BedrockInventory load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {
        File file = (File) objects[0];

        String title = configuration.getString("name", "");
        String content = configuration.getString("content", "");

        ZBedrockInventory BedrockInventory = new ZBedrockInventory(menuPlugin, file.getName(), title, content);

        String typeString = configuration.getString("type", "SIMPLE");
        BedrockType bedrockType;
        try {
            bedrockType = BedrockType.valueOf(typeString.toUpperCase());
            BedrockInventory.setBedrockType(bedrockType);
        } catch (IllegalArgumentException e) {
            throw new InventoryException("Invalid dialog type: " + typeString);
        }

        if (configuration.isConfigurationSection("open-requirement")){
            try {
                Requirement openRequirement = loadRequirement(configuration, "open-requirement.", file);
                BedrockInventory.setOpenRequirement(openRequirement);
            } catch (InventoryException e) {
                Logger.info("Failed to load open requirement: " + e.getMessage(), Logger.LogType.WARNING);
            }
        }

        loadSpecificItems(bedrockType, configuration, BedrockInventory, file);

        BedrockInventory.setFile(file);
        return BedrockInventory;
    }

    /**
     * Loads input buttons from the configuration
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
    public void save(BedrockInventory object, YamlConfiguration configuration, String path, File file, Object... objects) {
        //TODO: Implement save logic if needed
    }

    private void loadSpecificItems(BedrockType bedrockType, YamlConfiguration configuration, ZBedrockInventory bedrockInventory, File file) throws InventoryException {
        switch (bedrockType) {
            case MODAL -> {
                List<BedrockButton> bodyButtons = loadButtons(configuration, file, "buttons", BedrockButton.class, null);
                int buttonSize = bodyButtons.size();
                if (buttonSize < 2){
                    throw new InventoryException("Invalid buttons count for type MODAL: " + buttonSize);
                } else if (buttonSize > 2) {
                    Logger.info("MODAL type supports a maximum of 2 buttons. Only the first two are considered.", Logger.LogType.ERROR);
                }
                bedrockInventory.setBedrockButtons(bodyButtons.subList(0, 2));
            }
            case SIMPLE -> {
                List<BedrockButton> bodyButtons = loadButtons(configuration, file, "buttons", BedrockButton.class, null);
                bedrockInventory.setBedrockButtons(bodyButtons);
            }
            case CUSTOM -> {
                List<InputButton> inputButtons = loadButtons(configuration, file, "buttons", InputButton.class, InputButton::setKey);
                List<Requirement> actions = loadRequirements(configuration, "actions", file);
                bedrockInventory.setInputButtons(inputButtons);
                bedrockInventory.setRequirements(actions);
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