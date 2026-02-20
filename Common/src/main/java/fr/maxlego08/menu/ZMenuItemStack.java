package fr.maxlego08.menu;

import com.google.common.collect.ArrayListMultimap;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.attribute.AttributeMergeStrategy;
import fr.maxlego08.menu.api.attribute.AttributeUtil;
import fr.maxlego08.menu.api.attribute.AttributeWrapper;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.enchantment.Enchantments;
import fr.maxlego08.menu.api.enums.MenuItemRarity;
import fr.maxlego08.menu.api.exceptions.ItemEnchantException;
import fr.maxlego08.menu.api.font.FontImage;
import fr.maxlego08.menu.api.itemstack.*;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.api.utils.LoreType;
import fr.maxlego08.menu.api.utils.MapConfiguration;
import fr.maxlego08.menu.api.utils.OfflinePlayerCache;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.context.ZBuildContext;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.common.utils.itemstack.MenuItemStackFormMap;
import fr.maxlego08.menu.common.utils.itemstack.MenuItemStackFromItemStack;
import fr.maxlego08.menu.common.utils.nms.NmsVersion;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

public class ZMenuItemStack extends ZUtils implements MenuItemStack {

    private final InventoryManager inventoryManager;
    private final String filePath;
    private final String path;
    private String material;
    private String targetPlayer;
    private String amount;
    private String url;
    private String data;
    private String tooltipStyle;
    private String durability;
    private Potion potion;
    private List<String> lore = new ArrayList<>();
    private List<ItemFlag> flags = new ArrayList<>();
    private String displayName;
    private Map<String, String> translatedDisplayName = new HashMap<>();
    private Map<String, List<String>> translatedLore = new HashMap<>();
    private boolean isGlowing;
    private String modelID;
    private String itemModel;
    private String equippedModel;
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private boolean clearDefaultAttributes = false;
    private List<AttributeWrapper> attributes = new ArrayList<>();
    private AttributeMergeStrategy attributeMergeStrategy;
    private Banner banner;
    private Firework firework;
    private LeatherArmor leatherArmor;
    private boolean needPlaceholderAPI = false;
    private ItemStack cacheItemStack;

    private boolean centerName;
    private boolean centerLore;
    private LoreType loreType = LoreType.REPLACE;

    private int maxStackSize;
    private int maxDamage;
    private int damage;
    private int repairCost;
    private Boolean unbreakableEnabled;
    private Boolean unbreakableShowInTooltip;
    private Boolean fireResistant;
    private Boolean hideTooltip;
    private Boolean hideAdditionalTooltip;
    private Boolean enchantmentGlint;
    private Boolean enchantmentShowInTooltip;
    private Boolean attributeShowInTooltip;
    private MenuItemRarity itemRarity;
    private TrimConfiguration trimConfiguration;

    private final List<ItemComponent> itemComponents = new ArrayList<>();

    public ZMenuItemStack(InventoryManager inventoryManager, String filePath, String path) {
        this.inventoryManager = inventoryManager;
        this.filePath = filePath;
        this.path = path;
    }

    public static ZMenuItemStack fromItemStack(InventoryManager manager, ItemStack itemStack) {
        return MenuItemStackFromItemStack.fromItemStack(manager, itemStack);
    }

    /**
     * Build a MenuItemStack from a map.
     *
     * @param inventoryManager the inventoryManager of the item
     * @param file             the file where the item is saved
     * @param path             the path of the item in the file
     * @param map              the map which contains the item data
     * @return the menuItemStack
     */
    public static ZMenuItemStack fromMap(InventoryManager inventoryManager, File file, String path, Map<String, Object> map) {
        return MenuItemStackFormMap.fromMap(inventoryManager, file, path, map);
    }

    /**
     * @return the inventoryManager
     */
    public @NonNull InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    @Override
    public @NonNull ItemStack build(Player player) {
        return build(new ZBuildContext.Builder().player(player).build());

    }

