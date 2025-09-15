package fr.maxlego08.menu;

import com.tcoded.folialib.impl.PlatformScheduler;
import fr.maxlego08.menu.api.*;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.ButtonOption;
import fr.maxlego08.menu.api.checker.InventoryLoadRequirement;
import fr.maxlego08.menu.api.checker.InventoryRequirementType;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.enchantment.Enchantments;
import fr.maxlego08.menu.api.event.FastEvent;
import fr.maxlego08.menu.api.event.events.ButtonLoaderRegisterEvent;
import fr.maxlego08.menu.api.event.events.InventoryLoadEvent;
import fr.maxlego08.menu.api.event.events.PlayerOpenInventoryEvent;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.exceptions.InventoryFileNotFound;
import fr.maxlego08.menu.api.font.FontImage;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.api.loader.NoneLoader;
import fr.maxlego08.menu.api.utils.*;
import fr.maxlego08.menu.button.buttons.ZNoneButton;
import fr.maxlego08.menu.button.loader.BackLoader;
import fr.maxlego08.menu.button.loader.*;
import fr.maxlego08.menu.command.validators.*;
import fr.maxlego08.menu.hooks.bedrock.button.loader.*;
import fr.maxlego08.menu.hooks.dialogs.button.loader.DialogItemBodyLoader;
import fr.maxlego08.menu.hooks.dialogs.button.loader.DialogPlainMessageBodyLoader;
import fr.maxlego08.menu.hooks.dialogs.button.loader.DialogBooleanInputLoader;
import fr.maxlego08.menu.hooks.dialogs.button.loader.DialogNumberRangeInputLoader;
import fr.maxlego08.menu.hooks.dialogs.button.loader.DialogSingleOptionInputLoader;
import fr.maxlego08.menu.hooks.dialogs.button.loader.DialogTextInputLoader;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.itemstack.*;
import fr.maxlego08.menu.loader.InventoryLoader;
import fr.maxlego08.menu.loader.MenuItemStackLoader;
import fr.maxlego08.menu.loader.actions.*;
import fr.maxlego08.menu.loader.deluxemenu.InventoryDeluxeMenuLoader;
import fr.maxlego08.menu.loader.permissible.*;
import fr.maxlego08.menu.requirement.checker.InventoryRequirementChecker;
import fr.maxlego08.menu.zcore.enums.EnumInventory;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.logger.Logger.LogType;
import fr.maxlego08.menu.zcore.utils.PlayerSkin;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.itemstack.MenuItemStackFormMap;
import fr.maxlego08.menu.zcore.utils.nms.ItemStackUtils;
import fr.maxlego08.menu.zcore.utils.nms.NMSUtils;
import fr.maxlego08.menu.zcore.utils.plugins.Plugins;
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
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ZInventoryManager extends ZUtils implements InventoryManager {

    private final Map<String, List<Inventory>> inventories = new HashMap<>();
    private final Map<Plugin, List<Class<? extends ButtonOption>>> buttonOptions = new HashMap<>();
    private final Map<Plugin, List<Class<? extends InventoryOption>>> inventoryOptions = new HashMap<>();
    private final List<InventoryListener> inventoryListeners = new ArrayList<>();
    private final List<MaterialLoader> loaders = new ArrayList<>();
    private final ZMenuPlugin plugin;
    private final Map<UUID, Inventory> currentInventories = new HashMap<>();
    private final Map<Plugin, FastEvent> fastEventMap = new HashMap<>();
    private final Map<String, ItemStackSimilar> itemStackSimilarMap = new HashMap<>();

    private final Map<UUID, Integer> playerPages = new HashMap<>();
    private final Map<UUID, Integer> playerMaxPages = new HashMap<>();

    private final List<InventoryLoadRequirement> inventoryLoadRequirements = new ArrayList<>();

    public ZInventoryManager(ZMenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public void load() {
        this.loadButtons();
        this.plugin.getPatternManager().loadPatterns();
        this.loadInventories();
        DialogManager dialogManager = this.plugin.getDialogManager();
        if (dialogManager != null) {
            dialogManager.loadDialogs();
        }
        BedrockManager bedrockManager = this.plugin.getBedrockManager();
        if (bedrockManager != null) {
            bedrockManager.loadBedrockInventory();
        }
        this.startOfflineTask(this.plugin.getConfig().getInt("cache-offline-player", 300));
    }

    private void startOfflineTask(int seconds) {

        if (seconds <= 0) return;

        this.plugin.getScheduler().runTimerAsync(OfflinePlayerCache::clearCache, seconds, seconds, TimeUnit.SECONDS);
    }

    @Override
    public MenuPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public MenuItemStack loadItemStack(YamlConfiguration configuration, String path, File file) {
        return new MenuItemStackLoader(this).load(configuration, path, file);
    }

    @Override
    public MenuItemStack loadItemStack(File file, String path, Map<String, Object> map) {
        return MenuItemStackFormMap.fromMap(this, file, path, map);
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

        boolean enableInventoryLoad = configuration.getBoolean("enable", true);
        if (!enableInventoryLoad) {
            if (Config.enableInformationMessage) {
                Logger.info("The inventory " + file.getPath() + " is disabled, so it will not be loaded.", LogType.WARNING);
            }
            return null;
        }

        InventoryRequirementChecker checker = new InventoryRequirementChecker(this.plugin);
        Optional<InventoryLoadRequirement> optional = checker.canLoadInventory(configuration, plugin, file, classz);
        if (optional.isPresent()) {

            if (Config.enableInformationMessage) {
                Logger.info("Cannot load inventory " + file.getPath() + ", inventory is waiting.", LogType.WARNING);

                InventoryLoadRequirement inventoryLoadRequirement = optional.get();
                plugin.getLogger().info("Inventory load requirement: " + inventoryLoadRequirement.getDisplayError());
            }

            this.inventoryLoadRequirements.add(optional.get());

            return null;
        }

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
        var inventories = this.getInventories();
        return inventories.stream().filter(i -> name != null && i.getFileName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public Optional<Inventory> getInventory(Plugin plugin, String name) {
        return this.getInventories(plugin).stream().filter(i -> name != null && i.getFileName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public Optional<Inventory> getInventory(String pluginName, String name) {
        Optional<Plugin> optional = this.getPluginIgnoreCase(pluginName);
        return optional.isEmpty() || name == null ? Optional.empty() : this.getInventory(optional.get(), name);
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

        List<Inventory> oldInventories = new ArrayList<>();

        if (player.getOpenInventory().getTopInventory().getHolder() instanceof InventoryDefault inventoryDefault) {
            Inventory fromInventory = inventoryDefault.getMenuInventory();
            oldInventories = inventoryDefault.getOldInventories();
            oldInventories.add(fromInventory);
        }

        this.openInventory(player, inventory, page, oldInventories);
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
        buttonManager.registerPermissible(new CurrencyPermissibleLoader(buttonManager));
        buttonManager.registerPermissible(new CuboidPermissibleLoader(buttonManager));
        if (this.plugin.isEnable(Plugins.JOBS)) {
            buttonManager.registerPermissible(new JobPermissibleLoader(buttonManager));
        }
        if (this.plugin.isEnable(Plugins.LUCKPERMS)) {
            buttonManager.registerPermissible(new LuckPermPermissibleLoader(buttonManager));
        }

        // Load actions
        buttonManager.registerAction(new BroadcastLoader(this.plugin));
        buttonManager.registerAction(new MessageLoader());
        buttonManager.registerAction(new BookLoader());
        buttonManager.registerAction(new SoundLoader());
        buttonManager.registerAction(new BroadcastSoundLoader());
        buttonManager.registerAction(new CloseLoader());
        buttonManager.registerAction(new ConnectLoader(this.plugin));
        buttonManager.registerAction(new DataLoader(this.plugin));
        buttonManager.registerAction(new fr.maxlego08.menu.loader.actions.InventoryLoader(this.plugin));
        buttonManager.registerAction(new ChatLoader());
        buttonManager.registerAction(new PlayerCommandAsOPLoader());
        buttonManager.registerAction(new PlayerCommandLoader());
        buttonManager.registerAction(new ConsoleCommandLoader());
        buttonManager.registerAction(new fr.maxlego08.menu.loader.actions.BackLoader(this.plugin));
        buttonManager.registerAction(new ShopkeeperLoader(this.plugin));
        buttonManager.registerAction(new TitleLoader());
        buttonManager.registerAction(new ActionBarLoader());
        buttonManager.registerAction(new RefreshLoader());
        buttonManager.registerAction(new RefreshInventoryLoader());
        buttonManager.registerAction(new DiscordLoader());
        buttonManager.registerAction(new DiscordComponentV2Loader());
        buttonManager.registerAction(new TeleportLoader(this.plugin));
        buttonManager.registerAction(new CurrencyWithdrawLoader());
        buttonManager.registerAction(new CurrencyDepositLoader());
        if (this.plugin.isEnable(Plugins.LUCKPERMS)) {
            buttonManager.registerAction(new LuckPermissionSetLoader());
        }
        buttonManager.registerAction(new ToastLoader(this.plugin));
        if (this.plugin.getDialogManager() != null) {
            buttonManager.registerAction(new DialogLoader(this.plugin, this.plugin.getDialogManager()));
        }

        // Loading ButtonLoader
        // The first step will be to load the buttons in the plugin, so each
        // inventory will have the same list of buttons

        buttonManager.register(new NoneLoader(this.plugin, ZNoneButton.class, "none"));
        buttonManager.register(new NoneLoader(this.plugin, ZNoneButton.class, "none_slot"));
        buttonManager.register(new NoneLoader(this.plugin, ZNoneButton.class, "perform_command"));
        buttonManager.register(new fr.maxlego08.menu.button.loader.InventoryLoader(this.plugin));
        buttonManager.register(new BackLoader(this.plugin));
        buttonManager.register(new HomeLoader(this.plugin));
        buttonManager.register(new NextLoader(this.plugin));
        buttonManager.register(new PreviousLoader(this.plugin));
        buttonManager.register(new MainMenuLoader(this.plugin));
        buttonManager.register(new JumpLoader(this.plugin));
        buttonManager.register(new SwitchLoader(this.plugin));

        // Loading Button Dialog
        // Register Button Dialog Body
        buttonManager.register(new DialogItemBodyLoader(this.plugin));
        buttonManager.register(new DialogPlainMessageBodyLoader(this.plugin));
        // Register Button Dialog Input
        buttonManager.register(new DialogTextInputLoader(this.plugin));
        buttonManager.register(new DialogBooleanInputLoader(this.plugin));
        buttonManager.register(new DialogNumberRangeInputLoader(this.plugin));
        buttonManager.register(new DialogSingleOptionInputLoader(this.plugin));

        // Register Button Bedrock
        buttonManager.register(new BedrockButtonLoader(this.plugin));
        buttonManager.register(new BedrockModalButtonLoader(this.plugin));
        buttonManager.register(new BedrockLabelLoader(this.plugin));
        buttonManager.register(new BedrockTextInputLoader(this.plugin));
        buttonManager.register(new BedrockToggleInputLoader(this.plugin));
        buttonManager.register(new BedrockSliderInputLoader(this.plugin));
        buttonManager.register(new BedrockDropDownInputLoader(this.plugin));

        // Register ItemStackSimilar
        registerItemStackVerification(new FullSimilar());
        registerItemStackVerification(new LoreSimilar());
        registerItemStackVerification(new MaterialSimilar());
        registerItemStackVerification(new ModelIdSimilar());
        registerItemStackVerification(new NameSimilar());
        registerItemStackVerification(new ItemModelSimilar()); // 1.21.4+

        ButtonLoaderRegisterEvent event = new ButtonLoaderRegisterEvent(buttonManager, this, this.plugin.getPatternManager());
        event.call();

        this.plugin.getWebsiteManager().loadButtons(buttonManager);

        var commandManager = this.plugin.getCommandManager();
        commandManager.registerArgumentValidator(new IntArgumentValidator());
        commandManager.registerArgumentValidator(new DoubleArgumentValidator());
        commandManager.registerArgumentValidator(new BedrockPlayerArgumentValidator(this.plugin.getBedrockManager()));
        commandManager.registerArgumentValidator(new BooleanArgumentValidator());
        commandManager.registerArgumentValidator(new EntityTypeArgumentValidator());
        commandManager.registerArgumentValidator(new MaterialArgumentValidator(Message.COMMAND_ARGUMENT_MATERIAL));
        commandManager.registerArgumentValidator(new MaterialArgumentValidator(Message.COMMAND_ARGUMENT_BLOCK));
        commandManager.registerArgumentValidator(new OnlinePlayerArgumentValidator(plugin));
        commandManager.registerArgumentValidator(new PlayerArgumentValidator(plugin));
        commandManager.registerArgumentValidator(new LocationArgumentValidator(plugin));
        commandManager.registerArgumentValidator(new WorldArgumentValidator(plugin));
    }

    @Override
    public void loadInventories() {

        this.playerMaxPages.clear();
        this.playerPages.clear();

        File folder = new File(this.plugin.getDataFolder(), "inventories");
        if (!folder.exists()) {
            folder.mkdir();
        }

        // Load inventories
        try (Stream<Path> stream = Files.walk(Paths.get(folder.getPath()))) {
            stream.skip(1).map(Path::toFile).filter(File::isFile).filter(e -> e.getName().endsWith(".yml")).forEach(file -> {
                try {
                    this.loadInventory(this.plugin, file);
                } catch (InventoryException exception) {
                    exception.printStackTrace();
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
                    } catch (Exception exception) {
                        exception.printStackTrace();
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

        if (optional.isEmpty()) {
            player.closeInventory();
            message(this.plugin, player, Message.INVENTORY_NOT_FOUND, "%name%", inventoryName, "%toName%", inventoryName, "%plugin%", plugin.getName());
            return;
        }

        this.openInventory(player, optional.get());
    }

    @Override
    public void openInventory(Player player, String pluginName, String inventoryName) {
        openInventory(player, pluginName, inventoryName, 1);
    }

    @Override
    public void openInventory(Player player, String pluginName, String inventoryName, int page) {
        Optional<Inventory> optional = this.getInventory(pluginName, inventoryName);

        if (optional.isEmpty()) {
            player.closeInventory();
            message(this.plugin, player, Message.INVENTORY_NOT_FOUND, "%name%", inventoryName, "%toName%", inventoryName, "%plugin%", pluginName);
            return;
        }

        this.openInventory(player, optional.get(), page);
    }

    @Override
    public void openInventory(Player player, String inventoryName, int page) {

        Optional<Inventory> optional = this.getInventory(inventoryName);

        if (optional.isEmpty()) {
            player.closeInventory();
            message(this.plugin, player, Message.INVENTORY_NOT_FOUND, "%name%", inventoryName, "%toName%", inventoryName, "%plugin%", this.plugin.getName());
            return;
        }

        this.openInventory(player, optional.get(), page);
    }

    @Override
    public void openInventory(Player player, String inventoryName) {
        openInventory(player, inventoryName, 1);
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
        return this.plugin.getMetaUpdater();
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
            message(this.plugin, sender, Message.LIST_EMPTY);
            return;
        }

        this.inventories.forEach((plugin, inventories) -> {
            String inventoriesAsString = toList(inventories.stream().map(Inventory::getFileName).collect(Collectors.toList()), "§8", "§7");
            messageWO(this.plugin, sender, Message.LIST_INFO, "%plugin%", plugin, "%amount%", inventories.size(), "%inventories%", inventoriesAsString);
        });
    }

    @Override
    public void createNewInventory(CommandSender sender, String fileName, int inventorySize, String inventoryName) {

        fileName = fileName.replace(".yml", "");
        File file = new File(this.plugin.getDataFolder(), "inventories/" + fileName + ".yml");
        if (file.exists()) {
            message(this.plugin, sender, Message.INVENTORY_CREATE_ERROR_ALREADY, "%name%", fileName);
            return;
        }
        try {
            file.createNewFile();
        } catch (IOException exception) {
            message(this.plugin, sender, Message.INVENTORY_CREATE_ERROR_EXCEPTION, "%error%", exception.getMessage());
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        configuration.set("name", inventoryName);
        configuration.set("size", inventorySize);
        configuration.set("items", new ArrayList<>());

        try {
            configuration.save(file);
        } catch (IOException exception) {
            message(this.plugin, sender, Message.INVENTORY_CREATE_ERROR_EXCEPTION, "%error%", exception.getMessage());
        }

        message(this.plugin, sender, Message.INVENTORY_CREATE_SUCCESS, "%name%", fileName);

        try {
            loadInventory(this.plugin, file);
        } catch (InventoryException exception) {
            message(this.plugin, sender, Message.INVENTORY_CREATE_ERROR_EXCEPTION, "%error%", exception.getMessage());
        }
    }

    @Override
    public void updateInventory(Player player) {
        this.plugin.getScheduler().runAtEntity(player, w -> {
            InventoryHolder holder = CompatibilityUtil.getTopInventory(player).getHolder();
            if (holder instanceof InventoryDefault inventoryDefault) {
                this.openInventory(player, inventoryDefault.getMenuInventory(), inventoryDefault.getPage(), inventoryDefault.getOldInventories());
            }
        });
    }

    @Override
    public void updateInventoryCurrentPage(Player player) {
        this.plugin.getScheduler().runAtEntity(player, w -> {
            InventoryHolder holder = CompatibilityUtil.getTopInventory(player).getHolder();
            if (holder instanceof InventoryDefault inventoryDefault) {
                this.openInventory(player, inventoryDefault.getMenuInventory(), getPage(player), inventoryDefault.getOldInventories());
            }
        });
    }

    @Override
    public void updateInventory(Player player, Plugin plugin) {
        this.plugin.getScheduler().runAtEntity(player, w -> {
            InventoryHolder holder = CompatibilityUtil.getTopInventory(player).getHolder();
            if (holder instanceof InventoryDefault inventoryDefault) {
                if (inventoryDefault.getMenuInventory().getPlugin() == plugin) {
                    this.openInventory(player, inventoryDefault.getMenuInventory(), inventoryDefault.getPage(), inventoryDefault.getOldInventories());
                }
            }
        });
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
                message(this.plugin, sender, Message.SAVE_ERROR_NAME);
                return;
            }
        }

        Loader<MenuItemStack> loader = new MenuItemStackLoader(this);
        ZMenuItemStack menuItemStack = ZMenuItemStack.fromItemStack(this, itemStack);
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
            message(this.plugin, sender, Message.SAVE_ERROR_TYPE, "%name%", name);
            return;
        }

        message(this.plugin, sender, Message.SAVE_SUCCESS, "%name%", name);

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
    public PlatformScheduler getScheduler() {
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

    @Override
    public void loadElement(InventoryRequirementType type, String value) {

        Iterator<InventoryLoadRequirement> iterator = this.inventoryLoadRequirements.iterator();
        while (iterator.hasNext()) {
            InventoryLoadRequirement inventoryLoadRequirement = iterator.next();
            inventoryLoadRequirement.removeRequirement(type, value);

            if (inventoryLoadRequirement.canLoad()) {
                if (Config.enableInformationMessage) {
                    Logger.info("Conditions are met to load the file " + inventoryLoadRequirement.getFile().getPath(), LogType.SUCCESS);
                }

                try {
                    this.loadInventory(inventoryLoadRequirement.getPlugin(), inventoryLoadRequirement.getFile(), inventoryLoadRequirement.getClassz());
                } catch (InventoryException exception) {
                    exception.printStackTrace();
                } finally {
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void registerInventoryOption(Plugin plugin, Class<? extends InventoryOption> inventoryOption) {

        if (getInventoryOption(inventoryOption.getName()).isPresent()) return;

        this.inventoryOptions.computeIfAbsent(plugin, e -> new ArrayList<>()).add(inventoryOption);
    }

    @Override
    public Map<Plugin, List<Class<? extends InventoryOption>>> getInventoryOptions() {
        return this.inventoryOptions;
    }

    @Override
    public Optional<Class<? extends InventoryOption>> getInventoryOption(String name) {
        return this.inventoryOptions.values().stream().flatMap(List::stream).filter(inventoryOption -> inventoryOption.getName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public void unregisterInventoryOptions(Plugin plugin) {
        this.inventoryOptions.remove(plugin);
    }

    @Override
    public void registerInventoryListener(InventoryListener inventoryListener) {
        this.inventoryListeners.add(inventoryListener);
    }

    @Override
    public void unregisterInventoryListener(InventoryListener inventoryListener) {
        this.inventoryListeners.remove(inventoryListener);
    }

    @Override
    public List<InventoryListener> getInventoryListeners() {
        return inventoryListeners;
    }

    @Override
    public ItemStack postProcessSkullItemStack(ItemStack itemStack, Button button, Player player) {
        String name = papi(this.plugin.parse(player, button.getPlayerHead().replace("%player%", player.getName())), player, true);
        if (!isMinecraftName(name)) {
            return itemStack;
        }

        if (NMSUtils.isNewHeadApi()) {

            OfflinePlayer offlinePlayer = OfflinePlayerCache.get(name);
            if (offlinePlayer != null) {
                SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                skullMeta.setOwnerProfile(offlinePlayer.getPlayerProfile().getTextures().isEmpty() ? offlinePlayer.getPlayerProfile().update().join() : offlinePlayer.getPlayerProfile());
                itemStack.setItemMeta(skullMeta);
            }
        } else {
            String texture = PlayerSkin.getTexture(name);
            if (texture == null) {

                SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                skullMeta.setOwner(name);
                itemStack.setItemMeta(skullMeta);
            } else {
                this.applyTexture(itemStack, texture);
            }
        }
        return itemStack;
    }

    @Override
    public void sendMessage(CommandSender sender, Message message, Object... args) {
        message(this.plugin, sender, message, args);
    }
}
