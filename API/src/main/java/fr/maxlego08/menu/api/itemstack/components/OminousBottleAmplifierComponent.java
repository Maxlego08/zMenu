package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.OminousBottleMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class OminousBottleAmplifierComponent extends ItemComponent {
    private final int amplifier;

    public OminousBottleAmplifierComponent(int amplifier) {
        this.amplifier = amplifier;
    }

    public int getAmplifier() {
        return this.amplifier;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, OminousBottleMeta.class, ominousBottleMeta -> {
            ominousBottleMeta.setAmplifier(this.amplifier);
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("OminousBottleAmplifierComponent couldn't be applied to " + itemStack.getType().name());
    }
}
