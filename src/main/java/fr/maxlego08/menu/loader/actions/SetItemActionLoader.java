package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoActionLoader;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.SetItemAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.List;

@AutoActionLoader
public class SetItemActionLoader extends ActionLoader {
    private final MenuPlugin menuPlugin;

    public SetItemActionLoader(MenuPlugin menuPlugin) {
        super("set_item", "set-item");
        this.menuPlugin = menuPlugin;
    }

    @Override
    public @Nullable Action load(@NotNull String path, @NotNull TypedMapAccessor accessor, @NotNull File file) {
        List<Integer> slots = accessor.getIntList("slots");
        if (slots.isEmpty()) {
            int slot = accessor.getInt("slot", -1);
            if (slot == -1) {
                return null;
            }
            slots = Collections.singletonList(slot);
        }
        boolean inPlayerInventory = accessor.getBoolean("in-player-inventory", false);
        MenuItemStack menuItemStack;
        if (accessor.contains("item")) {
            menuItemStack = this.menuPlugin.getInventoryManager().loadItemStack(file, path, (java.util.Map<String, Object>) accessor.getObject("item"));
        } else {
            menuItemStack = this.menuPlugin.getInventoryManager().loadItemStack(file, path, accessor.map());
        }
        boolean dupeProtection = accessor.getBoolean("dupe-protection", true);
        return new SetItemAction(slots, inPlayerInventory, menuItemStack, dupeProtection);

    }
}
