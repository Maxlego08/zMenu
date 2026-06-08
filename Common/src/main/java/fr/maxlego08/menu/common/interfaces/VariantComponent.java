package fr.maxlego08.menu.common.interfaces;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.Art;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Factory interface for creating variant components.
 * Provides factory methods to create different animal/entity variant components.
 */
public interface VariantComponent {

    Axolotl createAxolotl(org.bukkit.entity.Axolotl.Variant variant);

    Cat.Collar createCatCollar(DyeColor color);
    Cat.Variant createCatVariant(org.bukkit.entity.Cat.Type variant);

    Chicken createChicken(org.bukkit.entity.Chicken.Variant variant);

    Cow createCow(org.bukkit.entity.Cow.Variant variant);

    Fox createFox(org.bukkit.entity.Fox.Type variant);

    Frog createFrog(org.bukkit.entity.Frog.Variant variant);

    Horse createHorse(org.bukkit.entity.Horse.Color variant);

    Llama createLlama(org.bukkit.entity.Llama.Color variant);

    MushroomCow createMushroomCow(org.bukkit.entity.MushroomCow.Variant variant);

    Painting createPainting(Art variant);

    Parrot createParrot(org.bukkit.entity.Parrot.Variant variant);

    Pig createPig(org.bukkit.entity.Pig.Variant variant);

    Rabbit createRabbit(org.bukkit.entity.Rabbit.Type variant);

    Salmon createSalmon(org.bukkit.entity.Salmon.Variant variant);

    Sheep createSheep(DyeColor color);

    ShulkerBox createShulkerBox(DyeColor color);

    TropicalFish.BaseColor createTropicalFishBaseColor(DyeColor color);
    TropicalFish.PatternColor createTropicalFishPatternColor(DyeColor color);

    Villager createVillager(org.bukkit.entity.Villager.Type variant);

    Wolf.Collar createWolfCollar(DyeColor color);
    Wolf.Variant createWolfVariant(org.bukkit.entity.Wolf.Variant variant);

    abstract class Axolotl extends ItemComponent {
        protected final org.bukkit.entity.Axolotl.Variant variant;

        protected Axolotl(@NotNull org.bukkit.entity.Axolotl.Variant variant) {
            this.variant = variant;
        }

        @NotNull
        public org.bukkit.entity.Axolotl.Variant getVariant() {
            return this.variant;
        }

