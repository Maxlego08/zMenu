package fr.maxlego08.menu.api.attribute;

import fr.maxlego08.menu.api.MenuPlugin;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface AttributApplier {

    void applyAttributesModern(@NotNull ItemStack itemStack,@NotNull List<AttributeWrapper> attributes,@NotNull MenuPlugin plugin,@Nullable AttributeMergeStrategy strategy);

}
