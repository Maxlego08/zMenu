package fr.maxlego08.menu;

import fr.maxlego08.menu.api.ItemManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.event.events.ZMenuItemsLoad;
import fr.maxlego08.menu.api.mechanic.MechanicFactory;
import fr.maxlego08.menu.api.mechanic.MechanicListener;
import fr.maxlego08.menu.item.CustomItemData;
import fr.maxlego08.menu.mechanics.itemjoin.ItemJoinMechanicFactory;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class ZItemManager implements ItemManager{
    private final NamespacedKey itemIdKey;
    private final NamespacedKey ownerKey;
    private final MenuPlugin menuPlugin;

    private final Map<String, MechanicListener> mechanicListeners = new HashMap<>();
    private final Map<String, MechanicFactory<?>> mechanicFactories = new HashMap<>();

    private final Map<String, CustomItemData> customItems = new HashMap<>();

    private boolean isFirstLoad = true;

    public ZItemManager(ZMenuPlugin menuPlugin) {
        this.itemIdKey = new NamespacedKey(menuPlugin, "item-id");
        this.ownerKey = new NamespacedKey(menuPlugin, "owner");
        this.menuPlugin = menuPlugin;
    }

    private void loadMechanics(){
        registerMechanicFactory(new ItemJoinMechanicFactory(this.menuPlugin));
    }

    @Override
    public void loadAll() {
        this.loadMechanics();
        this.loadCustomItems();
    }

    @Override
    public void loadCustomItems() {
        File itemsFolder = new File(menuPlugin.getDataFolder(), "items");
        if (!itemsFolder.exists()) {
            if (itemsFolder.mkdirs()) {
                Logger.info("Items folder created.");
            } else {
                Logger.info("Failed to create items folder.", Logger.LogType.ERROR);
            }
        }
        try (Stream<Path> stream = Files.walk(Paths.get(itemsFolder.getPath()))) {
            stream.skip(1).map(Path::toFile).filter(File::isFile).filter(e -> e.getName().endsWith(".yml")).forEach(this::loadCustomItem);
            ZMenuItemsLoad event = new ZMenuItemsLoad(new HashSet<>(customItems.keySet()), !isFirstLoad);
            this.menuPlugin.getServer().getPluginManager().callEvent(event);
            if (isFirstLoad) {
                isFirstLoad = false;
            }
        } catch (IOException exception) {
            if (Config.enableDebug){
                Logger.info("Error while loading items: " + exception.getMessage(), Logger.LogType.ERROR);
            }
        }
    }

    @Override
    public void loadCustomItem(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String itemId : config.getKeys(false)) {
            String path = itemId + ".";
            MenuItemStack menuItemStack = this.menuPlugin.loadItemStack(config, path, file);
            if (menuItemStack != null) {
                Set<String> mechanicIds = new HashSet<>();

                ConfigurationSection mechanicSection = config.getConfigurationSection(itemId + ".mechanics");
                if (mechanicSection != null) {
                    path += "mechanics.";
                    for (String mechanicId : mechanicSection.getKeys(false)) {
                        MechanicFactory<?> factory = this.mechanicFactories.get(mechanicId);
                        if (factory != null) {
                            factory.parse(this.menuPlugin, itemId, mechanicSection.getConfigurationSection(mechanicId), config, file, path + mechanicId + ".");
                            mechanicIds.add(mechanicId);
                        } else {
                            Logger.info("No MechanicFactory found for mechanicId " + mechanicId + " in item " + itemId, Logger.LogType.WARNING);
                        }
                    }
                }

                this.customItems.put(itemId, new CustomItemData(menuItemStack, mechanicIds));
            } else {
                if (Config.enableDebug){
                    Logger.info("Impossible to load item " + itemId + " from file " + file.getName());
                }
            }
        }
    }

    @Override
    public void reloadCustomItems() {
        this.customItems.clear();
        for (MechanicFactory<?> factory : mechanicFactories.values()) {
            factory.clearMechanics();
        }
        this.loadCustomItems();
    }

    @Override
    public boolean isCustomItem(String itemId) {
        return this.customItems.containsKey(itemId);
    }

    @Override
    public boolean isCustomItem(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().getPersistentDataContainer().has(itemIdKey)) {
            return false;
        }
        String itemId = itemStack.getItemMeta().getPersistentDataContainer().get(itemIdKey, PersistentDataType.STRING);
        return isCustomItem(itemId);
    }

    @Override
    public Optional<String> getItemId(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().getPersistentDataContainer().has(itemIdKey)) {
            return Optional.empty();
        }
        String itemId = itemStack.getItemMeta().getPersistentDataContainer().get(itemIdKey, PersistentDataType.STRING);
        if (itemId == null || !isCustomItem(itemId)) {
            return Optional.empty();
        }
        return Optional.of(itemId);
    }

    @Override
    public void registerListeners(Plugin plugin, String mechanicId, MechanicListener listener) {
        if (mechanicListeners.containsKey(mechanicId)) {
            Logger.info("Listener for mechanic " + mechanicId + " is already registered.", Logger.LogType.WARNING);
            return;
        }
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        mechanicListeners.put(mechanicId, listener);
    }

    @Override
    public void unloadListeners() {
        for (Listener listener: mechanicListeners.values()) {
            HandlerList.unregisterAll(listener);
        }
    }

    @Override
    public void registerMechanicFactory(MechanicFactory<?> factory) {
        if (mechanicFactories.containsKey(factory.getMechanicId())) {
            Logger.info("MechanicFactory " + factory.getMechanicId() + " is already registered.", Logger.LogType.WARNING);
            return;
        }
        mechanicFactories.put(factory.getMechanicId(), factory);
    }

    @Override
    public void giveItem(Player player, String itemId) {
        if (!isCustomItem(itemId)) {
            Logger.info("Item " + itemId + " is not a custom item.", Logger.LogType.WARNING);
            return;
        }

        CustomItemData itemData = this.customItems.get(itemId);
        MenuItemStack menuItemStack = itemData.menuItemStack();
        ItemStack itemStack = menuItemStack.build(player).clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
            persistentDataContainer.set(itemIdKey, PersistentDataType.STRING, itemId);
            persistentDataContainer.set(ownerKey, PersistentDataType.STRING, player.getUniqueId().toString());
            itemStack.setItemMeta(itemMeta);
        }

        boolean shouldCancel = false;
        for (String mechanicId : itemData.mechanicIds()) {
            MechanicListener mechanicListener = mechanicListeners.get(mechanicId);
            if (mechanicListener != null) {
                boolean cancel = mechanicListener.onItemGive(player, itemStack, itemId);
                if (cancel) shouldCancel = true;
            }
        }

        if (shouldCancel) return;
        player.getInventory().addItem(itemStack);
    }

    @Override
    public Set<String> getItemIds() {
        return this.customItems.keySet();
    }

    @Override
    public void executeCheckInventoryItems(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack itemStack = contents[i];
            if (itemStack == null || !itemStack.hasItemMeta()) continue;

            ItemMeta meta = itemStack.getItemMeta();
            if (meta == null || !meta.getPersistentDataContainer().has(itemIdKey)) continue;

            String itemId = meta.getPersistentDataContainer().get(itemIdKey, PersistentDataType.STRING);
            if (itemId == null) continue;

            if (!isCustomItem(itemId)) {
                meta.getPersistentDataContainer().remove(itemIdKey);
                meta.getPersistentDataContainer().remove(ownerKey);
                itemStack.setItemMeta(meta);
                player.getInventory().setItem(i, itemStack);
                continue;
            }

            CustomItemData itemData = customItems.get(itemId);
            if (itemData == null) continue;

            MenuItemStack menuItemStack = itemData.menuItemStack();
            String ownerUuid = meta.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING);
            if (ownerUuid == null) continue;
            Player owner = this.menuPlugin.getServer().getPlayer(UUID.fromString(ownerUuid));
            if (owner == null) continue;

            ItemStack built = menuItemStack.build(owner).clone();

            built.setAmount(itemStack.getAmount());

            ItemMeta builtMeta = built.getItemMeta();
            if (builtMeta == null) continue;
            builtMeta.getPersistentDataContainer().set(itemIdKey, PersistentDataType.STRING, itemId);
            builtMeta.getPersistentDataContainer().set(ownerKey, PersistentDataType.STRING, owner.getUniqueId().toString());

            built.setItemMeta(builtMeta);

            if (!built.isSimilar(itemStack)) {
                player.getInventory().setItem(i, built);
            }
        }
    }

}
