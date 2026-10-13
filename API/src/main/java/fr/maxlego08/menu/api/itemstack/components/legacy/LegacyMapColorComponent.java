package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.MapColorComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyMapColorComponent extends MapColorComponent {

    public LegacyMapColorComponent(@NotNull ResolvableColor color) {
        super(color);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, MapMeta.class, mapMeta -> {
            this.applyResolvable(context, mapMeta::setColor, this.getColor());
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply MapColorComponent to itemStack: " + itemStack.getType().name());
    }
}
