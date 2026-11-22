package fr.maxlego08.menu.loader.materials;

import fr.maxlego08.menu.api.annotations.AutoMaterialLoader;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Material;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Locale;
import org.bukkit.configuration.file.YamlConfiguration;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoMaterialLoader
public class ArmorLoader extends MaterialLoader {

    public ArmorLoader() {
        super("armor");
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlDocument configuration, @NonNull String path, @NonNull String materialString) {
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

    @Override
    @Deprecated
    public ItemStack load(Player player, YamlConfiguration configuration, String path, String materialString) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(player, configurationDocument, path, materialString);
    }
}
