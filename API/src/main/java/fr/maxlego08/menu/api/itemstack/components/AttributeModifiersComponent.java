package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.attribute.AttributApplier;
import fr.maxlego08.menu.api.attribute.AttributeMergeStrategy;
import fr.maxlego08.menu.api.attribute.AttributeWrapper;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class AttributeModifiersComponent extends ItemComponent {
    private final MenuPlugin plugin;
    private final AttributApplier attributApplier;
    private final List<AttributeWrapper> attributes;
    private final AttributeMergeStrategy mergeStrategy;

    public AttributeModifiersComponent(MenuPlugin plugin, @NotNull List<AttributeWrapper> attributes, @Nullable AttributeMergeStrategy mergeStrategy) {
        this.plugin = plugin;
        this.attributApplier = plugin.getAttributApplier();
        this.attributes = attributes;
        this.mergeStrategy = mergeStrategy;
    }

    @NotNull
    public List<AttributeWrapper> getAttributes() {
        return attributes;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        attributApplier.applyAttributesModern(itemStack, this.attributes, this.plugin, this.mergeStrategy);
    }

}
