package fr.maxlego08.menu.api.itemstack.components.legacy;

import com.google.common.collect.Multimap;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.attribute.AttributeEntry;
import fr.maxlego08.menu.api.attribute.AttributeMergeStrategy;
import fr.maxlego08.menu.api.attribute.AttributeUtil;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.AttributeModifiersComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableAttributeWrapper;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LegacyAttributeModifiersComponent extends AttributeModifiersComponent {

    public LegacyAttributeModifiersComponent(MenuPlugin plugin, @Nullable List<ResolvableAttributeWrapper> resolvableAttributes, @Nullable ResolvableEnum<AttributeMergeStrategy> mergeStrategy) {
        super(plugin, resolvableAttributes, mergeStrategy);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        List<AttributeEntry> newEntries = this.resolveEntries(context);
        if (newEntries.isEmpty()) return;

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        List<AttributeEntry> existingEntries = new ArrayList<>();
        Multimap<Attribute, AttributeModifier> existing = itemMeta.getAttributeModifiers();
        if (existing != null) {
            for (Map.Entry<Attribute, AttributeModifier> entry : existing.entries()) {
                existingEntries.add(new AttributeEntry(entry.getKey(), entry.getValue()));
            }
        }

        AttributeMergeStrategy attributeMergeStrategy = Resolvable.resolve(context, this.getMergeStrategy());
        List<AttributeEntry> resultEntries = AttributeUtil.mergeAttributes(existingEntries, newEntries, attributeMergeStrategy);

        itemMeta.setAttributeModifiers(null);
        for (AttributeEntry entry : resultEntries) {
            itemMeta.addAttributeModifier(entry.attribute(), entry.modifier());
        }

        itemStack.setItemMeta(itemMeta);
    }
}
