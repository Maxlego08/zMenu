package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class ProfileComponent extends ItemComponent {
    private final @Nullable PlayerProfile profile;

    public ProfileComponent(@Nullable PlayerProfile profile) {
        this.profile = profile;
    }

    public @Nullable PlayerProfile getProfile() {
        return this.profile;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, SkullMeta.class, skullMeta -> {
            skullMeta.setOwnerProfile(this.profile);
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Cannot apply ProfileComponent to itemStack: " + itemStack.getType().name());
    }
}
