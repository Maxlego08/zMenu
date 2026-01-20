package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class TrimComponent extends ItemComponent {
    private final @Nullable ArmorTrim trim;

    public TrimComponent(@Nullable ArmorTrim trim) {
        this.trim = trim;
    }

    public @Nullable ArmorTrim getTrim() {
        return this.trim;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, ArmorMeta.class, armorMeta -> armorMeta.setTrim(this.trim));
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply TrimComponent to item: " + itemStack.getType().name());
    }
}
