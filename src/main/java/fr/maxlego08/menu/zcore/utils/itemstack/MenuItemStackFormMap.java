package fr.maxlego08.menu.zcore.utils.itemstack;

import fr.maxlego08.menu.ZMenuItemStack;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.attribute.AttributeMergeStrategy;
import fr.maxlego08.menu.api.attribute.AttributeWrapper;
import fr.maxlego08.menu.api.enchantment.Enchantments;
import fr.maxlego08.menu.api.enchantment.MenuEnchantment;
import fr.maxlego08.menu.api.enums.MenuItemRarity;
import fr.maxlego08.menu.api.itemstack.TrimConfiguration;
import fr.maxlego08.menu.api.utils.TrimHelper;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.api.exceptions.ItemEnchantException;
import fr.maxlego08.menu.zcore.utils.nms.NmsVersion;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MenuItemStackFormMap {

    public static ZMenuItemStack fromMap(InventoryManager inventoryManager, File file, String path, Map<String, Object> map) {
        TypedMapAccessor accessor = new TypedMapAccessor(map);
        ZMenuItemStack menuItemStack = new ZMenuItemStack(inventoryManager, file.getPath(), path);

        menuItemStack.setData(accessor.getString("data", "0"));
        menuItemStack.setDurability(accessor.getInt("durability", 0));
        menuItemStack.setAmount(accessor.getString("amount", "1"));
        menuItemStack.setMaterial(accessor.getString("material", null));
        menuItemStack.setTargetPlayer(accessor.getString("target", null));
        menuItemStack.setUrl(accessor.getString("url", null));

        List<String> lore = accessor.getStringList("lore");
        if (lore.isEmpty()) {
            Object object = accessor.getObject("lore", null);
            if (object instanceof String loreString) {
                lore = Arrays.asList(loreString.split("\n"));
            }
        }
        menuItemStack.setLore(lore);
        menuItemStack.setDisplayName(accessor.getString("name", accessor.getString("display_name", null)));
        menuItemStack.setGlowing(accessor.getBoolean("glow"));
        menuItemStack.setModelID(accessor.getString("modelID", accessor.getString("model-id", accessor.getString("modelId", accessor.getString("customModelId", accessor.getString("customModelData", accessor.getString("model_data", "0")))))));

        Enchantments helperEnchantments = inventoryManager.getEnchantments();
        List<String> enchants = accessor.getStringList("enchants");
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
                if (optional.isEmpty()) {
                    throw new ItemEnchantException("an error occurred while loading the enchantment " + enchantString + " for file " + file.getAbsolutePath() + " with path " + path);
                }

                enchantments.put(optional.get().enchantment(), level);

            } catch (ItemEnchantException e) {
                e.printStackTrace();
            }
        }

        List<String> flagStrings = accessor.getStringList("flags");
        List<ItemFlag> flags = new ArrayList<>(flagStrings.size());
        for (String flagName : flagStrings) {
            flags.add(menuItemStack.getFlag(flagName));
        }

        List<AttributeWrapper> attributeModifiers = new ArrayList<>();

        if (accessor.contains("attributes")) {
            List<Map<String, Object>> attributesFromConfig = (List<Map<String, Object>>) accessor.getList("attributes");
            if (attributesFromConfig != null) {
                for (Map<String, Object> attributeMap : attributesFromConfig) {
                    attributeModifiers.add(AttributeWrapper.deserialize(attributeMap));
                }
            }
        }

        menuItemStack.setEnchantments(enchantments);
        menuItemStack.setFlags(flags);
        menuItemStack.setAttributeMergeStrategy(AttributeMergeStrategy.valueOf(accessor.getString("attribute-merge-strategy", "REPLACE").toUpperCase()));
        menuItemStack.setAttributes(attributeModifiers);

        if (NmsVersion.getCurrentVersion().isNewItemStackAPI()) {
            loadNewItemStacks(menuItemStack, accessor, path, file);
        }
        if (NmsVersion.getCurrentVersion().isNewHeadApi()){
            loadTrims(menuItemStack, accessor, path, file);
        }

        return menuItemStack;
    }

    private static void loadNewItemStacks(ZMenuItemStack menuItemStack, TypedMapAccessor accessor, String path, File file) {
        menuItemStack.setMaxStackSize(accessor.getInt("max-stack-size", 0));
        menuItemStack.setMaxDamage(accessor.getInt("max-damage", 0));
        menuItemStack.setDamage(accessor.getInt("damage", 0));
        menuItemStack.setRepairCost(accessor.getInt("repair-cost", 0));
        menuItemStack.setUnbreakableEnabled(getOrNull(accessor.getObject("unbreakable", null)));
        menuItemStack.setUnbreakableShowInTooltip(getOrNull(accessor.getObject("unbreakable-show-in-tooltip", null)));
        menuItemStack.setFireResistant(getOrNull(accessor.getObject("fire-resistant", null)));
        menuItemStack.setHideTooltip(getOrNull(accessor.getObject("hide-tooltip", null)));
        menuItemStack.setHideAdditionalTooltip(getOrNull(accessor.getObject("hide-additional-tooltip", null)));
        menuItemStack.setEnchantmentGlint(getOrNull(accessor.getObject("enchantment-glint", null)));
        menuItemStack.setEnchantmentShowInTooltip(getOrNull(accessor.getObject("enchantment-show-in-tooltip", null)));
        menuItemStack.setAttributeShowInTooltip(getOrNull(accessor.getObject("attribute-show-in-tooltip", null)));

        String rarityString = accessor.getString("item-rarity", null);
        if (rarityString != null) {
            menuItemStack.setItemRarity(MenuItemRarity.valueOf(rarityString.toUpperCase()));
        }

    }
    private static void loadTrims(ZMenuItemStack menuItemStack, TypedMapAccessor accessor, String path, File file) {
        boolean enableTrim = accessor.getBoolean("trim.enable", false);
        if (enableTrim) {
            TrimHelper trimHelper = new TrimHelper();
            TrimPattern trimPattern = trimHelper.getTrimPatterns().get(accessor.getString("trim.pattern", "").toLowerCase());
            if (trimPattern == null) {
                enableTrim = false;
                Bukkit.getLogger().severe("Trim pattern " + accessor.getString("trim.pattern", "") + " was not found for item " + file.getAbsolutePath());
            }
            TrimMaterial trimMaterial = trimHelper.getTrimMaterials().get(accessor.getString("trim.material", "").toLowerCase());
            if (trimMaterial == null) {
                enableTrim = false;
                Bukkit.getLogger().severe("Trim material " + accessor.getString("trim.material", "") + " was not found for item " + file.getAbsolutePath());
            }
            menuItemStack.setTrimConfiguration(new TrimConfiguration(enableTrim, trimMaterial, trimPattern));
        }
    }
    private static Boolean getOrNull(Object o) {
        if (o instanceof Boolean) {
            return (Boolean) o;
        }
        return null;
    }

}
