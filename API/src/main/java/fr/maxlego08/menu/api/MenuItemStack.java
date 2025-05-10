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

    String getTargetPlayer();

    String getAmount();

    String getUrl();

    String getData();

    int getDurability();

    Potion getPotion();

    List<String> getLore();

    List<ItemFlag> getFlags();

    String getDisplayName();

    boolean isGlowing();

    String getModelID();

    Map<Enchantment, Integer> getEnchantments();

    List<IAttribute> getAttributes();

    Banner getBanner();

    Firework getFirework();

    LeatherArmor getLeatherArmor();

    Map<String, String> getTranslatedDisplayName();

    Map<String, List<String>> getTranslatedLore();

    boolean isNeedPlaceholderAPI();

    ItemStack getCacheItemStack();

    int getMaxStackSize();

    int getMaxDamage();

    int getDamage();

    int getRepairCost();

    boolean isUnbreakableEnabled();

    boolean isUnbreakableShowInTooltip();

    boolean isFireResistant();

    boolean isHideTooltip();

    boolean isHideAdditionalTooltip();

    boolean isEnchantmentGlint();

    boolean isEnchantmentShowInTooltip();

    boolean isAttributeShowInTooltip();

    TrimConfiguration getTrimConfiguration();

    MenuItemRarity getItemRarity();

    boolean isCenterLore();

    boolean isCenterName();

    LoreType getLoreType();
}
