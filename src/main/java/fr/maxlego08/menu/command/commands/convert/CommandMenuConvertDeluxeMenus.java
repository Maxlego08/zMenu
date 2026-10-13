package fr.maxlego08.menu.command.commands.convert;

import com.mojang.brigadier.arguments.StringArgumentType;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.maxlego08.menu.loader.deluxemenu.DeluxeMenusConverter;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CommandMenuConvertDeluxeMenus extends SubCommand<ZMenuPlugin> {

    public CommandMenuConvertDeluxeMenus(ZMenuPlugin plugin) {
        super(plugin, "deluxemenus", "dm");
        this.setPermission(Permission.ZMENU_CONVERT.getPermission());
        this.addOptionalArgument("menu", StringArgumentType.string());
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        ZMenuPlugin plugin = commandDispatch.getPlugin();
        CommandSender sender = commandDispatch.getSender();
        DeluxeMenusConverter converter = new DeluxeMenusConverter(plugin);
        String menuName = commandDispatch.getOptionalArgument("menu", String.class).orElse(null);

        List<File> menus;
        try {
            if (menuName == null) {
                menus = converter.findMenus();
            } else {
                File menu = converter.findMenu(menuName);
                menus = menu == null ? List.of() : List.of(menu);
            }
        } catch (IOException exception) {
            MessageUtils.message(plugin, sender, "<red>Cannot read the DeluxeMenus folder: " + exception.getMessage());
            return CommandResultType.FAILURE;
        }

        if (menus.isEmpty()) {
            String what = menuName == null ? "No menu" : "No menu named " + menuName;
            MessageUtils.message(plugin, sender, "<red>" + what + " was found in <white>" + converter.getSourceFolder().getPath());
            return CommandResultType.FAILURE;
        }

        int converted = 0;
        for (File menu : menus) {
            DeluxeMenusConverter.Result result = converter.convert(menu);
            if (result.isSuccess()) {
                converted++;
                String warnings = result.warnings().isEmpty() ? "" : " <yellow>(" + result.warnings().size() + " not converted exactly, see the top of the file)";
                String command = result.commandFile() == null ? "" : " <gray>+ command";
                MessageUtils.message(plugin, sender, "<green>✔ <white>" + result.name() + " <gray>→ <white>" + converter.relative(result.inventoryFile()) + command + warnings);
            } else {
                MessageUtils.message(plugin, sender, "<red>✘ <white>" + result.name() + "<gray>: <red>" + result.error());
            }
        }

        MessageUtils.message(plugin, sender, "<white>Converted <green>" + converted + "<white>/<green>" + menus.size() + "<white> menus. Review them, then run <gray>/zm reload<white> to load them.");
        if (Bukkit.getPluginManager().isPluginEnabled("DeluxeMenus")) {
            MessageUtils.message(plugin, sender, "<yellow>DeluxeMenus is still enabled: disable it, or its open commands will conflict with the converted ones.");
        }
        return CommandResultType.SUCCESS;
    }
}
