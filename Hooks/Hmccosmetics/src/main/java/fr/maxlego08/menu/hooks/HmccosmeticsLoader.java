package fr.maxlego08.menu.hooks;

import com.hibiscusmc.hmccosmetics.api.HMCCosmeticsAPI;
import com.hibiscusmc.hmccosmetics.cosmetic.CosmeticSlot;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class HmccosmeticsLoader extends MaterialLoader {

    public HmccosmeticsLoader() {
        super("hmc_cosmetics");
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlConfiguration configuration, @NonNull String path, @NonNull String materialString) {
        Player target;
        CosmeticSlot cosmeticSlot;
        if (materialString.contains("-")) {
            String[] split = materialString.split("-");
            if (split.length != 2) {
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
