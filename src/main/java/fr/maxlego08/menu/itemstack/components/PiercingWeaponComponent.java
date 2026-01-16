package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record PiercingWeaponComponent(
    boolean dealsKnockback,
    boolean dismounts,
    Optional<Sound> sound,
    Optional<Sound> hitSound
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
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
