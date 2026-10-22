package fr.maxlego08.menu.api.registry;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.Animation;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.loader.animation.slot.SlotAnimationLoader;

public final class Registries {
    public static final RegistryType<SlotAnimationLoader> SLOT_ANIMATION = RegistryType.createStatic(ZMenuKeys.key("slot_animation"), SlotAnimationLoader.class);

    public static final RegistryType<Inventory> INVENTORY = RegistryType.createReloadable(ZMenuKeys.key("inventory"), Inventory.class);

    public static final RegistryType<Animation> ANIMATION = RegistryType.createReloadable(ZMenuKeys.key("animation"), Animation.class);

    /**
     * The requirement types a menu can use, by their {@code type}: {@code permission}, {@code placeholder}...
     */
    public static final RegistryType<PermissibleLoader> PERMISSIBLE = RegistryType.createOpen(ZMenuKeys.key("permissible"), PermissibleLoader.class);

    private Registries() {
    }
}
