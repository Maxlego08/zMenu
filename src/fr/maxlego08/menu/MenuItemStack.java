package fr.maxlego08.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.zcore.utils.Potion;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.nms.NMSUtils;

public class MenuItemStack extends ZUtils {

	private final InventoryManager inventoryManager;

	private String material;
	private String amount;
	private String url;
	private int data;
	private int durability;
	private Potion potion;
	private List<String> lore = new ArrayList<String>();
	private List<ItemFlag> flags = new ArrayList<ItemFlag>();
	private String displayName;
	private boolean isGlowing;
	private int modelID;
	private Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();

	/**
	 * @param inventoryManager
	 */
	public MenuItemStack(InventoryManager inventoryManager) {
		super();
		this.inventoryManager = inventoryManager;
	}

	/**
	 * @return the inventoryManager
	 */
	public InventoryManager getInventoryManager() {
		return inventoryManager;
	}

	@SuppressWarnings("deprecation")
	public ItemStack build(Player player) {

		ItemStack itemStack = null;
		Material material = null;

		int amount = 1;
		try {
			amount = Integer.valueOf(papi(this.amount, player));
		} catch (Exception e) {
		}

		try {
			material = getMaterial(Integer.valueOf(this.material));
		} catch (Exception e) {
		}

		if (material == null) {
			try {
				material = Material.getMaterial(this.material.toUpperCase());
			} catch (Exception e) {
			}
		}

		if (material == null || material.equals(Material.AIR)) {

			if (this.material.contains(":")) {

				String[] values = this.material.split(":");
				if (values.length == 2) {

					String key = values[0];
					String value = values[1];

					Optional<MaterialLoader> optional = this.inventoryManager.getMaterialLoader(key);
					if (optional.isPresent()) {
						MaterialLoader loader = optional.get();
						itemStack = loader.load(null, null, value);
					}
				}
			}
		}

		itemStack = new ItemStack(material, amount, (byte) this.data);

		if (this.url != null) {
			this.createSkull(this.url);
		}

		if (this.potion != null) {
			itemStack = potion.toItemStack(amount);
		}

		if (itemStack == null) {
			return null;
		}

		if (this.durability != 0) {
			itemStack.setDurability((short) this.durability);
		}

		Material finalMaterial = itemStack.getType();
		ItemMeta itemMeta = itemStack.getItemMeta();

		if (this.displayName != null) {
			itemMeta.setDisplayName(color(this.displayName));
		}

		itemMeta.setLore(color(this.lore));

		if (this.isGlowing && NMSUtils.getNMSVersion() != 1.7) {

			itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

		}

		if (this.modelID > 0) {
			itemMeta.setCustomModelData(this.modelID);
		}

		this.enchantments.forEach((enchantment, level) -> {
			if (finalMaterial.equals(Material.ENCHANTED_BOOK)) {
				((EnchantmentStorageMeta) itemMeta).addStoredEnchant(enchantment, level, true);
			} else {
				itemMeta.addEnchant(enchantment, level, true);
			}
		});

		this.flags.forEach(flag -> itemMeta.addItemFlags(flag));

		itemStack.setItemMeta(itemMeta);

		return itemStack;
	}
}
