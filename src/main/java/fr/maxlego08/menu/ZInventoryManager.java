package fr.maxlego08.menu;

import com.cryptomorin.xseries.XSound;
import com.tcoded.folialib.impl.PlatformScheduler;
import fr.maxlego08.menu.animation.ZAnimationManager;
import fr.maxlego08.menu.api.*;
import fr.maxlego08.menu.api.animation.slot.AnimationManager;
import fr.maxlego08.menu.api.annotations.*;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.ButtonOption;
import fr.maxlego08.menu.api.checker.InventoryLoadRequirement;
import fr.maxlego08.menu.api.checker.InventoryRequirementType;
import fr.maxlego08.menu.api.command.CommandArgumentValidator;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.enchantment.Enchantments;
import fr.maxlego08.menu.api.engine.OpenCause;
import fr.maxlego08.menu.api.event.FastEvent;
import fr.maxlego08.menu.api.event.events.ButtonLoaderRegisterEvent;
import fr.maxlego08.menu.api.event.events.InventoryLoadEvent;
import fr.maxlego08.menu.api.event.events.PlayerOpenInventoryEvent;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.exceptions.InventoryFileNotFound;
import fr.maxlego08.menu.api.font.FontImage;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.itemstack.MenuItemStackConversion;
import fr.maxlego08.menu.api.loader.*;
import fr.maxlego08.menu.api.loader.animation.slot.SlotAnimationLoader;
import fr.maxlego08.menu.api.pagination.PaginationManager;
import fr.maxlego08.menu.api.registry.KeyedRegistry;
import fr.maxlego08.menu.api.registry.RegistrationException;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.registry.reload.ReloadContext;
import fr.maxlego08.menu.api.registry.reload.ReloadReport;
import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.api.utils.*;
import fr.maxlego08.menu.api.utils.version.VersionFilter;
import fr.maxlego08.menu.button.buttons.ZNoneButton;
import fr.maxlego08.menu.command.validators.MaterialArgumentValidator;
import fr.maxlego08.menu.common.utils.PlayerUtil;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.common.utils.cache.YamlFileCache;
import fr.maxlego08.menu.common.utils.cache.YamlFileCacheEntry;
import fr.maxlego08.menu.common.utils.itemstack.MenuItemStackFromItemStack;
import fr.maxlego08.menu.common.utils.nms.ItemStackUtils;
import fr.maxlego08.menu.common.utils.yaml.YamlParser;
import fr.maxlego08.menu.hooks.packetevents.loader.PacketEventChangeTitleNameLoader;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.inventory.zinv.ZInventory;
import fr.maxlego08.menu.loader.InventoryLoader;
import fr.maxlego08.menu.loader.MenuItemStackLoader;
import fr.maxlego08.menu.loader.actions.BedrockLoader;
import fr.maxlego08.menu.loader.actions.DialogLoader;
import fr.maxlego08.menu.loader.actions.PlayerCommandAsOPLoader;
import fr.maxlego08.menu.loader.deluxemenu.InventoryDeluxeMenuLoader;
import fr.maxlego08.menu.pagination.ZPaginationManager;
import fr.maxlego08.menu.registry.ZRegistryManager;
import fr.maxlego08.menu.requirement.checker.InventoryRequirementChecker;
import fr.maxlego08.menu.sound.ZSoundOption;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.logger.Logger.LogType;
import fr.maxlego08.menu.zcore.utils.PerformanceDebug;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ZInventoryManager extends ZUtils implements InventoryManager {
    private final PaginationManager paginationManager = new ZPaginationManager();
    private final AnimationManager animationManager;
    private final ZRegistryManager registries = new ZRegistryManager();
    private boolean slotAnimationsLoaded;
    private final Map<String, List<Inventory>> inventories = new ConcurrentHashMap<>();
    private final Map<Plugin, List<Class<? extends ButtonOption>>> buttonOptions = new ConcurrentHashMap<>();
    private final Map<Plugin, List<Class<? extends InventoryOption>>> inventoryOptions = new ConcurrentHashMap<>();
    private final List<InventoryListener> inventoryListeners = new ArrayList<>();
    private final List<MaterialLoader> loaders = new ArrayList<>();
    private final ZMenuPlugin plugin;
    private final Map<UUID, Inventory> currentInventories = new ConcurrentHashMap<>();
    private final Map<Plugin, FastEvent> fastEventMap = new ConcurrentHashMap<>();
    private final Map<String, ItemStackSimilar> itemStackSimilarMap = new ConcurrentHashMap<>();

    private final Map<UUID, Integer> playerPages = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> playerMaxPages = new ConcurrentHashMap<>();
    private final Map<Key, Inventory> otherInventories = new ConcurrentHashMap<>();

    private final List<InventoryLoadRequirement> inventoryLoadRequirements = new ArrayList<>();

    public ZInventoryManager(@NotNull ZMenuPlugin plugin) {
        super();
        this.plugin = plugin;
        this.animationManager = new ZAnimationManager(plugin, this.registries);
        this.registries.provider(Registries.INVENTORY, ZMenuKeys.NAMESPACE, this::provideInventories);
    }

    @Override
    public void load() {
        this.loadSlotAnimations();
        this.loadButtons();
        this.plugin.getPatternManager().loadActionsPatterns();
        this.plugin.getPatternManager().loadPatterns();
        this.animationManager.loadAnimations();
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

    /**
     * Registers the slot animations of zMenu, once: their registry stops accepting entries when the
     * server has finished loading.
     */
    private void loadSlotAnimations() {
        if (this.slotAnimationsLoaded) return;
        this.slotAnimationsLoaded = true;

        ClassRegistry<SlotAnimationLoader, MenuPlugin> registry = ClassRegistry.
                <SlotAnimationLoader, MenuPlugin>of(SlotAnimationLoader.class, loader -> {
                    try {
                        this.registerSlotAnimation(this.plugin, loader);
                    } catch (RegistrationException exception) {
                        Logger.info(exception.getMessage(), LogType.ERROR);
                    }
                })
                .tryNoArgsConstructor()
                .tryConstructor((clazz, plugin) -> clazz.getConstructor(MenuPlugin.class).newInstance(plugin))
                .errorLogger(Logger::error);

        int count = VersionFilter.scanAndRegister("fr.maxlego08.menu", this.plugin, AutoSlotAnimationLoader.class, registry);
        if (Configuration.enableInformationMessage) {
            Logger.info("Registered " + count + " auto slot animation loader(s).");
        }
    }

    private void startOfflineTask(int seconds) {

        if (seconds <= 0) return;

        this.plugin.getScheduler().runTimerAsync(OfflinePlayerCache::clearCache, seconds, seconds, TimeUnit.SECONDS);
    }

    @Override
    @NotNull
    public MenuPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public MenuItemStack loadItemStack(YamlConfiguration configuration, String path, File file) {
        return new MenuItemStackLoader(this).load(configuration, path, file);
    }

    @Override
    public MenuItemStack loadItemStack(File file, String path, Map<String, Object> map) {
        return this.loadItemStack(file, map);
    }

    @Override
    public MenuItemStack loadItemStack(File file, Map<String, Object> map) {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.createSection("item", map);
        return new MenuItemStackLoader(this).load(configuration, "item.", file);
    }

    @Override
    public ZRegistryManager getRegistries() {
        return this.registries;
    }

    @Override
    public AnimationManager getAnimationManager() {
        return this.animationManager;
    }

    @Override
    public PaginationManager getPaginationManager() {
        return this.paginationManager;
    }

    @Override
    public String serializeItemStack(ItemStack itemStack) {
        return ItemStackUtils.serializeItemStack(itemStack);
    }

    @Override
    public ItemStack deserializeItemStack(String serializedItemStack) {
        return ItemStackUtils.deserializeItemStack(serializedItemStack);
    }

    @Override
    public SoundOption createSoundOption(String soundName, String categoryName, float pitch, float volume, boolean isCustom) {
        Optional<XSound> xSound = XSound.of(soundName);

        return xSound.map(sound -> new ZSoundOption(sound, categoryName, soundName, pitch, volume, isCustom)).orElse(null);

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

    /**
     * Loads an inventory. One of zMenu's own replaces the entry of the same file name in the
     * inventory registry; one of another plugin goes in the maps kept for other plugins.
     */
    @Override
    public Inventory loadInventory(Plugin plugin, File file, Class<? extends Inventory> classz) throws InventoryException {

        Inventory inventory = this.parseInventory(plugin, file, classz);
        if (inventory == null) return null;

        if (plugin == this.plugin) {
            ReloadReport report = this.registries.update(Registries.INVENTORY, ZMenuKeys.NAMESPACE, editor -> {
                editor.remove(inventory.key());
                editor.register(inventory);
                editor.onPublish(() -> this.inventoryLoaded(plugin, file, inventory));
            });
            this.logReport(report);
            return report.applied() ? inventory : null;
        }

        List<Inventory> inventories = this.inventories.getOrDefault(plugin.getName(), new ArrayList<>());
        inventories.add(inventory);
        this.inventories.put(plugin.getName(), inventories);
        this.otherInventories.put(inventory.key(), inventory);
        this.inventoryLoaded(plugin, file, inventory);

        return inventory;
    }

    /**
     * Provides the inventories of zMenu's own folder to the inventory registry. A file that cannot
     * be read keeps its previous version, and the load events fire only once the new snapshot is live.
     */
    private void provideInventories(ReloadContext<Inventory> context) {
        KeyedRegistry<Inventory> next = context.registries().registry(Registries.INVENTORY);

        for (File file : this.inventoryFiles()) {
            try {
                Inventory inventory = this.parseInventory(this.plugin, file, ZInventory.class);
                if (inventory == null) continue;

                if (next.contains(inventory.key())) {
                    context.warn(inventory.key(), file.getPath() + " has the same name as another inventory, it is not loaded");
                    continue;
                }
                context.register(inventory);
                context.onPublish(() -> this.inventoryLoaded(this.plugin, file, inventory));
            } catch (Exception exception) {
                Logger.error(exception);
                Optional<Inventory> previous = context.previous().stream().filter(inventory -> file.equals(inventory.getFile())).findFirst();
                if (previous.isPresent() && !next.contains(previous.get().key())) {
                    context.register(previous.get());
                    context.warn(previous.get().key(), file.getPath() + " cannot be loaded, its previous version is kept: " + exception.getMessage());
                } else {
                    context.warn(file.getPath() + " cannot be loaded: " + exception.getMessage());
                }
            }
        }
    }

    /**
     * @return the files of the inventories folder, then the ones of {@code specify-path-menus}
     */
    private List<File> inventoryFiles() {
        List<File> files = new ArrayList<>();

        File folder = new File(this.plugin.getDataFolder(), "inventories");
        if (!folder.exists()) {
            folder.mkdir();
        }

        try (Stream<Path> stream = Files.walk(Paths.get(folder.getPath()))) {
            stream.skip(1).map(Path::toFile).filter(file -> file.isFile() && file.getName().endsWith(".yml")).forEach(files::add);
        } catch (IOException exception) {
            Logger.error(exception);
        }

        for (String path : Configuration.specifyPathMenus) {
            File file = new File(path);
            if (file.isFile() && file.getName().endsWith(".yml")) {
                files.add(file);
            }
        }
        return files;
    }

    private void inventoryLoaded(Plugin plugin, File file, Inventory inventory) {
        if (Configuration.enableInformationMessage) {
            Logger.info(file.getPath() + " loaded successfully !", LogType.INFO);
        }
        this.warnAboutSharedName(inventory);

        InventoryLoadEvent inventoryLoadEvent = new InventoryLoadEvent(plugin, file, inventory);
        if (Configuration.enableFastEvent) this.getFastEvents().forEach(event -> event.onInventoryLoad(inventoryLoadEvent));
        else inventoryLoadEvent.call();
    }

    private void logReport(ReloadReport report) {
        for (ReloadReport.Issue issue : report.issues()) {
            String line = (issue.subject() == null ? "" : issue.subject().asString() + ": ") + issue.message();
            Logger.info(line, issue.severity() == ReloadReport.Severity.ERROR ? LogType.ERROR : LogType.WARNING);
        }
        if (!report.applied()) {
            Logger.info("The reload of " + report.type() + " was rejected, the previous version stays loaded.", LogType.ERROR);
        }
    }

    /**
     * Reads an inventory file, without registering it anywhere.
     *
     * @return the inventory, or null if it is disabled or waits for a requirement
     */
    @Nullable
    private Inventory parseInventory(Plugin plugin, File file, Class<? extends Inventory> classz) throws InventoryException {

        Optional<YamlFileCacheEntry> yamlFileEntry;
        try {
            yamlFileEntry = YamlFileCache.getYamlFileEntryStrict(file.toPath());
        } catch (IOException | InvalidConfigurationException exception) {
            throw new InventoryException("Cannot read " + file.getPath() + ": " + exception.getMessage(), exception);
        }

        if (yamlFileEntry.isEmpty()) throw new InventoryFileNotFound("Cannot find " + file.getAbsolutePath() + ". File does not exist.");

        YamlConfiguration configuration = yamlFileEntry.get().getYamlConfiguration();

        Map<String, Object> placeholders = this.plugin.getGlobalPlaceholders();

        this.loadLocalPlaceholders(configuration, placeholders);

        configuration = YamlParser.parseConfiguration(configuration, placeholders);

        boolean enableInventoryLoad = configuration.getBoolean("enable", true);
        if (!enableInventoryLoad) {
            if (Configuration.enableInformationMessage) {
                Logger.info("The inventory " + file.getPath() + " is disabled, so it will not be loaded.", LogType.WARNING);
            }
            return null;
        }

        InventoryRequirementChecker checker = new InventoryRequirementChecker(this.plugin);
        Optional<InventoryLoadRequirement> optional = checker.canLoadInventory(configuration, plugin, file, classz);
        if (optional.isPresent()) {

            if (Configuration.enableInformationMessage) {
                Logger.info("Cannot load inventory " + file.getPath() + ", inventory is waiting.", LogType.WARNING);

                InventoryLoadRequirement inventoryLoadRequirement = optional.get();
                Logger.info("Inventory load requirement: " + inventoryLoadRequirement.getDisplayError(), LogType.WARNING);
            }

            // Only one pending load per file, the file may be loaded several times (hot reload)
            this.removeInventoryLoadRequirement(file);
            this.inventoryLoadRequirements.add(optional.get());

            return null;
        }

        boolean isDeluxeMenu = configuration.contains("menu_title");

        Loader<Inventory> loader = isDeluxeMenu ? new InventoryDeluxeMenuLoader(this.plugin) : new InventoryLoader(this.plugin);
        return loader.load(configuration, "", file, classz, plugin);
    }

    @Override
    public Optional<Plugin> getPluginIgnoreCase(String pluginName) {
        if (pluginName == null) {
            return Optional.empty();
        }
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getName().equalsIgnoreCase(pluginName)) {
                return Optional.of(plugin);
            }
        }
        return Optional.empty();
    }

    private KeyedRegistry<Inventory> inventoryRegistry() {
        return this.registries.registry(Registries.INVENTORY);
    }

    /**
     * Warns when several plugins provide an inventory with the same file name: the name on its own
     * then always opens the same one, and the plugin:name form is needed to pick another.
     */
    private void warnAboutSharedName(Inventory inventory) {
        String shortName = inventory.key().value();
        List<Inventory> matches = new ArrayList<>(this.getInventories().stream()
                .filter(other -> other.key().value().equals(shortName))
                .collect(Collectors.toMap(Inventory::key, other -> other, (first, second) -> second))
                .values());
        if (matches.size() < 2) return;

        StringBuilder owners = new StringBuilder();
        for (Inventory match : matches) {
            if (!owners.isEmpty()) owners.append(", ");
            owners.append(match.getPlugin().getName()).append(':').append(match.getFileName());
        }
        String winner = this.findInventory(shortName).map(found -> found.getPlugin().getName()).orElse("?");
        Logger.info("The inventory name \"" + inventory.getFileName() + "\" is provided by several plugins ("
                + owners + "). Using that name on its own will always open the one from " + winner
                + ", use the plugin:name form to pick another.", LogType.WARNING);
    }

    @Override
    public Optional<Inventory> getInventory(@NotNull Key key) {
        Optional<Inventory> own = this.inventoryRegistry().get(key);
        return own.isPresent() ? own : Optional.ofNullable(this.otherInventories.get(key));
    }

    @Override
    public Set<Key> getInventoryKeys() {
        Set<Key> keys = new HashSet<>(this.inventoryRegistry().keys());
        keys.addAll(this.otherInventories.keySet());
        return Collections.unmodifiableSet(keys);
    }

    @Override
    @Deprecated(forRemoval = true)
    @SuppressWarnings("removal")
    public Optional<Inventory> getInventory(String name) {
        return name == null ? Optional.empty() : this.findInventory(name);
    }

    @Override
    @Deprecated(forRemoval = true)
    @SuppressWarnings("removal")
    public Optional<Inventory> getInventory(Plugin plugin, String name) {
        return plugin == null || name == null ? Optional.empty() : this.getInventory(ZMenuKeys.fileKey(plugin.getName(), name));
    }

    @Override
    @Deprecated(forRemoval = true)
    @SuppressWarnings("removal")
    public Optional<Inventory> getInventory(String pluginName, String name) {
        return pluginName == null || name == null ? Optional.empty() : this.getInventory(ZMenuKeys.fileKey(pluginName, name));
    }

    @Override
    public Collection<Inventory> getInventories() {
        List<Inventory> allInventories = new ArrayList<>(this.inventoryRegistry().values());
        for (List<Inventory> inventoryList : this.inventories.values()) {
            allInventories.addAll(inventoryList);
        }
        return allInventories;
    }

    @Override
    public Collection<Inventory> getInventories(Plugin plugin) {
        if (plugin == null) return new ArrayList<>();
        if (plugin == this.plugin) return new ArrayList<>(this.inventoryRegistry().values());
        return this.inventories.getOrDefault(plugin.getName(), new ArrayList<>());
    }

    @Override
    public Optional<Inventory> findInventory(@NotNull String inventoryName) {
        Optional<Inventory> optional = this.getInventory(ZMenuKeys.inventoryKey(inventoryName));
        if (optional.isPresent() || inventoryName.indexOf(':') >= 0) return optional;

        return this.otherInventories.values().stream()
                .filter(inventory -> ZMenuKeys.matches(inventory.key(), inventoryName))
                .min(Comparator.comparing(inventory -> inventory.getPlugin().getName(), String.CASE_INSENSITIVE_ORDER));
    }

    @Override
    public void deleteInventory(Inventory inventory) {
        if (inventory.getPlugin() == this.plugin) {
            this.logReport(this.registries.update(Registries.INVENTORY, ZMenuKeys.NAMESPACE, editor -> {
                if (editor.previous().get(inventory.key()).orElse(null) == inventory) editor.remove(inventory.key());
            }));
            return;
        }

        String pluginName = inventory.getPlugin().getName();
        List<Inventory> inventories = this.inventories.getOrDefault(pluginName, new ArrayList<>());
        inventories.remove(inventory);
        this.inventories.put(pluginName, inventories);
        if (this.otherInventories.remove(inventory.key(), inventory)) {
            inventories.stream()
                    .filter(other -> other.key().equals(inventory.key()))
                    .reduce((first, second) -> second)
                    .ifPresent(other -> this.otherInventories.put(other.key(), other));
        }
    }

    /**
     * Remove the pending load of this file, if it is waiting for a load requirement.
     *
     * @param file the inventory file
     */
    public void removeInventoryLoadRequirement(File file) {
        File absoluteFile = file.getAbsoluteFile();
        this.removeInventoryLoadRequirements(requirementFile -> requirementFile.getAbsoluteFile().equals(absoluteFile));
    }

    /**
     * Remove the pending loads whose file matches the predicate.
     *
     * @param predicate the file predicate
     */
    public void removeInventoryLoadRequirements(Predicate<File> predicate) {
        this.inventoryLoadRequirements.removeIf(requirement -> predicate.test(requirement.getFile()));
    }

    @Override
    public boolean deleteInventory(@NotNull Key key) {
        Optional<Inventory> optional = this.getInventory(key);
        optional.ifPresent(this::deleteInventory);
        return optional.isPresent();
    }

    @Override
    @Deprecated(forRemoval = true)
    @SuppressWarnings("removal")
    public boolean deleteInventory(String name) {
        Optional<Inventory> optional = this.findInventory(name);
        optional.ifPresent(this::deleteInventory);
        return optional.isPresent();
    }

    @Override
    public void deleteInventories(Plugin plugin) {
        if (plugin == this.plugin) {
            this.logReport(this.registries.update(Registries.INVENTORY, ZMenuKeys.NAMESPACE, editor -> editor.previous().keys().forEach(editor::remove)));
            return;
        }

        List<Inventory> removed = this.inventories.remove(plugin.getName());
        if (removed != null) {
            for (Inventory inventory : removed) {
                this.otherInventories.remove(inventory.key(), inventory);
            }
        }
    }

    @Override
    @Deprecated(forRemoval = true)
    @SuppressWarnings("removal")
    public Set<String> getInventoryNames() {
        Set<String> names = new HashSet<>();
        for (Key key : this.getInventoryKeys()) {
            names.add(key.asString());
        }
        return Collections.unmodifiableSet(names);
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


        var topInventory = CompatibilityUtil.getTopInventory(player);
        if (topInventory != null && topInventory.getHolder() instanceof InventoryDefault inventoryDefault) {
            Inventory fromInventory = inventoryDefault.getMenuInventory();
            oldInventories = inventoryDefault.getOldInventories();
            oldInventories.add(fromInventory);
        }

        this.openInventory(player, inventory, page, oldInventories);
    }

    @Override
    public void openInventory(Player player, Inventory inventory, int page, List<Inventory> oldInventories) {

        PlayerOpenInventoryEvent playerOpenInventoryEvent = new PlayerOpenInventoryEvent(player, inventory, page, oldInventories);
        if (Configuration.enableFastEvent) {
            this.getFastEvents().forEach(event -> event.onPlayerOpenInventory(playerOpenInventoryEvent));
        } else playerOpenInventoryEvent.call();

        if (playerOpenInventoryEvent.isCancelled()) return;

        page = playerOpenInventoryEvent.getPage();
        oldInventories = playerOpenInventoryEvent.getOldInventories();

        OpenCause cause = this.isAlreadyOpen(player, inventory) ? OpenCause.REFRESH : OpenCause.OPEN;

        this.currentInventories.put(player.getUniqueId(), inventory);
        this.createInventory(this.plugin, player, EnumInventory.INVENTORY_DEFAULT, page, inventory, oldInventories, cause);
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
        ClassRegistry<PermissibleLoader, MenuPlugin> permissibleRegistry = ClassRegistry.
                <PermissibleLoader,MenuPlugin>of(PermissibleLoader.class, buttonManager::registerPermissible)
                .tryConstructor((clazz, plugin) -> clazz.getConstructor(MenuPlugin.class).newInstance(plugin))
                .tryConstructor(((clazz, plugin) -> clazz.getConstructor(ButtonManager.class).newInstance(buttonManager)))
                .tryNoArgsConstructor()
                .errorLogger(Logger::error);

        int count = VersionFilter.scanAndRegister("fr.maxlego08.menu", this.plugin, AutoPermissibleLoader.class, permissibleRegistry);
        if (Configuration.enableInformationMessage) {
            Logger.info("Registered " + count + " auto permissible loader(s).");
        }

        // Load actions

        if (Configuration.enablePlayerCommandsAsOPAction) { // Disabled by default for security reasons
            buttonManager.registerAction(new PlayerCommandAsOPLoader());
        }
        if (this.plugin.getDialogManager() != null) {
            buttonManager.registerAction(new DialogLoader(this.plugin, this.plugin.getDialogManager()));
        }
        this.plugin.getPacketManager().ifPresent(manager -> buttonManager.registerAction(new PacketEventChangeTitleNameLoader(manager)));
        if (this.plugin.getBedrockManager() != null) {
            buttonManager.registerAction(new BedrockLoader(this.plugin, this.plugin.getBedrockManager()));
        }

        ClassRegistry<ActionLoader, MenuPlugin> actionRegistry = ClassRegistry.
                <ActionLoader,MenuPlugin>of(ActionLoader.class, buttonManager::registerAction)
                .tryConstructor((clazz, plugin) -> clazz.getConstructor(MenuPlugin.class).newInstance(plugin))
                .tryNoArgsConstructor()
                .errorLogger(Logger::error);

        int actionCount = VersionFilter.scanAndRegister("fr.maxlego08.menu", this.plugin, AutoActionLoader.class, actionRegistry);
        if (Configuration.enableInformationMessage) {
            Logger.info("Registered " + actionCount + " auto action loader(s).");
        }

        // NoneLoader
        buttonManager.register(new NoneLoader(this.plugin, ZNoneButton.class, "none"));
        buttonManager.register(new NoneLoader(this.plugin, ZNoneButton.class, "none_slot"));
        buttonManager.register(new NoneLoader(this.plugin, ZNoneButton.class, "perform_command"));

        ClassRegistry<ButtonLoader, MenuPlugin> buttonRegistry = ClassRegistry.
                <ButtonLoader, MenuPlugin>of(ButtonLoader.class, buttonManager::register)
                .tryConstructor((clazz, plugin) -> clazz.getConstructor(MenuPlugin.class).newInstance(plugin))
                .tryConstructor(((clazz, plugin) -> clazz.getConstructor(Plugin.class).newInstance(plugin)))
                .tryNoArgsConstructor()
                .errorLogger(Logger::error);

        int buttonCount = VersionFilter.scanAndRegister("fr.maxlego08.menu", this.plugin, AutoButtonLoader.class, buttonRegistry);
        if (Configuration.enableInformationMessage) {
            Logger.info("Registered " + buttonCount + " auto button loader(s).");
        }

        ClassRegistry<ItemStackSimilar, MenuPlugin> itemStackRegistry = ClassRegistry.
                <ItemStackSimilar, MenuPlugin>of(ItemStackSimilar.class, this::registerItemStackVerification)
                .tryNoArgsConstructor()
                .errorLogger(Logger::error);

        int itemStackCount = VersionFilter.scanAndRegister("fr.maxlego08.menu", this.plugin, AutoItemStackSimilar.class, itemStackRegistry);
        if (Configuration.enableInformationMessage) {
            Logger.info("Registered " + itemStackCount + " auto item stack similar(s).");
        }

        ButtonLoaderRegisterEvent event = new ButtonLoaderRegisterEvent(buttonManager, this, this.plugin.getPatternManager());
        event.call();

        var commandManager = this.plugin.getCommandManager();
        // MaterialArgumentValidator
        commandManager.registerArgumentValidator(new MaterialArgumentValidator(Message.COMMAND_ARGUMENT_MATERIAL));
        commandManager.registerArgumentValidator(new MaterialArgumentValidator(Message.COMMAND_ARGUMENT_BLOCK));

        ClassRegistry<CommandArgumentValidator, MenuPlugin> validatorRegistry = ClassRegistry.
                <CommandArgumentValidator, MenuPlugin>of(CommandArgumentValidator.class, commandManager::registerArgumentValidator)
                .tryNoArgsConstructor()
                .tryConstructor((clazz, plugin) -> clazz.getConstructor(MenuPlugin.class).newInstance(plugin))
                .tryConstructor((clazz, plugin) -> clazz.getConstructor(Plugin.class).newInstance(plugin))
                .errorLogger(Logger::error);

        int validatorCount = VersionFilter.scanAndRegister("fr.maxlego08.menu", this.plugin, AutoCommandArgumentValidator.class, validatorRegistry);
        if (Configuration.enableInformationMessage) {
            Logger.info("Registered " + validatorCount + " auto command argument validator(s).");
        }
    }

    @Override
    public void loadInventories() {

        this.playerMaxPages.clear();
        this.playerPages.clear();
        this.inventoryLoadRequirements.removeIf(requirement -> requirement.getPlugin() == this.plugin);

        this.logReport(this.registries.reload(Registries.INVENTORY));
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
        for (MaterialLoader loader : this.loaders) {
            if (loader.getKey().equalsIgnoreCase(key)) {
                return Optional.of(loader);
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<MaterialLoader> getMaterialLoader() {
        return Collections.unmodifiableCollection(this.loaders);
    }

    @Override
    public void openInventory(Player player, @NotNull Key key) {
        this.openInventory(player, key, 1);
    }

    @Override
    public void openInventory(Player player, @NotNull Key key, int page) {
        this.openOrTell(player, this.getInventory(key), key.value(), key.namespace(), page);
    }

    /**
     * Opens the inventory, or closes the player's one and tells them it does not exist.
     */
    private void openOrTell(Player player, Optional<Inventory> optional, String name, String pluginName, int page) {
        if (optional.isEmpty()) {
            player.closeInventory();
            message(this.plugin, player, Message.INVENTORY_NOT_FOUND, "%name%", name, "%toName%", name, "%plugin%", pluginName);
            return;
        }

        this.openInventory(player, optional.get(), page);
    }

    @Override
    @Deprecated(forRemoval = true)
    @SuppressWarnings("removal")
    public void openInventory(Player player, Plugin plugin, String inventoryName) {
        this.openOrTell(player, this.getInventory(ZMenuKeys.fileKey(plugin.getName(), inventoryName)), inventoryName, plugin.getName(), 1);
    }

    @Override
    @Deprecated(forRemoval = true)
    @SuppressWarnings("removal")
    public void openInventory(Player player, String pluginName, String inventoryName) {
        this.openInventory(player, pluginName, inventoryName, 1);
    }

    @Override
    @Deprecated(forRemoval = true)
    @SuppressWarnings("removal")
    public void openInventory(Player player, String pluginName, String inventoryName, int page) {
        this.openOrTell(player, this.getInventory(ZMenuKeys.fileKey(pluginName, inventoryName)), inventoryName, pluginName, page);
    }

    @Override
    @Deprecated(forRemoval = true)
    @SuppressWarnings("removal")
    public void openInventory(Player player, String inventoryName, int page) {
        this.openOrTell(player, this.findInventory(inventoryName), inventoryName, this.plugin.getName(), page);
    }

    @Override
    @Deprecated(forRemoval = true)
    @SuppressWarnings("removal")
    public void openInventory(Player player, String inventoryName) {
        this.openInventory(player, inventoryName, 1);
    }

    @Override
    public void reloadInventory(Inventory inventory) {

        if (inventory.getPlugin() != this.plugin) {
            this.deleteInventory(inventory);
        }

        /*
         * CommandManager commandManager = this.plugin.getCommandManager();
         * Optional<Command> optional = commandManager.getCommand(inventory); if
         * (optional.isPresent()) { Command command = optional.get();
         * commandManager.unregistetCommands(command); }
         */

        try {
            this.loadInventory(inventory.getPlugin(), inventory.getFile());
        } catch (InventoryException e) {
            Logger.error(e);
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

        Map<String, List<Inventory>> byPlugin = new LinkedHashMap<>();
        List<Inventory> own = new ArrayList<>(this.inventoryRegistry().values());
        if (!own.isEmpty()) byPlugin.put(this.plugin.getName(), own);
        this.inventories.forEach((plugin, inventories) -> {
            if (!inventories.isEmpty()) byPlugin.put(plugin, inventories);
        });

        if (byPlugin.isEmpty()) {
            message(this.plugin, sender, Message.LIST_EMPTY);
            return;
        }

        byPlugin.forEach((plugin, inventories) -> {
            List<String> fileNames = new ArrayList<>(inventories.size());
            for (Inventory inventory : inventories) {
                fileNames.add(inventory.getFileName());
            }
            String inventoriesAsString = ZUtils.toList(fileNames, "§8", "§7");
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
            this.loadInventory(this.plugin, file);
        } catch (InventoryException exception) {
            message(this.plugin, sender, Message.INVENTORY_CREATE_ERROR_EXCEPTION, "%error%", exception.getMessage());
        }
    }

    private boolean isAlreadyOpen(Player player, Inventory inventory) {
        InventoryHolder holder = CompatibilityUtil.getTopInventory(player).getHolder();
        return holder instanceof InventoryDefault inventoryDefault && inventoryDefault.getMenuInventory() == inventory;
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
                this.openInventory(player, inventoryDefault.getMenuInventory(), this.getPage(player), inventoryDefault.getOldInventories());
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
                Logger.error(exception);
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

        if (type.equalsIgnoreCase("yml")) {
            MenuItemStackConversion conversion = this.toMenuItemStack(itemStack);
            conversion.menuItemStack().serialize(configuration.createSection("items." + name));
            if (!conversion.isReadable()) {
                message(this.plugin, sender, Message.SAVE_BASE64_FALLBACK, "%name%", name, "%components%", String.join(", ", conversion.unsupportedComponents()));
            }
        } else if (type.equalsIgnoreCase("base64")) {
            String base64 = ItemStackUtils.serializeItemStack(itemStack);
            configuration.set("items." + name + ".material", "base64:" + base64);
        } else {
            message(this.plugin, sender, Message.SAVE_ERROR_TYPE, "%name%", name);
            return;
        }

        try {
            configuration.save(file);
        } catch (IOException exception) {
            Logger.error(exception);
        }

        message(this.plugin, sender, Message.SAVE_SUCCESS, "%name%", name);

    }

    @Override
    public @NotNull MenuItemStackConversion toMenuItemStack(@NotNull ItemStack itemStack) {
        return MenuItemStackFromItemStack.convert(this, itemStack);
    }

    @EventHandler
    public void onQuid(PlayerQuitEvent event) {
        UUID uniqueId = event.getPlayer().getUniqueId();
        this.currentInventories.remove(uniqueId);
        // Cleared here too: otherwise they only reset on reload, and a returning player
        // inherits the page they were on in a previous session.
        this.playerPages.remove(uniqueId);
        this.playerMaxPages.remove(uniqueId);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;

        for (Inventory inventory : this.getInventories()) {
            OpenWithItem openWithItem = inventory.getOpenWithItem();
            if (openWithItem != null && openWithItem.shouldTrigger(event)) {
                event.setCancelled(true);
                this.openInventory(event.getPlayer(), inventory);
                return;
            }
        }
    }

    @Override
    public List<ClickType> loadClicks(List<String> loadClicks) {
        List<ClickType> clickTypes = new ArrayList<>();
        loadClicks.forEach(clickType -> {
            if (clickType == null) return;
            if (clickType.equalsIgnoreCase("all") || clickType.equalsIgnoreCase("any")) {
                clickTypes.addAll(Configuration.allClicksType);
            } else {
                try {
                    clickTypes.add(ClickType.valueOf(clickType.toUpperCase(Locale.ROOT)));
                } catch (Exception ignored) {
                    Logger.info(clickType + " click type was not found.", LogType.ERROR);
                }
            }
        });

        if (clickTypes.isEmpty()) { // Use all clicks by default
            clickTypes.addAll(Configuration.allClicksType);
        }

        return clickTypes;
    }

    @Override
    public void registerItemStackVerification(ItemStackSimilar itemStackSimilar) {
        this.itemStackSimilarMap.put(itemStackSimilar.getName().toLowerCase(Locale.ROOT), itemStackSimilar);
    }

    @Override
    public Optional<ItemStackSimilar> getItemStackVerification(String name) {
        return Optional.ofNullable(this.itemStackSimilarMap.getOrDefault(name.toLowerCase(Locale.ROOT), null));
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
        for (List<Class<? extends ButtonOption>> options : this.buttonOptions.values()) {
            for (Class<? extends ButtonOption> buttonOption : options) {
                if (buttonOption.getName().equalsIgnoreCase(name)) {
                    return Optional.of(buttonOption);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void registerOption(Plugin plugin, Class<? extends ButtonOption> buttonOption) {

        if (this.getOption(buttonOption.getName()).isPresent()) return;

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
        return YamlParser.loadAndParseFile(file, this.plugin.getGlobalPlaceholders());
    }

    @Override
    public void loadElement(InventoryRequirementType type, String value) {

        Iterator<InventoryLoadRequirement> iterator = this.inventoryLoadRequirements.iterator();
        while (iterator.hasNext()) {
            InventoryLoadRequirement inventoryLoadRequirement = iterator.next();
            inventoryLoadRequirement.removeRequirement(type, value);

            if (inventoryLoadRequirement.canLoad()) {
                if (Configuration.enableInformationMessage) {
                    Logger.info("Conditions are met to load the file " + inventoryLoadRequirement.getFile().getPath(), LogType.SUCCESS);
                }

                try {
                    this.loadInventory(inventoryLoadRequirement.getPlugin(), inventoryLoadRequirement.getFile(), inventoryLoadRequirement.getClassz());
                } catch (InventoryException exception) {
                    Logger.error(exception);
                } finally {
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void registerInventoryOption(Plugin plugin, Class<? extends InventoryOption> inventoryOption) {

        if (this.getInventoryOption(inventoryOption.getName()).isPresent()) return;

        this.inventoryOptions.computeIfAbsent(plugin, e -> new ArrayList<>()).add(inventoryOption);
    }

    @Override
    public Map<Plugin, List<Class<? extends InventoryOption>>> getInventoryOptions() {
        return this.inventoryOptions;
    }

    @Override
    public Optional<Class<? extends InventoryOption>> getInventoryOption(String name) {
        for (List<Class<? extends InventoryOption>> options : this.inventoryOptions.values()) {
            for (Class<? extends InventoryOption> inventoryOption : options) {
                if (inventoryOption.getName().equalsIgnoreCase(name)) {
                    return Optional.of(inventoryOption);
                }
            }
        }
        return Optional.empty();
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
        return this.inventoryListeners;
    }

    @Override
    public ItemStack postProcessSkullItemStack(ItemStack itemStack, Button button, Player player) {
        return this.postProcessSkullItemStack(itemStack, button, player, new fr.maxlego08.menu.api.utils.Placeholders());
    }

    @Override
    public ItemStack postProcessSkullItemStack(ItemStack itemStack, Button button, Player player, Placeholders placeholders) {

        PerformanceDebug performanceDebug = PerformanceDebug.create("Skull item stack processing");

        performanceDebug.start("papi");
        String name = this.papi(this.plugin.parse(player, placeholders.parse(button.getPlayerHead().replace("%player%", player.getName()))), player, true);
        performanceDebug.end();

        if (!PlayerUtil.isValidMinecraftUsername(name)) {
            performanceDebug.printSummary();
            return itemStack;
        }

        performanceDebug.start("findOfflinePlayer");
        OfflinePlayer offlinePlayer = OfflinePlayerCache.get(name);
        performanceDebug.end();

        if (itemStack.getItemMeta() instanceof SkullMeta skullMeta) {
            performanceDebug.start("applyOwnerProfile");
            skullMeta.setOwnerProfile(offlinePlayer.getPlayerProfile().getTextures().isEmpty() ? offlinePlayer.getPlayerProfile().update().join() : offlinePlayer.getPlayerProfile());
            itemStack.setItemMeta(skullMeta);
            performanceDebug.end();
        }

        performanceDebug.printSummary();

        return itemStack;
    }

    @Override
    public void sendMessage(CommandSender sender, Message message, Object... args) {
        message(this.plugin, sender, message, args);
    }
}
