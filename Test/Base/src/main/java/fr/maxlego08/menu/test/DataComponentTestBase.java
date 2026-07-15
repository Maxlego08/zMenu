package fr.maxlego08.menu.test;

import fr.maxlego08.menu.api.utils.PlatformType;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class DataComponentTestBase {

    protected static void checkComponents(Set<String> registered) {
        MinecraftVersion current = MinecraftVersion.getCurrentVersion();

        List<String> missing = new ArrayList<>();
        List<String> present = new ArrayList<>();

        for (DataComponent component : DataComponent.values()) {
            if (component.getSince().isAfter(current)) {
                continue;
            }

            String expected = component.getName().replace('_', '-');

            boolean found = registered.contains(expected)
                    || registered.contains("minecraft:" + expected);

            if (found) {
                present.add(expected);
            } else {
                missing.add(expected + (component.getSince().isAfter(MinecraftVersion.parse("1.20.5")) ? " (since " + component.getSince() + ")" : ""));
            }
        }

        System.out.println("=== Registered component count: " + registered.size());
        System.out.println("=== Present: " + present.size());
        System.out.println("=== Missing: " + missing.size());

        if (!missing.isEmpty()) {
            throw new AssertionError("Missing " + missing.size() + " component(s): " + missing + " for platform type " + PlatformType.get());
        }
    }

    private enum DataComponent {
        ATTACK_RANGE("attack_range", "1.21.11"),
        ATTRIBUTE_MODIFIERS("attribute_modifiers"),
        BANNER_PATTERNS("banner_patterns"),
        BASE_COLOR("base_color"),
//         BEES("bees"),
//         BLOCK_ENTITY_DATA("block_entity_data"),
        BLOCK_STATE("block_state"),
//         BLOCK_TRANSFORMER("block_transformer", "26.3"),
        BLOCKS_ATTACKS("blocks_attacks", "1.21.5"),
        BREAK_SOUND("break_sound", "1.21.5"),
//         BUCKET_ENTITY_DATA("bucket_entity_data"),
        BUNDLE_CONTENTS("bundle_contents"),
        CAN_BREAK("can_break"),
        CAN_PLACE_ON("can_place_on"),
        CHARGED_PROJECTILES("charged_projectiles"),
        CONSUMABLE("consumable", "1.21.2"),
        CONTAINER("container"),
        CONTAINER_LOOT("container_loot"),
        CUSTOM_DATA("custom_data"),
        CUSTOM_MODEL_DATA("custom_model_data"),
        CUSTOM_NAME("custom_name"),
        DAMAGE("damage"),
        DAMAGE_RESISTANT("damage_resistant", "1.21.2"),
        DAMAGE_TYPE("damage_type", "1.21.11"),
        DEATH_PROTECTION("death_protection", "1.21.2"),
//         DEBUG_STICK_STATE("debug_stick_state"),
        DYE("dye", "26.1"),
        DYED_COLOR("dyed_color"),
        ENCHANTABLE("enchantable", "1.21.2"),
        ENCHANTMENT_GLINT_OVERRIDE("enchantment_glint_override"),
        ENCHANTMENTS("enchantments"),
//         ENTITY_DATA("entity_data"),
        EQUIPPABLE("equippable", "1.21.2"),
        FIREWORK_EXPLOSION("firework_explosion"),
        FIREWORKS("fireworks"),
        FOOD("food"),
        GLIDER("glider", "1.21.2"),
        INSTRUMENT("instrument"),
        INTANGIBLE_PROJECTILE("intangible_projectile"),
        ITEM_MODEL("item_model", "1.21.2"),
        ITEM_NAME("item_name"),
        JUKEBOX_PLAYABLE("jukebox_playable", "1.21"),
        KINETIC_WEAPON("kinetic_weapon", "1.21.11"),
//         LOCK("lock"),
        LODESTONE_TRACKER("lodestone_tracker"),
        LORE("lore"),
        MAP_COLOR("map_color"),
        MAP_DECORATIONS("map_decorations"),
        MAP_ID("map_id"),
        MAX_DAMAGE("max_damage"),
        MAX_STACK_SIZE("max_stack_size"),
        MINIMUM_ATTACK_CHARGE("minimum_attack_charge", "1.21.11"),
        NOTE_BLOCK_SOUND("note_block_sound"),
        OMINOUS_BOTTLE_AMPLIFIER("ominous_bottle_amplifier"),
        PIERCING_WEAPON("piercing_weapon", "1.21.11"),
        POT_DECORATIONS("pot_decorations"),
        POTION_CONTENTS("potion_contents"),
        POTION_DURATION_SCALE("potion_duration_scale", "1.21.5"),
        PROFILE("profile"),
        PROVIDES_BANNER_PATTERNS("provides_banner_patterns", "1.21.5"),
        PROVIDES_TRIM_MATERIAL("provides_trim_material", "1.21.5"),
        RARITY("rarity"),
        RECIPES("recipes"),
        REPAIR_COST("repair_cost"),
        REPAIRABLE("repairable", "1.21.2"),
        STORED_ENCHANTMENTS("stored_enchantments"),
        SULFUR_CUBE_CONTENT("sulfur_cube_content", "26.2"),
        SUSPICIOUS_STEW_EFFECTS("suspicious_stew_effects"),
        SWING_ANIMATION("swing_animation", "1.21.11"),
        TOOL("tool"),
        TOOLTIP_DISPLAY("tooltip_display", "1.21.5"),
        TOOLTIP_STYLE("tooltip_style", "1.21.2"),
        TRIM("trim"),
        UNBREAKABLE("unbreakable"),
        USE_COOLDOWN("use_cooldown", "1.21.2"),
        USE_EFFECTS("use_effects", "1.21.11"),
        USE_REMAINDER("use_remainder", "1.21.2"),
        WEAPON("weapon", "1.21.5"),
        WRITABLE_BOOK_CONTENT("writable_book_content"),
        WRITTEN_BOOK_CONTENT("written_book_content"),

        // Entity variant components (all introduced in 1.21.5 except where noted)
        AXOLOTL_VARIANT("axolotl/variant", "1.21.5"),
        CAT_COLLAR("cat/collar", "1.21.5"),
        CAT_VARIANT("cat/variant", "1.21.5"),
        CHICKEN_VARIANT("chicken/variant", "1.21.5"),
        COW_VARIANT("cow/variant", "1.21.5"),
        FOX_VARIANT("fox/variant", "1.21.5"),
        FROG_VARIANT("frog/variant", "1.21.5"),
        HORSE_VARIANT("horse/variant", "1.21.5"),
        LLAMA_VARIANT("llama/variant", "1.21.5"),
        MOOSHROOM_VARIANT("mooshroom/variant", "1.21.5"),
        PAINTING_VARIANT("painting/variant", "1.21.5"),
        PARROT_VARIANT("parrot/variant", "1.21.5"),
        PIG_VARIANT("pig/variant", "1.21.5"),
        RABBIT_VARIANT("rabbit/variant", "1.21.5"),
        SALMON_SIZE("salmon/size", "1.21.5"),
        SHEEP_COLOR("sheep/color", "1.21.5"),
        SHULKER_COLOR("shulker/color", "1.21.5"),
        TROPICAL_FISH_BASE_COLOR("tropical_fish/base_color", "1.21.5"),
        TROPICAL_FISH_PATTERN("tropical_fish/pattern", "1.21.5"),
        TROPICAL_FISH_PATTERN_COLOR("tropical_fish/pattern_color", "1.21.5"),
        VILLAGER_VARIANT("villager/variant", "1.21.5"),
        WOLF_COLLAR("wolf/collar", "1.21.5"),
        WOLF_SOUND_VARIANT("wolf/sound_variant", "1.21.5"),
        WOLF_VARIANT("wolf/variant", "1.21.5"),
        ;

        private final String name;
        private final MinecraftVersion since;

        DataComponent(@NotNull String name) {
            this(name, "1.20.5");
        }

        DataComponent(@NotNull String name, @NotNull String since) {
            this.name = name;
            this.since = MinecraftVersion.parse(since);
        }

        public @NotNull String getName() {
            return this.name;
        }

        public @NotNull MinecraftVersion getSince() {
            return this.since;
        }

    }
}
