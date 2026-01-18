package fr.maxlego08.common.loader.components;

import fr.maxlego08.common.VariantComponent;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.function.Function;

public class BukkitVariantItemComponentLoader implements VariantItemComponentLoaderFactory {

    protected final VariantComponent variantFactory;

    public BukkitVariantItemComponentLoader(VariantComponent variantFactory) {
        this.variantFactory = variantFactory;
    }

    @Override
    public @NonNull ItemComponentLoader getLoaderAxolotl() {
        return new Axolotl();
    }


    @Override
    public @NotNull ItemComponentLoader getLoaderCatCollar() {
        return new Cat(this.variantFactory).new Collar();
    }


    @Override
    public @NonNull ItemComponentLoader getLoaderCatVariant() {
        return new Cat(this.variantFactory).new Variant();
    }


    @Override
    public @NonNull ItemComponentLoader getLoaderChicken() {
        return new Chicken();
    }


    @Override
    public @NonNull ItemComponentLoader getLoaderCow() {
        return new Cow();
    }


    @Override
    public @NonNull ItemComponentLoader getLoaderFox() {
        return new Fox();
    }


    @Override
    public @NonNull ItemComponentLoader getLoaderFrog() {
        return new Frog();
    }


    @Override
    public @NonNull ItemComponentLoader getLoaderHorse() {
        return new Horse();
    }


    @Override
    public @NonNull ItemComponentLoader getLoaderLlama() {
        return new Llama();
    }


    @Override
    public @NonNull ItemComponentLoader getLoaderMushroomCow() {
        return new MushroomCow();
    }


    @Override
    public @NonNull ItemComponentLoader getLoaderPainting() {
        return new Painting();
    }


    @Override
    public @NotNull ItemComponentLoader getLoaderParrot() {
        return new Parrot();
    }


    @Override
    public @NonNull ItemComponentLoader getLoaderPig() {
        return new Pig();
    }


    @Override
    public @NonNull ItemComponentLoader getLoaderRabbit() {
        return new Rabbit();
    }


    @Override
    public @NonNull ItemComponentLoader getLoaderSalmon() {
        return new Salmon();
    }


    @Override
    public @NonNull ItemComponentLoader getLoaderSheep() {
        return new Sheep();
    }


    @Override
    public @NonNull ItemComponentLoader getLoaderShulkerBox() {
        return new ShulkerBox();
    }


    @Override
    public @NonNull ItemComponentLoader getLoaderTropicalFishBaseColor() {
        return new TropicalFish(this.variantFactory).new BaseColor();
    }

    @Override
    public @NonNull ItemComponentLoader getLoaderTropicalFishPatternColor() {
        return new TropicalFish(this.variantFactory).new PatternColor();
    }

    @Override
    public @NonNull ItemComponentLoader getLoaderVillager() {
        return new Villager();
    }


    @Override
    public @NonNull ItemComponentLoader getLoaderWolfCollar() {
        return new Wolf(this.variantFactory).new Collar();
    }

    @Override
    public @NonNull ItemComponentLoader getLoaderWolfVariant() {
        return new Wolf(this.variantFactory).new Variant();
    }

    private static abstract class EnumVariantLoader<T extends Enum<T>> extends ItemComponentLoader {
        private final Class<T> enumClass;
        private final Function<T, ItemComponent> componentFactory;

        protected EnumVariantLoader(String path, Class<T> enumClass, Function<T, ItemComponent> componentFactory) {
            super(path);
            this.enumClass = enumClass;
            this.componentFactory = componentFactory;
        }

