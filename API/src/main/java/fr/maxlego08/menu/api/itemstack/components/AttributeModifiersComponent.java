package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.attribute.AttributApplier;
import fr.maxlego08.menu.api.attribute.AttributeMergeStrategy;
import fr.maxlego08.menu.api.attribute.AttributeWrapper;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableAttributeWrapper;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class AttributeModifiersComponent extends ItemComponent {
    private final MenuPlugin plugin;
    private final AttributApplier attributApplier;
    private final @Nullable List<ResolvableAttributeWrapper> resolvableAttributes;
    private final @Nullable ResolvableEnum<AttributeMergeStrategy> mergeStrategy;

    public AttributeModifiersComponent(MenuPlugin plugin, @Nullable List<ResolvableAttributeWrapper> resolvableAttributes, @Nullable ResolvableEnum<AttributeMergeStrategy> mergeStrategy) {
        this.plugin = plugin;
        this.attributApplier = plugin.getAttributApplier();
        this.resolvableAttributes = resolvableAttributes;
        this.mergeStrategy = mergeStrategy;
    }

    @Nullable
    public List<ResolvableAttributeWrapper> getAttributes() {
        return this.resolvableAttributes;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        List<AttributeWrapper> resolved = null;
        if (this.resolvableAttributes != null) {
            resolved = new ArrayList<>();
            for (Resolvable<AttributeWrapper> resolvable : this.resolvableAttributes) {
                AttributeWrapper wrapper = resolvable.resolve(context);
                if (wrapper != null) {
                    resolved.add(wrapper);
                }
            }
        }
        if (resolved == null || resolved.isEmpty()) return;
        AttributeMergeStrategy attributeMergeStrategy = Resolvable.resolve(context, this.mergeStrategy);
        this.attributApplier.applyAttributesModern(itemStack, resolved, this.plugin, attributeMergeStrategy);
    }

}
