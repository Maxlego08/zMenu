# ToDo

- [ ] Improve the firework system to put more buttonOption. Being able to put several effects, power etc.
- [ ] Add a system that allows you to cache an ItemStack, so instead of returning the ItemStack at each opening, there
  can be several minutes without having to recreate a new ItemStack. For decorations, buttons without placeholders for
  example.
- [ ] Add an order to display the list of official addons.
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
- [ ] When loading inventories, check the name, or the color of the item or the name of the inventory will contain a
  placeholders, so do not call placeholder API if it is not useful. The same can be done on messages, commands or any
  other action that uses placeholder API
- [ ] Add the Pagination button, it will allow to take an input number, an output number and browse the value number
- [ ] Add a way to display the list of online players in a button list
- [ ] Add Citizen and [ZNPCsPlus](https://www.spigotmc.org/resources/znpcsplus.109380/) support to open an inventory by
  clicking on an NPC
- [ ] Fix the bug about the `clearInventory: true`. It does not restore inventory when the menu is closed.
- [ ] Add the support for
  the `http://textures.minecraft.net/texture/e34969c2684e4f62d5f87875460441a9f849d296c01e4c621636bb6acda696f7` in the
  URL of a custom head.
- [ ] Add matrix support for slot (like
  this: https://abstractmenus.github.io/docs/general/item_format.html#way-4-matrix)
- [ ] Adding more logs on the errors that can occur with custom items like ItemAdder, this will cause an error but the
  user will not have the information of why, for example when the item does not exist.
- [ ] Create a new class for loading buttons to add more elements, like a boolean to check if the button needs an
  itemstack
- [ ] Can split a file into several and thus avoid having too large files
- [ ] Add slot type for create pattern (Allows to fill slot areas as do the outline of the inventory)

- [ ] Ajouter un systéme qui permet de load un inventaire uniquement quand tout les requirements sont pr�sents

# Unreleased

- Fix NMS for 1.21.10.
- Fix deluxemenus inventories loading.
- Fix minimessage regex.
- Added ``ZMenuItemsLoad`` event. Called when zMenu custom items are loaded or reloaded.
- Improve logger system.
- Improve items mechanics.
- Added ``deny-chance-actions`` for actions.
- Various minor corrections and code improvements
- Added placeholder ``%zmenu_time_unix_timestamp%``, ``%zmenu_time_next_day_unix_timestamp%``, ``%zmenu_time_today_start_unix_timestamp%``

# 1.1.0.6

- Fixed the removal of default attributes.
- Improved attribute configuration, you now have more comprehensive options. More information available [here](https://docs.zmenu.dev/configurations/items#attributes).
- Added more configuration options for patterns.
- Added Brewery support to create your items.
- Added an option to run actions as an administrator.
- Added support for Oraxen fonts.
- Fixed the loading of certain plugins.
- Added a system to apply actions to items.

# 1.1.0.5

- Improved and optimized internal code. These consist of micro-optimizations that will only be noticeable on servers with several hundred players.
- Added an option to disable toasts.
- Fixed antivirus-related flag issues.
- Fixed default attributes applied to items.
- Added support for MythicMobs.
- Added a placeholder for global placeholders.

# 1.1.0.4

- Update to Sarah 1.20. Added MARIADB support
- Fixed NONE database settings
- Improve pattern file loading

# 1.1.0.3

- Added dialogs inventories ``/zm dialog`` [#186](https://github.com/Maxlego08/zMenu/pull/186) by [1robie](https://github.com/1robie) and [saildrag](https://github.com/saildrag)
- Added ``%player%`` placeholder for each action
- Added a view requirement system with the ability to define an else for another pattern
- Added option to disable inventory loading: ``enable: false``
- Added ``reason`` for withdraw and deposit action
- Fixed withdraw and deposit action with zEssentials
- Improve update checker [#183](https://github.com/Maxlego08/zMenu/pull/183)
- Fix ``openInventoryWithOldInventories`` method
- Fixed inventory button drop on death when clear inv is active
- Fixed item giving on disconnect when clear inv is active
- Fixed textures of offline players’ heads
- Optimized Enum NmsVersion → no more repeated calls to getVersion (unnecessary since the version doesn’t change at startup)
- Fixed commands not displaying the "No permission" message during setup
- New command: ``/zm contributors``
- New command: ``/zm addons``
- New command: ``/zm dumplog``
- Update to Sarah 1.19

# 1.1.0.2

- Added [toast](https://docs.zmenu.dev/configurations/actions#toast) action
- Added more API methods
- Added equipped model, for custom armor
- Fixed error if button is not found
- Fixed luckperm action
- Fixed discord parsing
- Fixed inventory reloading issues [#173](https://github.com/Maxlego08/zMenu/pull/173)
- Updated source code to java 21 [#166](https://github.com/Maxlego08/zMenu/pull/166)
- Updated to last CurrenciesAPI version
- Some fixs [#170](https://github.com/Maxlego08/zMenu/pull/170)

# 1.1.0.1

- Added player target with inventory name [#169](https://github.com/Maxlego08/zMenu/pull/169)
- Added player data convert with ``/zm player convert``, convert old JSON data to SQL
- Update to last [CurrenciesAPI](https://github.com/GroupeZ-dev/CurrenciesAPI) version
- Use EntityScheduler instead of RegionScheduler [#168](https://github.com/Maxlego08/zMenu/pull/168)
- Fix items attributes

# 1.1.0.0

- Codebase structure overhaul: the project now uses Gradle. This major change has enabled numerous modifications and improvements to the API. The API is now cleaner, more efficient, and easier to use. **You will need to update all your plugins that use zMenu at the same time** to avoid compatibility issues.
- Upgraded to Java 21, and the minimum supported Minecraft version is now 1.19. Since very few servers using zMenu were still on older versions, we decided to drop support for legacy Minecraft versions. This simplifies the codebase and opens the door to many new enhancements.
- Added a `config.yml` file. You can now delete the old `config.json` file. New configuration options are also available.
- Integrated database support. This allows logging of opened inventories by players and enables cleaner data storage compared to JSON files.
- Added `/zm players removeall <key>` command, which removes a specific key from all players.
- Added support for mathematical expressions in placeholder requirements and `player data` actions. To enable math operations, set `math: true`. See `cookie.yml` for examples on how to use math expressions.
- Added `%zmenu_math_<expression>%` placeholder to perform math operations via a placeholder.
- Added `%zmenu_formatted_math_<expression>%` placeholder to perform math operations and return a formatted value.
- Introduced a validation system for command arguments. This lets you define the expected argument type to ensure correct values are used in your commands.
- Added `tooltip-style: <style>` option.
- Introduced a cache system for online players to improve performance by avoiding repeated lookups for offline player data.
- Added `cancel-item-pickup: <true/false>`, which prevents players from picking up items while an inventory is open.
- Added `item-model: <model>` option to customize the item’s model.
- Added support for potion arrows using `arrow: true`.
- Added `player command as op` action, which executes a command as if the player were OP. ⚠️ Be cautious: this gives the player all permissions during command execution.
- Added support for **ExecutableItems** and **ExecutableBlocks**.
- Added the **SWITCH** button. This displays a button based on the result of a placeholder, simplifying your configuration. See `switch.yml` for an example.
- Added `refresh inventory` action to reload the inventory content.
- Dates now include **month and year** in their display.
- Fixed issues with the `book` action.


# 1.0.4.1

- Added action ``discord``, Send a classic webhook discord.
- Added action ``discord component``, Send a webhook discord with the new [component](https://discord.com/developers/docs/components/reference) system [#132](https://github.com/Maxlego08/zMenu/pull/132/).
- Added requirements for broadcast action.
- Added a new way to load messages action.
- Fixed enchant loader.
- Fixed magiccosmetics loader.
- Fixed MenuItemStack when loaded by an ItemStack.
- Fixed MenuItemStack attributes.
- Change broadcast action placeholder for `%receiver%` and `%sender%`
- Disabling the `/zm download` command by default, you must enable it in the configuration if you want to use it.

# 1.0.4.0

- Fixed placeholders in actions
- Fixed commands with arguments
- Fixed minimessage [#124](https://github.com/Maxlego08/zMenu/pull/124)
- Added RoyaleEconomy support

# 1.0.3.9

- Added dependency management for inventory loading. If an inventory needs to be loaded but one of its dependencies is not yet loaded, it will be put on hold until all dependencies are loaded. This allows you to use any element from any other plugin in any zMenu inventory.
- Added teleport action.
- Added default value for commands. This allows having optional arguments with a default value.
- Added API method : ``InventoryManager#loadYamlConfiguration(File file)``, load a YAML file using the file�s placeholders `global-placeholders.yml`. This allows you to reduce the size of your configurations by avoiding the repetition of certain values.
- Added API method : ``ZPermissibleLoader#loadPermissible(ButtonManager buttonManager, TypedMapAccessor accessor, String key, String path, File file)``, Allows you to load a list of permissible. 
- Added API method : ``ButtonManager#loadPermissible(YamlConfiguration configuration, String path, File file)``, allows you to load a list of permissible list from a configuration.
- Added API method : ``ButtonManager#loadActions(YamlConfiguration configuration, String path, File file)``, allows you to load a list of action list from a configuration.
- Added ``conditional-name``, allows you to change the name of the inventory based on conditions. (available with [zMenu+](https://www.spigotmc.org/resources/zmenu-premium-zmenu-addon.115533/)).
- Added MagicCosmestics material loader.
- Added the ability to include `\n` in actions. This allows you to send a list of commands in an inventory argument and more !
- Added ``lore-type``, allows you to define how the lore should interact with the itemstack. If you are using a material loader, you can specify that the lore is added at the end of the existing lore, added before the existing lore, or replaces the existing lore.
- Added `and` and `or` permissible. Allows you to create more complex requirements. (available with [zMenu+](https://www.spigotmc.org/resources/zmenu-premium-zmenu-addon.115533/)). 
- Fixed placeholders for messages and commands.
- Fixed ConcurrentModificationException that could occur when using PlayerData [#104](https://github.com/Maxlego08/zMenu/issues/104).
- Fixed material's loaders.
- Fixed commands and messages placeholders.
- Fixed buttons with multiple slots that could not be on another page. You can now define a list of slots and a page for your buttons.
- Fixed next and back button with old inventories
- Rework `messages.yml`, you need to redo your configuration.
- You can use trims in 1.20.
- Updated `miniumRequirement` to `minimum-requirement` for requirements.
- Updated `maxPage` to `max-page` for inventory name.
- Register command permissions in Spigot. This prevents the command from appearing in the player's tab completion if they do not have permission.

# 1.0.3.8

- Added [Global Placeholders](https://docs.zmenu.dev/configurations/global-placeholders), which allows you to define
  values that will be available across all inventory configuration files.
- Fixed custom heads with URLs
- Fixed inventories that do not have a default type
- Added sound category
- Removed deprecated method ``onInventoryOpen(Player player, InventoryEngine inventory)``
- Improved code, removed the ``slot`` variable to use only the list of ``slots`` for each button
- Fixed actions withdraw, deposit and requirement money, now allowing lowercase
- Added API code for use of slots in player inventory. Coming soon in[ zMenu+](https://www.spigotmc.org/resources/zmenu-premium-zmenu-addon.115533/) !
- Updated folia API
- All configuration keys are now in kebab case. Old configurations will still work but the documentation has been
  updated to reflect the new format.
- Improved pagination performance by avoiding unnecessary list management operations

# 1.0.3.7

- Added inventory type [#97](https://github.com/Maxlego08/zMenu/pull/97)
- Added placeholder priority [#96](https://github.com/Maxlego08/zMenu/pull/96)
- Added the possibility to include a prefix for lists when using a pattern. You need to add text in front of your
  placeholder
- Modified the ``money`` permissible to support more economies
  using [CurrenciesAPI](https://github.com/Traqueur-dev/CurrenciesAPI)
- Modified the ``deposit`` and ``withdraw`` action to support more economies
  using [CurrenciesAPI](https://github.com/Traqueur-dev/CurrenciesAPI)
- Added ``deny-message`` for commands. Allows to send a custom error message if the player does not have permission.
- Fixed the max page placeholder that says 0, it will now say 1 by default

# 1.0.3.6

- Added the ability to use font ItemsAdder with format `:font-name:`
- Improve TypedMapAccessor
- Added API method ``InventoryManager#openInventoryWithOldInventories``
- Added API method ``ZButton#paginate``
- Added action ``set permission``, allows setting a permission to a player with luckperms
- Added aliases ``command`` and `commands` for console command action.
- Fixed max-stack-size for itemstack with 1.21

# 1.0.3.5

- Added support for DeluxeMenus configurations. You no longer need to convert your DeluxeMenu configurations to zMenu,
  they are now automatically compatible!
- Added command ``/zm documentation [<word>]``
- Added key `title` for inventory name, you can use `name` or `title` now
- Fixed itemstack with 1.21, components was added when it should not be added by default.
- Fixed the item backup system for potions and firework (use in `/zm save` command and in `zEssentials`)

# 1.0.3.4

- Add support for ``\n`` in item lore
- Add ``refresh_requirement``, allows refreshing the lore, name of the item or the button in its entirety according to
  several conditions.
- Add permissions on default commands
- Add PlaceholderAction: ``DIFFERENT_STRING``, check that a string is different from the value
- Add sub commands for commands. You can now have sub commands to perform actions, for example, create a `/votes party`
  command, a `/votes info` command !
- Added aliases for enchantments, this allows using the word `protection` to obtain the `PROTECTION_ENVIRONMENTAL`
  enchantment
- Deprecated method ``onInventoryOpen(Player, InventoryDefault)`` use
  now `onInventoryOpen(Player, InventoryDefault, Placeholders)`
- Fixed items flags (HIDE_ATTRIBUTES) for 1.20.4 and above
- Added [zItems](https://www.spigotmc.org/resources/zitems-demo.118638/) support, Example:
  ``material: "zitem:<item name>"``, zItems is an item that allows you to create items taking advantage of all the
  features of 1.21

### Only for version 1.21 and above

- Added ``max-stack-size: <number>``, Overrides the default maximum stack size of this item.
- Added ``max-damage: <number>``, Controls the maximum amount of damage an item can take.
- Added ``damage: <number>``, The absolute amount of damage or use this item has taken.
- Added ``repair-cost: <number>``, Number of enchantment levels to add to the base level cost when repairing, combining,
  or renaming this item with an Anvil.
- Added ``unbreakable: <true/fasle>``, Tools, armor and weapons set with this won't lose durability when used.
- Added ``unbreakable-show-in-tooltip: <true/fasle>``, If false, an 'Unbreakable' line will not be included in the
  tooltip. Default is True.
- Added ``fire-resistant: <true/fasle>``, If true, this item will not burn in fire.
- Added ``hide-tooltip: <true/fasle>``, If present, it will completely hide whole item tooltip (that includes item
  name). The tooltip will be still visible and searchable in creative mode.
- Added ``hide-additional-tooltip: <true/fasle>``, If true, disables 'additional' tooltip part which comes from the item
  type.
- Added ``enchantment-glint: <true/fasle>``, If true, the item will glint, even without enchantments; if false, the item
  will not glint, even with enchantments. If null, the override will be cleared.
- Added ``enchantment-show-in-tooltip: <true/fasle>``, If false, no enchantments will be shown in the item tooltip.
  Default is true.
- Added ``attribute-show-in-tooltip: <true/fasle>``, If false. The attributes will not show on the item tooltip. Default
  is true.
- Added ``item-rarity: <COMMON/UNCOMMON,RARE,EPIC>``, Determines the default color of its name. This enum is ordered
  from least rare to most rare.
- Added ``trim: <trim configuration>``, Represents an armor trim that may be applied to an item.

# 1.0.3.3

- Fix nullable player in MenuItemStack
- Add ``performMainAction`` for commands, allows to activate or not the main actions of the command. This allows you to
  create commands with arguments without opening an inventory for
  example. [#89](https://github.com/Maxlego08/zMenu/issues/89)
- Add the possibility to have several languages for the lore and name of items and inventory name. Depending on the
  player�s game language
  you can set other messages and make your server international
- Remove ``eco`` from plugin.yml, this caused problems loading various plugins
- Tab completes for more of the sub commands [#75](https://github.com/Maxlego08/zMenu/issues/75)

# 1.0.3.2

- Create action ``actionbar``, allows to send a message in the action bar of the player
- Add placeholder ``%zmenu_player_is_expired_<key>%`` Check if a key has expired, if the key does not exist it will be
  considered expired
- Add ``updateMasterButton``, allows to update the button entirely during the update. You have `update` enabled
- Add requirement permissible ``money``, allows you to check if the player has enough money by using the Vault API
- Add action `withdraw` and `deposit`, allows to withdraw and add money to the player�s account using the Vault API
- Fix folia (omg folia wtf)
- Fix refresh action
- Fix placeholder API cache, its disable by default
- Add support for 1.20.6 (There has been a big change on how to encode and decode itemstacks in base64, the old values
  will no longer work)

# 1.0.3.1

- Fix folia commands and data manager [#63](https://github.com/Maxlego08/zMenu/issues/63)
- Fix actions for command with delay
- Fix MenuItemStack with AIR material
- Fix color with messages in button
- Fixed saving enchantments with /zm save
- Change Item Permission to check in all inventory instead of just the player's hand
- Create new Button for zMenu+ ``INPUT``, allows you to choose a number or a text in the chat and perform actions
- You can choose the pattern plugin for the button with ``pluginName``
- Fix MenuItemStackLoader for save model id

# 1.0.3.0

- Create new placeholder ``%zmenu_player_next_page%``, return player next page
- Create new placeholder ``%zmenu_player_previous_page%``, return player previous page
- Create new requirement: ``playername``, Check if a text is a player nickname
- Fixed the display of a button with a playerHead if the text is not a `playerHead`. This avoids server lag when opening
  inventory.
- Fixed Numbers saved in PlayerData are not Numbers after a restart. [#59](https://github.com/Maxlego08/zMenu/issues/59)
- Added Placeholder in head url [#58](https://github.com/Maxlego08/zMenu/issues/58)
- Added Action - Data placeholder [#56](https://github.com/Maxlego08/zMenu/issues/56)
- Expand player data commands with add & subtract [#46](https://github.com/Maxlego08/zMenu/issues/46)
- New features for Commands (Perform action and Auto-completion) [#26](https://github.com/Maxlego08/zMenu/issues/26)
- You can now open a book
- Create ``/zm download <link>`` command. You can download configuration files from links, discord links for example.

# 1.0.2.9

- Added support for [zHead](https://www.spigotmc.org/resources/zhead-database-plugin-for-heads.115717/). A free and open
  source head database plugin (with more than 72.000 heads)
- Create new placeholder ``%zmenu_player_page%``, return player page
- Create new placeholder ``%zmenu_player_max_page%``, return player max page
- Change NEXT and PREVIOUS button. If you click right you will go directly to the first or last page.
- Improve MiniMessage format with italic text
- Create new file: `default_values.yml`, it will contain all the default values for the placeholders of the player data.
- Create pattern for Button. You can create a pattern file for a button with placeholders. And in your inventory, just
  call the pattern with a placeholder list. The placeholders will be replaced by your values and the button will be
  loaded with the new values. Thus, you can have dozens of similar buttons while reducing your configurations by several
  hundred lines. More information [here](https://docs.zmenu.dev/configurations/patterns#button).
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

- Start of development of the [zMenu+](https://m.zmenu.dev/4) paid addon. This addon will be available with the purchase
  of [Premium](https://minecraft-inventory-builder.com/account-upgrade) upgrade.
- Huge API Change. This change introduces [zMenu+](https://m.zmenu.dev/4)
- Create ButtonOption, allows any developer to add options that will be available for all types of buttons.
- Create PaginateButton interface. This interface allows you to create buttons that need pagination. This interface will
  manage the number of pages in the inventory.
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
- Added a cache on placeholders. By default, the cache is 500ms. That corresponds to opening a menu. So if you have
  several times the same placeholder used, it will only be called once.
- Fix sound action, pitch and volume was reversed
- Fix commands async

# 1.0.2.5

- Add new lore as string support
- Fix itemstack amount of 0
- Support file with space
- Improved API, added many new methods to make API more permissive.
- Patterns can now be on multiple pages
- Add custom sound to SoundOption
- Added ability to add arguments and select page for INVENTORY
  button [#45](https://github.com/Maxlego08/zMenu/issues/45)
- Change update interval to milliseconds [#37](https://github.com/Maxlego08/zMenu/issues/37)

# 1.0.2.4

- Added replacement of , by . for placeholders requirements. If your placeholder returns a number with a comma instead
  of a period, the plugin can handle that.
- Added base64 MaterialLoader. Allows to load ItemStack with all the data it can contain.
- Added cooldown on button click. The default cooldown will be 350ms.
- Fix Folia with VersionChecker [#35](https://github.com/Maxlego08/zMenu/issues/35)
- Fix error with mini message format in 1.16
- Fix JUMP Button
- Change `/zm save <item name> <base64/yml>`, saves an element in YML or base64 format. The base64 format will save the
  itemStack with all its data.
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
- Added [ShopKeeper](https://www.spigotmc.org/threads/shopkeepers.447969/) support for
  action. [#27](https://github.com/Maxlego08/zMenu/issues/27)
- Added deny and success action for each requirement. In addition to its global actions, you can add them for each
  requirement. Perfect to set the error message for the player.
- When creating ItemStacks, the plugin will no longer use PlaceholderAPI if the item contains no placeholders.
- Added cache system for ItemStack. If ItemStack does not use placeholder API, then it will be created once and cached.
  All inventories will therefore use the same ItemStack. This allows to improve performance (from a few thousand nano
  seconds per item, so if you have a lot of items in your inventory the gain can be very important).
- Fixed command /zm open. The opening arguments were only taken into account if the command had more than 5 arguments
  instead of starting from 5.
- Fixed command /zm reload with commands.

# 1.0.2.0

- Added ANY and ALL support for click requirements [#24](https://github.com/Maxlego08/zMenu/issues/24)
- Added a check when displaying the item to prevent that if the slot is outside the slots this triggers an error but
  just a message in the console
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
- Add material loader
  for [eco](https://plugins.auxilor.io/all-plugins/the-item-lookup-system) [#19](https://github.com/Maxlego08/zMenu/pull/19)
- Set ``enableAntiDupeDiscordNotification`` to false by default
- Fix ItemStackCompound
- Fix PDC dupe, replace BOOLEAN by INT

# 1.0.1.7

- Added number of inventory loaded with command /zm reload
- Added anti dupe system with NMS Tag
- Fix model id value in config, you can now you modelId, customModelId or customModelData
- Fix player head for
  1.20+ (https://blog.jeff-media.com/creating-custom-heads-in-spigot-1-18-1/) https://github.com/Maxlego08/zMenu/issues/16
- Fix docs for potion
- Added information about potion in 1.8 - 1.12. You have to do this:

```yml
material: POTION
durability: 16454
```

# 1.0.1.6

- Start of the translation of the plugin documentation in French. If you want to translate zMenu documentation into your
  language, please contact me.
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
  A requirement consists of a permission list to check (placeholder, permission, regex and item), an action list on
  success and an action list on deny.
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
- Add event cancel before use the button. It is now possible to activate the click of items in the inventory from the
  api

# 1.0.1.4

- Inventory can load with error in button
- Fix commands register and unregister. The commands are now saved in the plugin that will load the command.
- Fix Firework meta for firework rocket
- Improve documentation: https://github.com/Maxlego08/zMenu/issues/15

# 1.0.1.3

- Added `DefaultButtonValue` in `Button`, Change the default values of the buttons if they are not present in the
  configuration. To avoid, for example, having to redefine the slot for elses buttons or not having to set isPermanent:
  true for NEXT or PREVIOUS type buttons
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
- Add ``FastEvent`` interface, allows you to listen to plugin events without going through the Bukkit API, performance
  gain
- Add ``InventoryLoadEvent`` event, Event called when an inventory is loaded
- Add ``ButtonLoadEvent`` event, Event called when a button is loaded
- Add ``PlayerOpenInventoryEvent`` event, Event called when a player opens an inventory
- Removed the requirement to have another button for the NEXT and PREVIOUS button
- Removed registerButtonListener
- Fix /zm open argument, you can now set argument like
  that: `/zm open zmenu:example_punish Maxlego08 false target:Maxlego09 reason:"this is a really long reason"`
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