        public abstract void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player);
    }

    interface Cat {
        abstract class Collar extends ItemComponent {
            protected final DyeColor color;

            protected Collar(@NotNull DyeColor color) {
                this.color = color;
            }

            @NotNull
            public DyeColor getColor() {
                return this.color;
            }
        }

        abstract class Variant extends ItemComponent {
            protected final org.bukkit.entity.Cat.Type variant;

            protected Variant(@NotNull org.bukkit.entity.Cat.Type variant) {
                this.variant = variant;
            }

            @NotNull
            public org.bukkit.entity.Cat.Type getVariant() {
                return this.variant;
            }
        }
    }

    abstract class Chicken extends ItemComponent {
        protected final org.bukkit.entity.Chicken.Variant variant;

        protected Chicken(@NotNull org.bukkit.entity.Chicken.Variant variant) {
            this.variant = variant;
        }

        @NotNull
        public org.bukkit.entity.Chicken.Variant getVariant() {
            return this.variant;
        }
    }

    abstract class Cow extends ItemComponent {
        protected final org.bukkit.entity.Cow.Variant variant;

        protected Cow(@NotNull org.bukkit.entity.Cow.Variant variant) {
            this.variant = variant;
        }

        @NotNull
        public org.bukkit.entity.Cow.Variant getVariant() {
            return this.variant;
        }
    }

    abstract class Fox extends ItemComponent {
        protected final org.bukkit.entity.Fox.Type variant;

        protected Fox(@NotNull org.bukkit.entity.Fox.Type variant) {
            this.variant = variant;
        }

        @NotNull
        public org.bukkit.entity.Fox.Type getVariant() {
            return this.variant;
        }
    }

    abstract class Frog extends ItemComponent {
        protected final org.bukkit.entity.Frog.Variant variant;

        protected Frog(@NotNull org.bukkit.entity.Frog.Variant variant) {
            this.variant = variant;
        }

        @NotNull
        public org.bukkit.entity.Frog.Variant getVariant() {
            return this.variant;
        }
    }

    abstract class Horse extends ItemComponent {
        protected final org.bukkit.entity.Horse.Color variant;

        protected Horse(@NotNull org.bukkit.entity.Horse.Color variant) {
            this.variant = variant;
        }

        @NotNull
        public org.bukkit.entity.Horse.Color getVariant() {
            return this.variant;
        }
    }

    abstract class Llama extends ItemComponent {
        protected final org.bukkit.entity.Llama.Color variant;

        protected Llama(@NotNull org.bukkit.entity.Llama.Color variant) {
            this.variant = variant;
        }

        @NotNull
        public org.bukkit.entity.Llama.Color getVariant() {
            return this.variant;
        }
    }

    abstract class MushroomCow extends ItemComponent {
        protected final org.bukkit.entity.MushroomCow.Variant variant;

        protected MushroomCow(@NotNull org.bukkit.entity.MushroomCow.Variant variant) {
            this.variant = variant;
        }

        @NotNull
        public org.bukkit.entity.MushroomCow.Variant getVariant() {
            return this.variant;
        }
    }

    abstract class Painting extends ItemComponent {
        protected final Art variant;

        protected Painting(@NotNull Art variant) {
            this.variant = variant;
        }

        @NotNull
        public Art getVariant() {
            return this.variant;
        }
    }

    abstract class Parrot extends ItemComponent {
        protected final org.bukkit.entity.Parrot.Variant variant;

        protected Parrot(@NotNull org.bukkit.entity.Parrot.Variant variant) {
            this.variant = variant;
        }

        @NotNull
        public org.bukkit.entity.Parrot.Variant getVariant() {
            return this.variant;
        }
    }

    abstract class Pig extends ItemComponent {
        protected final org.bukkit.entity.Pig.Variant variant;

        protected Pig(@NotNull org.bukkit.entity.Pig.Variant variant) {
            this.variant = variant;
        }

        @NotNull
        public org.bukkit.entity.Pig.Variant getVariant() {
            return this.variant;
        }
    }

    abstract class Rabbit extends ItemComponent {
        protected final org.bukkit.entity.Rabbit.Type variant;

        protected Rabbit(@NotNull org.bukkit.entity.Rabbit.Type variant) {
            this.variant = variant;
        }

        @NotNull
        public org.bukkit.entity.Rabbit.Type getVariant() {
            return this.variant;
        }
    }

    abstract class Salmon extends ItemComponent {
        protected final org.bukkit.entity.Salmon.Variant variant;

        protected Salmon(@NotNull org.bukkit.entity.Salmon.Variant variant) {
            this.variant = variant;
        }

        @NotNull
        public org.bukkit.entity.Salmon.Variant getVariant() {
            return this.variant;
        }
    }

    abstract class Sheep extends ItemComponent {
        protected final DyeColor color;

        protected Sheep(@NotNull DyeColor color) {
            this.color = color;
        }

        @NotNull
        public DyeColor getColor() {
            return this.color;
        }
    }

    abstract class ShulkerBox extends ItemComponent {
        protected final DyeColor color;

        protected ShulkerBox(@NotNull DyeColor color) {
            this.color = color;
        }

        @NotNull
        public DyeColor getColor() {
            return this.color;
        }
    }

    interface TropicalFish {
        abstract class BaseColor extends ItemComponent {
            protected final DyeColor color;

            protected BaseColor(@NotNull DyeColor color) {
                this.color = color;
            }

            @NotNull
            public DyeColor getColor() {
                return this.color;
            }
        }

        abstract class PatternColor extends ItemComponent {
            protected final DyeColor color;

            protected PatternColor(@NotNull DyeColor color) {
                this.color = color;
            }

            @NotNull
            public DyeColor getColor() {
                return this.color;
            }
        }
    }

    abstract class Villager extends ItemComponent {
        protected final org.bukkit.entity.Villager.Type variant;

        protected Villager(@NotNull org.bukkit.entity.Villager.Type variant) {
            this.variant = variant;
        }

        @NotNull
        public org.bukkit.entity.Villager.Type getVariant() {
            return this.variant;
        }
    }

    interface Wolf {
        abstract class Collar extends ItemComponent {
            protected final DyeColor color;

            protected Collar(@NotNull DyeColor color) {
                this.color = color;
            }

            @NotNull
            public DyeColor getColor() {
                return this.color;
            }
        }

        abstract class Variant extends ItemComponent {
            protected final org.bukkit.entity.Wolf.Variant variant;

            protected Variant(@NotNull org.bukkit.entity.Wolf.Variant variant) {
                this.variant = variant;
            }

            @NotNull
            public org.bukkit.entity.Wolf.Variant getVariant() {
                return this.variant;
            }
        }
    }
}