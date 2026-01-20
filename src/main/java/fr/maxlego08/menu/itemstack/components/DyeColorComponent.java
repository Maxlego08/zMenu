package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class DyeColorComponent extends ItemComponent {
    private final @NotNull Color color;

    public DyeColorComponent(@NotNull Color color) {
        this.color = color;
    }

    public @NotNull Color getColor() {
        return this.color;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, ColorableArmorMeta.class, colorableArmorMeta -> colorableArmorMeta.setColor(this.color));
        if (!apply && Configuration.enableDebug){
            Logger.info("Could not apply DyeColorComponent to item: " + itemStack.getType().name() + " because it does not support colorable armor meta.");
        }
    }
}
