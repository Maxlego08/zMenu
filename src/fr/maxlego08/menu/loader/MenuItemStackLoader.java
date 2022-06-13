package fr.maxlego08.menu.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionType;

import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.exceptions.ItemEnchantException;
import fr.maxlego08.menu.zcore.utils.Potion;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.loader.Loader;

@SuppressWarnings("deprecation")
public class MenuItemStackLoader extends ZUtils implements Loader<MenuItemStack> {

	private final InventoryManager manager;

	/**
	 * @param manager
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

		if (configuration.contains(path + "potion")) {

			PotionType type = PotionType.valueOf(configuration.getString(path + "potion", "REGEN").toUpperCase());
			int level = configuration.getInt(path + "level", 1);
			boolean splash = configuration.getBoolean(path + "splash", false);
			boolean extended = configuration.getBoolean(path + "extended", false);

			Potion potion = new Potion(type, level, splash, extended);
			menuItemStack.setPotion(potion);

		}

		menuItemStack.setLore(configuration.getStringList(path + "lore"));
		menuItemStack.setDisplayName(configuration.getString(path + "name", null));
		menuItemStack.setGlowing(configuration.getBoolean(path + "glow"));
		menuItemStack.setModelID(configuration.getInt(path + "modelID", 0));

		List<String> enchants = configuration.getStringList(path + "enchants");
		Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();

		for (String enchantString : enchants) {

			try {

				String[] splitEnchant = enchantString.split(",");

				if (splitEnchant.length == 1)
					throw new ItemEnchantException("an error occurred while loading the enchantment " + enchantString);

				int level = 0;
				String enchant = splitEnchant[0];
				try {
					level = Integer.valueOf(splitEnchant[1]);
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

	/**
	 * 
	 */
	public void save(MenuItemStack item, YamlConfiguration configuration, String path, Object... objects) {

	}

}
