package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.common.enums.Permission;
import fr.robie.paperdispatch.argument.EnumArgument;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CommandMenuTestDupe extends SubCommand<ZMenuPlugin> {

    public CommandMenuTestDupe(ZMenuPlugin plugin) {
        super(plugin, "testdupe");
        this.setPermission(Permission.ZMENU_TEST_DUPE.getPermission());
        this.addRequiredArgument("type", new EnumArgument<>(TestDupeType.class));
        this.setPlayerOnly();
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {

        TestDupeType dupeType = commandDispatch.getArgument("type", TestDupeType.class);

        ItemStack itemStack = new ItemStack(Material.STONE);
        itemStack = commandDispatch.getPlugin().getDupeManager().protectItem(itemStack);

        switch (dupeType) {
            case INVENTORY -> commandDispatch.getPlayer().getInventory().addItem(itemStack.clone());
            case ITEM -> commandDispatch.getPlayer().getWorld().dropItem(commandDispatch.getPlayer().getLocation(), itemStack.clone());
        }

        return CommandResultType.SUCCESS;
    }

    private enum TestDupeType {
        INVENTORY,
        ITEM
    }
}
