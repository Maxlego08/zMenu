package fr.maxlego08.menu;

import com.google.common.collect.ArrayListMultimap;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.attribute.AttributeMergeStrategy;
import fr.maxlego08.menu.api.attribute.AttributeUtil;
import fr.maxlego08.menu.api.attribute.AttributeWrapper;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.context.ZBuildContext;
import fr.maxlego08.menu.api.enchantment.Enchantments;
import fr.maxlego08.menu.api.enums.AmountType;
import fr.maxlego08.menu.api.enums.MenuItemRarity;
import fr.maxlego08.menu.api.font.FontImage;
import fr.maxlego08.menu.api.itemstack.*;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.utils.LoreType;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.OfflinePlayerCache;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.robie.minimessageflow.Template;
import fr.robie.minimessageflow.TemplateList;
import fr.robie.minimessageflow.TextTemplate;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.common.utils.itemstack.MenuItemStackFromItemStack;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.PerformanceDebug;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.pointer.Pointered;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class ZMenuItemStack extends ZUtils implements MenuItemStack {

    private final InventoryManager inventoryManager;
    private final String filePath;
    private final String path;
    private final List<ItemComponent> itemComponents = new ArrayList<>();
    private @Nullable TextTemplate material;
    private @Nullable TextTemplate targetPlayer;
    private @Nullable TextTemplate amount;
    private AmountType amountType = AmountType.SET;
    private @Nullable TextTemplate url;
    private @Nullable TextTemplate data;
    private String tooltipStyle;
    private @Nullable TextTemplate durability;
    private fr.maxlego08.menu.api.itemstack.Potion potion;
    private TemplateList lore = TemplateList.of(List.of());
    private List<ItemFlag> flags = new ArrayList<>();
    private @Nullable Template displayName;
    private Map<String, Template> translatedDisplayName = Map.of();
    private Map<String, TemplateList> translatedLore = Map.of();
    private boolean isGlowing;
    private ResolvableInt modelID;
    private @Nullable TextTemplate itemModel;
    private String equippedModel;
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private boolean clearDefaultAttributes = false;
    private List<AttributeWrapper> attributes = new ArrayList<>();
    private AttributeMergeStrategy attributeMergeStrategy;
    private Banner banner;
    private Firework firework;
    private LeatherArmor leatherArmor;
    private boolean needPlaceholderAPI = false;
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

    private transient ItemStack cacheItemStack;
    /**
     * The text engine's revision the cached item was built at: an older one is built again.
     */
    private transient int cacheRevision;
    /**
     * {@link #isStatic()} and the text engine's revision it was found at.
     */
    private transient volatile int staticRevision = Integer.MIN_VALUE;
    private transient volatile boolean staticValue;

    public ZMenuItemStack(InventoryManager inventoryManager, String filePath, String path) {
        this.inventoryManager = inventoryManager;
        this.filePath = filePath;
        this.path = path;
    }

    public static ZMenuItemStack fromItemStack(InventoryManager manager, ItemStack itemStack) {
        return MenuItemStackFromItemStack.fromItemStack(manager, itemStack);
    }

    /**
     * @return the inventoryManager
     */
    public @NonNull InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    @Override
    public @NonNull ItemStack build(Player player) {
        return this.build(player, true);
    }

    @Override
    public ItemStack build(BuildContext context) {
        int revision = TextService.get().flow().revision();
        Player player = context.getPlayer();
        boolean useCache = context.isUseCache();
        Placeholders placeholders = context.getPlaceholders();

        var performanceDebug = PerformanceDebug.create("menuItemStack:" + this.path + ":" + this.filePath);

        performanceDebug.start("build.shouldUseCache");
        if (this.shouldUseCache(useCache)) {
            performanceDebug.end();
            performanceDebug.printSummary();
            return this.cacheItemStack;
        }
        performanceDebug.end();

        performanceDebug.start("build.createDefaultMaterial");
        if (this.material == null) {
            this.material = TextService.text("STONE");
        }
        performanceDebug.end();

        performanceDebug.start("build.resolveOfflinePlayer");
        OfflinePlayer offlinePlayer = this.resolveOfflinePlayer(player, placeholders);
        performanceDebug.end();

        performanceDebug.start("build.parseAmount");
        int amount = this.parseAmount(offlinePlayer == null ? player : offlinePlayer, placeholders);
        performanceDebug.end();

        performanceDebug.start("build.createItemStack");
        boolean editContextItem = context.getItemStack() != null;
        ItemStack itemStack = this.applySpecialItemStack(player, offlinePlayer, placeholders, amount, editContextItem ? context.getItemStack() : this.createItemStack(player, placeholders, offlinePlayer, amount));
        performanceDebug.end();

        performanceDebug.start("build.applyItemMeta");
        this.applyItemMeta(player, placeholders, offlinePlayer, useCache, itemStack);
        performanceDebug.end();

        performanceDebug.start("build.applyAttributes");
        this.applyAttributes(itemStack);
        performanceDebug.end();

        performanceDebug.start("build.itemComponents");
        if (!this.itemComponents.isEmpty()) {
            for (ItemComponent metadata : this.itemComponents) {
                try {
                    performanceDebug.start("build.itemComponents.apply." + metadata.getParentLoader().getComponentName());
                    metadata.apply(context, itemStack, player);
                    performanceDebug.end();
                } catch (Exception e) {
                    if (Configuration.enableDebug) {
                        Logger.info("Error while applying ItemComponent '" + metadata.getParentLoader().getComponentName() + "' for item " + this.path + " in file " + this.filePath + " (" + player + ")", Logger.LogType.ERROR);
                        Logger.error(e);
                    }
                }
            }
        }
        performanceDebug.end();

        performanceDebug.start("build.setStackSize");
        if (this.amountType == AmountType.ADD) {
            itemStack.setAmount(Math.max(1, itemStack.getAmount() + amount));
        } else if (this.amountType == AmountType.REMOVE) {
            itemStack.setAmount(Math.max(1, itemStack.getAmount() - amount));
        } else if (!editContextItem) {
            itemStack.setAmount(Math.max(1, amount));
        }
        performanceDebug.end();


        if (Configuration.enableCacheItemStack && this.isStatic()) {
            this.cacheItemStack = itemStack;
            this.cacheRevision = revision;
        }

        performanceDebug.printSummary();

        return itemStack;
    }

    @Override
    public @NonNull ItemStack build(Player player, boolean useCache) {
        return this.build(player, useCache, new Placeholders());
    }

    @Override
    public @NonNull ItemStack build(Player player, boolean useCache, Placeholders placeholders) {
        return this.build(new ZBuildContext.Builder().player(player).useCache(useCache).placeholders(placeholders).build());
    }

    private boolean shouldUseCache(boolean useCache) {
        return useCache && Configuration.enableCacheItemStack && this.cacheItemStack != null
                && this.cacheRevision == TextService.get().flow().revision() && this.isStatic();
    }

    /**
     * Drops the built item kept, and what was found about it: called when the item changes.
     */
    private void invalidateCache() {
        this.cacheItemStack = null;
        this.staticRevision = Integer.MIN_VALUE;
    }

    /**
     * The item is the same for every player and every build: every text, value and component is static (no
     * placeholder, no value tag, no nested item that depends on the player), it has no translations, no target
     * player, no material read per player and is not a player head without a texture. Found once per revision of the
     * text engine (a tag added later can make a text dynamic).
     */
    @Override
    public boolean isStatic() {
        int revision = TextService.get().flow().revision();
        if (this.staticRevision == revision) {
            return this.staticValue;
        }
        boolean value = this.computeStatic();
        this.staticValue = value;
        this.staticRevision = revision;
        return value;
    }

    private boolean computeStatic() {
        if (this.needPlaceholderAPI || this.targetPlayer != null || this.isDynamicMaterial()) {
            return false;
        }
        if (!this.translatedDisplayName.isEmpty() || !this.translatedLore.isEmpty()) {
            return false;
        }
        String material = source(this.material);
        if (this.url == null && ("PLAYER_HEAD".equals(material) || "SKULL_ITEM".equals(material))) {
            return false;
        }
        return StaticCheck.isStatic(this.displayName) && StaticCheck.isStatic(this.lore) && StaticCheck.fieldsStatic(this);
    }

    private OfflinePlayer resolveOfflinePlayer(Player player, Placeholders placeholders) {
        return this.targetPlayer != null ? OfflinePlayerCache.get(this.dataText(this.targetPlayer, placeholders, player)) : null;
    }

    private ItemStack createItemStack(Player player, Placeholders placeholders, OfflinePlayer offlinePlayer, int amount) {
        Material material = this.resolveMaterial(player, placeholders, offlinePlayer);
        ItemStack itemStack = this.resolveCustomItem(player, placeholders, material);

        if (itemStack == null) {
            itemStack = this.createDefaultItemStack(player, material, amount);
        }

        return itemStack;
    }

    private Material resolveMaterial(Player player, Placeholders placeholders, OfflinePlayer offlinePlayer) {
        String papiMaterial = this.dataText(this.material, placeholders, offlinePlayer == null ? player : offlinePlayer);

        try {
            return papiMaterial != null ? Material.getMaterial(papiMaterial.toUpperCase(Locale.ROOT)) : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private ItemStack resolveCustomItem(Player player, Placeholders placeholders, Material material) {
        if (material != null && !material.equals(Material.AIR)) {
            return null;
        }
        String papiMaterial = this.dataText(this.material, placeholders, player);

        if (papiMaterial != null && papiMaterial.contains(":")) {
            String[] values = papiMaterial.split(":", 2);
            if (values.length == 2) {
                String key = values[0];
                String value = values[1];

                Optional<MaterialLoader> optional = this.findMaterialLoader(key);
                if (optional.isPresent()) {
                    MaterialLoader loader = optional.get();
                    return loader.load(player, null, this.path, value);
                }
            }
        }
        return null;
    }

    private ItemStack createDefaultItemStack(Player player, Material material, int amount) {
        if (material == null) {
            material = Material.STONE;
        }
        return this.data != null && !source(this.data).isEmpty() ? new ItemStack(material, amount, Byte.parseByte(this.dataText(this.data, new Placeholders(), player))) : new ItemStack(material);
    }

    private ItemStack applySpecialItemStack(Player player, OfflinePlayer offlinePlayer, Placeholders placeholders, int amount, ItemStack itemStack) {
        if (this.url != null && !source(this.url).equalsIgnoreCase("null")) {
            String urlResult = this.dataText(this.url, placeholders, player);
            if (urlResult != null) {
                itemStack = this.createSkull(urlResult);
            }
        }

        if (this.potion != null) {
            itemStack = this.potion.toItemStack(amount);
        }

        if (this.banner != null) {
            itemStack = this.banner.toItemStack(amount);
        }

        if (this.firework != null) {
            itemStack = this.firework.toItemStack(amount);
        }

        if (this.leatherArmor != null) {
            itemStack = this.leatherArmor.toItemStack(amount);
        }

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
        String locale = this.findPlayerLocale(player);
        FontImage fontImage = this.inventoryManager.getPlugin().getFontImage();

        this.applyDisplayNameLore(player, placeholders, itemMeta, offlinePlayer, locale, fontImage, useCache);
        this.applyGlowing(itemMeta);
        this.applyCustomModelData(player, placeholders, offlinePlayer, itemMeta);
        this.applyEnchantments(finalMaterial, itemMeta);
        this.applyFlags(itemMeta);
        this.applyVersionSpecificMeta(itemStack, itemMeta, player, placeholders);

        itemStack.setItemMeta(itemMeta);
    }

    private void applyGlowing(ItemMeta itemMeta) {
        if (!this.isGlowing) {
            return;
        }

        Enchantments helperEnchantments = this.inventoryManager.getPlugin().getEnchantments();
        helperEnchantments.getEnchantments("power").ifPresent(menuEnchantment -> {
            itemMeta.addEnchant(menuEnchantment.enchantment(), 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        });
    }

    private void applyCustomModelData(Player player, Placeholders placeholders, OfflinePlayer offlinePlayer, ItemMeta itemMeta) {
        if (this.modelID == null) {
            return;
        }
        Integer customModelData = this.modelID.resolve(new ZBuildContext.Builder().player(player).placeholders(placeholders).build());
        if (customModelData != null && customModelData != 0) {
            itemMeta.setCustomModelData(customModelData);
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
            itemMeta.addItemFlags(flag);
        }
    }

    private void applyVersionSpecificMeta(ItemStack itemStack, ItemMeta itemMeta, Player player, Placeholders placeholders) {
        if (MinecraftVersion.getCurrentVersion().isAtLeast(MinecraftVersion.parse("1.21"))) {
            this.buildNewItemStackAPI(itemStack, itemMeta, player, placeholders);
        }

        if (MinecraftVersion.getCurrentVersion().isAtLeast(MinecraftVersion.parse("1.20"))) {
            this.buildTrimAPI(itemStack, itemMeta, player, placeholders);
        }

        if (this.clearDefaultAttributes && this.attributes.isEmpty() && MinecraftVersion.getCurrentVersion().isAtLeast(MinecraftVersion.parse("1.20.4"))) {
            itemMeta.setAttributeModifiers(ArrayListMultimap.create());
        }
    }

    private void applyAttributes(@NotNull ItemStack itemStack) {
        if (this.attributes.isEmpty() || itemStack.getType() == Material.AIR) return;

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
        if (offlinePlayer == null && this.inventoryManager.getPlugin().getMetaUpdater() instanceof PaperMetaUpdater metaUpdater) {
            Template name = this.getDisplayNameTemplate(locale);
            if (name != null) {
                try {
                    metaUpdater.updateDisplayName(itemMeta, name.render(placeholders.values(), player));
                } catch (Exception exception) {
                    Logger.info("Error with update display name for item " + this.path + " in file " + this.filePath + " (" + player + ", " + this.getDisplayNameSource() + ")", Logger.LogType.ERROR);
                    Logger.error(exception);
                }
            }
            TemplateList lore = this.getLoreTemplate(locale);
            if (!lore.isEmpty()) {
                metaUpdater.updateLoreComponents(itemMeta, lore.render(placeholders.values(), player), this.loreType);
            }
            return;
        }

        String itemName = null;
        List<String> itemLore = new ArrayList<>();

        if (this.displayName != null) {
            try {
                Template line = locale == null ? this.displayName : this.translatedDisplayName.getOrDefault(locale, this.displayName);
                String displayName = line.source();
                if (displayName != null)
                    itemName = fontImage.replace(this.papi(placeholders.parse(displayName), offlinePlayer == null ? player : offlinePlayer, useCache));
            } catch (Exception exception) {
                Logger.info("Error with update display name for item " + this.path + " in file " + this.filePath + " (" + player + ", " + this.getDisplayNameSource() + ")", Logger.LogType.ERROR);
                Logger.error(exception);
            }
        }

        if (!this.lore.isEmpty()) {
            TemplateList lines = locale == null ? this.lore : this.translatedLore.getOrDefault(locale, this.lore);
            List<String> lore = this.papi(placeholders.parse(TextService.sources(lines)), offlinePlayer == null ? player : offlinePlayer, useCache);
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
            this.inventoryManager.getPlugin().getMetaUpdater().updateDisplayName(itemMeta, itemName, player);
        }

        if (!itemLore.isEmpty()) {
            this.inventoryManager.getPlugin().getMetaUpdater().updateLore(itemMeta, itemLore, this.loreType);
        }
    }

    @Override
    public @Nullable Template getDisplayNameTemplate(@Nullable String locale) {
        if (locale != null) {
            Template translated = this.translatedDisplayName.get(locale);
            if (translated != null) {
                return translated;
            }
        }
        return this.displayName;
    }

    @Override
    public @NotNull TemplateList getLoreTemplate(@Nullable String locale) {
        if (locale != null) {
            TemplateList translated = this.translatedLore.get(locale);
            if (translated != null) {
                return translated;
            }
        }
        return this.lore;
    }

    /**
     * A data text (material, amount...) with its placeholders read: zMenu's values and PlaceholderAPI for the viewer.
     * It was compiled when it was set; a text without placeholders is given as it is. A viewer that is an offline player (not an
     * audience) is read through PlaceholderAPI's offline API as before.
     */
    private @Nullable String dataText(@Nullable TextTemplate template, @NotNull Placeholders placeholders, @Nullable OfflinePlayer viewer) {
        if (template == null) {
            return null;
        }
        if (template.isStatic()) {
            return template.source();
        }
        if (viewer != null && !(viewer instanceof Pointered)) {
            return this.papi(placeholders.parse(template.source()), viewer, true);
        }
        return template.render(placeholders.values(), (Pointered) viewer);
    }

    /**
     * The text as written of a data field, or null when it is not set.
     */
    private static @Nullable String source(@Nullable TextTemplate template) {
        return template == null ? null : template.source();
    }

    private @Nullable String getDisplayNameSource() {
        return this.displayName == null ? null : this.displayName.source();
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
            NamespacedKey itemModelKey = this.parseItemModel(player, placeholders);
            if (itemModelKey != null) {
                itemMeta.setItemModel(itemModelKey);
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
        return source(this.targetPlayer);
    }

    /**
     * @param targetPlayer the targetPlayer to set
     */
    public void setTargetPlayer(String targetPlayer) {
        this.invalidateCache();
        this.targetPlayer = TextService.text(targetPlayer);
    }

    /**
     * @return the material
     */
    public String getMaterial() {
        return source(this.material);
    }

    /**
     * @param material the material to set
     */
    public void setMaterial(String material) {
        this.invalidateCache();
        this.material = TextService.text(material);
    }

    /**
     * @return the amount
     */
    public String getAmount() {
        return source(this.amount);
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(String amount) {
        this.invalidateCache();
        this.amount = TextService.text(amount);
    }

    /**
     * @return the url
     */
    @Nullable
    public String getUrl() {
        return source(this.url);
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.invalidateCache();
        this.url = TextService.text(url);
    }

    /**
     * @return the data
     */
    public String getData() {
        return source(this.data);
    }

    /**
     * @param data the data to set
     */
    public void setData(String data) {
        this.invalidateCache();
        this.data = TextService.text(data);
    }

    /**
     * @return the durability
     */
    public int getDurability() {
        int dura = 1;
        try {
            dura = Integer.parseInt(source(this.durability));
        } catch (Exception ignored) {
        }
        return dura;
    }

    public String getDura() {
        return source(this.durability);
    }

    @Override
    public int parseDura(Player player) {
        return this.parseDura(player, new Placeholders());
    }

    @Override
    public int parseDura(OfflinePlayer offlinePlayer, Placeholders placeholders) {
        int amount = 1;
        try {
            amount = Integer.parseInt(this.dataText(this.durability, placeholders, offlinePlayer));
        } catch (Exception ignored) {
        }
        return amount;
    }

    /**
     * @param durability the durability to set
     */
    public void setDurability(int durability) {
        this.invalidateCache();
        this.durability = TextService.text(Integer.toString(durability));
    }

    public void setDurability(String durability) {
        this.invalidateCache();
        this.durability = TextService.text(durability);
    }

    /**
     * @return the potion
     */
    public Potion getPotion() {
        return this.potion;
    }

    /**
     * @param potion the potion to set
     */
    public void setPotion(Potion potion) {
        this.invalidateCache();
        this.potion = potion;
    }

    /**
     * @return the lore
     */
    @Override
    @Deprecated(since = "1.1.2.2")
    public List<String> getLore() {
        return TextService.sources(this.lore);
    }

    /**
     * @param lore the lore to set
     */
    public void setLore(List<String> lore) {
        this.invalidateCache();
        this.lore = TextService.compileItemLines(lore);
    }

    @Override
    public void setLoreTemplate(@NotNull TemplateList lore) {
        this.invalidateCache();
        this.lore = lore.style(template -> TextService.itemLineStyle(template.source()));
    }

    /**
     * @return the flags
     */
    public List<ItemFlag> getFlags() {
        return this.flags;
    }

    /**
     * @param flags the flags to set
     */
    public void setFlags(List<ItemFlag> flags) {
        this.invalidateCache();
        this.flags = flags;
    }

    /**
     * @return the displayName
     */
    @Override
    @Deprecated(since = "1.1.2.2")
    public String getDisplayName() {
        return this.getDisplayNameSource();
    }

    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.invalidateCache();
        this.displayName = displayName == null ? null : TextService.compileItemLine(displayName);
    }

    @Override
    public void setDisplayNameTemplate(@Nullable Template displayName) {
        this.invalidateCache();
        this.displayName = displayName == null ? null : displayName.style(TextService.itemLineStyle(displayName.source()));
    }

    /**
     * @return the isGlowing
     */
    public boolean isGlowing() {
        return this.isGlowing;
    }

    /**
     * @param isGlowing the isGlowing to set
     */
    public void setGlowing(boolean isGlowing) {
        this.invalidateCache();
        this.isGlowing = isGlowing;
    }

    /**
     * @return the modelID, as its placeholder expression or as the number it was set to
     */
    public String getModelID() {
        if (this.modelID == null) return null;
        if (this.modelID.getExpression() != null) return this.modelID.getExpression();
        Integer resolvedValue = this.modelID.getResolvedValue();
        return resolvedValue != null ? String.valueOf(resolvedValue) : null;
    }

    /**
     * @param modelID the modelID to set
     */
    public void setModelID(String modelID) {
        this.invalidateCache();
        if (modelID == null) {
            this.modelID = null;
            return;
        }
        try {
            this.modelID = ResolvableInt.of(Integer.parseInt(modelID));
        } catch (NumberFormatException e) {
            this.modelID = ResolvableInt.of(modelID);
        }
    }

    /**
     * @param modelID the modelID to set
     */
    public void setModelID(int modelID) {
        this.invalidateCache();
        this.modelID = ResolvableInt.of(modelID);
    }

    /**
     * @return the enchantments
     */
    public Map<Enchantment, Integer> getEnchantments() {
        return this.enchantments;
    }

    /**
     * @param enchantments the enchantments to set
     */
    public void setEnchantments(Map<Enchantment, Integer> enchantments) {
        this.invalidateCache();
        this.enchantments = enchantments;
    }

    /**
     * @return the attributes
     */
    public List<AttributeWrapper> getAttributes() {
        return this.attributes;
    }

    /**
     * @param attributes the attributes to set.
     */
    public void setAttributes(List<AttributeWrapper> attributes) {
        this.invalidateCache();
        this.attributes = attributes;
    }

    @Override
    public AttributeMergeStrategy getAttributeMergeStrategy() {
        return this.attributeMergeStrategy;
    }

    @Override
    public void setAttributeMergeStrategy(AttributeMergeStrategy attributeMergeStrategy) {
        this.invalidateCache();
        this.attributeMergeStrategy = attributeMergeStrategy;
    }

    /**
     * @return the banner
     */
    public Banner getBanner() {
        return this.banner;
    }

    /**
     * @param banner the banner to set
     */
    public void setBanner(Banner banner) {
        this.invalidateCache();
        this.banner = banner;
    }

    /**
     * @return the firework
     */
    public Firework getFirework() {
        return this.firework;
    }

    /**
     * @param firework the firework to set
     */
    public void setFirework(Firework firework) {
        this.invalidateCache();
        this.firework = firework;
    }

    /**
     * @return the leather armor
     */
    public LeatherArmor getLeatherArmor() {
        return this.leatherArmor;
    }

    /**
     * @param leatherArmor the leather armor to set
     */
    public void setLeatherArmor(LeatherArmor leatherArmor) {
        this.invalidateCache();
        this.leatherArmor = leatherArmor;
    }

    @Override
    public String getToolTipStyle() {
        return this.tooltipStyle;
    }

    @Override
    public void setToolTipStyle(String toolTipStyle) {
        this.invalidateCache();
        this.tooltipStyle = toolTipStyle;
    }

    @Override
    public NamespacedKey getItemModel() {
        return this.parseItemModel(null, new Placeholders());
    }

    @Override
    public String getItemModelString() {
        return source(this.itemModel);
    }

    private NamespacedKey parseItemModel(@Nullable Player player, @NotNull Placeholders placeholders) {
        String itemModel = this.dataText(this.itemModel, placeholders, player);
        if (itemModel == null) {
            return null;
        }
        try {
            String[] split = itemModel.split(":", 2);
            if (split.length == 2) {
                return new NamespacedKey(split[0], split[1]);
            } else {
                return NamespacedKey.minecraft(itemModel);
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void setItemModel(NamespacedKey itemModel) {
        this.invalidateCache();
        this.itemModel = TextService.text(itemModel != null ? itemModel.toString() : null);
    }

    @Override
    public void setItemModel(String itemModel) {
        this.invalidateCache();
        this.itemModel = TextService.text(itemModel);
    }

    @Override
    public @NonNull String getFilePath() {
        return this.filePath;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public int parseAmount(Player player) {
        return this.parseAmount(player, new Placeholders());
    }

    @Override
    public int parseAmount(Player player, Placeholders placeholders) {
        int amount = 1;
        try {
            amount = Integer.parseInt(this.dataText(this.amount, placeholders, player));
        } catch (Exception ignored) {
        }
        return amount;
    }

    @Override
    public int parseAmount(OfflinePlayer offlinePlayer, Placeholders placeholders) {
        int amount = 1;
        try {
            amount = Integer.parseInt(this.dataText(this.amount, placeholders, offlinePlayer));
        } catch (Exception ignored) {
        }
        return Math.clamp(amount, 1, 99);
    }

    @Override
    public AmountType getAmountType() {
        return this.amountType;
    }

    @Override
    public void setAmountType(AmountType amountType) {
        this.invalidateCache();
        this.amountType = amountType;
    }

    @Override
    @Deprecated(since = "1.1.2.2")
    public Map<String, String> getTranslatedDisplayName() {
        Map<String, String> names = new HashMap<>(this.translatedDisplayName.size());
        this.translatedDisplayName.forEach((locale, name) -> names.put(locale, name.source()));
        return names;
    }

    public void setTranslatedDisplayName(Map<String, String> translatedDisplayName) {
        this.invalidateCache();
        Map<String, Template> names = new HashMap<>(translatedDisplayName.size());
        translatedDisplayName.forEach((locale, name) -> {
            if (name != null) {
                names.put(locale, TextService.compileItemLine(name));
            }
        });
        this.translatedDisplayName = names;
    }

    @Override
    @Deprecated(since = "1.1.2.2")
    public Map<String, List<String>> getTranslatedLore() {
        Map<String, List<String>> lores = new HashMap<>(this.translatedLore.size());
        this.translatedLore.forEach((locale, lore) -> lores.put(locale, TextService.sources(lore)));
        return lores;
    }

    public void setTranslatedLore(Map<String, List<String>> translatedLore) {
        this.invalidateCache();
        Map<String, TemplateList> lores = new HashMap<>(translatedLore.size());
        translatedLore.forEach((locale, lore) -> lores.put(locale, TextService.compileItemLines(lore)));
        this.translatedLore = lores;
    }

    /**
     * @deprecated use {@link #isStatic()}; true when the item is not static.
     */
    @Override
    @Deprecated(since = "1.1.2.2")
    public boolean isNeedPlaceholderAPI() {
        return !this.isStatic();
    }

    /**
     * @param needPlaceholderAPI true to never keep the built item, whatever {@link #isStatic()} finds
     * @deprecated the item finds whether it can be kept ({@link #isStatic()}); kept to force an item to be built each
     * time.
     */
    @Override
    @Deprecated(since = "1.1.2.2")
    public void setNeedPlaceholderAPI(boolean needPlaceholderAPI) {
        this.invalidateCache();
        this.needPlaceholderAPI = needPlaceholderAPI;
    }

    public ItemStack getCacheItemStack() {
        return this.cacheItemStack;
    }

    public void setCacheItemStack(ItemStack cacheItemStack) {
        this.cacheItemStack = cacheItemStack;
    }

    public int getMaxStackSize() {
        return this.maxStackSize;
    }

    public void setMaxStackSize(int maxStackSize) {
        this.invalidateCache();
        this.maxStackSize = maxStackSize;
    }

    public int getMaxDamage() {
        return this.maxDamage;
    }

    public void setMaxDamage(int maxDamage) {
        this.invalidateCache();
        this.maxDamage = maxDamage;
    }

    public int getDamage() {
        return this.damage;
    }

    public void setDamage(int damage) {
        this.invalidateCache();
        this.damage = damage;
    }

    public int getRepairCost() {
        return this.repairCost;
    }

    public void setRepairCost(int repairCost) {
        this.invalidateCache();
        this.repairCost = repairCost;
    }

    public boolean isUnbreakableEnabled() {
        return this.unbreakableEnabled;
    }

    public void setUnbreakableEnabled(Boolean unbreakableEnabled) {
        this.invalidateCache();
        this.unbreakableEnabled = unbreakableEnabled;
    }

    public boolean isUnbreakableShowInTooltip() {
        return this.unbreakableShowInTooltip;
    }

    public void setUnbreakableShowInTooltip(Boolean unbreakableShowInTooltip) {
        this.invalidateCache();
        this.unbreakableShowInTooltip = unbreakableShowInTooltip;
    }

    public boolean isFireResistant() {
        return this.fireResistant;
    }

    public void setFireResistant(Boolean fireResistant) {
        this.invalidateCache();
        this.fireResistant = fireResistant;
    }

    public boolean isHideTooltip() {
        return this.hideTooltip;
    }

    public void setHideTooltip(Boolean hideTooltip) {
        this.invalidateCache();
        this.hideTooltip = hideTooltip;
    }

    public boolean isHideAdditionalTooltip() {
        return this.hideAdditionalTooltip;
    }

    public void setHideAdditionalTooltip(Boolean hideAdditionalTooltip) {
        this.invalidateCache();
        this.hideAdditionalTooltip = hideAdditionalTooltip;
    }

    public boolean isEnchantmentGlint() {
        return this.enchantmentGlint;
    }

    public void setEnchantmentGlint(Boolean enchantmentGlint) {
        this.invalidateCache();
        this.enchantmentGlint = enchantmentGlint;
    }

    public boolean isEnchantmentShowInTooltip() {
        return this.enchantmentShowInTooltip;
    }

    public void setEnchantmentShowInTooltip(Boolean enchantmentShowInTooltip) {
        this.invalidateCache();
        this.enchantmentShowInTooltip = enchantmentShowInTooltip;
    }

    public boolean isAttributeShowInTooltip() {
        return this.attributeShowInTooltip;
    }

    public void setAttributeShowInTooltip(Boolean attributeShowInTooltip) {
        this.invalidateCache();
        this.attributeShowInTooltip = attributeShowInTooltip;
    }

    public MenuItemRarity getItemRarity() {
        return this.itemRarity;
    }

    public void setItemRarity(MenuItemRarity itemRarity) {
        this.invalidateCache();
        this.itemRarity = itemRarity;
    }

    public TrimConfiguration getTrimConfiguration() {
        return this.trimConfiguration;
    }

    public void setTrimConfiguration(TrimConfiguration trimConfiguration) {
        this.invalidateCache();
        this.trimConfiguration = trimConfiguration;
    }

    public boolean isCenterLore() {
        return this.centerLore;
    }

    public void setCenterLore(boolean centerLore) {
        this.invalidateCache();
        this.centerLore = centerLore;
    }

    public boolean isCenterName() {
        return this.centerName;
    }

    public void setCenterName(boolean centerName) {
        this.invalidateCache();
        this.centerName = centerName;
    }

    public LoreType getLoreType() {
        return this.loreType;
    }

    public void setLoreType(LoreType loreType) {
        this.invalidateCache();
        this.loreType = loreType;
    }

    @Override
    public String getEquippedModel() {
        return this.equippedModel;
    }

    @Override
    public void setEquippedModel(String equippedModel) {
        this.invalidateCache();
        this.equippedModel = equippedModel;
    }

    @Override
    public boolean isClearDefaultAttributes() {
        return this.clearDefaultAttributes;
    }

    @Override
    public void setClearDefaultAttributes(boolean clearDefaultAttributes) {
        this.invalidateCache();
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

    public boolean isDynamicMaterial() {
        if (this.material == null) return false;
        if (!this.material.isStatic()) return true;
        String material = this.material.source();
        if (material.contains(":")) {
            String[] values = material.split(":", 2);
            if (values.length == 2) {
                return this.findMaterialLoader(values[0]).isPresent();
            }
        }
        return false;
    }

    private Optional<MaterialLoader> findMaterialLoader(String prefix) {
        Key key = ZMenuKeys.tryParse(prefix);
        return key == null ? Optional.empty() : this.inventoryManager.getRegistries().registry(Registries.MATERIAL_LOADER).get(key);
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {

        section.set("material", source(this.material));

        if (this.displayName != null) section.set("name", this.displayName.source());
        if (!this.lore.isEmpty()) section.set("lore", TextService.sources(this.lore));
        if (this.loreType != LoreType.REPLACE) section.set("lore-type", this.loreType.name());
        if (this.centerName) section.set("center-name", true);
        if (this.centerLore) section.set("center-lore", true);
        this.serializeTranslations(section);

        if (this.isGlowing) section.set("glow", true);
        String modelId = this.getModelID();
        if (modelId != null && !modelId.equalsIgnoreCase("0")) {
            section.set("model-id", modelId);
        }
        try {
            if (Integer.parseInt(source(this.data)) > 0) section.set("data", source(this.data));
        } catch (Exception ignored) {
            section.set("data", source(this.data));
        }
        if (this.durability != null) section.set("durability", source(this.durability));
        if (this.amount != null && !source(this.amount).equals("1")) {
            try {
                section.set("amount", Integer.parseInt(source(this.amount)));
            } catch (NumberFormatException exception) {
                section.set("amount", source(this.amount));
            }
        }
        if (this.amountType != AmountType.SET) section.set("amount-type", this.amountType.name());
        if (this.targetPlayer != null) section.set("target", source(this.targetPlayer));
        if (this.url != null) section.set("url", source(this.url));

        if (this.potion != null) {
            Color potionColor = this.potion.getColor();

            section.set("potion", this.potion.getType().toString());
            if (this.potion.getLevel() != 1) section.set("level", this.potion.getLevel());
            if (this.potion.isSplash()) section.set("splash", true);
            if (this.potion.hasExtendedDuration()) section.set("extended", true);
            if (this.potion.isArrow()) section.set("arrow", true);

            if (potionColor != null) {
                section.set("color", potionColor.getAlpha() + "," + potionColor.getRed() + "," + potionColor.getGreen() + "," + potionColor.getBlue());
            }
        }

        if (this.firework != null) {
            ConfigurationSection fireworkSection = section.createSection("firework");
            FireworkEffect effect = this.firework.getEffect();
            List<String> stringColors = new ArrayList<>();
            effect.getColors().forEach(color -> stringColors.add(color.getAlpha() + "," + color.getRed() + "," + color.getGreen() + "," + color.getBlue()));
            List<String> stringFadeColors = new ArrayList<>();
            effect.getFadeColors().forEach(color -> stringFadeColors.add(color.getAlpha() + "," + color.getRed() + "," + color.getGreen() + "," + color.getBlue()));

            fireworkSection.set("star", this.firework.isStar());
            fireworkSection.set("flicker", effect.hasFlicker());
            fireworkSection.set("trail", effect.hasTrail());
            fireworkSection.set("type", effect.getType().toString());

            fireworkSection.set("colors", stringColors);
            fireworkSection.set("fadeColors", stringFadeColors);
        }

        if (this.leatherArmor != null) {
            Color leatherArmorColor = this.leatherArmor.getColor();
            section.set("color", leatherArmorColor.getAlpha() + "," + leatherArmorColor.getRed() + "," + leatherArmorColor.getGreen() + "," + leatherArmorColor.getBlue());
        }

        if (this.banner != null) {
            List<Pattern> patterns = this.banner.getPatterns();

            section.set("banner", this.banner.getBaseColor().name());
            if (!patterns.isEmpty()) {
                List<String> stringPatterns = new ArrayList<>();
                for (Pattern pattern : patterns) {
                    stringPatterns.add(pattern.getColor().name() + ":" + pattern.getPattern().name());
                }
                section.set("patterns", stringPatterns);
            }
        }

        if (this.enchantments != null && !this.enchantments.isEmpty()) {
            List<String> stringEnchantments = new ArrayList<>(this.enchantments.size());
            this.enchantments.forEach((enchantment, level) -> stringEnchantments.add(this.enchantmentName(enchantment) + "," + level));
            section.set("enchantments", stringEnchantments);
        }

        if (this.flags != null && !this.flags.isEmpty()) {
            List<String> stringFlags = new ArrayList<>(this.flags.size());
            for (ItemFlag flag : this.flags) {
                stringFlags.add(flag.toString());
            }
            section.set("flags", stringFlags);
        }

        if (!this.attributes.isEmpty()) {
            List<Map<String, Object>> serializedAttributes = new ArrayList<>(this.attributes.size());
            for (AttributeWrapper attribute : this.attributes) {
                serializedAttributes.add(attribute.serialize());
            }
            section.set("attributes", serializedAttributes);
        }
        if (this.attributeMergeStrategy != null && this.attributeMergeStrategy != AttributeMergeStrategy.ADD) {
            section.set("attribute-merge-strategy", this.attributeMergeStrategy.name());
        }
        if (this.clearDefaultAttributes) section.set("clear-default-attributes", true);

        this.serializeNewItemStackOptions(section);

        if (this.trimConfiguration != null) {
            section.set("trim.enable", this.trimConfiguration.isEnable());
            section.set("trim.pattern", this.trimConfiguration.getPattern().getKey().toString());
            section.set("trim.material", this.trimConfiguration.getMaterial().getKey().toString());
        }

        for (ItemComponent component : this.itemComponents) {
            section.set("components." + component.getParentLoader().getComponentName(), component.serialize());
        }
    }

    private void serializeNewItemStackOptions(ConfigurationSection section) {
        if (this.maxStackSize > 0) section.set("max-stack-size", this.maxStackSize);
        if (this.maxDamage > 0) section.set("max-damage", this.maxDamage);
        if (this.damage != 0) section.set("damage", this.damage);
        if (this.repairCost > 0) section.set("repair-cost", this.repairCost);
        if (this.unbreakableEnabled != null) section.set("unbreakable", this.unbreakableEnabled);
        if (this.unbreakableShowInTooltip != null) section.set("unbreakable-show-in-tooltip", this.unbreakableShowInTooltip);
        if (this.fireResistant != null) section.set("fire-resistant", this.fireResistant);
        if (this.hideTooltip != null) section.set("hide-tooltip", this.hideTooltip);
        if (this.hideAdditionalTooltip != null) section.set("hide-additional-tooltip", this.hideAdditionalTooltip);
        if (this.enchantmentGlint != null) section.set("enchantment-glint", this.enchantmentGlint);
        if (this.enchantmentShowInTooltip != null) section.set("enchantment-show-in-tooltip", this.enchantmentShowInTooltip);
        if (this.attributeShowInTooltip != null) section.set("attribute-show-in-tooltip", this.attributeShowInTooltip);
        if (this.itemRarity != null) section.set("item-rarity", this.itemRarity.name());
        if (this.tooltipStyle != null) section.set("tooltip-style", this.tooltipStyle);
        if (this.itemModel != null) section.set("item-model", source(this.itemModel));
        if (this.equippedModel != null) section.set("equipped-model", this.equippedModel);
    }

    private void serializeTranslations(ConfigurationSection section) {
        if (!this.translatedDisplayName.isEmpty()) {
            List<Map<String, Object>> names = new ArrayList<>(this.translatedDisplayName.size());
            this.translatedDisplayName.forEach((locale, name) -> names.add(new LinkedHashMap<>(Map.of("locale", locale, "name", name.source()))));
            section.set("translated-name", names);
        }
        if (!this.translatedLore.isEmpty()) {
            List<Map<String, Object>> lores = new ArrayList<>(this.translatedLore.size());
            this.translatedLore.forEach((locale, lore) -> lores.add(new LinkedHashMap<>(Map.of("locale", locale, "lore", TextService.sources(lore)))));
            section.set("translated-lore", lores);
        }
    }

    private String enchantmentName(Enchantment enchantment) {
        String key = enchantment.getKey().getKey();
        boolean readsBack = this.inventoryManager.getPlugin().getEnchantments().getEnchantments(key)
                .map(menuEnchantment -> menuEnchantment.enchantment().equals(enchantment))
                .orElse(false);
        return readsBack ? key : enchantment.getName();
    }
}
