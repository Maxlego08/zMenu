package fr.maxlego08.menu.api.utils.itemstack;

import fr.maxlego08.menu.api.MenuItemStack;
import org.jetbrains.annotations.NotNull;

public record ZContainerSlot(@NotNull MenuItemStack itemStack, int slot) {
}
