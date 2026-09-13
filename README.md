# zMenu

[![Modrinth](https://img.shields.io/modrinth/dt/zmenu?logo=modrinth&label=Modrinth&color=00AF5C)](https://modrinth.com/project/XPQ42u1g)
[![Servers](https://img.shields.io/endpoint?url=https://faststats.dev/api/shields/zmenu?metric=servers)](https://faststats.dev/project/zmenu/minecraft-plugin)
[![Discord](https://img.shields.io/discord/511516467615760405?logo=discord&label=Discord&color=5865F2)](https://discord.gg/daTBzuk)
[![Java](https://img.shields.io/badge/Java-21+-orange?logo=openjdk)](https://www.oracle.com/java/)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.19--26.2+-green)](https://www.minecraft.net/)

zMenu is a powerful Minecraft **[Paper](https://github.com/PaperMC/Paper)** plugin for creating custom inventory GUIs through YAML configuration files. No coding is required for server administrators, while developers get a complete API for integration.

![showcase](https://img.groupez.dev/zmenu/basic_inventory.gif)![punish](https://img.groupez.dev/zmenu/punishv2.gif)
![optionnal arg](https://img.groupez.dev/zmenu/ao.gif)![shop](https://img.groupez.dev/zmenu/shop.gif)
![title animation](https://img.groupez.dev/zmenu/title-inventories.gif)

[![Servers and players](https://faststats.dev/embed/default:f3dc1a5c-c48b-4187-bf05-5406120bb86d:servers-and-players?w=800&h=300)](https://faststats.dev/project/zmenu/minecraft-plugin)

## Features

- **YAML Configuration** - Create menus without coding
- **30+ Item Components** - Full support for Minecraft 1.20.5+ item system
- **Animated Titles** - Dynamic inventory titles (requires PacketEvents)
- **Pattern System** - Reusable layouts and button templates
- **Actions & Requirements** - 40+ actions, conditional logic, click handlers
- **PlaceholderAPI** - Full placeholder support with local overrides
- **Paper & Folia** - Supports Paper, compatible Paper forks, and Folia; Spigot is not supported
- **26 Optional Hooks** - Integrations for plugins such as ItemsAdder, Oraxen, LuckPerms, MythicMobs, and more
- **Bedrock** - Bedrock support through Geyser/Floodgate

## Requirements

- Java 21 or newer
- Paper 1.19 or newer, a compatible Paper fork, or Folia
- Java 25 for Paper 26.1 and newer

Since version 1.1.1.6, zMenu is Paper-only and no longer supports Spigot servers.

## Links

| Resource | Link |
|----------|------|
| Download | [Modrinth](https://modrinth.com/project/XPQ42u1g) |
| Documentation | [docs.groupez.dev](https://docs.groupez.dev/zmenu/getting-started) |
| JavaDocs | [API Reference](https://repo.groupez.dev/javadoc/releases/fr/maxlego08/menu/zmenu-api/1.1.0.8) |
| Discord | [discord.groupez.dev](https://discord.groupez.dev/) |
| Inventory Builder | [minecraft-inventory-builder.com](https://minecraft-inventory-builder.com/) |

## Quick Start

1. Download zMenu from [Modrinth](https://modrinth.com/project/XPQ42u1g)
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
<project>
    <repositories>
        <repository>
            <id>groupez</id>
            <url>https://repo.groupez.dev/releases</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>fr.maxlego08.menu</groupId>
            <artifactId>zmenu-api</artifactId>
            <version>1.1.1.8</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

**Gradle**
```kotlin
repositories {
    maven("https://repo.groupez.dev/releases")
}

dependencies {
    compileOnly("fr.maxlego08.menu:zmenu-api:1.1.1.8")
}
```

**Opening an inventory**
```java
class Example {
    void openExample(Plugin plugin, Player player) {
        InventoryManager manager = plugin.getServer().getServicesManager()
            .getRegistration(InventoryManager.class).getProvider();

        manager.openInventory(player, "zmenu:example");
    }
}
```

## 26 Optional Hooks

- Geyser/Floodgate
- BreweryX
- CraftEngine
- Denizen
- Eco
- ExecutableItems
- ExecutableBlocks
- HeadDatabase
- HMCCosmetics
- ItemsAdder
- Jobs
- LuckPerms
- MagicCosmetics
- MMOItems
- MythicMobs
- Nexo
- NextGens
- Nova
- Oraxen
- PacketEvents
- ProtocolSupport
- Shopkeepers
- SlimeFun
- ViaVersion
- zHead
- zItems

## Sponsors

- [Serveur Minecraft Vote](https://serveur-minecraft-vote.fr/)
- [MineStrator](https://minestrator.com/a/GROUPEZ)
- [CashBack Minestrator](https://cashback.groupez.dev/)
