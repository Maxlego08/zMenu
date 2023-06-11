package fr.maxlego08.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.maxlego08.menu.zcore.utils.meta.Meta;
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

		// If the material is null, then by default it will be stone, stone is a
		// material present in all versions, so no conflict problem.
		if (this.material == null) {
			this.material = "STONE";
		}

		String papiMaterial = papi(this.material, player);
		int amount = 1;
		try {
			amount = Integer.parseInt(papi(this.amount, player));
		} catch (Exception ignored) {
		}

		try {
			material = getMaterial(Integer.parseInt(papiMaterial));
		} catch (Exception ignored) {
		}

		if (material == null && papiMaterial != null) {
			try {
				material = Material.getMaterial(papiMaterial.toUpperCase());
			} catch (Exception ignored) {
			}
		}

		if (material == null || material.equals(Material.AIR)) {

			if (papiMaterial.contains(":")) {

				String[] values = papiMaterial.split(":");

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

		if (itemStack == null) {
			itemStack = new ItemStack(material, amount, (byte) this.data);
		}

		if (this.url != null) {
			itemStack = this.createSkull(this.url);
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
			Meta.meta.updateDisplayName(itemMeta, this.displayName, player);
		}

		Meta.meta.updateLore(itemMeta, this.lore, player);

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

		this.flags.forEach(itemMeta::addItemFlags);

		itemStack.setItemMeta(itemMeta);

		return itemStack;
	}

	/**
	 * @return the material
	 */
	public String getMaterial() {
		return material;
	}

	/**
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the data
	 */
	public int getData() {
		return data;
	}

	/**
	 * @return the durability
	 */
	public int getDurability() {
		return durability;
	}

	/**
	 * @return the potion
	 */
	public Potion getPotion() {
		return potion;
	}

	/**
	 * @return the lore
	 */
	public List<String> getLore() {
		return lore;
	}

	/**
	 * @return the flags
	 */
	public List<ItemFlag> getFlags() {
		return flags;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @return the isGlowing
	 */
	public boolean isGlowing() {
		return isGlowing;
	}

	/**
	 * @return the modelID
	 */
	public int getModelID() {
		return modelID;
	}

	/**
	 * @return the enchantments
	 */
	public Map<Enchantment, Integer> getEnchantments() {
		return enchantments;
	}

	/**
	 * @param material
	 *            the material to set
	 */
	public void setMaterial(String material) {
		this.material = material;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(int data) {
		this.data = data;
	}

	/**
	 * @param durability
	 *            the durability to set
	 */
	public void setDurability(int durability) {
		this.durability = durability;
	}

	/**
	 * @param potion
	 *            the potion to set
	 */
	public void setPotion(Potion potion) {
		this.potion = potion;
	}

	/**
	 * @param lore
	 *            the lore to set
	 */
	public void setLore(List<String> lore) {
		this.lore = lore;
	}

	/**
	 * @param flags
	 *            the flags to set
	 */
	public void setFlags(List<ItemFlag> flags) {
		this.flags = flags;
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @param isGlowing
	 *            the isGlowing to set
	 */
	public void setGlowing(boolean isGlowing) {
		this.isGlowing = isGlowing;
	}

	/**
	 * @param modelID
	 *            the modelID to set
	 */
	public void setModelID(int modelID) {
		this.modelID = modelID;
	}

	/**
	 * @param enchantments
	 *            the enchantments to set
	 */
	public void setEnchantments(Map<Enchantment, Integer> enchantments) {
		this.enchantments = enchantments;
	}

}