        @Override
        public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
            path = normalizePath(path);
            String value = configuration.getString(path);
            if (value == null) return null;
            try {
                T variant = Enum.valueOf(enumClass, value.toUpperCase());
                return componentFactory.apply(variant);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    protected static abstract class RegistryVariantLoader<T extends Keyed> extends ItemComponentLoader {
        private final Registry<T> registry;
        private final Function<T, ItemComponent> componentFactory;

        protected RegistryVariantLoader(String path, Registry<T> registry, Function<T, ItemComponent> componentFactory) {
            super(path);
            this.registry = registry;
            this.componentFactory = componentFactory;
        }

        @Override
        public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
            path = normalizePath(path);
            String value = configuration.getString(path);
            if (value == null) return null;
            NamespacedKey key = NamespacedKey.fromString(value);
            if (key == null) return null;
            try {
                return componentFactory.apply(registry.getOrThrow(key));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    private static abstract class DyeColorVariantLoader extends ItemComponentLoader {
        private final Function<DyeColor, ItemComponent> componentFactory;

        protected DyeColorVariantLoader(String path, Function<DyeColor, ItemComponent> componentFactory) {
            super(path);
            this.componentFactory = componentFactory;
        }

        @Override
        public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
            path = normalizePath(path);
            String value = configuration.getString(path);
            if (value == null) return null;
            try {
                DyeColor dyeColor = DyeColor.valueOf(value.toUpperCase());
                return componentFactory.apply(dyeColor);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    private static abstract class CollarColorLoader extends AbstractColorItemComponentLoader {
        private final Function<DyeColor, ItemComponent> componentFactory;

        protected CollarColorLoader(String path, Function<DyeColor, ItemComponent> componentFactory) {
            super(path);
            this.componentFactory = componentFactory;
        }

        @Override
        public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
            path = normalizePath(path);
            Object rawColor = configuration.get(path);
            if (rawColor == null) return null;
            Color color = parseColor(rawColor);
            if (color == null) return null;
            DyeColor dyeColor = DyeColor.getByColor(color);
            if (dyeColor == null) return null;
            return componentFactory.apply(dyeColor);
        }
    }

    // Specific loaders
    public class Axolotl extends EnumVariantLoader<org.bukkit.entity.Axolotl.Variant> {
        public Axolotl() {
            super("axolotl/variant", org.bukkit.entity.Axolotl.Variant.class, variantFactory::createAxolotl);
        }
    }

    public static class Cat {
        private final VariantComponent variantFactory;

        public Cat(VariantComponent variantFactory) {
            this.variantFactory = variantFactory;
        }

        public class Collar extends CollarColorLoader {
            public Collar() {
                super("cat/collar", variantFactory::createCatCollar);
            }
        }

        public class Variant extends RegistryVariantLoader<org.bukkit.entity.Cat.Type> {
            public Variant() {
                super("cat/variant", Registry.CAT_VARIANT, variantFactory::createCatVariant);
            }
        }
    }

    public class Chicken extends RegistryVariantLoader<org.bukkit.entity.Chicken.Variant> {
        public Chicken() {
            super("chicken/variant", Registry.CHICKEN_VARIANT, variantFactory::createChicken);
        }
    }

    public class Cow extends RegistryVariantLoader<org.bukkit.entity.Cow.Variant> {
        public Cow() {
            super("cow/variant", Registry.COW_VARIANT, variantFactory::createCow);
        }
    }

    public class Fox extends EnumVariantLoader<org.bukkit.entity.Fox.Type> {
        public Fox() {
            super("fox/variant", org.bukkit.entity.Fox.Type.class, variantFactory::createFox);
        }
    }

    public class Frog extends RegistryVariantLoader<org.bukkit.entity.Frog.Variant> {
        public Frog() {
            super("frog/variant", Registry.FROG_VARIANT, variantFactory::createFrog);
        }
    }

    public class Horse extends EnumVariantLoader<org.bukkit.entity.Horse.Color> {
        public Horse() {
            super("horse/variant", org.bukkit.entity.Horse.Color.class, variantFactory::createHorse);
        }
    }

    public class Llama extends EnumVariantLoader<org.bukkit.entity.Llama.Color> {
        public Llama() {
            super("llama/variant", org.bukkit.entity.Llama.Color.class, variantFactory::createLlama);
        }
    }

    public class MushroomCow extends EnumVariantLoader<org.bukkit.entity.MushroomCow.Variant> {
        public MushroomCow() {
            super("mooshroom/variant", org.bukkit.entity.MushroomCow.Variant.class, variantFactory::createMushroomCow);
        }
    }

    public class Painting extends RegistryVariantLoader<Art> {
        public Painting() {
            super("painting/variant", Registry.ART, variantFactory::createPainting);
        }
    }

    public class Parrot extends EnumVariantLoader<org.bukkit.entity.Parrot.Variant> {
        public Parrot() {
            super("parrot/variant", org.bukkit.entity.Parrot.Variant.class, variantFactory::createParrot);
        }
    }

    public class Pig extends RegistryVariantLoader<org.bukkit.entity.Pig.Variant> {
        public Pig() {
            super("pig/variant", Registry.PIG_VARIANT, variantFactory::createPig);
        }
    }

    public class Rabbit extends EnumVariantLoader<org.bukkit.entity.Rabbit.Type> {
        public Rabbit() {
            super("rabbit/variant", org.bukkit.entity.Rabbit.Type.class, variantFactory::createRabbit);
        }
    }

    public class Salmon extends EnumVariantLoader<org.bukkit.entity.Salmon.Variant> {
        public Salmon() {
            super("salmon/size", org.bukkit.entity.Salmon.Variant.class, variantFactory::createSalmon);
        }
    }

    public class Sheep extends DyeColorVariantLoader {
        public Sheep() {
            super("sheep/color", variantFactory::createSheep);
        }
    }

    public class ShulkerBox extends DyeColorVariantLoader {
        public ShulkerBox() {
            super("shulker/color", variantFactory::createShulkerBox);
        }
    }

    public static class TropicalFish {
        private final VariantComponent variantFactory;

        public TropicalFish(VariantComponent variantFactory) {
            this.variantFactory = variantFactory;
        }

        public class BaseColor extends DyeColorVariantLoader {
            public BaseColor() {
                super("tropical_fish/base_color", variantFactory::createTropicalFishBaseColor);
            }
        }

        public class PatternColor extends DyeColorVariantLoader {
            public PatternColor() {
                super("tropical_fish/pattern_color", variantFactory::createTropicalFishPatternColor);
            }
        }
    }

    public class Villager extends RegistryVariantLoader<org.bukkit.entity.Villager.Type> {
        public Villager() {
            super("villager/variant", Registry.VILLAGER_TYPE, variantFactory::createVillager);
        }
    }

    public static class Wolf {
        private final VariantComponent variantFactory;

        public Wolf(VariantComponent variantFactory) {
            this.variantFactory = variantFactory;
        }

        public class Collar extends CollarColorLoader {
            public Collar() {
                super("wolf/collar", variantFactory::createWolfCollar);
            }
        }

        public class Variant extends RegistryVariantLoader<org.bukkit.entity.Wolf.Variant> {
            public Variant() {
                super("wolf/variant", Registry.WOLF_VARIANT, variantFactory::createWolfVariant);
            }
        }
    }
}