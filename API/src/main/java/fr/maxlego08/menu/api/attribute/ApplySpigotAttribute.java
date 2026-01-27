package fr.maxlego08.menu.api.attribute;

import fr.maxlego08.menu.api.MenuPlugin;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Spigot-specific implementation of the AttributApplier interface.
 * Applies attribute modifiers to items using Spigot's ItemMeta API.
 */
public class ApplySpigotAttribute implements AttributApplier {

    @Override
    public void applyAttributesModern(@NotNull ItemStack itemStack, @NotNull List<AttributeWrapper> attributes, @NotNull MenuPlugin plugin, @Nullable AttributeMergeStrategy strategy) {
        ItemMeta meta = itemStack.getItemMeta();
        // Get existing modifiers
        List<AttributeEntry> existingEntries = new ArrayList<>();
        if (meta.hasAttributeModifiers()) {
            for (org.bukkit.attribute.Attribute attr : org.bukkit.attribute.Attribute.values()) {
                var modifiers = meta.getAttributeModifiers(attr);
                if (modifiers != null && !modifiers.isEmpty()) {
                    for (AttributeModifier modifier : modifiers) {
                        existingEntries.add(new AttributeEntry(attr, modifier));
                    }
                }
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

        // Clear existing and apply merged modifiers
        for (org.bukkit.attribute.Attribute attr : Attribute.values()) {
            meta.removeAttributeModifier(attr);
        }

        for (AttributeEntry entry : resultEntries) {
            meta.addAttributeModifier(entry.attribute(), entry.modifier());
        }
        itemStack.setItemMeta(meta);
    }
}
