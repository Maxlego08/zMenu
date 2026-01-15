package fr.maxlego08.menu.zcore.utils.itemstack;

import fr.maxlego08.menu.api.MenuItemStack;
import org.jetbrains.annotations.NotNull;

public record ContainerSlot(@NotNull MenuItemStack itemStack, int slot) {
}
