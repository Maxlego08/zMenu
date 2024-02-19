package fr.maxlego08.menu;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.event.FastEvent;
import fr.maxlego08.menu.api.event.events.ButtonLoaderRegisterEvent;
import fr.maxlego08.menu.api.event.events.InventoryLoadEvent;
import fr.maxlego08.menu.api.event.events.PlayerOpenInventoryEvent;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.OpenWithItem;
import fr.maxlego08.menu.button.buttons.ZNoneButton;
import fr.maxlego08.menu.button.loader.BackLoader;
import fr.maxlego08.menu.button.loader.HomeLoader;
import fr.maxlego08.menu.button.loader.JumpLoader;
import fr.maxlego08.menu.button.loader.MainMenuLoader;
import fr.maxlego08.menu.button.loader.NextLoader;
import fr.maxlego08.menu.button.loader.NoneLoader;
import fr.maxlego08.menu.button.loader.PreviousLoader;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.exceptions.InventoryFileNotFound;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.itemstack.FullSimilar;
import fr.maxlego08.menu.itemstack.LoreSimilar;
import fr.maxlego08.menu.itemstack.MaterialSimilar;
import fr.maxlego08.menu.itemstack.ModelIdSimilar;
import fr.maxlego08.menu.itemstack.NameSimilar;
import fr.maxlego08.menu.loader.InventoryLoader;
import fr.maxlego08.menu.loader.MenuItemStackLoader;
import fr.maxlego08.menu.loader.actions.BroadcastLoader;
import fr.maxlego08.menu.loader.actions.BroadcastSoundLoader;
import fr.maxlego08.menu.loader.actions.ChatLoader;
import fr.maxlego08.menu.loader.actions.CloseLoader;
import fr.maxlego08.menu.loader.actions.ConnectLoader;
import fr.maxlego08.menu.loader.actions.ConsoleCommandLoader;
import fr.maxlego08.menu.loader.actions.DataLoader;
import fr.maxlego08.menu.loader.actions.MessageLoader;
import fr.maxlego08.menu.loader.actions.PlayerCommandLoader;
import fr.maxlego08.menu.loader.actions.ShopkeeperLoader;
import fr.maxlego08.menu.loader.actions.SoundLoader;
import fr.maxlego08.menu.loader.actions.TitleLoader;
import fr.maxlego08.menu.loader.permissible.ItemPermissibleLoader;
import fr.maxlego08.menu.loader.permissible.PermissionPermissibleLoader;
import fr.maxlego08.menu.loader.permissible.PlaceholderPermissibleLoader;
import fr.maxlego08.menu.loader.permissible.RegexPermissibleLoader;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.zcore.enums.EnumInventory;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.logger.Logger.LogType;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import fr.maxlego08.menu.zcore.utils.meta.Meta;
import fr.maxlego08.menu.zcore.utils.nms.ItemStackUtils;
import fr.maxlego08.menu.zcore.utils.storage.Persist;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ZInventoryManager extends ZUtils implements InventoryManager {

    private final Map<String, List<Inventory>> inventories = new HashMap<>();
    private final List<MaterialLoader> loaders = new ArrayList<>();
    private final MenuPlugin plugin;
    private final Map<UUID, Inventory> currentInventories = new HashMap<>();
    private final Map<Plugin, FastEvent> fastEventMap = new HashMap<>();
    private final Map<String, ItemStackSimilar> itemStackSimilarMap = new HashMap<>();

    public ZInventoryManager(MenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public void save(Persist persist) {
        // TODO
    }

    @Override
    public void load(Persist persist) {

        this.loadButtons();
        this.plugin.getPatternManager().loadPatterns();
        this.loadInventories();

    }

    @Override
    public Inventory loadInventory(Plugin plugin, File file) throws InventoryException {
        return this.loadInventory(plugin, file, ZInventory.class);
    }

    @Override
    public Inventory loadInventory(Plugin plugin, String fileName) throws InventoryException {
        return this.loadInventory(plugin, fileName, ZInventory.class);
    }

    @Override
    public Inventory loadInventory(Plugin plugin, String fileName, Class<? extends Inventory> classz) throws InventoryException {

        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            throw new InventoryFileNotFound("Cannot find " + plugin.getDataFolder().getAbsolutePath() + "/" + fileName);
        }

        return this.loadInventory(plugin, file, classz);
    }

    @Override
    public Inventory loadInventory(Plugin plugin, File file, Class<? extends Inventory> classz) throws InventoryException {

        Loader<Inventory> loader = new InventoryLoader(this.plugin);
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        Inventory inventory = loader.load(configuration, "", file, classz, plugin);

        List<Inventory> inventories = this.inventories.getOrDefault(plugin.getName(), new ArrayList<>());
        inventories.add(inventory);
        this.inventories.put(plugin.getName(), inventories);

        if (Config.enableInformationMessage) {
            Logger.info(file.getPath() + " loaded successfully !", LogType.INFO);
        }

        InventoryLoadEvent inventoryLoadEvent = new InventoryLoadEvent(plugin, file, inventory);
        if (Config.enableFastEvent) getFastEvents().forEach(event -> event.onInventoryLoad(inventoryLoadEvent));
        else inventoryLoadEvent.call();

        return inventory;
    }

    @Override
    public Optional<Plugin> getPluginIgnoreCase(String pluginName) {
        return Arrays.stream(Bukkit.getPluginManager().getPlugins()).filter(e -> pluginName != null && e.getName().equalsIgnoreCase(pluginName)).findFirst();
    }

    @Override
    public Optional<Inventory> getInventory(String name) {
        return this.getInventories().stream().filter(i -> name != null && i.getFileName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public Optional<Inventory> getInventory(Plugin plugin, String name) {
        return this.getInventories(plugin).stream().filter(i -> name != null && i.getFileName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public Optional<Inventory> getInventory(String pluginName, String name) {
        Optional<Plugin> optional = this.getPluginIgnoreCase(pluginName);
        return !optional.isPresent() || name == null ? Optional.empty() : this.getInventory(optional.get(), name);
    }

    @Override
    public Collection<Inventory> getInventories() {
        return this.inventories.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public Collection<Inventory> getInventories(Plugin plugin) {
        return plugin == null ? new ArrayList<>() : this.inventories.getOrDefault(plugin.getName(), new ArrayList<>());
    }

    @Override
    public void deleteInventory(Inventory inventory) {
        String pluginName = inventory.getPlugin().getName();
        List<Inventory> inventories = this.inventories.getOrDefault(pluginName, new ArrayList<>());
        inventories.remove(inventory);
        this.inventories.put(pluginName, inventories);
    }

    @Override
    public boolean deleteInventory(String name) {
        Optional<Inventory> optional = this.getInventory(name);
        if (optional.isPresent()) {
            this.deleteInventory(optional.get());
            return false;
        }
        return false;
    }

    @Override
    public void deleteInventories(Plugin plugin) {
        this.inventories.remove(plugin.getName());
    }

    @Override
    public void openInventory(Player player, Inventory inventory) {
        this.openInventory(player, inventory, 1, new ArrayList<>());
    }

    @Override
    public void openInventory(Player player, Inventory inventory, int page) {
        this.openInventory(player, inventory, page, new ArrayList<>());
    }

    @Override
    public void openInventory(Player player, Inventory inventory, int page, List<Inventory> oldInventories) {

        PlayerOpenInventoryEvent playerOpenInventoryEvent = new PlayerOpenInventoryEvent(player, inventory, page, oldInventories);
        if (Config.enableFastEvent) {
            getFastEvents().forEach(event -> event.onPlayerOpenInventory(playerOpenInventoryEvent));
        } else playerOpenInventoryEvent.call();

        if (playerOpenInventoryEvent.isCancelled()) return;

        page = playerOpenInventoryEvent.getPage();
        oldInventories = playerOpenInventoryEvent.getOldInventories();

        this.currentInventories.put(player.getUniqueId(), inventory);
        this.createInventory(this.plugin, player, EnumInventory.INVENTORY_DEFAULT, page, inventory, oldInventories);
    }

    @Override
    public void openInventory(Player player, Inventory inventory, int page, Inventory... inventories) {
        List<Inventory> oldInventories = new ArrayList<>();
        Collections.addAll(oldInventories, inventories);
        this.openInventory(player, inventory, page, oldInventories);
    }

    @Override
    public void loadButtons() {

        ButtonManager buttonManager = this.plugin.getButtonManager();
        // Load Permissible before
        buttonManager.registerPermissible(new PlaceholderPermissibleLoader(buttonManager));
        buttonManager.registerPermissible(new PermissionPermissibleLoader(buttonManager));
        buttonManager.registerPermissible(new ItemPermissibleLoader(buttonManager));
        buttonManager.registerPermissible(new RegexPermissibleLoader(buttonManager));

        // Load actions
        buttonManager.registerAction(new BroadcastLoader());
        buttonManager.registerAction(new MessageLoader());
        buttonManager.registerAction(new SoundLoader());
        buttonManager.registerAction(new BroadcastSoundLoader());
        buttonManager.registerAction(new CloseLoader());
        buttonManager.registerAction(new ConnectLoader(this.plugin));
        buttonManager.registerAction(new DataLoader(this.plugin));
        buttonManager.registerAction(new fr.maxlego08.menu.loader.actions.InventoryLoader(this.plugin));
        buttonManager.registerAction(new ChatLoader());
        buttonManager.registerAction(new PlayerCommandLoader());
        buttonManager.registerAction(new ConsoleCommandLoader());
        buttonManager.registerAction(new fr.maxlego08.menu.loader.actions.BackLoader(this.plugin));
        buttonManager.registerAction(new ShopkeeperLoader());
        buttonManager.registerAction(new TitleLoader());

        // Loading ButtonLoader
        // The first step will be to load the buttons in the plugin, so each
        // inventory will have the same list of buttons

        buttonManager.register(new NoneLoader(this.plugin, ZNoneButton.class, "none"));
        buttonManager.register(new NoneLoader(this.plugin, ZNoneButton.class, "none_slot"));
        buttonManager.register(new NoneLoader(this.plugin, ZNoneButton.class, "perform_command"));
        buttonManager.register(new fr.maxlego08.menu.button.loader.InventoryLoader(this.plugin, this, this.plugin.getCommandManager()));
        buttonManager.register(new BackLoader(this.plugin, this));
        buttonManager.register(new HomeLoader(this.plugin, this));
        buttonManager.register(new NextLoader(this.plugin, this));
        buttonManager.register(new PreviousLoader(this.plugin, this));
        buttonManager.register(new MainMenuLoader(this.plugin, this));
        buttonManager.register(new JumpLoader(this.plugin, this));

        // Register ItemStackSimilar
        registerItemStackVerification(new FullSimilar());
        registerItemStackVerification(new LoreSimilar());
        registerItemStackVerification(new MaterialSimilar());
        registerItemStackVerification(new ModelIdSimilar());
        registerItemStackVerification(new NameSimilar());

        ButtonLoaderRegisterEvent event = new ButtonLoaderRegisterEvent(buttonManager);
        event.call();
    }

    @Override
    public void loadInventories() {

        // Check if file exist
        File folder = new File(this.plugin.getDataFolder(), "inventories");
        if (!folder.exists()) {
            folder.mkdir();
        }

        // Load inventories
        try (Stream<Path> s = Files.walk(Paths.get(folder.getPath()))) {
            s.skip(1).map(Path::toFile).filter(File::isFile).filter(e -> e.getName().endsWith(".yml")).forEach(file -> {
                try {
                    this.loadInventory(this.plugin, file);
                } catch (InventoryException e1) {
                    e1.printStackTrace();
                }
            });
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        // Load specify path inventories
        List<String> list = Config.specifyPathMenus;
        for (String s : list) {
            File file = new File(s);
            if (file.isFile()) {
                if (file.getName().endsWith(".yml")) {
                    try {
                        this.loadInventory(this.plugin, file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public boolean registerMaterialLoader(MaterialLoader materialLoader) {

        Optional<MaterialLoader> optional = this.getMaterialLoader(materialLoader.getKey());
        if (optional.isPresent()) {
            return false;
        }

        this.loaders.add(materialLoader);
        return true;
    }

    @Override
    public Optional<MaterialLoader> getMaterialLoader(String key) {
        return this.loaders.stream().filter(e -> e.getKey().equalsIgnoreCase(key)).findFirst();
    }

    @Override
    public Collection<MaterialLoader> getMaterialLoader() {
        return Collections.unmodifiableCollection(this.loaders);
    }

    @Override
    public void openInventory(Player player, Plugin plugin, String inventoryName) {

        Optional<Inventory> optional = this.getInventory(plugin, inventoryName);

        if (!optional.isPresent()) {
            player.closeInventory();
            message(player, Message.INVENTORY_NOT_FOUND, "%name%", inventoryName, "%toName%", inventoryName, "%plugin%", plugin.getName());
            return;
        }

        this.openInventory(player, optional.get());
    }

    @Override
    public void openInventory(Player player, String pluginName, String inventoryName) {

        Optional<Inventory> optional = this.getInventory(pluginName, inventoryName);

        if (!optional.isPresent()) {
            player.closeInventory();
            message(player, Message.INVENTORY_NOT_FOUND, "%name%", inventoryName, "%toName%", inventoryName, "%plugin%", pluginName);
            return;
        }

        this.openInventory(player, optional.get());
    }

    @Override
    public void openInventory(Player player, String inventoryName) {

        Optional<Inventory> optional = this.getInventory(inventoryName);

        if (!optional.isPresent()) {
            player.closeInventory();
            message(player, Message.INVENTORY_NOT_FOUND, "%name%", inventoryName, "%toName%", inventoryName, "%plugin%", this.plugin.getName());
            return;
        }

        this.openInventory(player, optional.get());
    }

    @Override
    public void reloadInventory(Inventory inventory) {

        this.deleteInventory(inventory);

        /*
         * CommandManager commandManager = this.plugin.getCommandManager();
         * Optional<Command> optional = commandManager.getCommand(inventory); if
         * (optional.isPresent()) { Command command = optional.get();
         * commandManager.unregistetCommands(command); }
         */

        try {
            this.loadInventory(inventory.getPlugin(), inventory.getFile());
        } catch (InventoryException e) {
            e.printStackTrace();
        }

    }

    @Override
    public MetaUpdater getMeta() {
        return Meta.meta;
    }

    @Override
    public Inventory loadInventoryOrSaveResource(Plugin plugin, String resourceName) throws InventoryException {

        File file = new File(plugin.getDataFolder(), resourceName);
        if (!file.exists()) {
            plugin.saveResource(resourceName, false);
        }

        return this.loadInventory(plugin, file);
    }

    @Override
    public Inventory loadInventoryOrSaveResource(Plugin plugin, String resourceName, Class<? extends Inventory> classz) throws InventoryException {

        File file = new File(plugin.getDataFolder(), resourceName);
        if (!file.exists()) {
            plugin.saveResource(resourceName, false);
        }

        return this.loadInventory(plugin, file, classz);

    }

    @Override
    public Optional<Inventory> getCurrentPlayerInventory(Player player) {
        return Optional.ofNullable(this.currentInventories.getOrDefault(player.getUniqueId(), null));
    }

    @Override
    public void unregisterListener(Plugin plugin) {
        this.fastEventMap.remove(plugin);
    }

    @Override
    public void registerFastEvent(Plugin plugin, FastEvent fastEvent) {
        this.fastEventMap.put(plugin, fastEvent);
    }

    @Override
    public Collection<FastEvent> getFastEvents() {
        return this.fastEventMap.values();
    }

    @Override
    public void sendInventories(CommandSender sender) {

        if (this.inventories.isEmpty()) {
            message(sender, Message.LIST_EMPTY);
            return;
        }

        this.inventories.forEach((plugin, inventories) -> {
            String inventoriesAsString = toList(inventories.stream().map(Inventory::getFileName).collect(Collectors.toList()), "§8", "§7");
            messageWO(sender, Message.LIST_INFO, "%plugin%", plugin, "%amount%", inventories.size(), "%inventories%", inventoriesAsString);
        });
    }

    @Override
    public void createNewInventory(CommandSender sender, String fileName, int inventorySize, String inventoryName) {

        fileName = fileName.replace(".yml", "");
        File file = new File(this.plugin.getDataFolder(), "inventories/" + fileName + ".yml");
        if (file.exists()) {
            message(sender, Message.INVENTORY_CREATE_ERROR_ALREADY, "%name%", fileName);
            return;
        }
        try {
            file.createNewFile();
        } catch (IOException exception) {
            message(sender, Message.INVENTORY_CREATE_ERROR_EXCEPTION, "%error%", exception.getMessage());
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        configuration.set("name", inventoryName);
        configuration.set("size", inventorySize);
        configuration.set("items", new ArrayList<>());

        try {
            configuration.save(file);
        } catch (IOException exception) {
            message(sender, Message.INVENTORY_CREATE_ERROR_EXCEPTION, "%error%", exception.getMessage());
        }

        message(sender, Message.INVENTORY_CREATE_SUCCESS, "%name%", fileName);

        try {
            loadInventory(this.plugin, file);
        } catch (InventoryException exception) {
            message(sender, Message.INVENTORY_CREATE_ERROR_EXCEPTION, "%error%", exception.getMessage());
        }
    }

    @Override
    public void updateInventory(Player player) {
        InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
        if (holder instanceof InventoryDefault) {
            InventoryDefault inventoryDefault = (InventoryDefault) holder;
            this.openInventory(player, inventoryDefault.getMenuInventory(), inventoryDefault.getPage(), inventoryDefault.getOldInventories());
        }
    }

    @Override
    public void updateInventory(Player player, Plugin plugin) {
        InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
        if (holder instanceof InventoryDefault) {
            InventoryDefault inventoryDefault = (InventoryDefault) holder;
            if (inventoryDefault.getMenuInventory().getPlugin() == plugin) {
                this.openInventory(player, inventoryDefault.getMenuInventory(), inventoryDefault.getPage(), inventoryDefault.getOldInventories());
            }
        }
    }

    @Override
    public void saveItem(CommandSender sender, ItemStack itemStack, String name, String type) {

        File file = new File(this.plugin.getDataFolder(), "save_items.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection configurationSection = configuration.getConfigurationSection("items.");
        if (configurationSection != null) {
            Set<String> names = configurationSection.getKeys(false);
            if (names.contains(name)) {
                message(sender, Message.SAVE_ERROR_NAME);
                return;
            }
        }

        Loader<MenuItemStack> loader = new MenuItemStackLoader(this);
        MenuItemStack menuItemStack = MenuItemStack.fromItemStack(this, itemStack);
        if (type.equalsIgnoreCase("yml")) {
            loader.save(menuItemStack, configuration, "items." + name + ".", file);
        } else if (type.equalsIgnoreCase("base64")) {

            String base64 = ItemStackUtils.serializeItemStack(itemStack);
            configuration.set("items." + name + ".material", "base64:" + base64);
            try {
                configuration.save(file);
            } catch (IOException exception) {
                exception.printStackTrace();
            }

        } else {
            message(sender, Message.SAVE_ERROR_TYPE, "%name%", name);
            return;
        }

        message(sender, Message.SAVE_SUCCESS, "%name%", name);

    }

    @EventHandler
    public void onQuid(PlayerQuitEvent event) {
        this.currentInventories.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        for (Inventory inventory : getInventories()) {
            OpenWithItem openWithItem = inventory.getOpenWithItem();
            if (openWithItem != null && openWithItem.shouldTrigger(event)) {
                openInventory(event.getPlayer(), inventory);
            }
        }
    }

    @Override
    public List<ClickType> loadClicks(List<String> loadClicks) {
        List<ClickType> clickTypes = new ArrayList<>();
        loadClicks.forEach(clickType -> {
            if (clickType == null) return;
            if (clickType.equalsIgnoreCase("all") || clickType.equalsIgnoreCase("any")) {
                clickTypes.addAll(Config.allClicksType);
            } else {
                try {
                    clickTypes.add(ClickType.valueOf(clickType.toUpperCase()));
                } catch (Exception ignored) {
                    Logger.info(clickType + " click type was not found.", LogType.ERROR);
                }
            }
        });
        return clickTypes;
    }

    @Override
    public void registerItemStackVerification(ItemStackSimilar itemStackSimilar) {
        this.itemStackSimilarMap.put(itemStackSimilar.getName().toLowerCase(), itemStackSimilar);
    }

    @Override
    public Optional<ItemStackSimilar> getItemStackVerification(String name) {
        return Optional.ofNullable(this.itemStackSimilarMap.getOrDefault(name.toLowerCase(), null));
    }

    @Override
    public Collection<ItemStackSimilar> getItemStackVerifications() {
        return this.itemStackSimilarMap.values();
    }
}
