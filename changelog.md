# ToDo

- [x] Add anti dupe system with NMS Tag
- [x] Add the BACK action, to return to the previous inventory when clicking   
- [x] Add the ability to perform actions when clicking
- [ ] Improve the firework system to put more buttonOption. Being able to put several effects, power etc.
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
- [ ] Add boolean for enable or disable inventory to load
- [ ] Add nbt support
- [ ] Upgrade potion support for multiple effect, main effect and color
- [ ] Remove config.json and setup a config.yml
- [ ] Create an extension for vscode, notepadd++ to configure the plugin faster
- [ ] Upgrade `/zmenu save <name>` to add support for heads, fireworks and banner etc   
- [ ] When loading inventories, check the name, or the color of the item or the name of the inventory will contain a placeholders, so do not call placeholder API if it is not useful. The same can be done on messages, commands or any other action that uses placeholder API   
- [ ] Add the Pagination button, it will allow to take an input number, an output number and browse the value number
- [ ] Add a way to display the list of online players in a button list
- [ ] Add Citizen and [ZNPCsPlus](https://www.spigotmc.org/resources/znpcsplus.109380/) support to open an inventory by clicking on an NPC
- [x] Add a tool to transform ItemStack to base64 and vice versa. This tool can be used to all my plugins.
- [ ] Fix the bug about the `clearInventory: true`. It does not restore inventory when the menu is closed.
- [ ] Add the support for the `http://textures.minecraft.net/texture/e34969c2684e4f62d5f87875460441a9f849d296c01e4c621636bb6acda696f7` in the URL of a custom head.
- [ ] Update pom.xml for add {projet.version} in plugin.yml
- [ ] Add matrix support for slot (like this: https://abstractmenus.github.io/docs/general/item_format.html#way-4-matrix)
- [ ] Adding more logs on the errors that can occur with custom items like ItemAdder, this will cause an error but the user will not have the information of why, for example when the item does not exist.
- [ ] Create a new class for loading buttons to add more elements, like a boolean to check if the button needs an itemstack
- [ ] Can split a file into several and thus avoid having too large files
- [x] Add open link with a book
- [ ] Add slot type for create pattern (Allows to fill slot areas as do the outline of the inventory)
- [ ] Be able to set default values for player data

# Unreleased

- Create new placeholder ``%zmenu_player_next_page%``, return player next page
- Create new placeholder ``%zmenu_player_previous_page%``, return player previous page
- Create new requirement: ``playername``, Check if a text is a player nickname
- Fixed the display of a button with a playerHead if the text is not a `playerHead`. This avoids server lag when opening inventory.
- You can now open a book

# 1.0.2.9

- Added support for [zHead](https://www.spigotmc.org/resources/zhead-database-plugin-for-heads.115717/). A free and open source head database plugin (with more than 72.000 heads)
- Create new placeholder ``%zmenu_player_page%``, return player page
- Create new placeholder ``%zmenu_player_max_page%``, return player max page
- Change NEXT and PREVIOUS button. If you click right you will go directly to the first or last page.
- Improve MiniMessage format with italic text
- Create new file: `default_values.yml`, it will contain all the default values for the placeholders of the player data.
- Create pattern for Button. You can create a pattern file for a button with placeholders. And in your inventory, just call the pattern with a placeholder list. The placeholders will be replaced by your values and the button will be loaded with the new values. Thus, you can have dozens of similar buttons while reducing your configurations by several hundred lines. More information [here](https://docs.zmenu.dev/configurations/patterns#button).
- Creation of 5 configurations for zMenu:
1. PLAYTIME LEVELS - zMenu Configurations: https://minecraft-inventory-builder.com/resources/9
2. VOTE MENU - zMenu Configurations: https://minecraft-inventory-builder.com/resources/8
3. DONUTSMP HOME - zMenu Configurations: https://minecraft-inventory-builder.com/resources/7
4. zAuctionHouse - Hypixel AuctionHouse: https://minecraft-inventory-builder.com/resources/6
5. zAuctionHouse - DonutSMP AuctionHouse: https://minecraft-inventory-builder.com/resources/5

# 1.0.2.8

- Add /zm inventories - Allows access to the inventories of the online inventory editor
- Add PlayerInteractEvent in DupeListener
- Fix classic meta with PAPI color
- Fix NPE with ButtonOption

# 1.0.2.7

- Start of development of the [zMenu+](https://m.zmenu.dev/4) paid addon. This addon will be available with the purchase of [Premium](https://minecraft-inventory-builder.com/account-upgrade) upgrade.
- Huge API Change. This change introduces [zMenu+](https://m.zmenu.dev/4)
- Create ButtonOption, allows any developer to add options that will be available for all types of buttons.
- Create PaginateButton interface. This interface allows you to create buttons that need pagination. This interface will manage the number of pages in the inventory.
- Add matrix support for button slots [#39](https://github.com/Maxlego08/zMenu/issues/39)
- Add /zm editor, at the moment this command redirects to the online editor
- Add target placeholder [#48](https://github.com/Maxlego08/zMenu/issues/48)
- Fix reload command with pattern. The pattern were reload after the inventories 
- Fix error with InventoryPlayer
- Fix NmsVersion with 1.8 and 1.9
- Fix anti-dupe for old version
- Fix MenuItemStack loader with placeholder check
- Fix CONTAINS_STRING action for placeholder
- Fix NMS with 1.20.4
- Fix method ``getRealSlot`` with isPermanent

# 1.0.2.6

- Add BlockPlaceEvent for Anti Dupe
- Add placeholder %zmenu_test% for testing
- Add ``job`` requirement. Check if the player has a job with JobsReborn plugins
- Added a cache on placeholders. By default, the cache is 500ms. That corresponds to opening a menu. So if you have several times the same placeholder used, it will only be called once.
- Fix sound action, pitch and volume was reversed
- Fix commands async

# 1.0.2.5

- Add new lore as string support
- Fix itemstack amount of 0
- Support file with space
- Improved API, added many new methods to make API more permissive.
- Patterns can now be on multiple pages
- Add custom sound to SoundOption
- Added ability to add arguments and select page for INVENTORY button [#45](https://github.com/Maxlego08/zMenu/issues/45)
- Change update interval to milliseconds [#37](https://github.com/Maxlego08/zMenu/issues/37)

# 1.0.2.4

- Added replacement of , by . for  placeholders requirements. If your placeholder returns a number with a comma instead of a period, the plugin can handle that.
- Added base64 MaterialLoader. Allows to load ItemStack with all the data it can contain.
- Added cooldown on button click. The default cooldown will be 350ms.
- Fix Folia with VersionChecker [#35](https://github.com/Maxlego08/zMenu/issues/35)
- Fix error with mini message format in 1.16
- Fix JUMP Button
- Change `/zm save <item name> <base64/yml>`, saves an element in YML or base64 format. The base64 format will save the itemStack with all its data.
- Change `page` from JUMP Button to `toPage` 

# 1.0.2.3

- Fixed the pattern display, they will now appear first and let the more important buttons pass over
- Fixed MenuItemStack build method, add boolean for use cache or not
- Fixed PAPI use if player is null
- Create new button type: JUMP [#34](https://github.com/Maxlego08/zMenu/pull/34)

# 1.0.2.2

- Added open menu with item interaction [#29](https://github.com/Maxlego08/zMenu/pull/29) by EnzoShoes
- Added ``/zm giveopenitem <inventory> [<player>]``

# 1.0.2.1

- Added the ability to perform actions when clicking. For simple actions you no longer need to use a click_requirements.
- Added [ShopKeeper](https://www.spigotmc.org/threads/shopkeepers.447969/) support for action. [#27](https://github.com/Maxlego08/zMenu/issues/27) 
- Added deny and success action for each requirement. In addition to its global actions, you can add them for each requirement. Perfect to set the error message for the player.
- When creating ItemStacks, the plugin will no longer use PlaceholderAPI if the item contains no placeholders.
- Added cache system for ItemStack. If ItemStack does not use placeholder API, then it will be created once and cached. All inventories will therefore use the same ItemStack. This allows to improve performance (from a few thousand nano seconds per item, so if you have a lot of items in your inventory the gain can be very important).
- Fixed command /zm open. The opening arguments were only taken into account if the command had more than 5 arguments instead of starting from 5.
- Fixed command /zm reload with commands.

# 1.0.2.0

- Added ANY and ALL support for click requirements [#24](https://github.com/Maxlego08/zMenu/issues/24) 
- Added a check when displaying the item to prevent that if the slot is outside the slots this triggers an error but just a message in the console
- Fix error with 1.20.4
- Fix error with Meta
- Fix error with slot page and else button 
- Fix checkPermission with view requirement who made it check placeholders and permissions impossible

# 1.0.1.9

- Added back type for action requirements
- Improved ItemStackLoader. This class is used for converting inventories from GuiPlus.
- Fixed loaded messages

# 1.0.1.8

- Add command ``/zmenu save <name>``. Allows to save the item in your hand in format for plugin configuration.
- Add material loader for [eco](https://plugins.auxilor.io/all-plugins/the-item-lookup-system) [#19](https://github.com/Maxlego08/zMenu/pull/19)
- Set ``enableAntiDupeDiscordNotification`` to false by default
- Fix ItemStackCompound
- Fix PDC dupe, replace BOOLEAN by INT

# 1.0.1.7

- Added number of inventory loaded with command /zm reload
- Added anti dupe system with NMS Tag
- Fix model id value in config, you can now you modelId, customModelId or customModelData
- Fix player head for 1.20+ (https://blog.jeff-media.com/creating-custom-heads-in-spigot-1-18-1/) https://github.com/Maxlego08/zMenu/issues/16
- Fix docs for potion
- Added information about potion in 1.8 - 1.12. You have to do this:
```yml
material: POTION
durability: 16454
```

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
- Add updateOnClick buttonOption, update button if player click on another button
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
