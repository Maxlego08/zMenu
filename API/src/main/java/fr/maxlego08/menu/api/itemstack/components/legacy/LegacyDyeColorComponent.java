package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.DyeColorComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyDyeColorComponent extends DyeColorComponent {

    public LegacyDyeColorComponent(@NotNull ResolvableColor color) {
        super(color);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Color resolved = this.resolveColor(context, itemStack);
        if (resolved == null) return;

        boolean apply = ItemUtil.editMeta(itemStack, LeatherArmorMeta.class, meta -> meta.setColor(resolved));
        if (!apply && Configuration.enableDebug) {
            Logger.info("Could not apply DyeColorComponent to item: " + itemStack.getType().name() + " because it does not support leather armor meta.");
        }
    }
}
