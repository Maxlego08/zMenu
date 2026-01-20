package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
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

@SuppressWarnings("unused")
public class EquippableComponent extends ItemComponent {
    private final Optional<EquipmentSlot> slot;
    private final Optional<Sound> equipSound;
    private final Optional<NamespacedKey> assetId;
    private final Boolean dispensable;
    private final Boolean swappable;
    private final Boolean damageOnHurt;
    private final Boolean equipOnInteract;
    private final Optional<NamespacedKey> cameraOverlay;
    private final Boolean canBeSheared;
    private final Optional<Sound> shearingSound;
    private final Optional<Collection<EntityType>> allowedEntities;
    private final Optional<Tag<EntityType>> allowedEntityTags;

    public EquippableComponent(Optional<EquipmentSlot> slot, Optional<Sound> equipSound, Optional<NamespacedKey> assetId, Boolean dispensable, Boolean swappable, Boolean damageOnHurt, Boolean equipOnInteract, Optional<NamespacedKey> cameraOverlay, Boolean canBeSheared, Optional<Sound> shearingSound, Optional<Collection<EntityType>> allowedEntities, Optional<Tag<EntityType>> allowedEntityTags) {
        this.slot = slot;
        this.equipSound = equipSound;
        this.assetId = assetId;
        this.dispensable = dispensable;
        this.swappable = swappable;
        this.damageOnHurt = damageOnHurt;
        this.equipOnInteract = equipOnInteract;
        this.cameraOverlay = cameraOverlay;
        this.canBeSheared = canBeSheared;
        this.shearingSound = shearingSound;
        this.allowedEntities = allowedEntities;
        this.allowedEntityTags = allowedEntityTags;
    }

    public Optional<EquipmentSlot> getSlot() {
        return slot;
    }

    public Optional<Sound> getEquipSound() {
        return equipSound;
    }

    public Optional<NamespacedKey> getAssetId() {
        return assetId;
    }

    public Boolean getDispensable() {
        return dispensable;
    }

    public Boolean getSwappable() {
        return swappable;
    }

    public Boolean getDamageOnHurt() {
        return damageOnHurt;
    }

    public Boolean getEquipOnInteract() {
        return equipOnInteract;
    }

    public Optional<NamespacedKey> getCameraOverlay() {
        return cameraOverlay;
    }

    public Boolean getCanBeSheared() {
        return canBeSheared;
    }

    public Optional<Sound> getShearingSound() {
        return shearingSound;
    }

    public Optional<Collection<EntityType>> getAllowedEntities() {
        return allowedEntities;
    }

    public Optional<Tag<EntityType>> getAllowedEntityTags() {
        return allowedEntityTags;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
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
