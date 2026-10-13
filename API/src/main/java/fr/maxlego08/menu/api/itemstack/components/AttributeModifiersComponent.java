package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.attribute.AttributeEntry;
import fr.maxlego08.menu.api.attribute.AttributeMergeStrategy;
import fr.maxlego08.menu.api.attribute.AttributeUtil;
import fr.maxlego08.menu.api.attribute.AttributeWrapper;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableAttributeWrapper;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class AttributeModifiersComponent extends ItemComponent {
    private final MenuPlugin plugin;
    private final @Nullable List<ResolvableAttributeWrapper> resolvableAttributes;
    private final @Nullable ResolvableEnum<AttributeMergeStrategy> mergeStrategy;

    public AttributeModifiersComponent(MenuPlugin plugin, @Nullable List<ResolvableAttributeWrapper> resolvableAttributes, @Nullable ResolvableEnum<AttributeMergeStrategy> mergeStrategy) {
        this.plugin = plugin;
        this.resolvableAttributes = resolvableAttributes;
        this.mergeStrategy = mergeStrategy;
    }

    @Nullable
    public List<ResolvableAttributeWrapper> getAttributes() {
        return this.resolvableAttributes;
    }

    public @Nullable ResolvableEnum<AttributeMergeStrategy> getMergeStrategy() {
        return this.mergeStrategy;
    }

    protected @NotNull List<AttributeEntry> resolveEntries(@NotNull BuildContext context) {
        List<AttributeEntry> entries = new ArrayList<>();
        if (this.resolvableAttributes == null) return entries;
        for (Resolvable<AttributeWrapper> resolvable : this.resolvableAttributes) {
            AttributeWrapper wrapper = resolvable.resolve(context);
            if (wrapper != null) {
                entries.add(new AttributeEntry(wrapper.attribute(), wrapper.toAttributeModifier(this.plugin)));
            }
        }
        return entries;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        List<AttributeEntry> newEntries = this.resolveEntries(context);
        if (newEntries.isEmpty()) return;

        List<AttributeEntry> existingEntries = new ArrayList<>();
        ItemAttributeModifiers existing = itemStack.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (existing != null) {
            for (ItemAttributeModifiers.Entry entry : existing.modifiers()) {
                existingEntries.add(new AttributeEntry(entry.attribute(), entry.modifier()));
            }
        }

        AttributeMergeStrategy attributeMergeStrategy = Resolvable.resolve(context, this.mergeStrategy);
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();
        for (AttributeEntry entry : AttributeUtil.mergeAttributes(existingEntries, newEntries, attributeMergeStrategy)) {
            builder.addModifier(entry.attribute(), entry.modifier());
        }

        itemStack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
    }

    @Override
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        if (this.mergeStrategy != null) {
            Object mergeStrategy = this.mergeStrategy.serialize();
            if (mergeStrategy != null && !"".equals(mergeStrategy)) map.put("attribute-merge-strategy", mergeStrategy);
        }
        if (this.resolvableAttributes != null) map.put("modifiers", Resolvable.serializeList(this.resolvableAttributes));
        return map;
    }

}
