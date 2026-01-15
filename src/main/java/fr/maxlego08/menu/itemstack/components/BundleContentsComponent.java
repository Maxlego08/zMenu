package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record BundleContentsComponent(
        @NotNull List<@NotNull MenuItemStack> contents
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, BundleMeta.class, bundleMeta -> {
            for (MenuItemStack menuItemStack : contents) {
                bundleMeta.addItem(menuItemStack.build(player));
            }
        });
        if (!apply) {
            if (Configuration.enableDebug)
                Logger.info("Failed to apply BundleContents to ItemStack of type " + itemStack.getType().name() + " check if it's a bundle.");
        }
    }
}
