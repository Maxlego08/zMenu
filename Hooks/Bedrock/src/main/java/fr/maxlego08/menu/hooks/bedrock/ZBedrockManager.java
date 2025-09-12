package fr.maxlego08.menu.hooks.bedrock;

import fr.maxlego08.menu.api.*;
import fr.maxlego08.menu.api.button.bedrock.BedrockButton;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.exceptions.DialogException;
import fr.maxlego08.menu.api.exceptions.DialogFileNotFound;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.hooks.bedrock.loader.BedrockLoader;
import fr.maxlego08.menu.hooks.bedrock.loader.builder.BedrockBuilderClass;
import fr.maxlego08.menu.hooks.bedrock.loader.builder.bedrock.ButtonBuilder;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.cumulus.response.FormResponse;
import org.geysermc.floodgate.api.FloodgateApi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class ZBedrockManager extends AbstractBedrockInventoryManager implements BedrockManager {
    private final MenuPlugin menuPlugin;
    private static InventoryManager inventoryManager;
    private final Map<String, List<BedrockInventory>> bedrockInventory = new HashMap<>();
    private final Map<UUID, BedrockInventory> activeBedrockInventory = new HashMap<>();
    private final BedrockBuilderClass dialogBuilders;

    public ZBedrockManager(final MenuPlugin menuPlugin) {
        super(menuPlugin);
        this.menuPlugin = menuPlugin;
        this.dialogBuilders = new BedrockBuilderClass(this.menuPlugin);
        inventoryManager = menuPlugin.getInventoryManager();
    }

    @Override
    public BedrockInventory loadBedrockInventory(Plugin plugin, String fileName) throws DialogException {
        try {
            return loadInventory(plugin, fileName);
        } catch (InventoryException e) {
            throw new DialogException("Failed to load dialog: " + e.getMessage());
        }
    }

    @Override
    public Optional<BedrockInventory> getBedrockInventory(String dialogName) {
        Optional<BedrockInventory> dialogs;
        if (dialogName.contains(":")){
            String[] values = dialogName.split(":",2);
            dialogs = getBedrockInventory(values[0], values[1]);
        } else {
            dialogs = getBedrockInventoryOptional(dialogName);
        }
        return dialogs;
    }
    public Optional<BedrockInventory> getBedrockInventoryOptional(String name) {
        for (List<BedrockInventory> dialogList : bedrockInventory.values()) {
            for (BedrockInventory dialog : dialogList) {
                if (dialog.getFileName().equals(name) || dialog.getName(null).equals(name)) {
                    return Optional.of(dialog);
                }
            }
        }
        return Optional.empty();
    }
    @Override
    public Optional<BedrockInventory> getBedrockInventory(String pluginName, String fileName) {
        List<BedrockInventory> pluginDialogs = bedrockInventory.get(pluginName);
        if (pluginDialogs == null) return Optional.empty();

        return pluginDialogs.stream()
                .filter(dialog -> dialog.getFileName().equals(fileName) || dialog.getName(null).equals(fileName))
                .findFirst();
    }

    @Override
    public Optional<BedrockInventory> getBedrockInventory(Plugin plugin, String fileName) {
        List<BedrockInventory> pluginDialogs = bedrockInventory.get(plugin.getName());
        if (pluginDialogs == null) return Optional.empty();

        return pluginDialogs.stream()
                .filter(dialog -> dialog.getFileName().equals(fileName))
                .findFirst();
    }

    @Override
    public void deleteBedrockInventory(String name) {
        for (List<BedrockInventory> dialogList : bedrockInventory.values()) {
            dialogList.removeIf(dialog ->
                    dialog.getFileName().equals(name) || dialog.getName(null).equals(name)
            );
        }
    }

    @Override
    public void deleteBedrockInventory(Plugin plugin) {
        bedrockInventory.remove(plugin.getName());
    }

    @Override
    public void loadBedrockInventory() {
        File folder = new File(menuPlugin.getDataFolder(), "bedrock");
        if (!folder.exists()) {
            folder.mkdirs();
            return;
        }

        try (Stream<Path> stream = Files.walk(Paths.get(folder.getPath()))) {
            stream.skip(1)
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(file -> file.getName().endsWith(".yml"))
                    .forEach(file -> {
                        try {
                            this.loadInventory(this.menuPlugin, file);
                        } catch (DialogException | InventoryException exception) {
                            Logger.info("Failed to load bedrock inventory from file: " + file.getName(), Logger.LogType.WARNING);

                        }
                    });
        } catch (IOException exception) {
            Logger.info("Failed to load bedrock inventory", Logger.LogType.WARNING);
        }
    }

    @Override
    public BedrockInventory loadInventory(Plugin plugin, String fileName) throws DialogException, InventoryException {
        return this.loadInventory(plugin, fileName, ZBedrockInventory.class);
    }

    @Override
    public BedrockInventory loadInventory(Plugin plugin, File file) throws DialogException, InventoryException {
        return this.loadInventory(plugin, file, ZBedrockInventory.class);
    }

    @Override
    public BedrockInventory loadInventory(Plugin plugin, String fileName, Class<? extends BedrockInventory> dialogClass)
            throws DialogException, InventoryException {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            throw new DialogFileNotFound("Dialog file not found: " + fileName + " in " +
                    plugin.getDataFolder().getAbsolutePath() + "/" + fileName);
        }
        return this.loadInventory(plugin, file, dialogClass);
    }

    @Override
    public BedrockInventory loadInventory(Plugin plugin, File file, Class<? extends BedrockInventory> dialogClass)
            throws DialogException, InventoryException {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        Loader<BedrockInventory> loader = new BedrockLoader(this.menuPlugin, this);
        BedrockInventory dialog = loader.load(configuration, "", file, dialogClass, plugin);

        List<BedrockInventory> dialogsList = this.bedrockInventory.computeIfAbsent(plugin.getName(), k -> new ArrayList<>());
        dialogsList.add(dialog);

        if (Config.enableInformationMessage) {
            Logger.info(file.getPath() + " loaded successfully!");
        }

        return dialog;
    }

    @Override
    public void reloadBedrockInventory() {
        bedrockInventory.clear();
        activeBedrockInventory.clear();

        loadBedrockInventory();

        Logger.info("Bedrock Inventory reloaded successfully!");
    }

    /**
     * Opens a specific dialog for the specified player
     */
    @Override
    public void openBedrockInventory(Player player, BedrockInventory bedrockInventory) {
        try {
            boolean canOpen = bedrockInventory.hasOpenRequirement(player);
            if (!canOpen){
                return;
            }

            Form form = createBedrockByType(bedrockInventory, player);

            activeBedrockInventory.put(player.getUniqueId(), bedrockInventory);
            FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
        } catch (Exception e) {
            if (Config.enableInformationMessage){
                Logger.info("Failed to open dialog for player: " + player.getName()+" error :"+ e.getMessage(), Logger.LogType.ERROR);
                if (Config.enableDebug){
                    Logger.info("Error details: "+e, Logger.LogType.ERROR);
                }
            }
        }
    }

    /**
     * Creates a dialog based on the dialog type
     */
    private Form createBedrockByType(BedrockInventory inventory, Player player) {
        return switch (inventory.getBedrockType()) {
            case MODAL -> {
                List<BedrockButton> buttons = inventory.getBedrockButtons(player);
                ModalForm.Builder builder = ModalForm.builder()
                        .title(inventory.getName(player))
                        .content(inventory.getContent(player))
                        .button1(buttons.get(0).getText())
                        .button2(buttons.get(1).getText())
                        .validResultHandler((form, responseData) -> {
                            Placeholders placeholders = new Placeholders();
                            placeholders.register("content", form.content());

                            int id = responseData.clickedButtonId();
                            for (Requirement requirement : buttons.get(id).getClickRequirements()) {
                                requirement.execute(player, null, inventoryManager.getFakeInventory(), placeholders);
                            }
                        });
                
                yield withDefaultResultHandler(builder, player, inventory);
            }

            case SIMPLE -> {
                SimpleForm.Builder builder = SimpleForm.builder()
                        .title(inventory.getName(player))
                        .content(inventory.getContent(player));
                
                List<BedrockButton> buttons = inventory.getBedrockButtons(player);
                ButtonBuilder buttonBuilder = this.dialogBuilders.getBedrockButtonBuilder();
                buttons.forEach(button -> {
                    builder.button(buttonBuilder.build(player, button));
                });
                builder.validResultHandler((form, responseData) -> {
                    int slot = responseData.clickedButtonId();

                    for (Requirement requirement : buttons.get(slot).getClickRequirements()) {
                        requirement.execute(player, null, inventoryManager.getFakeInventory(), new Placeholders());
                    }
                });
                yield withDefaultResultHandler(builder, player, inventory);
            }

            case CUSTOM -> {
                CustomForm.Builder builder = CustomForm.builder()
                        .title(inventory.getName(player));

                
                List<InputButton> buttons = inventory.getInputButtons(player);
                getInputComponents(player, buttons).forEach(builder::component);

                builder.validResultHandler((form, responseData) -> {
                    Placeholders placeholders = new Placeholders();

                    for (int i = 0; i < buttons.size(); i++) {
                        InputButton input = buttons.get(i);
                        String key = input.getKey();

                        Object rawValue = responseData.valueAt(i);
                        if (rawValue == null) {
                            continue;
                        }

                        String value = rawValue.toString();
                        if (value == null) {
                            continue;
                        }

                        placeholders.register(key, value);
                    }

                    for (Requirement requirement : inventory.getRequirements()) {
                        requirement.execute(player, null, inventoryManager.getFakeInventory(), placeholders);
                    }
                });
                yield withDefaultResultHandler(builder, player, inventory);
            }
        };
    }

    protected  <B extends FormBuilder<B, F, R>, F extends Form, R extends FormResponse> Form withDefaultResultHandler(B builder, Player player, BedrockInventory inventory) {
        builder.closedOrInvalidResultHandler((form, responseData) -> {
            player.sendMessage("Formulaire fermé ou invalide !");
        });
        return builder.build();
    }

    /**
     * Gets the active bedrock Inventory for a player
     */
    public Optional<BedrockInventory> getActiveBedrockInventory(Player player) {
        return Optional.ofNullable(activeBedrockInventory.get(player.getUniqueId()));
    }

    /**
     * Removes the active bedrock Inventory for a player
     */
    public void removeActiveBedrockInventory(Player player) {
        activeBedrockInventory.remove(player.getUniqueId());
    }

    public boolean openBedrockInventoryByName(Player player, String dialogName) {
        Optional<BedrockInventory> dialog = getBedrockInventory(dialogName);
        if (dialog.isPresent()) {
            openBedrockInventory(player, dialog.get());
            return true;
        }
        return false;
    }
    
    @Override
    public Collection<BedrockInventory> getBedrockInventory() {
        List<BedrockInventory> allDialogs = new ArrayList<>();
        for (List<BedrockInventory> dialogList : bedrockInventory.values()) {
            allDialogs.addAll(dialogList);
        }
        return Collections.unmodifiableCollection(allDialogs);
    }
    @Override
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
}