package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class PotionDurationScaleComponent extends ItemComponent {
    private final float durationScale;

    public PotionDurationScaleComponent(float durationScale) {
        this.durationScale = durationScale;
    }

    public float getDurationScale() {
        return this.durationScale;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, PotionMeta.class, potionMeta -> {
            potionMeta.setDurationScale(this.durationScale);
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("PotionContentsComponent couldn't be applied to " + itemStack.getType().name());
    }
}
