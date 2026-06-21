package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class MapColorComponent extends ItemComponent {

    private final @NotNull ResolvableColor color;

    public MapColorComponent(@NotNull ResolvableColor color) {
        this.color = color;
    }

    public @NotNull ResolvableColor getColor() {
        return this.color;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, MapMeta.class, mapMeta -> {
            this.applyResolvable(context, mapMeta::setColor, this.color);
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply MapColorComponent to itemStack: " + itemStack.getType().name());
    }

}
