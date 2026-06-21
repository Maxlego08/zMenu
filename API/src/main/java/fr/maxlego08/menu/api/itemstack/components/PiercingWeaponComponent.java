package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableSound;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class PiercingWeaponComponent extends ItemComponent {
    private final @NotNull ResolvableBoolean dealsKnockback;
    private final @NotNull ResolvableBoolean dismounts;
    private final @Nullable ResolvableSound sound;
    private final @Nullable ResolvableSound hitSound;

    public PiercingWeaponComponent(@NotNull ResolvableBoolean dealsKnockback, @NotNull ResolvableBoolean dismounts, @Nullable ResolvableSound sound, @Nullable ResolvableSound hitSound) {
        this.dealsKnockback = dealsKnockback;
        this.dismounts = dismounts;
        this.sound = sound;
        this.hitSound = hitSound;
    }

    public @NotNull ResolvableBoolean isDealsKnockback() {
        return this.dealsKnockback;
    }

    public @NotNull ResolvableBoolean isDismounts() {
        return this.dismounts;
    }

    public @Nullable ResolvableSound getSound() {
        return this.sound;
    }

    public @Nullable ResolvableSound getHitSound() {
        return this.hitSound;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        org.bukkit.inventory.meta.components.PiercingWeaponComponent piercingWeapon = itemMeta.getPiercingWeapon();

        this.applyResolvable(context, piercingWeapon::setDealsKnockback, this.dealsKnockback);
        this.applyResolvable(context, piercingWeapon::setDismounts, this.dismounts);
        this.applyResolvable(context, piercingWeapon::setSound, this.sound);
        this.applyResolvable(context, piercingWeapon::setHitSound, this.hitSound);

        itemStack.setItemMeta(itemMeta);
    }
}