    @Override
    public ItemStack build(BuildContext context) {
        Player player = context.getPlayer();
        boolean useCache = context.isUseCache();
        Placeholders placeholders = context.getPlaceholders();

        if (shouldUseCache(useCache)) {
            return this.cacheItemStack;
        }

        // If the material is null, then by default it will be stone, stone is a
        // material present in all versions, so no conflict problem.
        if (this.material == null) {
            this.material = "STONE";
        }

        OfflinePlayer offlinePlayer = resolveOfflinePlayer(player, placeholders);
        int amount = this.parseAmount(offlinePlayer == null ? player : offlinePlayer, placeholders);

        ItemStack itemStack = context.getItemStack() != null ? context.getItemStack() : createItemStack(player, placeholders, offlinePlayer, amount);

        applyItemMeta(player, placeholders, offlinePlayer, useCache, itemStack);
        applyAttributes(itemStack);

        if (!this.itemComponents.isEmpty()) {
            for (ItemComponent metadata : this.itemComponents) {
                try {
                    metadata.apply(context, itemStack, player);
                } catch (Exception e) {
                    if (Configuration.enableDebug){
                        Logger.info("Error while applying ItemComponent '" + metadata.getParentLoader().getComponentName() + "' for item " + path + " in file " + filePath + " (" + player + ")", Logger.LogType.ERROR);
                        e.printStackTrace();
                    }
                }
            }
        }

        if (!needPlaceholderAPI && Configuration.enableCacheItemStack) {
            this.cacheItemStack = itemStack;
        }

        return itemStack;
    }

    @Override
    public @NonNull ItemStack build(Player player, boolean useCache) {
        return build(player);
    }

    @Override
    public @NonNull ItemStack build(Player player, boolean useCache, Placeholders placeholders) {
        return this.build(new ZBuildContext.Builder().player(player).useCache(useCache).placeholders(placeholders).build());
    }

    private boolean shouldUseCache(boolean useCache) {
        return !this.needPlaceholderAPI && this.cacheItemStack != null &&Configuration.enableCacheItemStack && useCache;
    }

    private OfflinePlayer resolveOfflinePlayer(Player player, Placeholders placeholders) {
        return this.targetPlayer != null ? OfflinePlayerCache.get(papi(placeholders.parse(this.targetPlayer), player, false)) : null;
    }

    private ItemStack createItemStack(Player player, Placeholders placeholders, OfflinePlayer offlinePlayer, int amount) {
        Material material = resolveMaterial(player, placeholders, offlinePlayer);
        ItemStack itemStack = resolveCustomItem(player, placeholders, material);

        if (itemStack == null) {
            itemStack = createDefaultItemStack(player, material, amount);
        }

        itemStack = applySpecialItemStack(player, offlinePlayer, placeholders, amount, itemStack);

        return itemStack;
    }

