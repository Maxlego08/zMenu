package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.EquippableComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableEntityType;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableEntityTypeTag;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableEquipmentSlot;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LegacyEquippableComponent extends EquippableComponent {

    public LegacyEquippableComponent(
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
        super(slot, equipSound, assetId, dispensable, swappable, damageOnHurt, equipOnInteract, cameraOverlay, canBeSheared, shearingSound, allowedEntities, allowedEntityTags);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        EquipmentSlot resolvedEquipmentSlot = Resolvable.resolve(context, this.getSlot());
        if (resolvedEquipmentSlot == null) return;

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        org.bukkit.inventory.meta.components.EquippableComponent equippable = itemMeta.getEquippable();
        equippable.setSlot(resolvedEquipmentSlot);
        Resolvable.applyResolvable(context, this.getEquipSound(), key -> {
            Sound sound = Registry.SOUNDS.get(key);
            if (sound != null) equippable.setEquipSound(sound);
        });
        Resolvable.applyResolvable(context, this.getAssetId(), equippable::setModel);
        Resolvable.applyResolvable(context, this.getDispensable(), equippable::setDispensable);
        Resolvable.applyResolvable(context, this.getSwappable(), equippable::setSwappable);
        Resolvable.applyResolvable(context, this.getDamageOnHurt(), equippable::setDamageOnHurt);
        Resolvable.applyResolvable(context, this.getCameraOverlay(), equippable::setCameraOverlay);

        List<EntityType> allowedEntities = Resolvable.resolveList(context, this.getAllowedEntities());
        Tag<EntityType> allowedEntityTag = Resolvable.resolve(context, this.getAllowedEntityTags());
        if (!allowedEntities.isEmpty()) {
            equippable.setAllowedEntities(allowedEntities);
        } else if (allowedEntityTag != null) {
            equippable.setAllowedEntities(allowedEntityTag);
        }
        itemMeta.setEquippable(equippable);

        itemStack.setItemMeta(itemMeta);
    }
}
