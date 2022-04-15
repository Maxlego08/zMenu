package fr.maxlego08.menu.command.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.ZInventory;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.button.buttons.ZNoneButton;
import fr.maxlego08.menu.button.buttons.ZSlotButton;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenu extends VCommand {

	public CommandMenu(MenuPlugin plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("deprecation")
	@Override
	protected CommandType perform(MenuPlugin plugin) {

		List<Button> buttons = new ArrayList<Button>();
		ZSlotButton zSlotButton = new ZSlotButton(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
		zSlotButton.setItemStack(new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, (byte) 8));
		buttons.add(zSlotButton);

		ZNoneButton button = new ZNoneButton();
		button.setItemStack(new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, (byte) 14));
		button.setSlot(13);
		buttons.add(button);
		Inventory inventory = new ZInventory(plugin, "§7Test", "test", 54, buttons);

		InventoryManager inventoryManager = plugin.getInventoryManager();
		Optional<Inventory> optional = inventoryManager.getInventory("example");
		
		if (optional.isPresent()){
			
			inventory = optional.get();
			
		}
		
		plugin.getInventoryManager().openInventory(this.player, inventory);
		
		

		return CommandType.SUCCESS;
	}

}
