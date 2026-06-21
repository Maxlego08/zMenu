package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.*;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class EquippableComponent extends ItemComponent {
    private final @Nullable ResolvableEquipmentSlot slot;
    private final @Nullable ResolvableSound equipSound;
    private final @Nullable ResolvableNamespacedKey assetId;
    private final @Nullable ResolvableBoolean dispensable;
    private final @Nullable ResolvableBoolean swappable;
    private final @Nullable ResolvableBoolean damageOnHurt;
    private final @Nullable ResolvableBoolean equipOnInteract;
    private final @Nullable ResolvableNamespacedKey cameraOverlay;
    private final @Nullable ResolvableBoolean canBeSheared;
    private final @Nullable ResolvableSound shearingSound;
    private final @Nullable List<ResolvableEntityType> allowedEntities;
    private final @Nullable ResolvableEntityTypeTag allowedEntityTags;

    public EquippableComponent(
            @Nullable ResolvableEquipmentSlot slot,
            @Nullable ResolvableSound equipSound,
            @Nullable ResolvableNamespacedKey assetId,
            @Nullable ResolvableBoolean dispensable,
            @Nullable ResolvableBoolean swappable,
            @Nullable ResolvableBoolean damageOnHurt,
            @Nullable ResolvableBoolean equipOnInteract,
            @Nullable ResolvableNamespacedKey cameraOverlay,
            @Nullable ResolvableBoolean canBeSheared,
            @Nullable ResolvableSound shearingSound,
            @Nullable List<ResolvableEntityType> allowedEntities,
            @Nullable ResolvableEntityTypeTag allowedEntityTags
    ) {
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

    public @Nullable ResolvableEquipmentSlot getSlot() {
        return this.slot;
    }

    public @Nullable ResolvableSound getEquipSound() {
        return this.equipSound;
    }

    public @Nullable ResolvableNamespacedKey getAssetId() {
        return this.assetId;
    }

    public @Nullable ResolvableBoolean getDispensable() {
        return this.dispensable;
    }

    public @Nullable ResolvableBoolean getSwappable() {
        return this.swappable;
    }

    public @Nullable ResolvableBoolean getDamageOnHurt() {
        return this.damageOnHurt;
    }

    public @Nullable ResolvableBoolean getEquipOnInteract() {
        return this.equipOnInteract;
    }

    public @Nullable ResolvableNamespacedKey getCameraOverlay() {
        return this.cameraOverlay;
    }

    public @Nullable ResolvableBoolean getCanBeSheared() {
        return this.canBeSheared;
    }

    public @Nullable ResolvableSound getShearingSound() {
        return this.shearingSound;
    }

    public @Nullable List<ResolvableEntityType> getAllowedEntities() {
        return this.allowedEntities;
    }

    public @Nullable ResolvableEntityTypeTag getAllowedEntityTags() {
        return this.allowedEntityTags;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        org.bukkit.inventory.meta.components.EquippableComponent equippable = itemMeta.getEquippable();
        
        this.applyResolvable(context, equippable::setSlot, this.slot);
        this.applyResolvable(context, equippable::setEquipSound, this.equipSound);
        this.applyResolvable(context, equippable::setModel, this.assetId);
        this.applyResolvable(context, equippable::setDispensable, this.dispensable);
        this.applyResolvable(context, equippable::setSwappable, this.swappable);
        this.applyResolvable(context, equippable::setDamageOnHurt, this.damageOnHurt);
        this.applyResolvable(context, equippable::setEquipOnInteract, this.equipOnInteract);
        this.applyResolvable(context, equippable::setCameraOverlay, this.cameraOverlay);
        this.applyResolvable(context, equippable::setCanBeSheared, this.canBeSheared);
        this.applyResolvable(context, equippable::setShearingSound, this.shearingSound);

        Resolvable.applyResolvable(context, this.allowedEntities, equippable::setAllowedEntities);

        Resolvable.applyResolvable(context, this.allowedEntityTags, equippable::setAllowedEntities);

        itemStack.setItemMeta(itemMeta);
    }
}
