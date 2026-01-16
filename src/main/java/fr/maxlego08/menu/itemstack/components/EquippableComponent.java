package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public record EquippableComponent(
    Optional<EquipmentSlot> slot,
    Optional<Sound> equipSound,
    Optional<NamespacedKey> assetId,
    Boolean dispensable,
    Boolean swappable,
    Boolean damageOnHurt,
    Boolean equipOnInteract,
    Optional<NamespacedKey> cameraOverlay,
    Boolean canBeSheared,
    Optional<Sound> shearingSound,
    Optional<Collection<EntityType>> allowedEntities,
    Optional<Tag<EntityType>> allowedEntityTags
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            org.bukkit.inventory.meta.components.EquippableComponent equippable = itemMeta.getEquippable();

            this.slot.ifPresent(equippable::setSlot);
            this.equipSound.ifPresent(equippable::setEquipSound);
            this.assetId.ifPresent(equippable::setModel);

            equippable.setDispensable(this.dispensable);
            equippable.setSwappable(this.swappable);
            equippable.setDamageOnHurt(this.damageOnHurt);

            equippable.setEquipOnInteract(this.equipOnInteract);

            this.cameraOverlay.ifPresent(equippable::setCameraOverlay);

            equippable.setCanBeSheared(this.canBeSheared);
            this.shearingSound.ifPresent(equippable::setShearingSound);

            this.allowedEntities.ifPresent(equippable::setAllowedEntities);
            this.allowedEntityTags.ifPresent(equippable::setAllowedEntities);

            itemStack.setItemMeta(itemMeta);
        }
    }
}
