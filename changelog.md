# ToDo

- [ ] Improve the firework system to put more option. Being able to put several effects, power etc.
- [ ] Add a system that allows you to cache an ItemStack, so instead of returning the ItemStack at each opening, there can be several minutes without having to recreate a new ItemStack. For decorations, buttons without placeholders for example.
- [ ] Add an order to display the list of official addons.
- [ ] Add a ``/zm help`` command to send to documentation.
- [ ] Add a ``/zm command <command> <inventory>`` command to create an order file for an inventory. 
- [ ] Add a system to translate plugin messages.
- [ ] Update developer doc
- [ ] Add a chance system for actions
- [ ] Add has experience / level for requirement
- [ ] Add is near for requirement
- [ ] Add delay support for zMenuConvert
- [ ] Add anti dupe system with NMS Tag 
- [ ] Add boolean for enable or disable inventory to load

# Unreleased

- Fix model id value in config, you can now you modelId, customModelId or customModelData

# 1.0.1.6

- Start of the translation of the plugin documentation in French. If you want to translate zMenu documentation into your language, please contact me.
- You can use multiple placeholders now, do like this:
```yml
placeholders:
  - placeholder: <your placeholder>
    value: <your value>
    action: <your action>
  - placeholder: <your placeholder>
    value: <your value>
    action: <your action>
``` 
Be careful, you must put an ``s`` to **placeholder**, otherwise it will not work.
- You can use now the text ``placeholder`` instanceof ``placeHolder``
- Start new way to configure the plugin, you have now ``requirement`` and ``actions``:
A requirement consists of a permission list to check (placeholder, permission, regex and item), an action list on success and an action list on deny.
You have 11 actions available:
1. broadcast 
2. broadcast_sound
3. close
4. connect
5. console_command
6. data
7. inventory
8. message
9. chat
10. player_command
11. sound

Example for ``view_requirement``:
```yml
view_requirement:
  deny:
    - type: chat
      messages:
        - "msg Maxlego08 test"
  success:
    - type: sound
      sound: ENTITY_PLAYER_LEVELUP
  requirements:
    - type: permission
      permission: "admin.use"
    - type: permission
      permission: "use.pro.config"
    - type: placeholder
      placeholder: "%player_gamemode%"
      value: "CREATIVE"
      action: equals_string
    - type: placeholder
      placeholder: "%player_is_flying%"
      value: "yes"
      action: equals_string
```
Example for ``open_requirement``:
```yml
open_requirement:
  requirements:
    - type: regex
      input: "%player_item_in_hand%"
      regex: "(NETHERITE_|DIAMOND_|IRON_|GOLDEN_|STONE_|WOODEN_|LEATHER_|BOW|CROSSBOW|FISHING_ROD|SHEARS|SHIELD|TRIDENT|TURTLE_HELMET|ELYTRA|FLINT_AND_STEEL)"
  deny:
    - type: message
      messages:
        - "&cYou doesn't have an item in your hand."
```
Example for ``click_requirement``:
```yml
click_requirement:
  left_click:
    clicks:
      - LEFT
      - SHIFT_LEFT
    requirements:
      - type: placeholder
        placeholder: "%player_gamemode%"
        value: "CREATIVE"
        action: equals_string
    deny:
      - type: inventory
        inventory: "example"
        plugin: "zMenu"
    success:
      - type: message
        messages:
          - "well <red>done <green>you did it"
```
More information on the plugin documentation: https://docs.zmenu.dev/
- Add alias for placeholder action:
1. BOOLEAN: `b=`
2. EQUALS_STRING: `s=`
3. EQUALSIGNORECASE_STRING: `s==`
4. CONTAINS_STRING: `sc`
5. EQUAL_TO: `==`
6. SUPERIOR: `>`
7. SUPERIOR_OR_EQUAL: `>=`
8. LOWER: `<`
9. LOWER_OR_EQUAL: <`=`
- Enable version checker
- Improve javadocs
- Remove example.yml, example2.yml and example3.yml in inventories folder
- Remove example.yml in commands folder
- Rename pattern1.yml to pattern_example.yml
- Edit commands.yml file with new default config
- Add pro_inventory.yml in inventory folder
- Fix component for inventory name
- Add cache system for ComponentMeta (optimizes code and makes it more efficient)

# 1.0.1.5

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