package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.exceptions.ItemEnchantException;
import fr.maxlego08.menu.zcore.utils.*;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
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
import org.bukkit.potion.PotionType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        MenuItemStack menuItemStack = new MenuItemStack(this.manager);
        menuItemStack.setData(configuration.getInt(path + "data", 0));
        menuItemStack.setDurability(configuration.getInt(path + "durability", 0));
        menuItemStack.setAmount(configuration.getString(path + "amount", "1"));
        menuItemStack.setMaterial(configuration.getString(path + "material", null));
        menuItemStack.setUrl(configuration.getString(path + "url", null));

        Color potionColor = getColor(configuration, path + "color", null);
        Material material = Material.valueOf(configuration.getString(path + "type", null));
        String materialName = material.toString();

        if (configuration.contains(path + "potion")) {
            PotionType type = PotionType.valueOf(configuration.getString(path + "potion", "REGEN").toUpperCase());
            int level = configuration.getInt(path + "level", 1);
            boolean splash = configuration.getBoolean(path + "splash", false);
            boolean extended = configuration.getBoolean(path + "extended", false);

            Potion potion = new Potion(type, level, splash, extended);
            potion.setColor(potionColor);
            menuItemStack.setPotion(potion);
        }

        if (materialName.startsWith("LEATHER_")) {
            Color armorColor = getColor(configuration, path + "color", Color.fromRGB(160, 101, 64));
            String type = materialName.replace("LEATHER_", "");
            menuItemStack.setLeatherArmor(new LeatherArmor(LeatherArmor.ArmorType.valueOf(type), armorColor));
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
                builder.withFade(getColors(section, "fakeColors"));
                menuItemStack.setFirework(new Firework(isStar, builder.build()));
            }

        }

        menuItemStack.setLore(configuration.getStringList(path + "lore"));
        menuItemStack.setDisplayName(configuration.getString(path + "name", null));
        menuItemStack.setGlowing(configuration.getBoolean(path + "glow"));
        menuItemStack.setModelID(configuration.getInt(path + "modelID", 0));

        List<String> enchants = configuration.getStringList(path + "enchants");
        Map<Enchantment, Integer> enchantments = new HashMap<>();

        for (String enchantString : enchants) {

            try {

                String[] splitEnchant = enchantString.split(",");

                if (splitEnchant.length == 1)
                    throw new ItemEnchantException("an error occurred while loading the enchantment " + enchantString);

                int level;
                String enchant = splitEnchant[0];
                try {
                    level = Integer.parseInt(splitEnchant[1]);
                } catch (NumberFormatException e) {
                    throw new ItemEnchantException("an error occurred while loading the enchantment " + enchantString);
                }

                Enchantment enchantment = Enchantment.getByName(enchant);
                if (enchantment == null) {
                    throw new ItemEnchantException("an error occurred while loading the enchantment " + enchantString);
                }

                enchantments.put(enchantment, level);

            } catch (ItemEnchantException e) {
                e.printStackTrace();
            }
        }

        List<ItemFlag> flags = configuration.getStringList(path + "flags").stream().map(this::getFlag)
                .collect(Collectors.toList());

        menuItemStack.setEnchantments(enchantments);
        menuItemStack.setFlags(flags);

        return menuItemStack;
    }

    private Color getColor(YamlConfiguration configuration, String key, Color def) {
        String[] split = configuration.getString(key,"").split(",");

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

    }

}
