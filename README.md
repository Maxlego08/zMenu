# zMenu

[![Modrinth](https://img.shields.io/modrinth/dt/zmenu?logo=modrinth&label=Modrinth&color=00AF5C)](https://modrinth.com/plugin/zmenu)
[![Discord](https://img.shields.io/discord/music?logo=discord&label=Discord&color=5865F2)](https://discord.gg/daTBzuk)
[![Java](https://img.shields.io/badge/Java-21+-orange?logo=openjdk)](https://www.oracle.com/java/)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.19--1.21+-green)](https://www.minecraft.net/)

zMenu is a powerful Minecraft plugin for creating custom inventory GUIs through YAML configuration files. No coding required for server admins, while developers get a complete API for integration.

![showcase](https://img.groupez.dev/zmenu/basic_inventory.gif)![punish](https://img.groupez.dev/zmenu/punishv2.gif)
![optionnal arg](https://img.groupez.dev/zmenu/ao.gif)![shop](https://img.groupez.dev/zmenu/shop.gif)
![title animation](https://img.groupez.dev/zmenu/title-inventories.gif)

## Features

- **YAML Configuration** - Create menus without coding
- **30+ Item Components** - Full support for Minecraft 1.20.5+ item system
- **Animated Titles** - Dynamic inventory titles (requires PacketEvents)
- **Pattern System** - Reusable layouts and button templates
- **Actions & Requirements** - 40+ actions, conditional logic, click handlers
- **PlaceholderAPI** - Full placeholder support with local overrides
- **Multi-platform** - Spigot, Paper, and Folia support
- **22+ Plugin Hooks** - ItemsAdder, Oraxen, LuckPerms, Vault, MythicMobs, and more

## Links

| Resource | Link |
|----------|------|
| Download | [Modrinth](https://modrinth.com/plugin/zmenu) |
| Documentation | [docs.groupez.dev](https://docs.groupez.dev/zmenu/getting-started) |
| JavaDocs | [API Reference](https://repo.groupez.dev/javadoc/releases/fr/maxlego08/menu/zmenu-api/1.1.0.8) |
| Discord | [discord.groupez.dev](https://discord.groupez.dev/) |
| Inventory Builder | [minecraft-inventory-builder.com](https://minecraft-inventory-builder.com/) |

## Quick Start

1. Download zMenu from [Modrinth](https://modrinth.com/plugin/zmenu)
2. Place the JAR in your `plugins/` folder
3. Restart your server
4. Edit files in `plugins/zMenu/inventories/`
5. Use `/zm reload` to apply changes

### Example Inventory

```yaml
size: 27
name: "&6My Menu"

items:
  example:
    slot: 13
    item:
      material: DIAMOND
      name: "&bClick me!"
      lore:
        - "&7This is an example button"
    click_requirement:
      left_click:
        clicks: [LEFT, SHIFT_LEFT]
        success:
          - type: message
            messages:
              - "&aYou clicked the button!"
```

## API Usage

**Maven**
```xml
<repository>
    <id>groupez</id>
    <url>https://repo.groupez.dev/releases</url>
</repository>

<dependency>
    <groupId>fr.maxlego08.menu</groupId>
    <artifactId>zmenu-api</artifactId>
    <version>1.1.0.8</version>
    <scope>provided</scope>
</dependency>
```

**Gradle**
```kotlin
repositories {
    maven("https://repo.groupez.dev/releases")
}

dependencies {
    compileOnly("fr.maxlego08.menu:zmenu-api:1.1.0.8")
}
```

**Opening an inventory**
```java
InventoryManager manager = plugin.getServer().getServicesManager()
    .getRegistration(InventoryManager.class).getProvider();

manager.openInventory(player, "zmenu:example");
```

## Supported Plugins

<details>
<summary>Click to expand</summary>

- ItemsAdder
- Oraxen
- Nexo
- Nova
- SlimeFun
- ExecutableItems
- ExecutableBlocks
- HeadDatabase
- zHead
- zItems
- MythicMobs
- LuckPerms
- Vault
- PlaceholderAPI
- PacketEvents
- Jobs
- Shopkeepers
- MagicCosmetics
- HMCCosmetics
- BreweryX
- CraftEngine
- Eco

</details>

## Sponsors

- [Serveur Minecraft Vote](https://serveur-minecraft-vote.fr/)
- [MineStrator](https://minestrator.com/a/GROUPEZ)

## License

This project is licensed under the MIT License.
