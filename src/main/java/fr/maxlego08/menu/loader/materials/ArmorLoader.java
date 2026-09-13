package fr.maxlego08.menu.loader.materials;

import fr.maxlego08.menu.api.annotations.AutoMaterialLoader;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Locale;

@AutoMaterialLoader
public class ArmorLoader extends MaterialLoader {

    public ArmorLoader() {
        super("armor");
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlConfiguration configuration, @NonNull String path, @NonNull String materialString) {
        EquipmentSlot equipmentSlot;
        try {
            equipmentSlot = EquipmentSlot.valueOf(materialString.toUpperCase(Locale.ROOT).trim());
        } catch (IllegalArgumentException exception) {
            Logger.info("Equipment slot " + materialString + " is not valid at " + path + ", expected one of " + Arrays.toString(EquipmentSlot.values()), Logger.LogType.ERROR);
            return new ItemStack(Material.AIR);
        }

        EntityEquipment equipment = player.getEquipment();
        return equipment.getItem(equipmentSlot);
    }
}
