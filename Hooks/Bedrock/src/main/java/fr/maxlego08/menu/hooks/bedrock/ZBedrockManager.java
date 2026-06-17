package fr.maxlego08.menu.hooks.bedrock;

import fr.maxlego08.menu.api.BedrockManager;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.event.events.PlayerOpenInventoryEvent;
import fr.maxlego08.menu.api.exceptions.DialogException;
import fr.maxlego08.menu.api.exceptions.DialogFileNotFound;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.inventory.bedrock.BedrockInventory;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.hooks.bedrock.loader.BedrockLoader;
import fr.maxlego08.menu.hooks.bedrock.loader.builder.BedrockBuilderManager;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.cumulus.response.FormResponse;
import org.geysermc.floodgate.api.FloodgateApi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class ZBedrockManager extends BedrockBuilderManager implements BedrockManager {
    private final MenuPlugin menuPlugin;
    private final InventoryManager inventoryManager;
    private final Map<String, List<BedrockInventory<?,?,?>>> bedrockInventory = new HashMap<>();
    private final Map<UUID, BedrockInventory<?,?,?>> activeBedrockInventory = new HashMap<>();
    private final MetaUpdater metaUpdater;

    public ZBedrockManager(final MenuPlugin menuPlugin) {
        super(menuPlugin);
        this.menuPlugin = menuPlugin;
        this.inventoryManager = menuPlugin.getInventoryManager();
        this.metaUpdater = menuPlugin.getMetaUpdater();
    }

    @Override
    public BedrockInventory<?,?,?> loadBedrockInventory(Plugin plugin, String fileName) throws DialogException {
        try {
            return this.loadInventory(plugin, fileName);
        } catch (InventoryException e) {
            throw new DialogException("Failed to load dialog: " + e.getMessage());
        }
    }

    @Override
    public Optional<BedrockInventory<?,?,?>> getBedrockInventory(String dialogName) {
        Optional<BedrockInventory<?,?,?>> dialogs;
        if (dialogName.contains(":")){
            String[] values = dialogName.split(":",2);
            dialogs = this.getBedrockInventory(values[0], values[1]);
        } else {
            dialogs = this.getBedrockInventoryOptional(dialogName);
        }
        return dialogs;
    }
    public Optional<BedrockInventory<?,?,?>> getBedrockInventoryOptional(String name) {
        for (List<BedrockInventory<?,?,?>> dialogList : this.bedrockInventory.values()) {
            for (BedrockInventory<?,?,?> dialog : dialogList) {
                if (dialog.getFileName().equals(name) || dialog.getName().equals(name)) {
                    return Optional.of(dialog);
                }
            }
        }
        return Optional.empty();
    }
    @Override
    public Optional<BedrockInventory<?,?,?>> getBedrockInventory(String pluginName, String fileName) {
        List<BedrockInventory<?,?,?>> pluginDialogs = this.bedrockInventory.get(pluginName);
        if (pluginDialogs == null) return Optional.empty();

        return pluginDialogs.stream()
                .filter(dialog -> dialog.getFileName().equals(fileName) || dialog.getName().equals(fileName))
                .findFirst();
    }

    @Override
    public Optional<BedrockInventory<?,?,?>> getBedrockInventory(Plugin plugin, String fileName) {
        List<BedrockInventory<?,?,?>> pluginDialogs = this.bedrockInventory.get(plugin.getName());
        if (pluginDialogs == null) return Optional.empty();

        return pluginDialogs.stream()
                .filter(dialog -> dialog.getFileName().equals(fileName))
                .findFirst();
    }

    @Override
    public void deleteBedrockInventory(String name) {
        for (List<BedrockInventory<?,?,?>> dialogList : this.bedrockInventory.values()) {
            dialogList.removeIf(dialog ->
                    dialog.getFileName().equals(name) || dialog.getName().equals(name)
            );
        }
    }

    @Override
    public void deleteBedrockInventory(Plugin plugin) {
        this.bedrockInventory.remove(plugin.getName());
    }

    @Override
    public void loadBedrockInventory() {
        File folder = new File(this.menuPlugin.getDataFolder(), "bedrock");
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
    public BedrockInventory<?,?,?> loadInventory(Plugin plugin, String fileName) throws DialogException, InventoryException {
        return this.loadInventory(plugin, fileName, (Class) BedrockInventory.class);
    }

    @Override
    public BedrockInventory<?,?,?> loadInventory(Plugin plugin, File file) throws DialogException, InventoryException {
        return this.loadInventory(plugin, file, (Class) BedrockInventory.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public BedrockInventory<?,?,?> loadInventory(Plugin plugin, String fileName, Class<? extends BedrockInventory<?, ?, ?>> dialogClass) throws DialogException, InventoryException {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            throw new DialogFileNotFound("Dialog file not found: " + fileName + " in " + plugin.getDataFolder().getAbsolutePath() + "/" + fileName);
        }
        return this.loadInventory(plugin, file, dialogClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public BedrockInventory<?,?,?> loadInventory(Plugin plugin, File file, Class<? extends BedrockInventory<?, ?, ?>> dialogClass) throws DialogException, InventoryException {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        Loader<AbstractBedrockInventory<?,?,?>> loader = new BedrockLoader(this.menuPlugin, this);
        BedrockInventory<?,?,?> dialog = loader.load(configuration, "", file, (Class<AbstractBedrockInventory<?,?,?>>) dialogClass, plugin);

        List<BedrockInventory<?,?,?>> dialogsList = this.bedrockInventory.computeIfAbsent(plugin.getName(), k -> new ArrayList<>());
        dialogsList.add(dialog);

        if (Configuration.enableInformationMessage) {
            Logger.info(file.getPath() + " loaded successfully!");
        }

        return dialog;
    }

    @Override
    public void reloadBedrockInventory() {
        this.bedrockInventory.clear();
        this.activeBedrockInventory.clear();

        this.loadBedrockInventory();

        Logger.info("Bedrock Inventory reloaded successfully!");
    }

    /**
     * Opens a specific dialog for the specified player
     */

    @Override
    public void openBedrockInventory(Player player, BedrockInventory<?,?,?> bedrockInventory) {
        this.openBedrockInventory(player, bedrockInventory, new ArrayList<>());
    }

    @Override
    public void openBedrockInventory(Player player, BedrockInventory<?,?,?> bedrockInventory, List<Inventory> oldInventories) {
        if (!this.isBedrockPlayer(player)){
            this.menuPlugin.getMetaUpdater().sendMessage(player, Message.BEDROCK_OPEN_ERROR_NOT_BEDROCK_PLAYER.getMessage());
            return;
        }

        PlayerOpenInventoryEvent playerOpenInventoryEvent = new PlayerOpenInventoryEvent(player, bedrockInventory, 1, oldInventories);
        if (Configuration.enableFastEvent) {
            this.menuPlugin.getInventoryManager().getFastEvents().forEach(event -> event.onPlayerOpenInventory(playerOpenInventoryEvent));
        } else playerOpenInventoryEvent.call();
        if (playerOpenInventoryEvent.isCancelled()) return;

        Player targetPlayer = Bukkit.getPlayer(this.menuPlugin.parse(player, bedrockInventory.getTargetPlayerNamePlaceholder()));
        if (targetPlayer == null) {
            targetPlayer = player;
        }

        try {
            boolean canOpen = this.checkRequirement(bedrockInventory.getOpenRequirement(), player);
            if (!canOpen){
                return;
            }

            InventoryEngine fakeInventory = this.inventoryManager.getFakeInventory();
            FormBuilder formBuilder = bedrockInventory.buildForm(targetPlayer, this.metaUpdater, fakeInventory);
            Form form = this.withDefaultResultHandler(formBuilder, player, bedrockInventory);

            FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);

            bedrockInventory.getOpenActions().forEach(action -> action.preExecute(player, null, fakeInventory, new Placeholders()));

            this.activeBedrockInventory.put(player.getUniqueId(), bedrockInventory);
        } catch (Exception e) {
            if (Configuration.enableInformationMessage){
                Logger.info("Failed to open bedrock inventory for player: " + player.getName()+" error :"+ e.getMessage(), Logger.LogType.ERROR);
                if (Configuration.enableDebug){
                    Logger.info("Error details: "+e, Logger.LogType.ERROR);
                }
            }
        }
    }

    protected  <B extends FormBuilder<B, F, R>, F extends Form, R extends FormResponse> Form withDefaultResultHandler(B builder, Player player, BedrockInventory inventory) {
        builder.closedResultHandler((form) -> {
            this.activeBedrockInventory.remove(player.getUniqueId());

            InventoryEngine fakeInventory = this.inventoryManager.getFakeInventory();
            inventory.getCloseActions().forEach(action -> action.preExecute(player, null, fakeInventory, new Placeholders()));
        });
        return builder.build();
    }

    /**
     * Gets the active bedrock Inventory for a player
     */
    public Optional<BedrockInventory<?,?,?>> getActiveBedrockInventory(Player player) {
        return Optional.ofNullable(this.activeBedrockInventory.get(player.getUniqueId()));
    }

    /**
     * Removes the active bedrock Inventory for a player
     */
    public void removeActiveBedrockInventory(Player player) {
        this.activeBedrockInventory.remove(player.getUniqueId());
    }

    public boolean openBedrockInventoryByName(Player player, String dialogName) {
        Optional<BedrockInventory<?,?,?>> dialog = this.getBedrockInventory(dialogName);
        if (dialog.isPresent()) {
            this.openBedrockInventory(player, dialog.get());
            return true;
        }
        return false;
    }

    protected boolean checkRequirement(Requirement requirement, Player player) {
        if (requirement == null) return true;
        InventoryEngine fakeInventory = this.menuPlugin.getInventoryManager().getFakeInventory();
        Placeholders placeholder = new Placeholders();
        return requirement.execute(player, null, fakeInventory, placeholder);
    }
    
    @Override
    public Collection<BedrockInventory<?,?,?>> getBedrockInventory() {
        List<BedrockInventory<?,?,?>> allDialogs = new ArrayList<>();
        for (List<BedrockInventory<?,?,?>> dialogList : this.bedrockInventory.values()) {
            allDialogs.addAll(dialogList);
        }
        return Collections.unmodifiableCollection(allDialogs);
    }

    @Override
    public boolean isBedrockPlayer(Player player) {
        return FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
    }

    @Override
    public boolean isBedrockPlayer(String playerName) {
        Player player = this.menuPlugin.getServer().getPlayerExact(playerName);
        return player != null && this.isBedrockPlayer(player);
    }
}