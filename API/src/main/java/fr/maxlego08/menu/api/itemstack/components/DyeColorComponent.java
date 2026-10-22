package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class DyeColorComponent extends ItemComponent {
    private final @NotNull ResolvableColor color;

    public DyeColorComponent(@NotNull Color color) {
        this.color = ResolvableColor.of(color);
    }

    public DyeColorComponent(@NotNull ResolvableColor color) {
        this.color = color;
    }

    public @NotNull ResolvableColor getColor() {
        return this.color;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Color resolved = this.resolveColor(context, itemStack);
        if (resolved == null) return;

        itemStack.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor().color(resolved).build());
    }

    protected @Nullable Color resolveColor(@NotNull BuildContext context, @NotNull ItemStack itemStack) {
        Color resolved = this.color.resolve(context);
        if (resolved == null && Configuration.enableDebug && this.color.isDynamic()) {
            Logger.info("Could not resolve dynamic color '" + this.color.getExpression() + "' for item: " + itemStack.getType().name());
        }
        return resolved;
    }

    @Override
    public @Nullable Object serialize() {
        return this.color.serialize();
    }
}
