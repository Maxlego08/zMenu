package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomModelDataComponent extends ItemComponent {
    private final @NotNull List<@NotNull ResolvableColor> colors;
    private final @NotNull List<@NotNull ResolvableBoolean> flags;
    private final @NotNull List<@NotNull ResolvableFloat> floats;
    private final @NotNull List<@NotNull ResolvableString> strings;

    public CustomModelDataComponent(@NotNull List<@NotNull ResolvableColor> colors, @NotNull List<@NotNull ResolvableBoolean> flags, @NotNull List<@NotNull ResolvableFloat> floats, @NotNull List<@NotNull ResolvableString> strings) {
        this.colors = colors;
        this.flags = flags;
        this.floats = floats;
        this.strings = strings;
    }

    public @NotNull List<@NotNull ResolvableColor> getColors() {
        return this.colors;
    }

    public @NotNull List<@NotNull ResolvableBoolean> getFlags() {
        return this.flags;
    }

    public @NotNull List<@NotNull ResolvableFloat> getFloats() {
        return this.floats;
    }

    public @NotNull List<@NotNull ResolvableString> getStrings() {
        return this.strings;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            org.bukkit.inventory.meta.components.CustomModelDataComponent customModelDataComponent = itemMeta.getCustomModelDataComponent();

            Resolvable.applyResolvable(context, this.colors, customModelDataComponent::setColors);

            Resolvable.applyResolvable(context, this.flags, customModelDataComponent::setFlags);

            Resolvable.applyResolvable(context, this.floats, customModelDataComponent::setFloats);

            Resolvable.applyResolvable(context, this.strings, customModelDataComponent::setStrings);

            itemStack.setItemMeta(itemMeta);
        }
    }
}
