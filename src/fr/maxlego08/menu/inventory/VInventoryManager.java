package fr.maxlego08.menu.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.exceptions.InventoryAlreadyExistException;
import fr.maxlego08.menu.exceptions.InventoryOpenException;
import fr.maxlego08.menu.listener.ListenerAdapter;
import fr.maxlego08.menu.zcore.enums.EnumInventory;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.inventory.InventoryResult;
import fr.maxlego08.menu.zcore.utils.inventory.ItemButton;

public class VInventoryManager extends ListenerAdapter {

	private final Map<Integer, VInventory> inventories = new HashMap<>();
	private final Map<UUID, VInventory> playerInventories = new HashMap<>();
	private final MenuPlugin plugin;

	/**
	 * @param plugin
	 */
	public VInventoryManager(MenuPlugin plugin) {
		super();
		this.plugin = plugin;
	}

	/**
	 * Allows you to record an inventory If the inventory ID already exists then
	 * an exception will be throw
	 * 
	 * @param enumInventory
	 * @param inventory
	 */
	public void registerInventory(EnumInventory enumInventory, VInventory inventory) {
		if (!this.inventories.containsKey(enumInventory.getId())) {
			this.inventories.put(enumInventory.getId(), inventory);
		} else {
			throw new InventoryAlreadyExistException("Inventory with id " + inventory.getId() + " already exist !");
		}
	}

	/**
	 * Allows you to open an inventory
	 * 
	 * @param enumInventory
	 *            - Inventory enum for get the ID
	 * @param player
	 *            - Player that will open the inventory
	 * @param page
	 *            - The inventory page
	 * @param objects
	 *            - The arguments used to make the inventory work
	 */
	public void createInventory(EnumInventory enumInventory, Player player, int page, Object... objects) {
		this.createInventory(enumInventory.getId(), player, page, objects);
	}

	/**
	 * Allows you to open an inventory When opening the inventory will be cloned
	 * 
	 * @param id
	 *            - Inventory ID
	 * @param player
	 *            - Player that will open the inventory
	 * @param page
	 *            - The inventory page
	 * @param objects
	 *            - The arguments used to make the inventory work
	 */
	public void createInventory(int id, Player player, int page, Object... objects) {
		Optional<VInventory> optional = this.getInventory(id);

		if (!optional.isPresent()) {
			message(player, Message.VINVENTORY_CLONE_NULL, "%id%", id);
			return;
		}

		VInventory inventory = optional.get();

		// We need to clone the object to have one object per open inventory
		// An inventory will remain open for several seconds, during this time
		// the inventories of the invary must be correctly saved according to
		// the player.
		VInventory clonedInventory = inventory.clone();

		if (clonedInventory == null) {
			message(player, Message.VINVENTORY_CLONE_NULL, "%id%", id);
			return;
		}

		clonedInventory.setId(id);
		try {
			InventoryResult result = clonedInventory.preOpenInventory(this.plugin, player, page, objects);
			if (result.equals(InventoryResult.SUCCESS)) {
				player.openInventory(clonedInventory.getSpigotInventory());
				this.playerInventories.put(player.getUniqueId(), clonedInventory);
			} else if (result.equals(InventoryResult.ERROR)) {
				message(player, Message.VINVENTORY_OPEN_ERROR, "%id%", id);
			}
		} catch (InventoryOpenException e) {
			message(player, Message.VINVENTORY_OPEN_ERROR, "%id%", id);
			e.printStackTrace();
		}
	}

	public void createInventory(VInventory parent, Player player) {
		createInventory(parent.getId(), player, parent.getPage(), parent.getArgs());
	}

	@Override
	protected void onInventoryClick(InventoryClickEvent event, Player player) {
		if (event.getClickedInventory() == null)
			return;
		if (event.getWhoClicked() instanceof Player) {
			if (!exist(player))
				return;
			VInventory gui = playerInventories.get(player.getUniqueId());
			if (gui.getGuiName() == null || gui.getGuiName().length() == 0) {
				Logger.info("An error has occurred with the menu ! " + gui.getClass().getName());
				return;
			}
			if (event.getView() != null && gui.getPlayer().equals(player)
					&& event.getView().getTitle().equals(gui.getGuiName())) {

				event.setCancelled(gui.isDisableClick());

				if (event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
					return;
				}

				ItemButton button = gui.getItems().getOrDefault(event.getSlot(), null);
				if (button != null)
					button.onClick(event);
			}
		}
	}

	@Override
	protected void onInventoryClose(InventoryCloseEvent event, Player player) {
		if (!exist(player))
			return;
		VInventory inventory = this.playerInventories.get(player.getUniqueId());
		remove(player);
		inventory.onPreClose(event, this.plugin, player);
	}

	@Override
	protected void onInventoryDrag(InventoryDragEvent event, Player player) {
		if (event.getWhoClicked() instanceof Player) {
			if (!exist(player)) {
				return;
			}
			this.playerInventories.get(player.getUniqueId()).onDrag(event, this.plugin, player);
		}
	}

	public boolean exist(Player player) {
		return this.playerInventories.containsKey(player.getUniqueId());
	}

	/**
	 * Allows you to remove the player from the list of open inventories
	 * 
	 * @param player
	 *            - Player who will close the inventory
	 */
	public void remove(Player player) {
		if (this.playerInventories.containsKey(player.getUniqueId())) {
			this.playerInventories.remove(player.getUniqueId());
		}
	}

	/**
	 * @param id
	 *            - Inventory Id
	 * @return Optional - Allows to return the inventory in an optional
	 */
	private Optional<VInventory> getInventory(int id) {
		return Optional.ofNullable(this.inventories.getOrDefault(id, null));
	}

	/**
	 * @param ids
	 */
	public void updateAllPlayer(int... ids) {
		for (int currentId : ids) {
			updateAllPlayer(currentId);
		}
	}

	/**
	 * @param id
	 */
	public void closeAllPlayer(int... ids) {
		for (int currentId : ids) {
			closeAllPlayer(currentId);
		}
	}

	/**
	 * @param id
	 */
	private void updateAllPlayer(int id) {
		Iterator<VInventory> iterator = this.playerInventories.values().stream().filter(inv -> inv.getId() == id)
				.collect(Collectors.toList()).iterator();
		while (iterator.hasNext()) {
			VInventory inventory = iterator.next();
			Bukkit.getScheduler().runTask(this.plugin, () -> createInventory(inventory, inventory.getPlayer()));
		}
	}

	/**
	 * @param id
	 */
	private void closeAllPlayer(int id) {
		Iterator<VInventory> iterator = this.playerInventories.values().stream().filter(inv -> inv.getId() == id)
				.collect(Collectors.toList()).iterator();
		while (iterator.hasNext()) {
			VInventory inventory = iterator.next();
			inventory.getPlayer().closeInventory();
		}
	}

	public void close() {
		this.close(v -> !v.isClose());
	}

	public void close(Predicate<VInventory> predicate) {
		new ArrayList<>(this.playerInventories.values()).stream().filter(predicate).forEach(vInventory -> {
			Player player = vInventory.getPlayer();
			if (player.isOnline()) {
				player.closeInventory();
			}
		});
	}

}
