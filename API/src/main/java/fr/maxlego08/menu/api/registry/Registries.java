package fr.maxlego08.menu.api.registry;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.Animation;
import fr.maxlego08.menu.api.command.CommandArgumentValidator;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.rules.loader.RuleLoader;
import fr.maxlego08.menu.api.loader.animation.slot.SlotAnimationLoader;

public final class Registries {
    public static final RegistryType<SlotAnimationLoader> SLOT_ANIMATION = RegistryType.createStatic(ZMenuKeys.key("slot_animation"), SlotAnimationLoader.class);

    public static final RegistryType<Inventory> INVENTORY = RegistryType.createReloadable(ZMenuKeys.key("inventory"), Inventory.class);

    public static final RegistryType<Animation> ANIMATION = RegistryType.createReloadable(ZMenuKeys.key("animation"), Animation.class);

    /**
     * The requirement types a menu can use, by their {@code type}: {@code permission}, {@code placeholder}...
     */
    public static final RegistryType<PermissibleLoader> PERMISSIBLE = RegistryType.createOpen(ZMenuKeys.key("permissible"), PermissibleLoader.class);

    /**
     * The action types a menu can run, by their {@code type}: {@code message}, {@code console_command}...
     */
    public static final RegistryType<ActionLoader> ACTION = RegistryType.createOpen(ZMenuKeys.key("action"), ActionLoader.class);

    /**
     * The button types a menu can use, by their {@code type}: {@code next}, {@code inventory}...
     */
    public static final RegistryType<ButtonLoader> BUTTON = RegistryType.createOpen(ZMenuKeys.key("button"), ButtonLoader.class);

    /**
     * The item rule types, by their {@code type}: {@code material}, {@code tag}, {@code and}...
     */
    public static final RegistryType<RuleLoader> RULE = RegistryType.createOpen(ZMenuKeys.key("rule"), RuleLoader.class);

    /**
     * The ways to compare two items, by name: {@code full}, {@code material}, {@code lore}...
     */
    public static final RegistryType<ItemStackSimilar> ITEM_VERIFICATION = RegistryType.createOpen(ZMenuKeys.key("item_verification"), ItemStackSimilar.class);

    /**
     * The loaders of items from other plugins, by the prefix of a material: {@code itemsadder},
     * {@code hdb}, {@code base64}...
     */
    public static final RegistryType<MaterialLoader> MATERIAL_LOADER = RegistryType.createOpen(ZMenuKeys.key("material_loader"), MaterialLoader.class);

    /**
     * The item components an item can set, by name: {@code minecraft:lore} (also {@code lore}),
     * {@code myplugin:glow}...
     */
    public static final RegistryType<ItemComponentLoader> ITEM_COMPONENT = RegistryType.createOpen(ZMenuKeys.key("item_component"), ItemComponentLoader.class);

    /**
     * The argument types a custom command can check, by their {@code type}: {@code player},
     * {@code integer}, {@code material}...
     */
    public static final RegistryType<CommandArgumentValidator> COMMAND_ARGUMENT_VALIDATOR = RegistryType.createOpen(ZMenuKeys.key("command_argument_validator"), CommandArgumentValidator.class);

    private Registries() {
    }
}
