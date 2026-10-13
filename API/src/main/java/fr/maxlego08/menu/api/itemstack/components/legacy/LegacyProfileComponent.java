package fr.maxlego08.menu.api.itemstack.components.legacy;

import com.destroystokyo.paper.profile.PlayerProfile;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.ProfileComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableProfileResolvable;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyProfileComponent extends ProfileComponent {

    public LegacyProfileComponent(@Nullable ResolvableProfileResolvable resolvable) {
        super(resolvable);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ResolvableProfileResolvable profile = this.getProfile();
        if (profile == null) return;

        PlayerProfile resolved = profile.resolvePlayerProfile(context);
        if (resolved == null) return;

        boolean apply = ItemUtil.editMeta(itemStack, SkullMeta.class, skullMeta -> skullMeta.setPlayerProfile(resolved));
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply ProfileComponent to item: " + itemStack.getType().name());
    }
}
