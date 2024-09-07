package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.attribute.Attribute;
import fr.maxlego08.menu.api.attribute.IAttribute;
import fr.maxlego08.menu.api.enchantment.Enchantments;
import fr.maxlego08.menu.api.enchantment.MenuEnchantment;
import fr.maxlego08.menu.api.enums.MenuItemRarity;
import fr.maxlego08.menu.api.itemstack.TrimConfiguration;
import fr.maxlego08.menu.api.utils.TrimHelper;
import fr.maxlego08.menu.exceptions.ItemEnchantException;
import fr.maxlego08.menu.zcore.utils.Banner;
import fr.maxlego08.menu.zcore.utils.Firework;
import fr.maxlego08.menu.zcore.utils.LeatherArmor;
import fr.maxlego08.menu.zcore.utils.Potion;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
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
import org.bukkit.inventory.ItemFlag;
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

        MenuItemStack menuItemStack = new MenuItemStack(this.manager, file.getPath(), path);
        menuItemStack.setData(configuration.getInt(path + "data", 0));
        menuItemStack.setDurability(configuration.getInt(path + "durability", 0));
        menuItemStack.setAmount(configuration.getString(path + "amount", "1"));
        menuItemStack.setMaterial(configuration.getString(path + "material", null));
        menuItemStack.setTargetPlayer(configuration.getString(path + "target", null));
        menuItemStack.setUrl(configuration.getString(path + "url", null));

        Color potionColor = getColor(configuration, path + "color", null);

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

        if (configuration.contains(path + "potion")) {
            PotionType type = PotionType.valueOf(configuration.getString(path + "potion", "REGEN").toUpperCase());
            int level = configuration.getInt(path + "level", 1);
            boolean splash = configuration.getBoolean(path + "splash", false);
            boolean extended = configuration.getBoolean(path + "extended", false);

            Potion potion = new Potion(type, level, splash, extended);
            potion.setColor(potionColor);
            menuItemStack.setPotion(potion);
        }


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

        List<String> lore = configuration.getStringList(path + "lore");
        if (lore.isEmpty()) {
            Object object = configuration.get(path + "lore", null);
            if (object instanceof String) {
                String loreString = (String) object;
                lore = Arrays.asList(loreString.split("\n"));
            }
        }
        menuItemStack.setLore(lore);
        menuItemStack.setDisplayName(configuration.getString(path + "name", null));
        menuItemStack.setGlowing(configuration.getBoolean(path + "glow"));
        menuItemStack.setModelID(configuration.getString(path + "modelID", configuration.getString(path + "modelId", configuration.getString(path + "customModelId", configuration.getString(path + "customModelData", "0")))));

        Map<String, String> translatedDisplayName = new HashMap<>();
        Map<String, List<String>> translatedLore = new HashMap<>();

        configuration.getMapList(path + "translatedName").forEach(map -> {
            if (map.containsKey("locale") && map.containsKey("name")) {
                String locale = (String) map.get("locale");
                String name = (String) map.get("name");
                translatedDisplayName.put(locale.toLowerCase(), name);
            }
        });

        configuration.getMapList(path + "translatedLore").forEach(map -> {
            if (map.containsKey("locale") && map.containsKey("lore")) {
                String locale = (String) map.get("locale");
                List<String> name = (List<String>) map.get("lore");
                translatedLore.put(locale.toLowerCase(), name);
            }
        });

        menuItemStack.setTranslatedDisplayName(translatedDisplayName);
        menuItemStack.setTranslatedLore(translatedLore);

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

        List<ItemFlag> flags = configuration.getStringList(path + "flags").stream().map(this::getFlag).collect(Collectors.toList());

        List<IAttribute> attributeModifiers = new ArrayList<>();

        if (configuration.contains(path + "attributes")) {
            List<Map<String, Object>> attributesFromConfig = (List<Map<String, Object>>) configuration.getList(path + "attributes");
            if (attributesFromConfig != null) {
                for (Map<String, Object> attributeMap : attributesFromConfig) {
                    attributeModifiers.add(Attribute.deserialize(attributeMap));
                }
            }
        }

        menuItemStack.setEnchantments(enchantments);
        menuItemStack.setFlags(flags);
        menuItemStack.setAttributes(attributeModifiers);

        if (NmsVersion.getCurrentVersion().isNewItemStackAPI()) {
            loadNewItemStacks(menuItemStack, configuration, path, file);
        }

        return menuItemStack;
    }

    private Boolean getOrNull(Object o) {
        if (o instanceof Boolean) {
            return (Boolean) o;
        }
        return null;
    }

    private void loadNewItemStacks(MenuItemStack menuItemStack, YamlConfiguration configuration, String path, File file) {
        menuItemStack.setMaxStackSize(configuration.getInt(path + "max-stack-size", 64));
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

    private Color getColor(YamlConfiguration configuration, String key, Color def) {
        String[] split = configuration.getString(key, "").split(",");

        if (split.length == 3) {
            return Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        } else if (split.length == 4) {
            return Color.fromARGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
        }

        return def;
    }

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
     *
     */
    public void save(MenuItemStack item, YamlConfiguration configuration, String path, File file, Object... objects) {

        configuration.set(path + "material", item.getMaterial());

        if (item.getDisplayName() != null) configuration.set(path + "name", item.getDisplayName());
        if (!item.getLore().isEmpty()) configuration.set(path + "lore", item.getLore());
        if (item.isGlowing()) configuration.set(path + "glow", item.isGlowing());
        if (item.getModelID() != null && !item.getModelID().equalsIgnoreCase("0")) {
            configuration.set(path + "modelID", item.getModelID());
        }
        if (item.getData() > 0) configuration.set(path + "data", item.getData());
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
            configuration.set(path + "level", potion.getLevel());
            configuration.set(path + "splash", potion.isSplash());
            configuration.set(path + "extended", potion.hasExtendedDuration());

            if (potionColor != null) {
                configuration.set("color", potionColor.getAlpha() + "," + potionColor.getRed() + "," + potionColor.getGreen() + "," + potionColor.getBlue());
            }
        }

        if (firework != null) {
            ConfigurationSection fireworkSection = configuration.createSection(path + "firework");
            FireworkEffect effect = firework.getEffect();
            List<String> stringColors = new ArrayList<>();
            effect.getColors().forEach(c -> stringColors.add(c.getAlpha() + "," + c.getRed() + "," + c.getGreen() + "," + c.getBlue()));
            List<String> stringFadeColors = new ArrayList<>();
            effect.getColors().forEach(c -> stringFadeColors.add(c.getAlpha() + "," + c.getRed() + "," + c.getGreen() + "," + c.getBlue()));

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
