package fr.maxlego08.menu;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.attribute.IAttribute;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.Banner;
import fr.maxlego08.menu.zcore.utils.Firework;
import fr.maxlego08.menu.zcore.utils.LeatherArmor;
import fr.maxlego08.menu.zcore.utils.Potion;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.attribute.AttributeApplier;
import fr.maxlego08.menu.zcore.utils.meta.Meta;
import fr.maxlego08.menu.zcore.utils.nms.NMSUtils;
import fr.maxlego08.menu.zcore.utils.nms.NmsVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class MenuItemStack extends ZUtils {

    private final InventoryManager inventoryManager;
    private final String filePath;
    private final String path;
    private String material;
    private String targetPlayer;
    private String amount;
    private String url;
    private int data;
    private int durability;
    private Potion potion;
    private List<String> lore = new ArrayList<>();
    private List<ItemFlag> flags = new ArrayList<>();
    private String displayName;
    private boolean isGlowing;
    private String modelID;
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private List<IAttribute> attributes = new ArrayList<>();
    private Banner banner;
    private Firework firework;
    private LeatherArmor leatherArmor;
    private boolean needPlaceholderAPI = false;
    private ItemStack cacheItemStack;

    public MenuItemStack(InventoryManager inventoryManager, String filePath, String path) {
        super();
        this.inventoryManager = inventoryManager;
        this.filePath = filePath;
        this.path = path;
    }

    public static MenuItemStack fromItemStack(InventoryManager manager, ItemStack itemStack) {

        MenuItemStack menuItemStack = new MenuItemStack(manager, "", "");

        menuItemStack.setMaterial(itemStack.getType().name());
        int amount = itemStack.getAmount();
        if (amount > 1) menuItemStack.setAmount(String.valueOf(itemStack.getAmount()));
        if (NmsVersion.getCurrentVersion().isItemLegacy()) {
            int durability = itemStack.getDurability();
            if (durability > 0) menuItemStack.setDurability(durability);
            int data = itemStack.getData().getData();
            if (data > 0) menuItemStack.setData(data);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {
            if (itemMeta.hasDisplayName()) {
                menuItemStack.setDisplayName(menuItemStack.colorReverse(itemMeta.getDisplayName()));
            }

            if (itemMeta.hasLore()) {
                menuItemStack.setLore(menuItemStack.colorReverse(Objects.requireNonNull(itemMeta.getLore())));
            }

            menuItemStack.setFlags(new ArrayList<>(itemMeta.getItemFlags()));
            menuItemStack.setEnchantments(itemMeta.getEnchants());

            if (NmsVersion.getCurrentVersion().isCustomModelData() && itemMeta.hasCustomModelData()) {
                menuItemStack.setModelID(itemMeta.getCustomModelData());
            }

            if (itemMeta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) itemMeta;
                // ToDo, upgrade ItemMeta
            }

            if (itemMeta instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemMeta;
                if (enchantmentStorageMeta.hasStoredEnchants()) {
                    menuItemStack.setEnchantments(enchantmentStorageMeta.getEnchants());
                }
            }
        }

        return menuItemStack;
    }

    /**
     * @return the inventoryManager
     */
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public ItemStack build(Player player) {
        return build(player, true);
    }

    public ItemStack build(Player player, boolean useCache) {
        return build(player, useCache, new Placeholders());
    }

    public ItemStack build(Player player, boolean useCache, Placeholders placeholders) {

        // If we don’t need PlaceHolderApi, then we use the cache
        if (!this.needPlaceholderAPI && this.cacheItemStack != null && Config.enableCacheItemStack && useCache) {
            return this.cacheItemStack;
        }

        ItemStack itemStack = null;
        Material material = null;

        // If the material is null, then by default it will be stone, stone is a
        // material present in all versions, so no conflict problem.
        if (this.material == null) {
            this.material = "STONE";
        }

        OfflinePlayer offlinePlayer = this.targetPlayer != null ? Bukkit.getOfflinePlayer(papi(placeholders.parse(this.targetPlayer), player)) : null;

        String papiMaterial = papi(placeholders.parse(this.material), offlinePlayer == null ? player : offlinePlayer);
        int amount = this.parseAmount(offlinePlayer == null ? player : offlinePlayer, placeholders);

        try {
            material = getMaterial(Integer.parseInt(papiMaterial));
        } catch (Exception ignored) {
        }

        if (material == null && papiMaterial != null) {
            try {
                material = Material.getMaterial(papiMaterial.toUpperCase());
            } catch (Exception ignored) {
            }
        }

        if (material == null || material.equals(Material.AIR)) {

            if (papiMaterial.contains(":")) {

                String[] values = papiMaterial.split(":", 2);

                if (values.length == 2) {

                    String key = values[0];
                    String value = values[1];

                    Optional<MaterialLoader> optional = this.inventoryManager.getMaterialLoader(key);
                    if (optional.isPresent()) {
                        MaterialLoader loader = optional.get();
                        itemStack = loader.load(null, null, value);
                    }
                }
            }
        }

        // Final check
        if (material == null) {
            material = Material.STONE;
        }

        if (itemStack == null) {
            itemStack = new ItemStack(material, amount, (byte) this.data);
        }

        if (this.url != null) {
            itemStack = this.createSkull(this.papi(this.url, player));
        }

        if (this.potion != null) {
            itemStack = potion.toItemStack(amount);
        }

        if (this.banner != null) {
            itemStack = banner.toItemStack(amount);
        }

        if (this.firework != null) {
            itemStack = firework.toItemStack(amount);
        }

        if (this.leatherArmor != null) {
            itemStack = leatherArmor.toItemStack(amount);
        }

        if (itemStack == null) {
            return null;
        }

        itemStack.setAmount(amount <= 0 ? 1 : amount);

        if (this.durability != 0) {
            itemStack.setDurability((short) this.durability);
        }

        Material finalMaterial = itemStack.getType();
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {
            if (this.displayName != null) {
                try {
                    Meta.meta.updateDisplayName(itemMeta, placeholders.parse(this.displayName), offlinePlayer == null ? player : offlinePlayer);
                } catch (Exception exception) {
                    Logger.info("Error with update display name for item " + path + " in file " + filePath + " (" + player + ", " + this.displayName + ")", Logger.LogType.ERROR);
                    exception.printStackTrace();
                }
            }

            if (!this.lore.isEmpty()) {
                Meta.meta.updateLore(itemMeta, placeholders.parse(this.lore), offlinePlayer == null ? player : offlinePlayer);
            }

            if (this.isGlowing && NMSUtils.getNMSVersion() != 1.7) {

                itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            try {

                int customModelData = Integer.parseInt(papi(placeholders.parse(this.modelID), offlinePlayer == null ? player : offlinePlayer));
                if (customModelData != 0) itemMeta.setCustomModelData(customModelData);
            } catch (NumberFormatException ignored) {
            }

            this.enchantments.forEach((enchantment, level) -> {
                if (finalMaterial.equals(Material.ENCHANTED_BOOK)) {
                    ((EnchantmentStorageMeta) itemMeta).addStoredEnchant(enchantment, level, true);
                } else {
                    itemMeta.addEnchant(enchantment, level, true);
                }
            });

            this.flags.forEach(itemMeta::addItemFlags);
            itemStack.setItemMeta(itemMeta);
        }

        if (!itemStack.getType().isAir()) {
            AttributeApplier attributeApplier = new AttributeApplier(attributes);
            attributeApplier.apply(itemStack);
        }

        if (!needPlaceholderAPI && Config.enableCacheItemStack) this.cacheItemStack = itemStack;
        return itemStack;
    }


    /**
     * @return the target player
     */
    public String getTargetPlayer() {
        return targetPlayer;
    }

    /**
     * @param targetPlayer the targetPlayer to set
     */
    public void setTargetPlayer(String targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    /**
     * @return the material
     */
    public String getMaterial() {
        return material;
    }

    /**
     * @param material the material to set
     */
    public void setMaterial(String material) {
        this.material = material;
        this.updatePlaceholder(material);
    }

    /**
     * @return the amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(String amount) {
        this.amount = amount;
        this.updatePlaceholder(amount);
    }

    /**
     * @return the url
     */
    @Nullable
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the data
     */
    public int getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(int data) {
        this.data = data;
    }

    /**
     * @return the durability
     */
    public int getDurability() {
        return durability;
    }

    /**
     * @param durability the durability to set
     */
    public void setDurability(int durability) {
        this.durability = durability;
    }

    /**
     * @return the potion
     */
    public Potion getPotion() {
        return potion;
    }

    /**
     * @param potion the potion to set
     */
    public void setPotion(Potion potion) {
        this.potion = potion;
    }

    /**
     * @return the lore
     */
    public List<String> getLore() {
        return lore;
    }

    /**
     * @param lore the lore to set
     */
    public void setLore(List<String> lore) {
        this.lore = lore;
        lore.forEach(this::updatePlaceholder);
    }

    /**
     * @return the flags
     */
    public List<ItemFlag> getFlags() {
        return flags;
    }

    /**
     * @param flags the flags to set
     */
    public void setFlags(List<ItemFlag> flags) {
        this.flags = flags;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.updatePlaceholder(displayName);
    }

    /**
     * @return the isGlowing
     */
    public boolean isGlowing() {
        return isGlowing;
    }

    /**
     * @param isGlowing the isGlowing to set
     */
    public void setGlowing(boolean isGlowing) {
        this.isGlowing = isGlowing;
    }

    /**
     * @return the modelID
     */
    public String getModelID() {
        return modelID;
    }

    /**
     * @param modelID the modelID to set
     */
    public void setModelID(String modelID) {
        this.modelID = modelID;
        this.updatePlaceholder(modelID);
    }

    /**
     * @param modelID the modelID to set
     */
    public void setModelID(int modelID) {
        this.modelID = String.valueOf(modelID);
    }

    /**
     * @return the enchantments
     */
    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    /**
     * @param enchantments the enchantments to set
     */
    public void setEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
    }

    /**
     * @return the attributes
     */
    public List<IAttribute> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set.
     */
    public void setAttributes(List<IAttribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * @return the banner
     */
    public Banner getBanner() {
        return banner;
    }

    /**
     * @param banner the banner to set
     */
    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    /**
     * @return the firework
     */
    public Firework getFirework() {
        return firework;
    }

    /**
     * @param firework the firework to set
     */
    public void setFirework(Firework firework) {
        this.firework = firework;
    }

    /**
     * @return the leather armor
     */
    public LeatherArmor getLeatherArmor() {
        return leatherArmor;
    }

    /**
     * @param leatherArmor the leather armor to set
     */
    public void setLeatherArmor(LeatherArmor leatherArmor) {
        this.leatherArmor = leatherArmor;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getPath() {
        return path;
    }

    public int parseAmount(Player player) {
        int amount = 1;
        try {
            amount = Integer.parseInt(papi(this.amount, player));
        } catch (Exception ignored) {
        }
        return amount;
    }

    public int parseAmount(Player player, Placeholders placeholders) {
        int amount = 1;
        try {
            amount = Integer.parseInt(papi(placeholders.parse(this.amount), player));
        } catch (Exception ignored) {
        }
        return amount;
    }

    public int parseAmount(OfflinePlayer offlinePlayer, Placeholders placeholders) {
        int amount = 1;
        try {
            amount = Integer.parseInt(papi(placeholders.parse(this.amount), offlinePlayer));
        } catch (Exception ignored) {
        }
        return amount;
    }

    /**
     * Let's know if the ItemStack needs a placeholder, if not then the ItemStack will be cached
     *
     * @param string - Current string
     */
    private void updatePlaceholder(String string) {
        if (string == null || needPlaceholderAPI) return;
        needPlaceholderAPI = string.contains("%");
    }

    public void setNeedPlaceholderAPI(boolean needPlaceholderAPI) {
        this.needPlaceholderAPI = needPlaceholderAPI;
    }

}
