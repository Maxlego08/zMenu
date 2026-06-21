package fr.maxlego08.menu.hooks.dialogs;

import fr.maxlego08.menu.api.DialogManager;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.ConfigManagerInt;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.event.events.PlayerOpenInventoryEvent;
import fr.maxlego08.menu.api.exceptions.DialogException;
import fr.maxlego08.menu.api.exceptions.DialogFileNotFound;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.hooks.ComponentMeta;
import fr.maxlego08.menu.hooks.dialogs.inventory.AbstractDialogInventory;
import fr.maxlego08.menu.hooks.dialogs.loader.DialogLoader;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.DialogBuilderManager;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.papermc.paper.dialog.Dialog;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class ZDialogManager extends DialogBuilderManager implements DialogManager {
    private final MenuPlugin menuPlugin;
    private final ConfigManagerInt configManager;
    private static InventoryManager inventoryManager;
    private final Map<String, List<AbstractDialogInventory>> dialogs = new HashMap<>();
    private final Map<UUID, DialogInventory> activeDialogs = new HashMap<>();

    private final ComponentMeta paperComponent;

    public ZDialogManager(final MenuPlugin menuPlugin, final ConfigManagerInt configManager) {
        super(menuPlugin);
        this.menuPlugin = menuPlugin;
        this.configManager = configManager;
        this.paperComponent = ((ComponentMeta) menuPlugin.getMetaUpdater());
        inventoryManager = menuPlugin.getInventoryManager();
    }

    @Override
    public DialogInventory loadDialog(Plugin plugin, String fileName) throws DialogException {
        try {
            return this.loadInventory(plugin, fileName);
        } catch (InventoryException e) {
            throw new DialogException("Failed to load dialog: " + e.getMessage());
        }
    }

    @Override
    public Optional<DialogInventory> getDialog(String dialogName) {
        Optional<DialogInventory> dialogs;
        if (dialogName.contains(":")){
            String[] values = dialogName.split(":",2);
            dialogs = this.getDialog(values[0], values[1]);
        } else {
            dialogs = this.getDialogOptional(dialogName);
        }
        return dialogs;
    }
    public Optional<DialogInventory> getDialogOptional(String name) {
        for (List<AbstractDialogInventory> dialogList : this.dialogs.values()) {
            for (AbstractDialogInventory dialog : dialogList) {
                if (dialog.getFileName().equals(name) || dialog.getName().equals(name)) {
                    return Optional.of(dialog);
                }
            }
        }
        return Optional.empty();
    }
    @Override
    public Optional<DialogInventory> getDialog(String pluginName, String fileName) {
        List<AbstractDialogInventory> pluginDialogs = this.dialogs.get(pluginName);
        if (pluginDialogs == null) return Optional.empty();

        return pluginDialogs.stream()
                .filter(dialog -> dialog.getFileName().equals(fileName) || dialog.getName().equals(fileName))
                .map(dialog -> (DialogInventory) dialog)
                .findFirst();
    }

    @Override
    public Optional<DialogInventory> getDialog(Plugin plugin, String fileName) {
        List<AbstractDialogInventory> pluginDialogs = this.dialogs.get(plugin.getName());
        if (pluginDialogs == null) return Optional.empty();

        return pluginDialogs.stream()
                .filter(dialog -> dialog.getFileName().equals(fileName))
                .map(dialog -> (DialogInventory) dialog)
                .findFirst();
    }

    @Override
    public void deleteDialog(String name) {
        for (List<AbstractDialogInventory> dialogList : this.dialogs.values()) {
            dialogList.removeIf(dialog ->
                    dialog.getFileName().equals(name) || dialog.getName().equals(name)
            );
        }
    }

    @Override
    public void deleteDialog(Plugin plugin) {
        this.dialogs.remove(plugin.getName());
    }

    @Override
    public void loadDialogs() {
        File folder = new File(this.menuPlugin.getDataFolder(), "dialogs");
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
                            Logger.info("Failed to load dialog from file: " + file.getName(), Logger.LogType.WARNING);

                        }
                    });
        } catch (IOException exception) {
            Logger.info("Failed to load dialogs", Logger.LogType.WARNING);
        }
    }

    @Override
    public DialogInventory loadInventory(Plugin plugin, String fileName) throws DialogException, InventoryException {
        return this.loadInventory(plugin, fileName, AbstractDialogInventory.class);
    }

    @Override
    public DialogInventory loadInventory(Plugin plugin, File file) throws DialogException, InventoryException {
        return this.loadInventory(plugin, file, AbstractDialogInventory.class);
    }

    @Override
    public DialogInventory loadInventory(Plugin plugin, String fileName, Class<? extends DialogInventory> dialogClass)
            throws DialogException, InventoryException {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            throw new DialogFileNotFound("Dialog file not found: " + fileName + " in " +
                    plugin.getDataFolder().getAbsolutePath() + "/" + fileName);
        }
        return this.loadInventory(plugin, file, dialogClass);
    }

    @Override
    public DialogInventory loadInventory(Plugin plugin, File file, Class<? extends DialogInventory> dialogClass)
            throws DialogException, InventoryException {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        Loader<AbstractDialogInventory> loader = new DialogLoader(this.menuPlugin, this);
        AbstractDialogInventory dialog = loader.load(configuration, "", file, dialogClass, plugin);

        List<AbstractDialogInventory> dialogsList = this.dialogs.computeIfAbsent(plugin.getName(), k -> new ArrayList<>());
        dialogsList.add(dialog);

        if (Configuration.enableInformationMessage) {
            Logger.info(file.getPath() + " loaded successfully!");
        }

        return dialog;
    }

    @Override
    public void reloadDialogs() {
        this.dialogs.clear();
        this.activeDialogs.clear();

        this.loadDialogs();

        Logger.info("Dialogs reloaded successfully!");
    }

    /**
     * Opens a specific dialog for the specified player
     */
    @Override
    public void openDialog(@NotNull Player player,@NotNull DialogInventory zDialog) {
        this.openDialog(player, zDialog, new ArrayList<>());
    }

    @Override
    public void openDialog(Player player, DialogInventory dialogInventory, List<Inventory> oldInventories) {
        PlayerOpenInventoryEvent playerOpenInventoryEvent = new PlayerOpenInventoryEvent(player, dialogInventory, 1, oldInventories);
        if (Configuration.enableFastEvent) {
            this.menuPlugin.getInventoryManager().getFastEvents().forEach(event -> event.onPlayerOpenInventory(playerOpenInventoryEvent));
        } else playerOpenInventoryEvent.call();
        if (playerOpenInventoryEvent.isCancelled()) return;


        Player targetPlayer = Bukkit.getPlayer(this.menuPlugin.parse(player, dialogInventory.getTargetPlayerNamePlaceholder()));
        if (targetPlayer == null) {
            targetPlayer = player;
        }

        try {
            boolean canOpen = this.checkRequirement(dialogInventory.getOpenRequirement(), player);
            if (!canOpen){
                return;
            }

            InventoryEngine fakeInventory = this.menuPlugin.getInventoryManager().getFakeInventory();
            Placeholders placeholders = new Placeholders();
            placeholders.register("player", player.getName());

            Dialog dialog = dialogInventory.buildDialog(targetPlayer, this.paperComponent, fakeInventory, placeholders);

            player.showDialog(dialog);

            this.activeDialogs.put(player.getUniqueId(), dialogInventory);
        } catch (Exception e) {
            if (Configuration.enableInformationMessage){
                Logger.info("Failed to open dialog for player: " + player.getName()+" error :"+ e.getMessage(), Logger.LogType.ERROR);
                if (Configuration.enableDebug){
                    Logger.info("Error details: "+e, Logger.LogType.ERROR);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Gets the active dialog for a player
     */
    public Optional<DialogInventory> getActiveDialog(Player player) {
        return Optional.ofNullable(this.activeDialogs.get(player.getUniqueId()));
    }

    /**
     * Removes the active dialog for a player
     */
    public void removeActiveDialog(@NotNull Player player) {
        this.activeDialogs.remove(player.getUniqueId());
    }

    public boolean openDialogByName(@NotNull Player player, String dialogName) {
        Optional<DialogInventory> dialog = this.getDialog(dialogName);
        if (dialog.isPresent()) {
            this.openDialog(player, dialog.get());
            return true;
        }
        return false;
    }
    @Override
    public Collection<DialogInventory> getDialogs() {
        List<DialogInventory> allDialogs = new ArrayList<>();
        for (List<AbstractDialogInventory> dialogList : this.dialogs.values()) {
            allDialogs.addAll(dialogList);
        }
        return Collections.unmodifiableCollection(allDialogs);
    }

    @Override
    public ConfigManagerInt getConfigManager(){
        return this.configManager;
    }

    protected boolean checkRequirement(Requirement requirement, Player player) {
        if (requirement == null) return true;
        InventoryEngine fakeInventory = this.menuPlugin.getInventoryManager().getFakeInventory();
        Placeholders placeholder = new Placeholders();
        return requirement.execute(player, null, fakeInventory, placeholder);
    }
}