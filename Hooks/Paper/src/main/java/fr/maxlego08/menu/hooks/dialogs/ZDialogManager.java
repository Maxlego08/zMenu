package fr.maxlego08.menu.hooks.dialogs;

import fr.maxlego08.menu.api.DialogManager;
import fr.maxlego08.menu.api.Inventory;
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
import fr.maxlego08.menu.api.utils.DialogFallback;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.version.ClientVersionManager;
import fr.maxlego08.menu.hooks.ComponentMeta;
import fr.maxlego08.menu.hooks.dialogs.inventory.AbstractDialogInventory;
import fr.maxlego08.menu.hooks.dialogs.loader.DialogLoader;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.papermc.paper.dialog.Dialog;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ZDialogManager implements DialogManager, Listener {
    private final MenuPlugin menuPlugin;
    private final ConfigManagerInt configManager;

    private final Set<String> dialogNames = ConcurrentHashMap.newKeySet();
    private final Map<String, List<AbstractDialogInventory>> dialogs = new ConcurrentHashMap<>();
    private final Map<UUID, DialogInventory> activeDialogs = new ConcurrentHashMap<>();

    private final ComponentMeta paperComponent;

    public ZDialogManager(final MenuPlugin menuPlugin, final ConfigManagerInt configManager) {
        this.menuPlugin = menuPlugin;
        this.configManager = configManager;
        this.paperComponent = ((ComponentMeta) menuPlugin.getMetaUpdater());
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
                if (dialog.getFileName().equalsIgnoreCase(name) || dialog.getName().equalsIgnoreCase(name)) {
                    return Optional.of(dialog);
                }
            }
        }
        return Optional.empty();
    }
    @Override
    public Optional<DialogInventory> getDialog(String pluginName, String fileName) {
        Optional<Plugin> plugin = this.menuPlugin.getInventoryManager().getPluginIgnoreCase(pluginName);
        return plugin.isEmpty() || fileName == null ? Optional.empty() : this.getDialog(plugin.get(), fileName);
    }

    @Override
    public Optional<DialogInventory> getDialog(Plugin plugin, String fileName) {
        List<AbstractDialogInventory> pluginDialogs = this.dialogs.get(plugin.getName());
        if (pluginDialogs == null) return Optional.empty();

        return pluginDialogs.stream()
                .filter(dialog -> dialog.getFileName().equalsIgnoreCase(fileName) || dialog.getName().equalsIgnoreCase(fileName))
                .map(dialog -> (DialogInventory) dialog)
                .findFirst();
    }

    @Override
    public void deleteDialog(String name) {
        for (List<AbstractDialogInventory> dialogList : this.dialogs.values()) {
            dialogList.removeIf(dialog ->
                    dialog.getFileName().equalsIgnoreCase(name) || dialog.getName().equalsIgnoreCase(name)
            );
        }
        String suffix = ":" + name.toLowerCase(Locale.ROOT);
        this.dialogNames.removeIf(dialogName -> dialogName.endsWith(suffix));
    }

    @Override
    public void deleteDialog(Plugin plugin) {
        this.dialogs.remove(plugin.getName());
        this.dialogNames.removeIf(name -> name.startsWith(plugin.getName().toLowerCase(Locale.ROOT) + ":"));
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
        this.dialogNames.add(dialog.getPlugin().getName() + ":" + dialog.getFileName());

        if (Configuration.enableInformationMessage) {
            Logger.info(file.getPath() + " loaded successfully!");
        }

        return dialog;
    }

    @Override
    public void reloadDialogs() {
        this.dialogs.clear();
        this.activeDialogs.clear();
        this.dialogNames.clear();

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
        if (!this.menuPlugin.getClientVersionManager().supportsDialogs(player)) {
            if (!this.checkRequirement(dialogInventory.getOpenRequirement(), player)) return;
            this.openFallbackInventory(player, dialogInventory, oldInventories);
            return;
        }

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
                    Logger.error(e);
                }
            }
        }
    }

    /**
     * Opens the inventory configured under {@code fallback-inventory} for a player whose
     * client cannot render dialogs. Sends a message when no usable fallback exists, so the
     * player is never left with nothing happening.
     */
    private void openFallbackInventory(Player player, DialogInventory dialogInventory, List<Inventory> oldInventories) {
        DialogFallback fallback = dialogInventory.getFallbackInventory();

        if (fallback == null || !fallback.isValid()) {
            this.menuPlugin.getInventoryManager().sendMessage(player, Message.DIALOG_NOT_SUPPORTED, "%version%", ClientVersionManager.DIALOG_MINIMUM_VERSION.toString(), "%name%", dialogInventory.getFileName());
            return;
        }

        Optional<Inventory> optional = this.menuPlugin.getInventoryManager().getInventory(fallback.plugin(), fallback.inventoryName());
        if (optional.isEmpty()) {
            this.menuPlugin.getInventoryManager().sendMessage(player, Message.INVENTORY_NOT_FOUND, "%name%", dialogInventory.getFileName(), "%toName%", fallback.inventoryName(), "%plugin%", fallback.plugin());
            return;
        }

        try {
            this.menuPlugin.getInventoryManager().openInventory(player, optional.get(), fallback.page(), oldInventories);
        } catch (Exception exception) {
            Logger.info("Failed to open the fallback inventory " + fallback.inventoryName() + " of the dialog " + dialogInventory.getFileName() + " for " + player.getName() + ": " + exception.getMessage(), Logger.LogType.ERROR);
            if (Configuration.enableDebug) Logger.error(exception);
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

    /**
     * Drops the active dialog of a leaving player. Without this the map keeps one entry per
     * player who ever opened a dialog, and a reconnecting player inherits the dialog they
     * had open in a previous session.
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.activeDialogs.remove(event.getPlayer().getUniqueId());
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

    @Override
    public Set<String> getDialogNames() {
        return Collections.unmodifiableSet(this.dialogNames);
    }

    protected boolean checkRequirement(Requirement requirement, Player player) {
        if (requirement == null) return true;
        InventoryEngine fakeInventory = this.menuPlugin.getInventoryManager().getFakeInventory();
        Placeholders placeholder = new Placeholders();
        return requirement.execute(player, null, fakeInventory, placeholder);
    }
}