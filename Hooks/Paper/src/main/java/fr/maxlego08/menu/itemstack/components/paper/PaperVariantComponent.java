package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Art;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperVariantComponent implements VariantComponent {

    @Override
    public VariantComponent.Axolotl createAxolotl(org.bukkit.entity.Axolotl.Variant variant) {
        return new AxolotlImpl(variant);
    }

    @Override
    public VariantComponent.Cat.Collar createCatCollar(DyeColor color) {
        return new CatCollarImpl(color);
    }

    @Override
    public VariantComponent.Cat.Variant createCatVariant(org.bukkit.entity.Cat.Type variant) {
        return new CatVariantImpl(variant);
    }

    @Override
    public Chicken createChicken(org.bukkit.entity.Chicken.Variant variant) {
        return new ChickenImpl(variant);
    }

    @Override
    public Cow createCow(org.bukkit.entity.Cow.Variant variant) {
        return new CowImpl(variant);
    }

    @Override
    public Fox createFox(org.bukkit.entity.Fox.Type variant) {
        return new FoxImpl(variant);
    }

    @Override
    public Frog createFrog(org.bukkit.entity.Frog.Variant variant) {
        return new FrogImpl(variant);
    }

    @Override
    public Horse createHorse(org.bukkit.entity.Horse.Color variant) {
        return new HorseImpl(variant);
    }

    @Override
    public Llama createLlama(org.bukkit.entity.Llama.Color variant) {
        return new LlamaImpl(variant);
    }

    @Override
    public MushroomCow createMushroomCow(org.bukkit.entity.MushroomCow.Variant variant) {
        return new MushroomCowImpl(variant);
    }

    @Override
    public Painting createPainting(Art variant) {
        return new PaintingImpl(variant);
    }

    @Override
    public Parrot createParrot(org.bukkit.entity.Parrot.Variant variant) {
        return new ParrotImpl(variant);
    }

    @Override
    public Pig createPig(org.bukkit.entity.Pig.Variant variant) {
        return new PigImpl(variant);
    }

    @Override
    public Rabbit createRabbit(org.bukkit.entity.Rabbit.Type variant) {
        return new RabbitImpl(variant);
    }

    @Override
    public Salmon createSalmon(org.bukkit.entity.Salmon.Variant variant) {
        return new SalmonImpl(variant);
    }

    @Override
    public Sheep createSheep(DyeColor color) {
        return new SheepImpl(color);
    }

    @Override
    public ShulkerBox createShulkerBox(DyeColor color) {
        return new ShulkerBoxImpl(color);
    }

    @Override
    public VariantComponent.TropicalFish.BaseColor createTropicalFishBaseColor(DyeColor color) {
        return new TropicalFishBaseColorImpl(color);
    }

    @Override
    public VariantComponent.TropicalFish.PatternColor createTropicalFishPatternColor(DyeColor color) {
        return new TropicalFishPatternColorImpl(color);
    }

    @Override
    public Villager createVillager(org.bukkit.entity.Villager.Type variant) {
        return new VillagerImpl(variant);
    }

    @Override
    public VariantComponent.Wolf.Collar createWolfCollar(DyeColor color) {
        return new WolfCollarImpl(color);
    }

    @Override
    public VariantComponent.Wolf.Variant createWolfVariant(org.bukkit.entity.Wolf.Variant variant) {
        return new WolfVariantImpl(variant);
    }

    private static class AxolotlImpl extends VariantComponent.Axolotl {
        protected AxolotlImpl(org.bukkit.entity.Axolotl.@NotNull Variant variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.AXOLOTL_VARIANT, this.getVariant());
        }
    }

    private static class CatCollarImpl extends VariantComponent.Cat.Collar {
        protected CatCollarImpl(DyeColor color) {
            super(color);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.CAT_COLLAR, this.getColor());
        }
    }

    private static class CatVariantImpl extends VariantComponent.Cat.Variant {
        protected CatVariantImpl(org.bukkit.entity.Cat.Type variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.CAT_VARIANT, this.getVariant());
        }
    }

    private static class ChickenImpl extends VariantComponent.Chicken {
        protected ChickenImpl(org.bukkit.entity.Chicken.Variant variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.CHICKEN_VARIANT, this.getVariant());
        }
    }

    private static class CowImpl extends VariantComponent.Cow {
        protected CowImpl(org.bukkit.entity.Cow.Variant variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.COW_VARIANT, this.getVariant());
        }
    }

    private static class FoxImpl extends VariantComponent.Fox {
        protected FoxImpl(org.bukkit.entity.Fox.Type variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.FOX_VARIANT, this.getVariant());
        }
    }

    private static class FrogImpl extends VariantComponent.Frog {
        protected FrogImpl(org.bukkit.entity.Frog.Variant variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.FROG_VARIANT, this.getVariant());
        }
    }

    private static class HorseImpl extends VariantComponent.Horse {
        protected HorseImpl(org.bukkit.entity.Horse.Color variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.HORSE_VARIANT, this.getVariant());
        }
    }

    private static class LlamaImpl extends VariantComponent.Llama {
        protected LlamaImpl(org.bukkit.entity.Llama.Color variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.LLAMA_VARIANT, this.getVariant());
        }
    }

    private static class MushroomCowImpl extends VariantComponent.MushroomCow {
        protected MushroomCowImpl(org.bukkit.entity.MushroomCow.Variant variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.MOOSHROOM_VARIANT, this.getVariant());
        }
    }

    private static class PaintingImpl extends VariantComponent.Painting {
        protected PaintingImpl(Art variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.PAINTING_VARIANT, this.getVariant());
        }
    }

    private static class ParrotImpl extends VariantComponent.Parrot {
        protected ParrotImpl(org.bukkit.entity.Parrot.Variant variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.PARROT_VARIANT, this.getVariant());
        }
    }

    private static class PigImpl extends VariantComponent.Pig {
        protected PigImpl(org.bukkit.entity.Pig.Variant variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.PIG_VARIANT, this.getVariant());
        }
    }

    private static class RabbitImpl extends VariantComponent.Rabbit {
        protected RabbitImpl(org.bukkit.entity.Rabbit.Type variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.RABBIT_VARIANT, this.getVariant());
        }
    }

    private static class SalmonImpl extends VariantComponent.Salmon {
        protected SalmonImpl(org.bukkit.entity.Salmon.Variant variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.SALMON_SIZE, this.getVariant());
        }
    }

    private static class SheepImpl extends VariantComponent.Sheep {
        protected SheepImpl(DyeColor color) {
            super(color);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.SHEEP_COLOR, this.getColor());
        }
    }

    private static class ShulkerBoxImpl extends VariantComponent.ShulkerBox {
        protected ShulkerBoxImpl(DyeColor color) {
            super(color);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.SHULKER_COLOR, this.getColor());
        }
    }

    private static class TropicalFishBaseColorImpl extends VariantComponent.TropicalFish.BaseColor {
        protected TropicalFishBaseColorImpl(DyeColor color) {
            super(color);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.TROPICAL_FISH_BASE_COLOR, this.getColor());
        }
    }

    private static class TropicalFishPatternColorImpl extends VariantComponent.TropicalFish.PatternColor {
        protected TropicalFishPatternColorImpl(DyeColor color) {
            super(color);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR, this.getColor());
        }
    }

    private static class VillagerImpl extends VariantComponent.Villager {
        protected VillagerImpl(org.bukkit.entity.Villager.Type variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.VILLAGER_VARIANT, this.getVariant());
        }
    }

    private static class WolfCollarImpl extends VariantComponent.Wolf.Collar {
        protected WolfCollarImpl(DyeColor color) {
            super(color);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.WOLF_COLLAR, this.getColor());
        }
    }

    private static class WolfVariantImpl extends VariantComponent.Wolf.Variant {
        protected WolfVariantImpl(org.bukkit.entity.Wolf.Variant variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.WOLF_VARIANT, this.getVariant());
        }
    }
}