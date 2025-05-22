package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.attribute.IAttribute;
import fr.maxlego08.menu.api.enums.MenuItemRarity;
import fr.maxlego08.menu.api.itemstack.Banner;
import fr.maxlego08.menu.api.itemstack.Firework;
import fr.maxlego08.menu.api.itemstack.LeatherArmor;
import fr.maxlego08.menu.api.itemstack.Potion;
import fr.maxlego08.menu.api.itemstack.TrimConfiguration;
import fr.maxlego08.menu.api.utils.LoreType;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public interface MenuItemStack {

    ItemStack build(Player player);

    ItemStack build(Player player, boolean useCache);

    ItemStack build(Player player, boolean useCache, Placeholders placeholders);

    InventoryManager getInventoryManager();

    String getFilePath();

    String getPath();

    String getMaterial();

    void setMaterial(String material);

    String getTargetPlayer();

    void setTargetPlayer(String targetPlayer);

    String getAmount();

    void setAmount(String amount);

    String getUrl();

    void setUrl(String url);

    String getData();

    void setData(String data);

    int getDurability();

    void setDurability(int durability);

    Potion getPotion();

    void setPotion(Potion potion);

    List<String> getLore();

    void setLore(List<String> lore);

    List<ItemFlag> getFlags();

    void setFlags(List<ItemFlag> flags);

    String getDisplayName();

    void setDisplayName(String displayName);

    boolean isGlowing();

    void setGlowing(boolean isGlowing);

    String getModelID();

    void setModelID(String modelID);

    void setModelID(int modelID);

    Map<Enchantment, Integer> getEnchantments();

    void setEnchantments(Map<Enchantment, Integer> enchantments);

    List<IAttribute> getAttributes();

    void setAttributes(List<IAttribute> attributes);

    Banner getBanner();

    void setBanner(Banner banner);

    Firework getFirework();

    void setFirework(Firework firework);

    LeatherArmor getLeatherArmor();

    void setLeatherArmor(LeatherArmor leatherArmor);

    String getToolTipStyle();

    void setToolTipStyle(String toolTipStyle);

    Map<String, String> getTranslatedDisplayName();

    void setTranslatedDisplayName(Map<String, String> translatedDisplayName);

    Map<String, List<String>> getTranslatedLore();

    void setTranslatedLore(Map<String, List<String>> translatedLore);

    boolean isNeedPlaceholderAPI();

    void setNeedPlaceholderAPI(boolean needPlaceholderAPI);

    ItemStack getCacheItemStack();

    void setCacheItemStack(ItemStack cacheItemStack);

    int getMaxStackSize();

    void setMaxStackSize(int maxStackSize);

    int getMaxDamage();

    void setMaxDamage(int maxDamage);

    int getDamage();

    void setDamage(int damage);

    int getRepairCost();

    void setRepairCost(int repairCost);

    boolean isUnbreakableEnabled();

    void setUnbreakableEnabled(Boolean unbreakableEnabled);

    boolean isUnbreakableShowInTooltip();

    void setUnbreakableShowInTooltip(Boolean unbreakableShowInTooltip);

    boolean isFireResistant();

    void setFireResistant(Boolean fireResistant);

    boolean isHideTooltip();

    void setHideTooltip(Boolean hideTooltip);

    boolean isHideAdditionalTooltip();

    void setHideAdditionalTooltip(Boolean hideAdditionalTooltip);

    boolean isEnchantmentGlint();

    void setEnchantmentGlint(Boolean enchantmentGlint);

    boolean isEnchantmentShowInTooltip();

    void setEnchantmentShowInTooltip(Boolean enchantmentShowInTooltip);

    boolean isAttributeShowInTooltip();

    void setAttributeShowInTooltip(Boolean attributeShowInTooltip);

    TrimConfiguration getTrimConfiguration();

    void setTrimConfiguration(TrimConfiguration trimConfiguration);

    MenuItemRarity getItemRarity();

    void setItemRarity(MenuItemRarity itemRarity);

    boolean isCenterLore();

    void setCenterLore(boolean centerLore);

    boolean isCenterName();

    void setCenterName(boolean centerName);

    LoreType getLoreType();

    void setLoreType(LoreType loreType);

    int parseAmount(Player player);

    int parseAmount(Player player, Placeholders placeholders);

    int parseAmount(OfflinePlayer offlinePlayer, Placeholders placeholders);

}
