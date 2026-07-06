package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableEntityType;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableEntityTypeTag;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableEquipmentSlot;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class EquippableComponent extends ItemComponent {
    private final @Nullable ResolvableEquipmentSlot slot;
    private final @Nullable ResolvableNamespacedKey equipSound;
    private final @Nullable ResolvableNamespacedKey assetId;
    private final @Nullable ResolvableBoolean dispensable;
    private final @Nullable ResolvableBoolean swappable;
    private final @Nullable ResolvableBoolean damageOnHurt;
    private final @Nullable ResolvableBoolean equipOnInteract;
    private final @Nullable ResolvableNamespacedKey cameraOverlay;
    private final @Nullable ResolvableBoolean canBeSheared;
    private final @Nullable ResolvableNamespacedKey shearingSound;
    private final @Nullable List<ResolvableEntityType> allowedEntities;
    private final @Nullable ResolvableEntityTypeTag allowedEntityTags;

    public EquippableComponent(
            @Nullable ResolvableEquipmentSlot slot,
            @Nullable ResolvableNamespacedKey equipSound,
            @Nullable ResolvableNamespacedKey assetId,
            @Nullable ResolvableBoolean dispensable,
            @Nullable ResolvableBoolean swappable,
            @Nullable ResolvableBoolean damageOnHurt,
            @Nullable ResolvableBoolean equipOnInteract,
            @Nullable ResolvableNamespacedKey cameraOverlay,
            @Nullable ResolvableBoolean canBeSheared,
            @Nullable ResolvableNamespacedKey shearingSound,
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

    public @Nullable ResolvableNamespacedKey getEquipSound() {
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

    public @Nullable ResolvableNamespacedKey getShearingSound() {
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
        EquipmentSlot resolvedEquipmentSlot = Resolvable.resolve(context, this.slot);
        if (resolvedEquipmentSlot != null) {
            Equippable.Builder equippable = Equippable.equippable(resolvedEquipmentSlot);

            Resolvable.applyResolvable(context, this.equipSound, equippable::equipSound);
            Resolvable.applyResolvable(context, this.assetId, equippable::assetId);
            Resolvable.applyResolvable(context, this.dispensable, equippable::dispensable);
            Resolvable.applyResolvable(context, this.swappable, equippable::swappable);
            Resolvable.applyResolvable(context, this.damageOnHurt, equippable::damageOnHurt);
            Resolvable.applyResolvable(context, this.equipOnInteract, equippable::equipOnInteract);
            Resolvable.applyResolvable(context, this.cameraOverlay, equippable::cameraOverlay);
            Resolvable.applyResolvable(context, this.canBeSheared, equippable::canBeSheared);
            Resolvable.applyResolvable(context, this.shearingSound, equippable::shearSound);

            //TODO: rework allowedEntities and allowedEntityTags to use RegistryKeySet and RegistrySet instead of List and Tag

            itemStack.setData(DataComponentTypes.EQUIPPABLE, equippable.build());
        }
    }
}
