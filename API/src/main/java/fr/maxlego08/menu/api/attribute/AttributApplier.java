package fr.maxlego08.menu.api.attribute;

import fr.maxlego08.menu.api.MenuPlugin;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface AttributApplier {

    void applyAttributesModern(ItemStack itemStack, List<AttributeWrapper> attributes, MenuPlugin plugin, AttributeMergeStrategy strategy);

}
