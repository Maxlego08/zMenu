package fr.maxlego08.menu.placeholder;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.dupe.DupeManager;
import fr.maxlego08.menu.api.utils.CompatibilityUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

public class ItemPlaceholders extends ZUtils {

    public void register(MenuPlugin plugin) {
        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.register("item_", (offlinePlayer, value) -> {
            if (!offlinePlayer.isOnline()) {
                return "";
            }

            Player player = offlinePlayer.getPlayer();
            if (player == null) return "";

            String[] parts = value.split("_", 2);
            if (parts.length < 2) return "invalid placeholder";

            try {
                ItemStack item = getItem(player, parts[0]);
                if (item == null) return "";

                return getProperty(plugin.getDupeManager(), item, parts[1]);
            } catch (Exception e) {
                return "exception";
            }
        });
    }

    public static ItemStack getItem(Player player, String input){
        String[] parts = input.split(":", 2);
        if (parts.length != 2) return null;

        String type = parts[0].toLowerCase();
        String value = parts[1].toLowerCase();

        switch (type) {
            case "inventory": {
                try {
                    int slot1 = Integer.parseInt(value);
                    Inventory topInv = CompatibilityUtil.getTopInventory(player);

                    if (slot1 >= 0 && slot1 < topInv.getSize()) {
                        return topInv.getItem(slot1);
                    }
                } catch (NumberFormatException ignored) {}
                break;
            }

            case "player": {
                try {
                    int slot = Integer.parseInt(value);
                    Inventory playerInv = player.getInventory();
                    if (slot >= 0 && slot < playerInv.getSize()) {
                        return playerInv.getItem(slot);
                    }
                } catch (NumberFormatException ignored) {}
                break;
            }

            case "armor": {
                PlayerInventory inv = player.getInventory();
                switch (value) {
                    case "helmet": return inv.getHelmet();
                    case "chestplate": return inv.getChestplate();
                    case "leggings": return inv.getLeggings();
                    case "boots": return inv.getBoots();
                }
                break;
            }
        }
        return null;
    }

    protected String getProperty(DupeManager dupeManager, ItemStack item, String property){
        if (item == null || item.getType() == Material.AIR) return "";

        ItemMeta meta = item.getItemMeta();
        switch (property) {
            case "is_player_item":
                return String.valueOf(!dupeManager.isDupeItem(item));
            case "is_zmenu_item":
                return String.valueOf(dupeManager.isDupeItem(item));
            // 🪨 Propriétés génériques
            case "material":
                return item.getType().name();
            case "amount":
                return String.valueOf(item.getAmount());
            case "dura":
                return String.valueOf(item.getDurability());
            case "max_dura":
                return String.valueOf(item.getType().getMaxDurability());
            case "name":
                return (meta != null && meta.hasDisplayName()) ? meta.getDisplayName() : "";
            case "lore":
                return (meta != null && meta.hasLore()) ? String.join("\n", meta.getLore()) : "";
            case "custommodeldata":
                return (meta != null && meta.hasCustomModelData()) ? String.valueOf(meta.getCustomModelData()) : "";
            case "enchantments":
                if (meta != null && meta.hasEnchants()) {
                    return meta.getEnchants().entrySet().stream()
                            .map(e -> e.getKey().getKey().getKey() + ":" + e.getValue())
                            .collect(java.util.stream.Collectors.joining(", "));
                }
                return "";
            case "flags":
                if (meta != null && !meta.getItemFlags().isEmpty()) {
                    return meta.getItemFlags().stream()
                            .map(ItemFlag::name)
                            .collect(java.util.stream.Collectors.joining(", "));
                }
                return "";
            case "unbreakable":
                return (meta != null && meta.isUnbreakable()) ? "true" : "false";

            // 🧴 Potion
            case "potion_type":
                if (meta instanceof PotionMeta potionMeta && potionMeta.getBasePotionData() != null) {
                    return potionMeta.getBasePotionData().getType().name();
                }
                return "";
            case "potion_extended":
                if (meta instanceof PotionMeta potionMeta2) {
                    return String.valueOf(potionMeta2.getBasePotionData().isExtended());
                }
                return "";
            case "potion_upgraded":
                if (meta instanceof PotionMeta potionMeta3) {
                    return String.valueOf(potionMeta3.getBasePotionData().isUpgraded());
                }
                return "";
            case "custom_potion_effects":
                if (meta instanceof PotionMeta potionMeta4 && potionMeta4.hasCustomEffects()) {
                    return potionMeta4.getCustomEffects().stream()
                            .map(e -> e.getType().getName() + ":" + e.getAmplifier() + ":" + e.getDuration())
                            .collect(java.util.stream.Collectors.joining(", "));
                }
                return "";

            // 🎨 Bannières
            case "banner_patterns":
                if (meta instanceof BannerMeta bannerMeta && !bannerMeta.getPatterns().isEmpty()) {
                    return bannerMeta.getPatterns().stream()
                            .map(p -> p.getColor().name() + ":" + p.getPattern().getIdentifier())
                            .collect(java.util.stream.Collectors.joining(", "));
                }
                return "";

            // 🎇 Fireworks
            case "firework_power":
                if (meta instanceof FireworkMeta fireworkMeta) {
                    return String.valueOf(fireworkMeta.getPower());
                }
                return "";
            case "firework_effects":
                if (meta instanceof FireworkMeta fireworkMeta2 && fireworkMeta2.hasEffects()) {
                    return fireworkMeta2.getEffects().stream()
                            .map(e -> e.getType().name())
                            .collect(java.util.stream.Collectors.joining(", "));
                }
                return "";

            // 👤 Têtes
            case "skull_owner":
                if (meta instanceof SkullMeta skullMeta && skullMeta.hasOwner()) {
                    return skullMeta.getOwningPlayer() != null ? skullMeta.getOwningPlayer().getName() : "";
                }
                return "";

            // 👔 Armures en cuir
            case "leather_color":
                if (meta instanceof LeatherArmorMeta leatherMeta) {
                    return leatherMeta.getColor().asRGB() + ""; // ou .asHex() si tu veux un hex
                }
                return "";

            // 📖 Livres
            case "book_title":
                if (meta instanceof BookMeta bookMeta && bookMeta.hasTitle()) {
                    return bookMeta.getTitle();
                }
                return "";
            case "book_author":
                if (meta instanceof BookMeta bookMeta2 && bookMeta2.hasAuthor()) {
                    return bookMeta2.getAuthor();
                }
                return "";
            case "book_pages":
                if (meta instanceof BookMeta bookMeta3 && bookMeta3.hasPages()) {
                    return String.valueOf(bookMeta3.getPageCount());
                }
                return "";

            // 🧰 Enchanted Books
            case "stored_enchantments":
                if (meta instanceof EnchantmentStorageMeta storageMeta && storageMeta.hasStoredEnchants()) {
                    return storageMeta.getStoredEnchants().entrySet().stream()
                            .map(e -> e.getKey().getKey().getKey() + ":" + e.getValue())
                            .collect(java.util.stream.Collectors.joining(", "));
                }
                return "";

            // 🎭 Trim (1.20+)
            case "armor_trim":
                if (meta instanceof ArmorMeta armorMeta && armorMeta.hasTrim()) {
                    return armorMeta.getTrim().getMaterial().getKey().getKey() + ":" +
                            armorMeta.getTrim().getPattern().getKey().getKey();
                }
                return "";

            // Si aucune propriété connue
            default:
                return "";
        }
    }
}
