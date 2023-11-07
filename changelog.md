# ToDo

- [ ] Improve the firework system to put more option. Being able to put several effects, power etc
- [ ] Add a system that allows you to cache an ItemStack, so instead of returning the ItemStack at each opening, there can be several minutes without having to recreate a new ItemStack. For decorations, buttons without placeholders for example.

# Unreleased

- Fix button load if type doesn't exist
- Add ``onBackClick`` in Button, is called when a player will click a ``BACK`` button just before performing his action.
- Add method ``setMaxPage`` in InventoryDefault class
- Add event cancel before use the button. It is now possible to activate the click of items in the inventory from the api

# 1.0.1.4

- Inventory can load with error in button
- Fix commands register and unregister. The commands are now saved in the plugin that will load the command.
- Fix Firework meta for firework rocket
- Improve documentation: https://github.com/Maxlego08/zMenu/issues/15

# 1.0.1.3

- Added `DefaultButtonValue` in `Button`, Change the default values of the buttons if they are not present in the configuration. To avoid, for example, having to redefine the slot for elses buttons or not having to set isPermanent: true for NEXT or PREVIOUS type buttons
- Added `advanced_inventory.yml` in default config file
- Fix `/zm reload` with patterns
- Previous and Next button are now permanent button by default
- Else button doesnt need to set slot and page
- Fix error with 1.20.2 (PlayerSkin#getFromPlayer)

# 1.0.1.2

- Add `/zm list` command
- Add `/zm create <file name> <inventory size> <inventory name>` command
- Add action, closeInventory
- Add new default inventory, basic_inventory. An inventory with lots of explanation for beginners.
- Fix ``ComponentMeta`` with old color tag
- Disable double click action

# 1.0.1.1

- Add ``playerCommands`` as an aliases for ``commands``
- Add ``FastEvent`` interface, allows you to listen to plugin events without going through the Bukkit API, performance gain
- Add ``InventoryLoadEvent`` event, Event called when an inventory is loaded
- Add ``ButtonLoadEvent`` event, Event called when a button is loaded
- Add ``PlayerOpenInventoryEvent`` event, Event called when a player opens an inventory
- Removed the requirement to have another button for the NEXT and PREVIOUS button
- Removed registerButtonListener
- Fix /zm open argument, you can now set argument like that: `/zm open zmenu:example_punish Maxlego08 false target:Maxlego09 reason:"this is a really long reason"`
- Fix Leather color material case
- Fix GameProfile with null name
- Change ButtonLoadEvent to ButtonLoaderRegisterEvent 

# 1.0.1.0

- Allow inventories without items
- Add %player% with papi utils
- Add getter for MetaUpdater in InventoryManager
- Add method getCurrentPlayerInventory in InventoryManager
- Add method buildDisplayName in Button
- Add method buildLore in Button
- Add updateOnClick option, update button if player click on another button
- Add button listener method in InventoryManager 
- Add check for new messages value saving
- Fix placeholder verification
- Fix patterns interactions
- Fix itemstack amount with custom material
- Fix item lore with lore is empty
- Fix message translation for hex color
- Fix Component meta regex 

# 1.0.0.9

- Add %player% variable for messages on button
- Add PlaceholderAPI support for custom model data (modelID) 
- Add argument for command /zm open

# 1.0.0.8

- Change super.onClick on each buttons
- Add inventory name for commands arguments (https://github.com/Maxlego08/zMenu/issues/12)