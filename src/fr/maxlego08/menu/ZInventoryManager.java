package fr.maxlego08.menu;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.ButtonOption;
import fr.maxlego08.menu.api.enchantment.Enchantments;
import fr.maxlego08.menu.api.event.FastEvent;
import fr.maxlego08.menu.api.event.events.ButtonLoaderRegisterEvent;
import fr.maxlego08.menu.api.event.events.InventoryLoadEvent;
import fr.maxlego08.menu.api.event.events.PlayerOpenInventoryEvent;
import fr.maxlego08.menu.api.font.FontImage;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.api.scheduler.ZScheduler;
import fr.maxlego08.menu.api.utils.CompatibilityUtil;
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
import fr.maxlego08.menu.loader.actions.ActionBarLoader;
import fr.maxlego08.menu.loader.actions.BookLoader;
import fr.maxlego08.menu.loader.actions.BroadcastLoader;
import fr.maxlego08.menu.loader.actions.BroadcastSoundLoader;
import fr.maxlego08.menu.loader.actions.ChatLoader;
import fr.maxlego08.menu.loader.actions.CloseLoader;
import fr.maxlego08.menu.loader.actions.ConnectLoader;
import fr.maxlego08.menu.loader.actions.ConsoleCommandLoader;
import fr.maxlego08.menu.loader.actions.CurrencyDepositLoader;
import fr.maxlego08.menu.loader.actions.CurrencyWithdrawLoader;
import fr.maxlego08.menu.loader.actions.DataLoader;
import fr.maxlego08.menu.loader.actions.LuckPermissionSetLoader;
import fr.maxlego08.menu.loader.actions.MessageLoader;
import fr.maxlego08.menu.loader.actions.PlayerCommandLoader;
import fr.maxlego08.menu.loader.actions.RefreshLoader;
import fr.maxlego08.menu.loader.actions.ShopkeeperLoader;
import fr.maxlego08.menu.loader.actions.SoundLoader;
import fr.maxlego08.menu.loader.actions.TitleLoader;
import fr.maxlego08.menu.loader.deluxemenu.InventoryDeluxeMenuLoader;
import fr.maxlego08.menu.loader.permissible.CurrencyPermissibleLoader;
import fr.maxlego08.menu.loader.permissible.ItemPermissibleLoader;
import fr.maxlego08.menu.loader.permissible.JobPermissibleLoader;
import fr.maxlego08.menu.loader.permissible.LuckPermPermissibleLoader;
import fr.maxlego08.menu.loader.permissible.PermissionPermissibleLoader;
import fr.maxlego08.menu.loader.permissible.PlaceholderPermissibleLoader;
import fr.maxlego08.menu.loader.permissible.PlayerNamePermissibleLoader;
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
import fr.maxlego08.menu.zcore.utils.plugins.Plugins;
import fr.maxlego08.menu.zcore.utils.storage.Persist;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
    private final Map<Plugin, List<Class<? extends ButtonOption>>> buttonOptions = new HashMap<>();
    private final List<MaterialLoader> loaders = new ArrayList<>();
    private final MenuPlugin plugin;
    private final Map<UUID, Inventory> currentInventories = new HashMap<>();
    private final Map<Plugin, FastEvent> fastEventMap = new HashMap<>();
    private final Map<String, ItemStackSimilar> itemStackSimilarMap = new HashMap<>();

    private final Map<UUID, Integer> playerPages = new HashMap<>();
    private final Map<UUID, Integer> playerMaxPages = new HashMap<>();

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

        YamlConfiguration configuration = loadAndReplaceConfiguration(file, this.plugin.getGlobalPlaceholders());
        boolean isDeluxeMenu = configuration.contains("menu_title");

        Loader<Inventory> loader = isDeluxeMenu ? new InventoryDeluxeMenuLoader(this.plugin) : new InventoryLoader(this.plugin);
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
    public void openInventoryWithOldInventories(Player player, Inventory inventory, int page) {
        if (player.getOpenInventory().getTopInventory().getHolder() instanceof InventoryDefault) {
            InventoryDefault inventoryDefault = (InventoryDefault) player.getOpenInventory().getTopInventory().getHolder();

            Inventory fromInventory = inventoryDefault.getMenuInventory();
            List<Inventory> oldInventories = inventoryDefault.getOldInventories();
            oldInventories.add(fromInventory);

            this.openInventory(player, inventory, page, oldInventories);
        }
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
        buttonManager.registerPermissible(new ItemPermissibleLoader(this.plugin));
        buttonManager.registerPermissible(new RegexPermissibleLoader(buttonManager));
        buttonManager.registerPermissible(new PlayerNamePermissibleLoader(buttonManager));
        if (this.plugin.isEnable(Plugins.JOBS)) {
            buttonManager.registerPermissible(new JobPermissibleLoader(buttonManager));
        }
        if (this.plugin.isEnable(Plugins.LUCKPERMS)) {
            buttonManager.registerPermissible(new LuckPermPermissibleLoader(buttonManager));
        }
        if (this.plugin.isEnable(Plugins.VAULT)) {
            buttonManager.registerPermissible(new CurrencyPermissibleLoader(buttonManager));
        }

        // Load actions
        buttonManager.registerAction(new BroadcastLoader());
        buttonManager.registerAction(new MessageLoader());
        buttonManager.registerAction(new BookLoader());
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
        buttonManager.registerAction(new ActionBarLoader());
        buttonManager.registerAction(new RefreshLoader());
        if (this.plugin.isEnable(Plugins.VAULT)) {
            buttonManager.registerAction(new CurrencyWithdrawLoader());
            buttonManager.registerAction(new CurrencyDepositLoader());
        }
        if (this.plugin.isEnable(Plugins.LUCKPERMS)) {
            buttonManager.registerAction(new LuckPermissionSetLoader());
        }

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

        ButtonLoaderRegisterEvent event = new ButtonLoaderRegisterEvent(buttonManager, this, this.plugin.getPatternManager());
        event.call();

        plugin.getWebsiteManager().loadButtons(buttonManager);
    }

    @Override
    public void loadInventories() {

        this.playerMaxPages.clear();
        this.playerPages.clear();

        // Check if file exist
        File folder = new File(this.plugin.getDataFolder(), "inventories");
        if (!folder.exists()) {
            folder.mkdir();
        }

        // Load inventories
        try (Stream<Path> stream = Files.walk(Paths.get(folder.getPath()))) {
            stream.skip(1).map(Path::toFile).filter(File::isFile).filter(e -> e.getName().endsWith(".yml")).forEach(file -> {
                try {
                    this.loadInventory(this.plugin, file);
                } catch (InventoryException e1) {
                    e1.printStackTrace();
                }
            });
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        // Load specifies path inventories
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

        this.plugin.getWebsiteManager().loadInventories(this);
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
        InventoryHolder holder = CompatibilityUtil.getTopInventory(player).getHolder();
        if (holder instanceof InventoryDefault) {
            InventoryDefault inventoryDefault = (InventoryDefault) holder;
            this.openInventory(player, inventoryDefault.getMenuInventory(), inventoryDefault.getPage(), inventoryDefault.getOldInventories());
        }
    }

    @Override
    public void updateInventory(Player player, Plugin plugin) {
        InventoryHolder holder = CompatibilityUtil.getTopInventory(player).getHolder();
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

    @Override
    public ZScheduler getScheduler() {
        return this.plugin.getScheduler();
    }

    @Override
    public Optional<Class<? extends ButtonOption>> getOption(String name) {
        return this.buttonOptions.values().stream().flatMap(List::stream).filter(buttonOption -> buttonOption.getName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public void registerOption(Plugin plugin, Class<? extends ButtonOption> buttonOption) {

        if (getOption(buttonOption.getName()).isPresent()) return;

        this.buttonOptions.computeIfAbsent(plugin, e -> new ArrayList<>()).add(buttonOption);
    }

    @Override
    public void unregisterOptions(Plugin plugin) {
        this.buttonOptions.remove(plugin);
    }

    @Override
    public Map<Plugin, List<Class<? extends ButtonOption>>> getOptions() {
        return this.buttonOptions;
    }

    @Override
    public void setPlayerPage(OfflinePlayer player, int page, int maxPage) {
        this.playerPages.put(player.getUniqueId(), page);
        this.playerMaxPages.put(player.getUniqueId(), maxPage);
    }

    @Override
    public int getPage(OfflinePlayer player) {
        return this.playerPages.getOrDefault(player.getUniqueId(), 0);
    }

    @Override
    public int getMaxPage(OfflinePlayer player) {
        return this.playerMaxPages.getOrDefault(player.getUniqueId(), 0);
    }

    @Override
    public InventoryDefault getFakeInventory() {
        InventoryDefault inventoryDefault = new InventoryDefault();
        inventoryDefault.setPlugin(this.plugin);
        return inventoryDefault;
    }

    @Override
    public Enchantments getEnchantments() {
        return this.plugin.getEnchantments();
    }

    @Override
    public FontImage getFontImage() {
        return this.plugin.getFontImage();
    }

    @Override
    public YamlConfiguration loadYamlConfiguration(File file) {
        return loadAndReplaceConfiguration(file, this.plugin.getGlobalPlaceholders());
    }
}
