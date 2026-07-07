package fr.maxlego08.menu.api.attribute;

import fr.maxlego08.menu.api.MenuPlugin;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for handling attribute modifiers on items.
 * Provides platform-specific implementations for Paper and Spigot.
 */
public final class AttributeUtil {

    private AttributeUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Applies a list of attribute wrappers to an item stack with a specific merge strategy.
     * Uses the appropriate method based on the server platform (Paper or Spigot).
     *
     * @param itemStack  the item stack to apply attributes to
     * @param attributes the list of attribute wrappers to apply
     * @param plugin     the plugin instance
     * @param strategy   the strategy to use when merging with existing attributes
     */
    public static void applyAttributes(@NotNull ItemStack itemStack,@NotNull List<AttributeWrapper> attributes,@NotNull MenuPlugin plugin,@Nullable AttributeMergeStrategy strategy) {
        if (attributes.isEmpty()) {
            return;
        }
        
        plugin.getAttributApplier().applyAttributesModern(itemStack, attributes, plugin, strategy);
    }

    /**
     * Merges two attribute entry lists according to the specified strategy.
     *
     * @param existing   list of existing attribute entries
     * @param newEntries list of new attribute entries to apply
     * @param strategy   the merge strategy
     * @return the merged list of attribute entries
     */
    public static List<AttributeEntry> mergeAttributes(@NotNull List<AttributeEntry> existing,@NotNull List<AttributeEntry> newEntries,@Nullable AttributeMergeStrategy strategy) {

        List<AttributeEntry> result = new ArrayList<>();

        if (strategy == null) {
            strategy = AttributeMergeStrategy.ADD;
        }

        switch (strategy) {
            case REPLACE -> {
                // Only keep new modifiers, discard all existing
                result.addAll(newEntries);
            }
            case ADD -> {
                // Keep all existing modifiers and add all new ones
                result.addAll(existing);
                result.addAll(newEntries);
            }
            case KEEP_HIGHEST -> {
                // For each (attribute, operation, slotGroup) triple, keep only the modifier with the highest value
                List<AttributeEntry> allEntries = new ArrayList<>();
                allEntries.addAll(existing);
                allEntries.addAll(newEntries);

                // Group by (attribute, operation, slotGroup)
                Map<ModifierKey, List<AttributeEntry>> grouped = allEntries.stream().collect(Collectors.groupingBy(entry -> new ModifierKey(entry.attribute(), entry.modifier().getOperation(), entry.modifier().getSlotGroup())));

                // For each group, keep the one with highest amount
                for (Map.Entry<ModifierKey, List<AttributeEntry>> group : grouped.entrySet()) {
                    List<AttributeEntry> entries = group.getValue();
                    if (!entries.isEmpty()) {
                        AttributeEntry highest = entries.stream().max(Comparator.comparingDouble(e -> e.modifier().getAmount())).orElse(entries.getFirst());
                        result.add(highest);
                    }
                }
            }
            case KEEP_LOWEST -> {
                // For each (attribute, operation, slotGroup) triple, keep only the modifier with the lowest value
                List<AttributeEntry> allEntries = new ArrayList<>();
                allEntries.addAll(existing);
                allEntries.addAll(newEntries);

                // Group by (attribute, operation, slotGroup)
                Map<ModifierKey, List<AttributeEntry>> grouped = allEntries.stream().collect(Collectors.groupingBy(entry -> new ModifierKey(entry.attribute(), entry.modifier().getOperation(), entry.modifier().getSlotGroup())));

                // For each group, keep the one with lowest amount
                for (Map.Entry<ModifierKey, List<AttributeEntry>> group : grouped.entrySet()) {
                    List<AttributeEntry> entries = group.getValue();
                    if (!entries.isEmpty()) {
                        AttributeEntry lowest = entries.stream().min(Comparator.comparingDouble(e -> e.modifier().getAmount())).orElse(entries.getFirst());
                        result.add(lowest);
                    }
                }
            }
            case SUM -> {
                // Group by (attribute, operation, slotGroup) and sum amounts
                List<AttributeEntry> allEntries = new ArrayList<>();
                allEntries.addAll(existing);
                allEntries.addAll(newEntries);

                // Group by (attribute, operation, slotGroup)
                Map<ModifierKey, List<AttributeEntry>> grouped = allEntries.stream().collect(Collectors.groupingBy(entry -> new ModifierKey(entry.attribute(), entry.modifier().getOperation(), entry.modifier().getSlotGroup())));

                // For each group, sum all amounts
                for (Map.Entry<ModifierKey, List<AttributeEntry>> group : grouped.entrySet()) {
                    List<AttributeEntry> entries = group.getValue();
                    if (entries.isEmpty()) continue;

                    // Sum all amounts
                    double totalAmount = entries.stream().mapToDouble(e -> e.modifier().getAmount()).sum();

                    // Create a new modifier with the summed amount
                    AttributeModifier first = entries.getFirst().modifier();
                    AttributeModifier summed = new AttributeModifier(first.getKey(), totalAmount, first.getOperation(), first.getSlotGroup());
                    result.add(new AttributeEntry(group.getKey().attribute(), summed));
                }
            }
        }

        return result;
    }
}