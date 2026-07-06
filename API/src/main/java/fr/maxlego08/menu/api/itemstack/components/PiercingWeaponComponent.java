package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableSound;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PiercingWeapon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class PiercingWeaponComponent extends ItemComponent {
    private final @NotNull ResolvableBoolean dealsKnockback;
    private final @NotNull ResolvableBoolean dismounts;
    private final @Nullable ResolvableNamespacedKey sound;
    private final @Nullable ResolvableNamespacedKey hitSound;

    public PiercingWeaponComponent(@NotNull ResolvableBoolean dealsKnockback, @NotNull ResolvableBoolean dismounts, @Nullable ResolvableNamespacedKey sound, @Nullable ResolvableNamespacedKey hitSound) {
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

    public @Nullable ResolvableNamespacedKey getSound() {
        return this.sound;
    }

    public @Nullable ResolvableNamespacedKey getHitSound() {
        return this.hitSound;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        PiercingWeapon.Builder builder = PiercingWeapon.piercingWeapon();

        this.applyResolvable(context, builder::dealsKnockback, this.dealsKnockback);
        this.applyResolvable(context, builder::dismounts, this.dismounts);
        this.applyResolvable(context, builder::sound, this.sound);
        this.applyResolvable(context, builder::hitSound, this.hitSound);

        itemStack.setData(DataComponentTypes.PIERCING_WEAPON, builder.build());
    }
}
