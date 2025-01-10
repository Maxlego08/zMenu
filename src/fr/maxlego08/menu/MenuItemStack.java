package fr.maxlego08.menu;

import com.google.common.collect.ArrayListMultimap;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.attribute.Attribute;
import fr.maxlego08.menu.api.attribute.IAttribute;
import fr.maxlego08.menu.api.enchantment.Enchantments;
import fr.maxlego08.menu.api.enchantment.MenuEnchantment;
import fr.maxlego08.menu.api.enums.MenuItemRarity;
import fr.maxlego08.menu.api.font.FontImage;
import fr.maxlego08.menu.api.itemstack.TrimConfiguration;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.api.utils.MapConfiguration;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.exceptions.ItemEnchantException;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.Banner;
import fr.maxlego08.menu.zcore.utils.Firework;
import fr.maxlego08.menu.zcore.utils.LeatherArmor;
import fr.maxlego08.menu.zcore.utils.Potion;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.attribute.AttributeApplier;
import fr.maxlego08.menu.zcore.utils.meta.Meta;
import fr.maxlego08.menu.zcore.utils.nms.NmsVersion;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.potion.PotionType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private Map<String, String> translatedDisplayName = new HashMap<>();
    private Map<String, List<String>> translatedLore = new HashMap<>();
    private boolean isGlowing;
    private String modelID;
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private List<IAttribute> attributes = new ArrayList<>();
    private Banner banner;
    private Firework firework;
    private LeatherArmor leatherArmor;
    private boolean needPlaceholderAPI = false;
    private ItemStack cacheItemStack;

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

            try {
                if (itemMeta instanceof PotionMeta) {
                    PotionMeta potionMeta = (PotionMeta) itemMeta;
                    PotionType type = potionMeta.getBasePotionType();
                    if (type != null) {
                        Potion menuPotion = new Potion(type, 0);
                        menuItemStack.setPotion(menuPotion);
                    }
                }
            } catch (Exception ignored) {
            }

            try {
                // ToDo, upgrade for multiple effect
                if (itemMeta instanceof FireworkMeta) {
                    FireworkMeta fireworkMeta = (FireworkMeta) itemMeta;
                    List<FireworkEffect> fireworkEffects = fireworkMeta.getEffects();
                    if (!fireworkEffects.isEmpty()) {
                        FireworkEffect effect = fireworkEffects.get(0);
                        Firework menuFirework = new Firework(false, effect);
                        menuItemStack.setFirework(menuFirework);
                    }
                }
            } catch (Exception ignored) {
            }

            try {
                // ToDo, upgrade for multiple effect
                if (itemMeta instanceof FireworkEffectMeta) {
                    FireworkEffectMeta fireworkMeta = (FireworkEffectMeta) itemMeta;
                    FireworkEffect effect = fireworkMeta.getEffect();
                    Firework menuFirework = new Firework(true, effect);
                    menuItemStack.setFirework(menuFirework);
                }
            } catch (Exception ignored) {
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

        OfflinePlayer offlinePlayer = this.targetPlayer != null ? Bukkit.getOfflinePlayer(papi(placeholders.parse(this.targetPlayer), player, false)) : null;

        String papiMaterial = papi(placeholders.parse(this.material), offlinePlayer == null ? player : offlinePlayer, true);
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
                        itemStack = loader.load(player, null, path, value);
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

        if (itemStack == null) {
            return null;
        }

        itemStack.setAmount(amount <= 0 ? 1 : amount);

        if (this.durability != 0) {
            itemStack.setDurability((short) this.durability);
        }

        Material finalMaterial = itemStack.getType();
        ItemMeta itemMeta = itemStack.getItemMeta();
        String locale = findPlayerLocale(player);
        FontImage fontImage = this.inventoryManager.getFontImage();

        if (itemMeta != null) {
            if (this.displayName != null) {
                try {
                    String displayName = locale == null ? this.displayName : this.translatedDisplayName.getOrDefault(locale, this.displayName);
                    Meta.meta.updateDisplayName(itemMeta, fontImage.replace(placeholders.parse(displayName)), offlinePlayer == null ? player : offlinePlayer);
                } catch (Exception exception) {
                    Logger.info("Error with update display name for item " + path + " in file " + filePath + " (" + player + ", " + this.displayName + ")", Logger.LogType.ERROR);
                    exception.printStackTrace();
                }
            }

            if (!this.lore.isEmpty()) {
                List<String> lore = papi(placeholders.parse(locale == null ? this.lore : this.translatedLore.getOrDefault(locale, this.lore)), player, useCache);
                lore = lore.stream().flatMap(str -> Arrays.stream(str.split("\n"))).map(fontImage::replace).collect(Collectors.toList());

                Meta.meta.updateLore(itemMeta, lore, offlinePlayer == null ? player : offlinePlayer);
            }

            Enchantments helperEnchantments = inventoryManager.getEnchantments();
            if (this.isGlowing) {

                Optional<MenuEnchantment> optional = helperEnchantments.getEnchantments("power");
                if (optional.isPresent()) {
                    MenuEnchantment menuEnchantment = optional.get();
                    itemMeta.addEnchant(menuEnchantment.getEnchantment(), 1, true);
                    itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
            }

            try {

                int customModelData = Integer.parseInt(papi(placeholders.parse(this.modelID), offlinePlayer == null ? player : offlinePlayer, true));
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

            this.flags.stream().filter(Objects::nonNull).forEach(itemMeta::addItemFlags);

            if (NmsVersion.getCurrentVersion().isNewItemStackAPI()) {
                this.buildNewItemStackAPI(itemStack, itemMeta, player, placeholders);
            }

            if (attributes.isEmpty() && NmsVersion.getCurrentVersion().getVersion() >= NmsVersion.V_1_20_4.getVersion()) {
                itemMeta.setAttributeModifiers(ArrayListMultimap.create());
            }

            itemStack.setItemMeta(itemMeta);
        }

        if (itemStack.getType() != Material.AIR) {
            AttributeApplier attributeApplier = new AttributeApplier(attributes);
            attributeApplier.apply(itemStack);
        }

        if (!needPlaceholderAPI && Config.enableCacheItemStack) this.cacheItemStack = itemStack;

        return itemStack;
    }

    private void buildNewItemStackAPI(ItemStack itemStack, ItemMeta itemMeta, Player player, Placeholders placeholders) {
        if (this.maxStackSize > 0 && this.maxStackSize != itemStack.getMaxStackSize()) {
            itemMeta.setMaxStackSize(this.maxStackSize);
        }

        if (itemMeta instanceof Damageable) {
            Damageable damageable = (Damageable) itemMeta;

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
            amount = Integer.parseInt(papi(this.amount, player, true));
        } catch (Exception ignored) {
        }
        return amount;
    }

    public int parseAmount(Player player, Placeholders placeholders) {
        int amount = 1;
        try {
            amount = Integer.parseInt(papi(placeholders.parse(this.amount), player, true));
        } catch (Exception ignored) {
        }
        return amount;
    }

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

        setData(configuration.getInt("data", 0));
        setDurability(configuration.getInt("durability", 0));
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

            Potion potion = new Potion(type, level, splash, extended);
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
            if (object instanceof String) {
                String loreString = (String) object;
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

                if (splitEnchant.length == 1)
                    throw new ItemEnchantException("an error occurred while loading the enchantment " + enchantString);

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

        List<ItemFlag> flags = configuration.getStringList("flags").stream().map(this::getFlag).collect(Collectors.toList());

        List<IAttribute> attributeModifiers = new ArrayList<>();

        if (configuration.contains("attributes")) {
            List<Map<String, Object>> attributesFromConfig = (List<Map<String, Object>>) configuration.getList("attributes");
            if (attributesFromConfig != null) {
                for (Map<String, Object> attributeMap : attributesFromConfig) {
                    attributeModifiers.add(Attribute.deserialize(attributeMap));
                }
            }
        }

        setEnchantments(enchantments);
        setFlags(flags);
        setAttributes(attributeModifiers);

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
}
