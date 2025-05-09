package fr.maxlego08.menu.loader.materials;

import com.hibiscusmc.hmccosmetics.api.HMCCosmeticsAPI;
import com.hibiscusmc.hmccosmetics.cosmetic.CosmeticSlot;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HmccosmeticsLoader implements MaterialLoader {
    @Override
    public String getKey() {
        return "hmc_cosmetics";
    }

    @Override
    public ItemStack load(Player player, YamlConfiguration configuration, String path, String materialString) {
        Player target;
        CosmeticSlot cosmeticSlot;
        if (materialString.contains("-")){
            String[] split = materialString.split("-");
            if (split.length != 2){
                throw new IllegalArgumentException("Invalid material string: " + materialString);
            }
            target = Bukkit.getPlayer(split[0].replace("%player%", player.getName()));
            try {
                cosmeticSlot = CosmeticSlot.valueOf(split[1]);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid cosmetic slot: " + split[1]);
            }
        } else {
            target = player;
            try {
                cosmeticSlot = CosmeticSlot.valueOf(materialString);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid cosmetic slot: " + materialString);
            }
        }
        return HMCCosmeticsAPI.getUser(target.getUniqueId()).getCosmetic(cosmeticSlot).getItem();
    }

}
