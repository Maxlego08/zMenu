package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("unused")
public class PiercingWeaponComponent extends ItemComponent {
    private final boolean dealsKnockback;
    private final boolean dismounts;
    private final Optional<Sound> sound;
    private final Optional<Sound> hitSound;

    public PiercingWeaponComponent(boolean dealsKnockback, boolean dismounts, Optional<Sound> sound, Optional<Sound> hitSound) {
        this.dealsKnockback = dealsKnockback;
        this.dismounts = dismounts;
        this.sound = sound;
        this.hitSound = hitSound;
    }

    public boolean isDealsKnockback() {
        return this.dealsKnockback;
    }

    public boolean isDismounts() {
        return this.dismounts;
    }

    public Optional<Sound> getSound() {
        return this.sound;
    }

    public Optional<Sound> getHitSound() {
        return this.hitSound;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            org.bukkit.inventory.meta.components.PiercingWeaponComponent piercingWeapon = itemMeta.getPiercingWeapon();

            piercingWeapon.setDealsKnockback(this.dealsKnockback);
            piercingWeapon.setDismounts(this.dismounts);

            this.sound.ifPresent(piercingWeapon::setSound);
            this.hitSound.ifPresent(piercingWeapon::setHitSound);

            itemStack.setItemMeta(itemMeta);
        }
    }
}
