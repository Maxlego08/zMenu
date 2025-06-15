package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.ZMenuItemStack;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.attribute.Attribute;
import fr.maxlego08.menu.api.attribute.IAttribute;
import fr.maxlego08.menu.api.enchantment.Enchantments;
import fr.maxlego08.menu.api.enchantment.MenuEnchantment;
import fr.maxlego08.menu.api.enums.MenuItemRarity;
import fr.maxlego08.menu.api.itemstack.TrimConfiguration;
import fr.maxlego08.menu.api.utils.LoreType;
import fr.maxlego08.menu.api.utils.TrimHelper;
import fr.maxlego08.menu.api.exceptions.ItemEnchantException;
import fr.maxlego08.menu.api.itemstack.Banner;
import fr.maxlego08.menu.api.itemstack.Firework;
import fr.maxlego08.menu.api.itemstack.LeatherArmor;
import fr.maxlego08.menu.api.itemstack.Potion;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.zcore.utils.nms.NmsVersion;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class MenuItemStackLoader extends ZUtils implements Loader<MenuItemStack> {

    private final InventoryManager manager;

    /**
     * @param manager the inventory manager
     */
    public MenuItemStackLoader(InventoryManager manager) {
        super();
        this.manager = manager;
    }

    /**
     * Load ItemStack
     */
    public MenuItemStack load(YamlConfiguration configuration, String path, Object... objects) {

        File file = (File) objects[0];

        ZMenuItemStack menuItemStack = new ZMenuItemStack(this.manager, file.getPath(), path);
        menuItemStack.setMaterial(configuration.getString(path + "material", null));
        menuItemStack.setData(configuration.getString(path + "data", "0"));
        menuItemStack.setDurability(configuration.getInt(path + "durability", 0));
        menuItemStack.setAmount(configuration.getString(path + "amount", "1"));
        menuItemStack.setTargetPlayer(configuration.getString(path + "target", null));
        menuItemStack.setUrl(configuration.getString(path + "url", null));

        this.loadLeather(menuItemStack, configuration, path);
        this.loadPotions(menuItemStack, configuration, path);
        this.loadBanner(menuItemStack, configuration, path);
        this.loadFireworks(menuItemStack, configuration, path);
        this.loadLore(menuItemStack, configuration, path);

        menuItemStack.setDisplayName(configuration.getString(path + "name", configuration.getString("display_name", configuration.getString("display-name", null))));
        menuItemStack.setCenterName(configuration.getBoolean(path + "center-name", false));
        menuItemStack.setCenterLore(configuration.getBoolean(path + "center-lore", false));
        try {
            menuItemStack.setLoreType(LoreType.valueOf(configuration.getString(path + "lore-type", LoreType.REPLACE.name()).toUpperCase()));
        } catch (Exception ignored) {
        }

        menuItemStack.setGlowing(configuration.getBoolean(path + "glow"));
        menuItemStack.setModelID(configuration.getString(path + "modelID", configuration.getString(path + "model-id", configuration.getString(path + "modelId", configuration.getString(path + "customModelId", configuration.getString(path + "customModelData", configuration.getString("model_data", configuration.getString("custom-model-id", configuration.getString("custom-model-data", configuration.getString("model-data", "0"))))))))));

        this.loadTranslation(menuItemStack, configuration, path);
        this.loadEnchantements(menuItemStack, configuration, path, file);
        menuItemStack.setFlags(configuration.getStringList(path + "flags").stream().map(this::getFlag).collect(Collectors.toList()));

        this.loadAttributes(menuItemStack, configuration, path);

        if (NmsVersion.getCurrentVersion().isNewItemStackAPI()) {
            loadNewItemStacks(menuItemStack, configuration, path, file);
        }
        if (NmsVersion.getCurrentVersion().isNewHeadApi()) {
            loadTrims(menuItemStack, configuration, path, file);
        }
        return menuItemStack;
    }

    /**
     * Loads the lore from the configuration and applies it to the MenuItemStack.
     * The lore can be specified in the configuration as a list of strings
     * under the key "lore", or as a single string with newline characters that
     * are split into a list of strings.
     *
     * @param menuItemStack the MenuItemStack to set the lore for
     * @param configuration the configuration to read from
     * @param path          the path to the configuration key for the lore
     */
    private void loadLore(ZMenuItemStack menuItemStack, YamlConfiguration configuration, String path) {
        List<String> lore = configuration.getStringList(path + "lore");
        if (lore.isEmpty()) {
            Object object = configuration.get(path + "lore", null);
            if (object instanceof String) {
                String loreString = (String) object;
                lore = Arrays.asList(loreString.split("\n"));
            }
        }
        menuItemStack.setLore(lore);
    }

    /**
     * Loads leather armor settings from the configuration and applies them to the MenuItemStack.
     * If the material specified in the configuration is a type of leather armor, it retrieves
     * the color configuration and sets the leather armor with the appropriate type and color.
     *
     * @param menuItemStack The MenuItemStack to apply the leather armor settings to.
     * @param configuration The YamlConfiguration to read the material and color from.
     * @param path          The path in the configuration where the material and color are specified.
     */
    private void loadLeather(ZMenuItemStack menuItemStack, YamlConfiguration configuration, String path) {
        try {
            Material material = Material.valueOf(configuration.getString(path + "material", "").toUpperCase());
            String materialName = material.toString();
            if (materialName.startsWith("LEATHER_")) {
                Color armorColor = getColor(configuration, path + "color", Color.fromRGB(160, 101, 64));
                String type = materialName.replace("LEATHER_", "");
                menuItemStack.setLeatherArmor(new LeatherArmor(LeatherArmor.ArmorType.valueOf(type), armorColor));
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Loads the attributes from the given configuration into the given
     * {@link ZMenuItemStack}. The attributes are loaded from the "attributes"
     * section of the configuration.
     *
     * @param menuItemStack The {@link ZMenuItemStack} to load the attributes
     *                      into.
     * @param configuration The configuration to load the attributes from.
     * @param path          The path of the configuration section to load the
     *                      attributes from.
     */
    private void loadAttributes(ZMenuItemStack menuItemStack, YamlConfiguration configuration, String path) {
        List<IAttribute> attributeModifiers = new ArrayList<>();

        if (configuration.contains(path + "attributes")) {
            List<Map<String, Object>> attributesFromConfig = (List<Map<String, Object>>) configuration.getList(path + "attributes");
            if (attributesFromConfig != null) {
                for (Map<String, Object> attributeMap : attributesFromConfig) {
                    attributeModifiers.add(Attribute.deserialize(attributeMap));
                }
            }
        }

        menuItemStack.setAttributes(attributeModifiers);
    }

    /**
     * Loads the enchantments from the given configuration into the given
     * MenuItemStack.
     * <p>
     * The enchantments are stored in a list under the given path with the
     * following format: "enchant,level". If the enchantment is not found or if
     * the format is incorrect, an ItemEnchantException is thrown.
     * <p>
     * The enchantments are loaded using the Enchantments helper class.
     * <p>
     * The enchantments are stored in a Map of Enchantment to Integer, where the
     * Enchantment is the enchantment and the Integer is the level.
     * <p>
     * {@inheritDoc}
     */
    private void loadEnchantements(ZMenuItemStack menuItemStack, YamlConfiguration configuration, String path, File file) {
        Enchantments helperEnchantments = this.manager.getEnchantments();
        List<String> enchants = configuration.getStringList(path + "enchants");
        Map<Enchantment, Integer> enchantments = new HashMap<>();

        for (String enchantString : enchants) {

            try {

                String[] splitEnchant = enchantString.split(",");

                if (splitEnchant.length == 1)
                    throw new ItemEnchantException("an error occurred while loading the enchantment " + enchantString + " for file " + file.getAbsolutePath() + " with path " + path);

                int level;
                String enchant = splitEnchant[0];
                try {
                    level = Integer.parseInt(splitEnchant[1]);
                } catch (NumberFormatException e) {
                    throw new ItemEnchantException("an error occurred while loading the enchantment " + enchantString + " for file " + file.getAbsolutePath() + " with path " + path);
                }

                Optional<MenuEnchantment> optional = helperEnchantments.getEnchantments(enchant);
                if (!optional.isPresent()) {
                    throw new ItemEnchantException("an error occurred while loading the enchantment " + enchantString + " for file " + file.getAbsolutePath() + " with path " + path);
                }

                enchantments.put(optional.get().getEnchantment(), level);

            } catch (ItemEnchantException e) {
                e.printStackTrace();
            }
        }
        menuItemStack.setEnchantments(enchantments);
    }

    /**
     * Loads potion effects from the given configuration and applies them to the specified MenuItemStack.
     * <p>
     * This method checks if the configuration contains potion data at the specified path. If so, it retrieves
     * the potion type, level, splash status, and extended duration status from the configuration. A Potion object
     * is then created with these attributes and its color is set. Finally, the potion is applied to the MenuItemStack.
     * <p>
     * Supported configuration keys at the given path include:
     * <ul>
     * <li>{@code potion}: The type of potion, defaulting to "REGEN" if not specified.</li>
     * <li>{@code level}: The level of the potion effect, defaulting to 1.</li>
     * <li>{@code splash}: Boolean indicating if the potion is a splash potion, defaulting to false.</li>
     * <li>{@code extended}: Boolean indicating if the potion has an extended duration, defaulting to false.</li>
     * </ul>
     *
     * @param menuItemStack the MenuItemStack to apply the potion effects to
     * @param configuration the configuration containing the potion data
     * @param path          the path within the configuration where the potion data is located
     */
    private void loadPotions(ZMenuItemStack menuItemStack, YamlConfiguration configuration, String path) {
        if (configuration.contains(path + "potion")) {

            Color potionColor = getColor(configuration, path + "color", null);
            PotionType type = PotionType.valueOf(configuration.getString(path + "potion", "REGEN").toUpperCase());
            int level = configuration.getInt(path + "level", 1);
            boolean splash = configuration.getBoolean(path + "splash", false);
            boolean extended = configuration.getBoolean(path + "extended", false);
            boolean arrow = configuration.getBoolean(path + "arrow", false);

            Potion potion = new Potion(type, level, splash, extended, arrow);
            potion.setColor(potionColor);
            menuItemStack.setPotion(potion);
        }
    }

    /**
     * Loads a banner from the given configuration at the given path.
     * <p>
     * The configuration should contain the following elements:
     * <ul>
     * <li>A string at {@code path + "banner"} which is the base color of the banner.</li>
     * <li>A list of strings at {@code path + "patterns"} which are the patterns on the banner. Each string should be in the format {@code "color:pattern"} where {@code color} is a string representing a {@link DyeColor} and {@code pattern} is a string representing a {@link PatternType}.</li>
     * </ul>
     *
     * @param menuItemStack the {@link ZMenuItemStack} to load the banner on
     * @param configuration the configuration to load the banner from
     * @param path          the path to the banner in the configuration
     */
    private void loadBanner(ZMenuItemStack menuItemStack, YamlConfiguration configuration, String path) {
        if (configuration.contains(path + "banner")) {

            DyeColor dyeColor = DyeColor.valueOf(configuration.getString(path + "banner", "WHITE").toUpperCase());
            List<String> stringPattern = configuration.getStringList(path + "patterns");
            List<Pattern> patterns = new ArrayList<>();
            for (String string : stringPattern) {
                String[] split = string.split(":");
                if (split.length != 2) continue;
                patterns.add(new Pattern(DyeColor.valueOf(split[0]), PatternType.valueOf(split[1])));
            }
            menuItemStack.setBanner(new Banner(dyeColor, patterns));

        }
    }

    /**
     * Loads a firework effect from the given configuration section
     * and applies it to the given {@link ZMenuItemStack}.
     * <p>
     * The configuration section should contain the following keys:
     *
     * <ul>
     *  <li> {@code star}: whether the firework effect is a star or not. Optional, defaults to {@code false}. </li>
     *  <li> {@code flicker}: whether the firework effect should flicker. Optional, defaults to {@code false}. </li>
     *  <li> {@code trail}: whether the firework effect should have a trail. Optional, defaults to {@code false}. </li>
     *  <li> {@code type}: the type of the firework effect. Optional, defaults to {@code BALL}. </li>
     *  <li> {@code colors}: the colors of the firework effect. Optional. </li>
     *  <li> {@code fadeColors}: the fade colors of the firework effect. Optional. </li>
     * </ul>
     *
     * @param menuItemStack the item stack to set the firework effect on
     * @param configuration the configuration section to load the firework effect from
     * @param path          the path to the configuration section
     */
    private void loadFireworks(ZMenuItemStack menuItemStack, YamlConfiguration configuration, String path) {
        if (configuration.contains(path + "firework")) {
            ConfigurationSection section = configuration.getConfigurationSection(path + "firework");
            if (section != null) {
                boolean isStar = section.getBoolean("star", false);
                FireworkEffect.Builder builder = FireworkEffect.builder();
                builder.flicker(section.getBoolean("flicker"));
                builder.trail(section.getBoolean("trail"));
                builder.with(FireworkEffect.Type.valueOf(section.getString("type", "BALL")));
                builder.withColor(getColors(section, "colors"));
                builder.withFade(getColors(section, "fadeColors"));
                menuItemStack.setFirework(new Firework(isStar, builder.build()));
            }
        }
    }

    /**
     * Load the translated name and lore from the configuration for the given menu item stack.
     * <p>
     * This method calls {@link #loadTranslatedName(ZMenuItemStack, YamlConfiguration, String)} and
     * {@link #loadTranslatedLore(ZMenuItemStack, YamlConfiguration, String)} to load the translated
     * name and lore respectively.
     *
     * @param menuItemStack the menu item stack to load the translated name and lore for
     * @param configuration the configuration to load the translated name and lore from
     * @param path          the path to the configuration key for the translated name and lore
     */
    private void loadTranslation(ZMenuItemStack menuItemStack, YamlConfiguration configuration, String path) {
        loadTranslatedName(menuItemStack, configuration, path);
        loadTranslatedLore(menuItemStack, configuration, path);
    }

    /**
     * Load the translated lore from the configuration for the given menu item stack.
     * <p>
     * The translated lore is a map from locale to lore, where the locale is the
     * language code and the lore is the translated lore of the item.
     * <p>
     * The translated lore is loaded from the configuration under the key
     * "translatedLore" or "translated-lore".
     *
     * @param menuItemStack the menu item stack to load the translated lore for
     * @param configuration the configuration to load the translated lore from
     * @param path          the path to the configuration key for the translated lore
     */
    private void loadTranslatedLore(ZMenuItemStack menuItemStack, YamlConfiguration configuration, String path) {
        Map<String, List<String>> translatedLore = new HashMap<>();
        String loadString = configuration.contains(path + "translatedLore") ? "translatedLore" : configuration.contains(path + "translated-lore") ? "translated-lore" : null;
        if (loadString != null) {
            configuration.getMapList(path + loadString).forEach(map -> {
                if (map.containsKey("locale") && map.containsKey("lore")) {
                    String locale = (String) map.get("locale");
                    List<String> name = (List<String>) map.get("lore");
                    translatedLore.put(locale.toLowerCase(), name);
                }
            });
        }
        menuItemStack.setTranslatedLore(translatedLore);
    }

    /**
     * Load the translated name from the configuration for the given menu item stack.
     * <p>
     * The translated name is a map from locale to name, where the locale is the
     * language code and the name is the translated name of the item.
     * <p>
     * The translated name is loaded from the configuration under the key
     * "translatedName" or "translated-name".
     *
     * @param menuItemStack the menu item stack to load the translated name for
     * @param configuration the configuration to load the translated name from
     * @param path          the path to the configuration key for the translated name
     */
    private void loadTranslatedName(ZMenuItemStack menuItemStack, YamlConfiguration configuration, String path) {
        Map<String, String> translatedDisplayName = new HashMap<>();
        String loadString = configuration.contains(path + "translatedName") ? "translatedName" : configuration.contains(path + "translated-name") ? "translated-name" : null;
        if (loadString != null) {
            configuration.getMapList(path + loadString).forEach(map -> {
                if (map.containsKey("locale") && map.containsKey("name")) {
                    String locale = (String) map.get("locale");
                    String name = (String) map.get("name");
                    translatedDisplayName.put(locale.toLowerCase(), name);
                }
            });
        }
        menuItemStack.setTranslatedDisplayName(translatedDisplayName);
    }

    /**
     * Returns the given object if it is a Boolean, otherwise returns null.
     * <p>
     * This is a utility method for deserializing booleans from configurations.
     *
     * @param o the object to check
     * @return the object if it is a Boolean, otherwise null
     */
    private Boolean getOrNull(Object o) {
        return o instanceof Boolean ? (Boolean) o : null;
    }

    /**
     * Loads additional item stack properties from the configuration into the provided MenuItemStack.
     * <p>
     * This method reads various properties such as max stack size, max damage, repair cost,
     * unbreakable status, fire resistance, tooltip visibility, enchantment and attribute glints,
     * and item rarity from the provided YamlConfiguration, and applies them to the given MenuItemStack.
     * <p>
     * Additionally, it configures trim patterns and materials if specified, logging errors for
     * any missing configurations.
     *
     * @param menuItemStack the MenuItemStack to configure
     * @param configuration the YamlConfiguration containing item stack properties
     * @param path          the path within the configuration to read from
     * @param file          the file from which the configuration was loaded, used for logging errors
     */
    private void loadNewItemStacks(ZMenuItemStack menuItemStack, YamlConfiguration configuration, String path, File file) {
        menuItemStack.setMaxStackSize(configuration.getInt(path + "max-stack-size", 0));
        menuItemStack.setMaxDamage(configuration.getInt(path + "max-damage", 0));
        menuItemStack.setDamage(configuration.getInt(path + "damage", 0));
        menuItemStack.setRepairCost(configuration.getInt(path + "repair-cost", 0));
        menuItemStack.setUnbreakableEnabled(getOrNull(configuration.get(path + "unbreakable", null)));
        menuItemStack.setUnbreakableShowInTooltip(getOrNull(configuration.get(path + "unbreakable-show-in-tooltip", null)));
        menuItemStack.setFireResistant(getOrNull(configuration.get(path + "fire-resistant", null)));
        menuItemStack.setHideTooltip(getOrNull(configuration.get(path + "hide-tooltip", null)));
        menuItemStack.setHideAdditionalTooltip(getOrNull(configuration.get(path + "hide-additional-tooltip", null)));
        menuItemStack.setEnchantmentGlint(getOrNull(configuration.get(path + "enchantment-glint", null)));
        menuItemStack.setEnchantmentShowInTooltip(getOrNull(configuration.get(path + "enchantment-show-in-tooltip", null)));
        menuItemStack.setAttributeShowInTooltip(getOrNull(configuration.get(path + "attribute-show-in-tooltip", null)));

        String rarityString = configuration.getString("item-rarity", null);
        if (rarityString != null) {
            menuItemStack.setItemRarity(MenuItemRarity.valueOf(rarityString.toUpperCase()));
        }
        String tooltypestyleString = configuration.getString(path+"tooltip-style", null);
        if (tooltypestyleString != null) {
            menuItemStack.setToolTipStyle(tooltypestyleString);
        }
        String itemModelString = configuration.getString(path + "item-model", null);
        if (itemModelString != null) {
            menuItemStack.setItemModel(itemModelString);
        }

    }

    private void loadTrims(ZMenuItemStack menuItemStack, YamlConfiguration configuration, String path, File file) {
        boolean enableTrim = configuration.getBoolean(path + "trim.enable", false);
        if (enableTrim) {
            TrimHelper trimHelper = new TrimHelper();
            TrimPattern trimPattern = trimHelper.getTrimPatterns().get(configuration.getString(path + "trim.pattern", "").toLowerCase());
            if (trimPattern == null) {
                enableTrim = false;
                Bukkit.getLogger().severe("Trim pattern " + configuration.getString(path + "trim.pattern", "") + " was not found for item " + file.getAbsolutePath());
            }
            TrimMaterial trimMaterial = trimHelper.getTrimMaterials().get(configuration.getString(path + "trim.material", "").toLowerCase());
            if (trimMaterial == null) {
                enableTrim = false;
                Bukkit.getLogger().severe("Trim material " + configuration.getString(path + "trim.material", "") + " was not found for item " + file.getAbsolutePath());
            }
            menuItemStack.setTrimConfiguration(new TrimConfiguration(enableTrim, trimMaterial, trimPattern));
        }
    }

    /**
     * Parse a color from a YamlConfiguration.
     * <p>
     * The color may be specified in either RGB (three integers) or ARGB (four integers) format.
     * If the configuration does not contain the specified key, or if the value is not a valid color,
     * the specified default value is returned.
     * </p>
     *
     * @param configuration the configuration to read the color from
     * @param key           the key to read the color from
     * @param def           the default value to return if the color cannot be parsed
     * @return the parsed color, or the default value if the color cannot be parsed
     */
    private Color getColor(YamlConfiguration configuration, String key, Color def) {
        String[] split = configuration.getString(key, "").split(",");
        try {
            if (split.length == 3) {
                return Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            }
            if (split.length == 4) {
                return Color.fromARGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
            }
        } catch (NumberFormatException ignored) {
        }
        return def;
    }

    /**
     * Reads a list of colors from a ConfigurationSection.
     * <p>
     * Colors may be specified in either RGB (three integers) or ARGB (four integers) format.
     * If a color is not a valid color, it is ignored.
     * </p>
     *
     * @param section the configuration section to read the colors from
     * @param key     the key to read the colors from
     * @return a list of colors
     */
    private List<Color> getColors(ConfigurationSection section, String key) {
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

    /**
     * Saves the properties of a MenuItemStack to a YamlConfiguration file.
     * <p>
     * This method serializes various properties of the given MenuItemStack, such as
     * material, display name, lore, model ID, durability, amount, URL, potion effects,
     * firework effects, leather armor color, banner patterns, enchantments, and flags.
     * These properties are stored in the provided YamlConfiguration under the specified path.
     * <p>
     * Additionally, it attempts to save the configuration to the provided file, logging
     * any IOExceptions that occur during the process.
     *
     * @param item          the MenuItemStack whose properties are to be saved
     * @param configuration the YamlConfiguration to store the properties in
     * @param path          the path within the configuration to store the properties under
     * @param file          the file in which to save the configuration
     * @param objects       additional objects for potential future use
     */
    public void save(MenuItemStack item, YamlConfiguration configuration, String path, File file, Object... objects) {

        configuration.set(path + "material", item.getMaterial());

        if (item.getDisplayName() != null) configuration.set(path + "name", item.getDisplayName());
        if (!item.getLore().isEmpty()) configuration.set(path + "lore", item.getLore());
        if (item.isGlowing()) configuration.set(path + "glow", item.isGlowing());
        if (item.getModelID() != null && !item.getModelID().equalsIgnoreCase("0")) {
            configuration.set(path + "model-id", item.getModelID());
        }
        try {
            if (Integer.parseInt(item.getData()) > 0) configuration.set(path + "data", item.getData());
        } catch (Exception ignored) {
            configuration.set(path + "data", item.getData());
        }
        if (item.getDurability() > 0) configuration.set(path + "durability", item.getDurability());
        if (item.getAmount() != null) {
            try {
                int value = Integer.parseInt(item.getAmount());
                if (value > 1) {
                    configuration.set(path + "amount", value);
                }
            } catch (Exception exception) {
                configuration.set(path + "amount", item.getAmount());
            }
        }
        if (item.getUrl() != null) configuration.set(path + "url", item.getUrl());

        Potion potion = item.getPotion();
        Firework firework = item.getFirework();
        LeatherArmor leatherArmor = item.getLeatherArmor();
        Banner banner = item.getBanner();

        if (potion != null) {
            Color potionColor = potion.getColor();

            configuration.set(path + "potion", potion.getType().toString());
            if (potion.getLevel() != 0) configuration.set(path + "level", potion.getLevel());
            if (potion.isSplash()) configuration.set(path + "splash", true);
            if (potion.hasExtendedDuration()) configuration.set(path + "extended", true);

            if (potionColor != null) {
                configuration.set("color", potionColor.getAlpha() + "," + potionColor.getRed() + "," + potionColor.getGreen() + "," + potionColor.getBlue());
            }
        }

        if (firework != null) {
            ConfigurationSection fireworkSection = configuration.createSection(path + "firework");
            FireworkEffect effect = firework.getEffect();
            List<String> stringColors = new ArrayList<>();
            effect.getColors().forEach(color -> stringColors.add(color.getAlpha() + "," + color.getRed() + "," + color.getGreen() + "," + color.getBlue()));
            List<String> stringFadeColors = new ArrayList<>();
            effect.getColors().forEach(color -> stringFadeColors.add(color.getAlpha() + "," + color.getRed() + "," + color.getGreen() + "," + color.getBlue()));

            fireworkSection.set("star", firework.isStar());
            fireworkSection.set("flicker", effect.hasFlicker());
            fireworkSection.set("trail", effect.hasTrail());
            fireworkSection.set("type", effect.getType().toString());


            fireworkSection.set("colors", stringColors);
            fireworkSection.set("fadeColors", stringFadeColors);
        }

        if (leatherArmor != null) {
            Color leatherArmorColor = leatherArmor.getColor();
            if (leatherArmorColor != null) {
                configuration.set("color", leatherArmorColor.getAlpha() + "," + leatherArmorColor.getRed() + "," + leatherArmorColor.getGreen() + "," + leatherArmorColor.getBlue());
            }
        }

        if (banner != null) {
            List<Pattern> patterns = banner.getPatterns();

            configuration.set(path + "banner", banner.getBaseColor().toString());
            if (patterns != null && !patterns.isEmpty()) {
                List<String> stringPatterns = new ArrayList<>();
                for (Pattern p : patterns) {
                    stringPatterns.add(p.getColor() + ":" + p.getPattern());
                }
                configuration.set(path + "patterns", stringPatterns);
            }
        }

        if (item.getEnchantments() != null && !item.getEnchantments().isEmpty()) {
            List<String> stringEnchantments = item.getEnchantments().entrySet().stream().map(e -> e.getKey().getName() + "," + e.getValue().toString()).collect(Collectors.toList());

            configuration.set(path + "enchants", stringEnchantments);
        }

        if (item.getFlags() != null && !item.getFlags().isEmpty()) {
            configuration.set(path + "flags", item.getFlags().stream().map(Enum::toString).collect(Collectors.toList()));
        }

        try {
            configuration.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