    private Material resolveMaterial(Player player, Placeholders placeholders, OfflinePlayer offlinePlayer) {
        String papiMaterial = papi(placeholders.parse(this.material), offlinePlayer == null ? player : offlinePlayer, true);

        try {
            return getMaterial(Integer.parseInt(papiMaterial));
        } catch (Exception ignored) {
        }

        try {
            return papiMaterial != null ? Material.getMaterial(papiMaterial.toUpperCase()) : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private ItemStack resolveCustomItem(Player player, Placeholders placeholders, Material material) {
        String papiMaterial = papi(placeholders.parse(this.material), player, true);
        if (material != null && !material.equals(Material.AIR)) {
            return null;
        }

        if (papiMaterial != null && papiMaterial.contains(":")) {
            String[] values = papiMaterial.split(":", 2);
            if (values.length == 2) {
                String key = values[0];
                String value = values[1];

                Optional<MaterialLoader> optional = this.inventoryManager.getMaterialLoader(key);
                if (optional.isPresent()) {
                    MaterialLoader loader = optional.get();
                    return loader.load(player, null, path, value);
                }
            }
        }
        return null;
    }

    private ItemStack createDefaultItemStack(Player player, Material material, int amount) {
        if (material == null) {
            material = Material.STONE;
        }
        return data != null && !data.isEmpty() ? new ItemStack(material, amount, Byte.parseByte(this.papi(this.data, player, false))) : new ItemStack(material);
    }

    private ItemStack applySpecialItemStack(Player player, OfflinePlayer offlinePlayer, Placeholders placeholders, int amount, ItemStack itemStack) {
        if (this.url != null && !url.equalsIgnoreCase("null")) {
            String urlResult = this.papi(this.url, player, false);
            if (urlResult != null) {
                itemStack = this.createSkull(urlResult);
            }
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
        itemStack.setAmount(Math.max(1 , amount));

        if (this.durability != null) {
            int dura = this.parseDura(offlinePlayer == null ? player : offlinePlayer, placeholders);
            itemStack.setDurability((short) dura);
        }

        return itemStack;
    }

    private void applyItemMeta(Player player, Placeholders placeholders, OfflinePlayer offlinePlayer, boolean useCache, ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        Material finalMaterial = itemStack.getType();
        String locale = findPlayerLocale(player);
        FontImage fontImage = this.inventoryManager.getFontImage();

        applyDisplayNameLore(player, placeholders, itemMeta, offlinePlayer, locale, fontImage, useCache);
        applyGlowing(itemMeta);
        applyCustomModelData(player, placeholders, offlinePlayer, itemMeta);
        applyEnchantments(finalMaterial, itemMeta);
        applyFlags(itemMeta);
        applyVersionSpecificMeta(itemStack, itemMeta, player, placeholders);

        itemStack.setItemMeta(itemMeta);
    }

    private void applyGlowing(ItemMeta itemMeta) {
        if (!this.isGlowing) {
            return;
        }

        Enchantments helperEnchantments = inventoryManager.getEnchantments();
        helperEnchantments.getEnchantments("power").ifPresent(menuEnchantment -> {
            itemMeta.addEnchant(menuEnchantment.enchantment(), 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        });
    }

    private void applyCustomModelData(Player player, Placeholders placeholders, OfflinePlayer offlinePlayer, ItemMeta itemMeta) {
        try {
            int customModelData = Integer.parseInt(papi(placeholders.parse(this.modelID), offlinePlayer == null ? player : offlinePlayer, true));
            if (customModelData != 0) {
                itemMeta.setCustomModelData(customModelData);
            }
        } catch (NumberFormatException ignored) {
        }
    }

    private void applyEnchantments(Material finalMaterial, ItemMeta itemMeta) {
        this.enchantments.forEach((enchantment, level) -> {
            if (finalMaterial.equals(Material.ENCHANTED_BOOK)) {
                ((EnchantmentStorageMeta) itemMeta).addStoredEnchant(enchantment, level, true);
            } else {
                itemMeta.addEnchant(enchantment, level, true);
            }
        });
    }

    private void applyFlags(ItemMeta itemMeta) {
        for (ItemFlag flag : this.flags) {
            if (flag != null) {
                itemMeta.addItemFlags(flag);
            }
        }
    }

    private void applyVersionSpecificMeta(ItemStack itemStack, ItemMeta itemMeta, Player player, Placeholders placeholders) {
        if (NmsVersion.getCurrentVersion().isNewItemStackAPI()) {
            this.buildNewItemStackAPI(itemStack, itemMeta, player, placeholders);
        }

        if (NmsVersion.getCurrentVersion().isNewHeadApi()) {
            this.buildTrimAPI(itemStack, itemMeta, player, placeholders);
        }

        if (this.clearDefaultAttributes && attributes.isEmpty() && NmsVersion.getCurrentVersion().getVersion() >= NmsVersion.V_1_20_4.getVersion()) {
            itemMeta.setAttributeModifiers(ArrayListMultimap.create());
        }
    }

    private void applyAttributes(@NotNull ItemStack itemStack) {
        if (itemStack.getType() == Material.AIR) {
            return;
        }

        AttributeUtil.applyAttributes(itemStack, this.attributes, this.inventoryManager.getPlugin(), this.attributeMergeStrategy);
    }

    /**
     * Applies the display name and lore to the item stack using the given Placeholders,
     * FontImage, and locale.
     *
     * @param player        the player viewing the item stack.
     * @param placeholders  the Placeholders to use.
     * @param itemMeta      the item meta to update.
     * @param offlinePlayer the offline player viewing the item stack, or null if not applicable.
     * @param locale        the locale to use for translation.
     * @param fontImage     the FontImage to use for replacing font codes.
     * @param useCache      whether to use the cache for the placeholders.
     */
    private void applyDisplayNameLore(Player player, Placeholders placeholders, ItemMeta itemMeta, OfflinePlayer offlinePlayer, String locale, FontImage fontImage, boolean useCache) {

        String itemName = null;
        List<String> itemLore = new ArrayList<>();

        if (this.displayName != null) {
            try {
                String displayName = locale == null ? this.displayName : this.translatedDisplayName.getOrDefault(locale, this.displayName);
                itemName = fontImage.replace(papi(placeholders.parse(displayName), offlinePlayer == null ? player : offlinePlayer, useCache));
            } catch (Exception exception) {
                Logger.info("Error with update display name for item " + path + " in file " + filePath + " (" + player + ", " + this.displayName + ")", Logger.LogType.ERROR);
                exception.printStackTrace();
            }
        }

        if (!this.lore.isEmpty()) {
            List<String> lore = papi(placeholders.parse(locale == null ? this.lore : this.translatedLore.getOrDefault(locale, this.lore)), offlinePlayer == null ? player : offlinePlayer, useCache);
            List<String> flattened = new ArrayList<>();
            for (String str : lore) {
                String[] parts = str.split("\n");
                for (String part : parts) {
                    flattened.add(fontImage.replace(part));
                }
            }
            itemLore = flattened;
        }

        if (itemName != null) {
            this.inventoryManager.getMeta().updateDisplayName(itemMeta, itemName, player);
        }

        if (!itemLore.isEmpty()) {
            this.inventoryManager.getMeta().updateLore(itemMeta, itemLore, this.loreType);
        }
    }

    private void buildNewItemStackAPI(ItemStack itemStack, ItemMeta itemMeta, Player player, Placeholders placeholders) {
        if (this.maxStackSize > 0 && this.maxStackSize != itemStack.getMaxStackSize()) {
            itemMeta.setMaxStackSize(this.maxStackSize);
        }

        if (itemMeta instanceof Damageable damageable) {

            if (this.maxDamage > 0) {
                damageable.setMaxDamage(this.maxDamage);
            }
            if (this.damage != 0) {
                damageable.setDamage(this.damage);
            }

            if (this.unbreakableEnabled != null) {
                damageable.setUnbreakable(this.unbreakableEnabled);

                if (this.unbreakableShowInTooltip != null && !this.unbreakableShowInTooltip) {
                    itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                }
            }
        }

        if (itemMeta instanceof Repairable && this.repairCost > 0) {
            ((Repairable) itemMeta).setRepairCost(this.repairCost);
        }

        if (this.hideTooltip != null) {
            itemMeta.setHideTooltip(this.hideTooltip);
        }
        if (this.hideAdditionalTooltip != null && this.hideAdditionalTooltip) {
            for (ItemFlag value : ItemFlag.values()) {
                itemMeta.addItemFlags(value);
            }
        }

        if (this.enchantmentShowInTooltip != null && !this.enchantmentShowInTooltip) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if (this.enchantmentGlint != null) {
            itemMeta.setEnchantmentGlintOverride(this.enchantmentGlint);
        }

        if (this.fireResistant != null) {
            itemMeta.setFireResistant(this.fireResistant);
        }

        if (this.attributeShowInTooltip != null && !this.attributeShowInTooltip) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        if (this.itemRarity != null) {
            itemMeta.setRarity(this.itemRarity.getItemRarity());
        }

        if (this.tooltipStyle != null) {
            String[] tooltipstyleSplit = this.tooltipStyle.split(":", 2);
            if (tooltipstyleSplit.length == 2) {
                itemMeta.setTooltipStyle(new NamespacedKey(tooltipstyleSplit[0], tooltipstyleSplit[1]));
            }
        }
        if (this.itemModel != null) {
            String[] itemModelSplit = this.itemModel.split(":", 2);
            if (itemModelSplit.length == 2) {
                itemMeta.setItemModel(new NamespacedKey(itemModelSplit[0], itemModelSplit[1]));
            }
        }

        if (this.equippedModel != null) {
            var equippedModelSplit = this.equippedModel.split(":", 2);
            if (equippedModelSplit.length == 2) {
                var equippable = itemMeta.getEquippable();
                equippable.setModel(new NamespacedKey(equippedModelSplit[0], equippedModelSplit[1]));
                itemMeta.setEquippable(equippable);
            }
        }
    }

    private void buildTrimAPI(ItemStack itemStack, ItemMeta itemMeta, Player player, Placeholders placeholders) {
        if (itemMeta instanceof ArmorMeta && this.trimConfiguration != null) {
            ((ArmorMeta) itemMeta).setTrim(new ArmorTrim(this.trimConfiguration.getMaterial(), this.trimConfiguration.getPattern()));
        }
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
    public String getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * @return the durability
     */
    public int getDurability() {
        int dura = 1;
        try {
            dura = Integer.parseInt(this.durability);
        } catch (Exception ignored) {
        }
        return dura;
    }

    public String getDura() {
        return durability;
    }

    @Override
    public int parseDura(Player player) {
        return parseDura(player, new Placeholders());
    }

    @Override
    public int parseDura(OfflinePlayer offlinePlayer, Placeholders placeholders) {
        int amount = 1;
        try {
            amount = Integer.parseInt(papi(placeholders.parse(this.durability), offlinePlayer, true));
        } catch (Exception ignored) {
        }
        return amount;
    }

    /**
     * @param durability the durability to set
     */
    public void setDurability(int durability) {
        this.durability = Integer.toString(durability);
    }

    public void setDurability(String durability) {
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
    public List<AttributeWrapper> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set.
     */
    public void setAttributes(List<AttributeWrapper> attributes) {
        this.attributes = attributes;
    }

    @Override
    public AttributeMergeStrategy getAttributeMergeStrategy() {
        return attributeMergeStrategy;
    }

    @Override
    public void setAttributeMergeStrategy(AttributeMergeStrategy attributeMergeStrategy) {
        this.attributeMergeStrategy = attributeMergeStrategy;
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

    @Override
    public String getToolTipStyle() {
        return tooltipStyle;
    }

    @Override
    public void setToolTipStyle(String toolTipStyle) {
        this.tooltipStyle = toolTipStyle;
    }

    @Override
    public String getItemModel() {
        return itemModel;
    }

    @Override
    public void setItemModel(String itemModel) {
        this.itemModel = itemModel;
    }

    @Override
    public @NonNull String getFilePath() {
        return filePath;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public int parseAmount(Player player) {
        return parseAmount(player, new Placeholders());
    }

    @Override
    public int parseAmount(Player player, Placeholders placeholders) {
        int amount = 1;
        try {
            amount = Integer.parseInt(papi(placeholders.parse(this.amount), player, true));
        } catch (Exception ignored) {
        }
        return amount;
    }

    @Override
    public int parseAmount(OfflinePlayer offlinePlayer, Placeholders placeholders) {
        int amount = 1;
        try {
            amount = Integer.parseInt(papi(placeholders.parse(this.amount), offlinePlayer, true));
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

    public void setTypeMapAccessor(MapConfiguration configuration) {

        setData(configuration.getString("data", "0"));
        setDurability(configuration.getString("durability", null));
        setAmount(configuration.getString("amount", "1"));
        setMaterial(configuration.getString("material", null));
        setTargetPlayer(configuration.getString("target", null));
        setUrl(configuration.getString("url", null));

        Color potionColor = getColor(configuration, "color", null);

        try {
            Material material = Material.valueOf(configuration.getString("material", "").toUpperCase());
            String materialName = material.toString();
            if (materialName.startsWith("LEATHER_")) {
                Color armorColor = getColor(configuration, "color", Color.fromRGB(160, 101, 64));
                String type = materialName.replace("LEATHER_", "");
                setLeatherArmor(new LeatherArmor(LeatherArmor.ArmorType.valueOf(type), armorColor));
            }
        } catch (Exception ignored) {
        }

        if (configuration.contains("potion")) {
            PotionType type = PotionType.valueOf(configuration.getString("potion", "REGEN").toUpperCase());
            int level = configuration.getInt("level", 1);
            boolean splash = configuration.getBoolean("splash", false);
            boolean extended = configuration.getBoolean("extended", false);
            boolean arrow = configuration.getBoolean("arrow", false);

            Potion potion = new Potion(type, level, splash, extended, arrow);
            potion.setColor(potionColor);
            setPotion(potion);
        }

        if (configuration.contains("banner")) {

            DyeColor dyeColor = DyeColor.valueOf(configuration.getString("banner", "WHITE").toUpperCase());
            List<String> stringPattern = configuration.getStringList("patterns");
            List<Pattern> patterns = new ArrayList<>();
            for (String string : stringPattern) {
                String[] split = string.split(":");
                if (split.length != 2) continue;
                patterns.add(new Pattern(DyeColor.valueOf(split[0]), PatternType.valueOf(split[1])));
            }
            setBanner(new Banner(dyeColor, patterns));

        }

        /*if (configuration.contains("firework")) {

            ConfigurationSection section = configuration.getConfigurationSection("firework");
            if (section != null) {
                boolean isStar = section.getBoolean("star", false);
                FireworkEffect.Builder builder = FireworkEffect.builder();
                builder.flicker(section.getBoolean("flicker"));
                builder.trail(section.getBoolean("trail"));
                builder.with(FireworkEffect.Type.valueOf(section.getString("type", "BALL")));
                builder.withColor(getColors(section, "colors"));
                builder.withFade(getColors(section, "fadeColors"));
                setFirework(new Firework(isStar, builder.build()));
            }
        }*/

        List<String> lore = configuration.getStringList("lore");
        if (lore.isEmpty()) {
            Object object = configuration.getObject("lore", null);
            if (object instanceof String loreString) {
                lore = Arrays.asList(loreString.split("\n"));
            }
        }
        setLore(lore);
        setDisplayName(configuration.getString("name", null));
        setGlowing(configuration.getBoolean("glow"));
        setModelID(configuration.getString("modelID", configuration.getString("model-id", configuration.getString("modelId", configuration.getString("customModelId", configuration.getString("customModelData", "0"))))));

        List<String> enchants = configuration.getStringList("enchants");
        Map<Enchantment, Integer> enchantments = new HashMap<>();

        for (String enchantString : enchants) {

            try {

                String[] splitEnchant = enchantString.split(",");

                if (splitEnchant.length == 1) throw new ItemEnchantException("an error occurred while loading the enchantment " + enchantString);

                int level;
                String enchant = splitEnchant[0];
                try {
                    level = Integer.parseInt(splitEnchant[1]);
                } catch (NumberFormatException e) {
                    throw new ItemEnchantException("an error occurred while loading the enchantment " + enchantString);
                }

                Enchantment enchantment = Enchantment.getByName(enchant);
                if (enchantment == null) {
                    throw new ItemEnchantException("an error occurred while loading the enchantment " + enchantString);
                }

                enchantments.put(enchantment, level);

            } catch (ItemEnchantException e) {
                e.printStackTrace();
            }
        }

        List<String> flagStrings = configuration.getStringList("flags");
        List<ItemFlag> flags = new ArrayList<>(flagStrings.size());
        for (String flag : flagStrings) {
            flags.add(this.getFlag(flag));
        }

        List<AttributeWrapper> attributeModifiers = new ArrayList<>();

        if (configuration.contains("attributes")) {
            List<Map<String, Object>> attributesFromConfig = (List<Map<String, Object>>) configuration.getList("attributes");
            if (attributesFromConfig != null) {
                for (Map<String, Object> attributeMap : attributesFromConfig) {
                    attributeModifiers.add(AttributeWrapper.deserialize(attributeMap));
                }
            }
        }

        setEnchantments(enchantments);
        setFlags(flags);
        setAttributes(attributeModifiers);
        setAttributeMergeStrategy(AttributeMergeStrategy.valueOf(configuration.getString("attribute-merge-strategy", "REPLACE").toUpperCase()));
    }

    private Color getColor(MapConfiguration configuration, String key, Color def) {
        String[] split = configuration.getString(key, "").split(",");

        if (split.length == 3) {
            return Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        } else if (split.length == 4) {
            return Color.fromARGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
        }

        return def;
    }

    private List<Color> getColors(MapConfiguration section, String key) {
        List<Color> colors = new ArrayList<>();

        for (String color : section.getStringList(key)) {
            String[] split = color.split(",");

            if (split.length == 3) {
                colors.add(Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])));
            } else if (split.length == 4) {
                colors.add(Color.fromARGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3])));
            }
        }

        return colors;
    }

    public Map<String, String> getTranslatedDisplayName() {
        return translatedDisplayName;
    }

    public void setTranslatedDisplayName(Map<String, String> translatedDisplayName) {
        this.translatedDisplayName = translatedDisplayName;
    }

    public Map<String, List<String>> getTranslatedLore() {
        return translatedLore;
    }

    public void setTranslatedLore(Map<String, List<String>> translatedLore) {
        this.translatedLore = translatedLore;
    }

    public boolean isNeedPlaceholderAPI() {
        return needPlaceholderAPI;
    }

    public void setNeedPlaceholderAPI(boolean needPlaceholderAPI) {
        this.needPlaceholderAPI = needPlaceholderAPI;
    }

    public ItemStack getCacheItemStack() {
        return cacheItemStack;
    }

    public void setCacheItemStack(ItemStack cacheItemStack) {
        this.cacheItemStack = cacheItemStack;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(int repairCost) {
        this.repairCost = repairCost;
    }

    public boolean isUnbreakableEnabled() {
        return unbreakableEnabled;
    }

    public void setUnbreakableEnabled(Boolean unbreakableEnabled) {
        this.unbreakableEnabled = unbreakableEnabled;
    }

    public boolean isUnbreakableShowInTooltip() {
        return unbreakableShowInTooltip;
    }

    public void setUnbreakableShowInTooltip(Boolean unbreakableShowInTooltip) {
        this.unbreakableShowInTooltip = unbreakableShowInTooltip;
    }

    public boolean isFireResistant() {
        return fireResistant;
    }

    public void setFireResistant(Boolean fireResistant) {
        this.fireResistant = fireResistant;
    }

    public boolean isHideTooltip() {
        return hideTooltip;
    }

    public void setHideTooltip(Boolean hideTooltip) {
        this.hideTooltip = hideTooltip;
    }

    public boolean isHideAdditionalTooltip() {
        return hideAdditionalTooltip;
    }

    public void setHideAdditionalTooltip(Boolean hideAdditionalTooltip) {
        this.hideAdditionalTooltip = hideAdditionalTooltip;
    }

    public boolean isEnchantmentGlint() {
        return enchantmentGlint;
    }

    public void setEnchantmentGlint(Boolean enchantmentGlint) {
        this.enchantmentGlint = enchantmentGlint;
    }

    public boolean isEnchantmentShowInTooltip() {
        return enchantmentShowInTooltip;
    }

    public void setEnchantmentShowInTooltip(Boolean enchantmentShowInTooltip) {
        this.enchantmentShowInTooltip = enchantmentShowInTooltip;
    }

    public boolean isAttributeShowInTooltip() {
        return attributeShowInTooltip;
    }

    public void setAttributeShowInTooltip(Boolean attributeShowInTooltip) {
        this.attributeShowInTooltip = attributeShowInTooltip;
    }

    public MenuItemRarity getItemRarity() {
        return itemRarity;
    }

    public void setItemRarity(MenuItemRarity itemRarity) {
        this.itemRarity = itemRarity;
    }

    public TrimConfiguration getTrimConfiguration() {
        return trimConfiguration;
    }

    public void setTrimConfiguration(TrimConfiguration trimConfiguration) {
        this.trimConfiguration = trimConfiguration;
    }

    public boolean isCenterLore() {
        return centerLore;
    }

    public void setCenterLore(boolean centerLore) {
        this.centerLore = centerLore;
    }

    public boolean isCenterName() {
        return centerName;
    }

    public void setCenterName(boolean centerName) {
        this.centerName = centerName;
    }

    public LoreType getLoreType() {
        return loreType;
    }

    public void setLoreType(LoreType loreType) {
        this.loreType = loreType;
    }

    @Override
    public String getEquippedModel() {
        return equippedModel;
    }

    @Override
    public void setEquippedModel(String equippedModel) {
        this.equippedModel = equippedModel;
    }

    @Override
    public boolean isClearDefaultAttributes() {
        return this.clearDefaultAttributes;
    }

    @Override
    public void setClearDefaultAttributes(boolean clearDefaultAttributes) {
        this.clearDefaultAttributes = clearDefaultAttributes;
    }

    @Override
    public Collection<@NotNull ItemComponent> getItemComponents() {
        return Collections.unmodifiableCollection(this.itemComponents);
    }

    @Override
    public void addItemComponent(@NotNull ItemComponent itemMetadata) {
        this.itemComponents.add(itemMetadata);
    }
}
