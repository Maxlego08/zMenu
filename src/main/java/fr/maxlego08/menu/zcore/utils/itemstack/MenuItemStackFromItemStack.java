package fr.maxlego08.menu.zcore.utils.itemstack;

import fr.maxlego08.menu.ZMenuItemStack;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.itemstack.Firework;
import fr.maxlego08.menu.api.itemstack.Potion;
import fr.maxlego08.menu.zcore.utils.nms.ItemStackUtils;
import fr.maxlego08.menu.zcore.utils.nms.NmsVersion;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuItemStackFromItemStack {

    public static ZMenuItemStack fromItemStack(InventoryManager manager, ItemStack itemStack) {

        ZMenuItemStack menuItemStack = new ZMenuItemStack(manager, "", "");

        menuItemStack.setMaterial(itemStack.getType().name());
        int amount = itemStack.getAmount();
        if (amount > 1) menuItemStack.setAmount(String.valueOf(itemStack.getAmount()));
        if (NmsVersion.getCurrentVersion().isItemLegacy()) {
            int durability = itemStack.getDurability();
            if (durability > 0) menuItemStack.setDurability(durability);
            int data = itemStack.getData().getData();
            if (data > 0) menuItemStack.setData(String.valueOf(data));
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

            if(!itemMeta.getPersistentDataContainer().getKeys().isEmpty()){
                String base64 = ItemStackUtils.serializeItemStack(itemStack);
                menuItemStack.setMaterial("base64:" + base64);
            }
        }

        return menuItemStack;
    }

}
