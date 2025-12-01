package fr.maxlego08.menu.hooks;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.attribute.*;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ApplyPaperAttribute implements AttributApplier {

    @Override
    public void applyAttributesModern(ItemStack itemStack, List<AttributeWrapper> attributes, MenuPlugin plugin, AttributeMergeStrategy strategy) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();

        // Get existing modifiers
        ItemAttributeModifiers existing = itemStack.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        List<AttributeEntry> existingEntries = new ArrayList<>();

        if (existing != null) {
            for (var entry : existing.modifiers()) {
                existingEntries.add(new AttributeEntry(entry.attribute(), entry.modifier()));
            }
        }

        // Create list of new modifiers
        List<AttributeEntry> newEntries = new ArrayList<>();
        for (AttributeWrapper wrapper : attributes) {
            AttributeModifier modifier = wrapper.toAttributeModifier(plugin);
            newEntries.add(new AttributeEntry(wrapper.attribute(), modifier));
        }

        // Apply strategy
        List<AttributeEntry> resultEntries = AttributeUtil.mergeAttributes(existingEntries, newEntries, strategy);

        // Build final attribute modifiers
        for (AttributeEntry entry : resultEntries) {
            builder.addModifier(entry.attribute(), entry.modifier());
        }

        itemStack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
    }

}
