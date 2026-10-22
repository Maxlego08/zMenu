package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BundleContents;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class BundleContentsComponent extends ItemComponent {

    private final @NotNull List<@NotNull MenuItemStack> contents;

    public BundleContentsComponent(@NotNull List<@NotNull MenuItemStack> contents) {
        this.contents = contents;
    }

    public @NotNull List<@NotNull MenuItemStack> getContents() {
        return this.contents;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        BundleContents.Builder bundleContents = BundleContents.bundleContents();
        for (MenuItemStack menuItemStack : this.contents) {
            bundleContents.add(menuItemStack.build(player));
        }
        itemStack.setData(DataComponentTypes.BUNDLE_CONTENTS, bundleContents.build());
    }

    @Override
    public @Nullable Object serialize() {
        List<Map<String, Object>> items = new ArrayList<>(this.contents.size());
        for (MenuItemStack menuItemStack : this.contents) {
            items.add(menuItemStack.serializeToMap());
        }
        return items;
    }

}
