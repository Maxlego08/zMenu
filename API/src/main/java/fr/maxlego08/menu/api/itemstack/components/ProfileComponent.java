package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvablePlayerProfile;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class ProfileComponent extends ItemComponent {
    private final @Nullable ResolvablePlayerProfile profile;
    private final @Nullable PlayerProfile fallbackProfile;

    public ProfileComponent(@NotNull ResolvablePlayerProfile profile) {
        this.profile = profile;
        this.fallbackProfile = null;
    }

    public ProfileComponent(@Nullable PlayerProfile fallbackProfile) {
        this.profile = null;
        this.fallbackProfile = fallbackProfile;
    }

    public @Nullable ResolvablePlayerProfile getProfile() {
        return this.profile;
    }

    public @Nullable PlayerProfile getFallbackProfile() {
        return this.fallbackProfile;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        PlayerProfile resolved = this.profile != null ? this.profile.resolve(context) : this.fallbackProfile;
        if (resolved == null) return;

        boolean apply = ItemUtil.editMeta(itemStack, SkullMeta.class, skullMeta -> {
            skullMeta.setOwnerProfile(resolved);
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Cannot apply ProfileComponent to itemStack: " + itemStack.getType().name());
    }
}
