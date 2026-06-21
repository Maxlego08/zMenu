package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ColorableArmorMeta;
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

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Color resolved = this.color.resolve(context);
        if (resolved == null) {
            if (Configuration.enableDebug && this.color.isDynamic()) {
                Logger.info("Could not resolve dynamic color '" + this.color.getExpression() + "' for item: " + itemStack.getType().name());
            }
            return;
        }

        boolean apply = ItemUtil.editMeta(itemStack, ColorableArmorMeta.class, meta -> meta.setColor(resolved));
        if (!apply && Configuration.enableDebug) {
            Logger.info("Could not apply DyeColorComponent to item: " + itemStack.getType().name() + " because it does not support colorable armor meta.");
        }
    }
}
