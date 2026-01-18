package fr.maxlego08.menu.itemstack.components.paper;

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

    // Implementation records using Paper's DataComponentTypes
    private record AxolotlImpl(
            org.bukkit.entity.Axolotl.Variant variant
    ) implements VariantComponent.Axolotl {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.AXOLOTL_VARIANT, this.variant);
        }
    }

    private record CatCollarImpl(
            @NotNull DyeColor color
    ) implements VariantComponent.Cat.Collar {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.CAT_COLLAR, this.color);
        }
    }

    private record CatVariantImpl(
            org.bukkit.entity.Cat.Type variant
    ) implements VariantComponent.Cat.Variant {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.CAT_VARIANT, this.variant);
        }
    }

    private record ChickenImpl(
            org.bukkit.entity.Chicken.Variant variant
    ) implements VariantComponent.Chicken {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.CHICKEN_VARIANT, this.variant);
        }
    }

    private record CowImpl(
            org.bukkit.entity.Cow.Variant variant
    ) implements VariantComponent.Cow {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.COW_VARIANT, this.variant);
        }
    }

    private record FoxImpl(
            org.bukkit.entity.Fox.Type variant
    ) implements VariantComponent.Fox {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.FOX_VARIANT, this.variant);
        }
    }

    private record FrogImpl(
            org.bukkit.entity.Frog.Variant variant
    ) implements VariantComponent.Frog {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.FROG_VARIANT, this.variant);
        }
    }

    private record HorseImpl(
            org.bukkit.entity.Horse.Color variant
    ) implements VariantComponent.Horse {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.HORSE_VARIANT, this.variant);
        }
    }

    private record LlamaImpl(
            org.bukkit.entity.Llama.Color variant
    ) implements VariantComponent.Llama {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.LLAMA_VARIANT, this.variant);
        }
    }

    private record MushroomCowImpl(
            org.bukkit.entity.MushroomCow.Variant variant
    ) implements VariantComponent.MushroomCow {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.MOOSHROOM_VARIANT, this.variant);
        }
    }

    private record PaintingImpl(
            Art variant
    ) implements VariantComponent.Painting {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.PAINTING_VARIANT, this.variant);
        }
    }

    private record ParrotImpl(
            org.bukkit.entity.Parrot.Variant variant
    ) implements VariantComponent.Parrot {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.PARROT_VARIANT, this.variant);
        }
    }

    private record PigImpl(
            org.bukkit.entity.Pig.Variant variant
    ) implements VariantComponent.Pig {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.PIG_VARIANT, this.variant);
        }
    }

    private record RabbitImpl(
            org.bukkit.entity.Rabbit.Type variant
    ) implements VariantComponent.Rabbit {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.RABBIT_VARIANT, this.variant);
        }
    }

    private record SalmonImpl(
            org.bukkit.entity.Salmon.Variant variant
    ) implements VariantComponent.Salmon {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.SALMON_SIZE, this.variant);
        }
    }

    private record SheepImpl(
            @NotNull DyeColor color
    ) implements VariantComponent.Sheep {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.SHEEP_COLOR, this.color);
        }
    }

    private record ShulkerBoxImpl(
            @NotNull DyeColor color
    ) implements VariantComponent.ShulkerBox {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.SHULKER_COLOR, this.color);
        }
    }

    private record TropicalFishBaseColorImpl(
            @NotNull DyeColor color
    ) implements VariantComponent.TropicalFish.BaseColor {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.TROPICAL_FISH_BASE_COLOR, this.color);
        }
    }

    private record TropicalFishPatternColorImpl(
            @NotNull DyeColor color
    ) implements VariantComponent.TropicalFish.PatternColor {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR, this.color);
        }
    }

    private record VillagerImpl(
            org.bukkit.entity.Villager.Type variant
    ) implements VariantComponent.Villager {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.VILLAGER_VARIANT, this.variant);
        }
    }

    private record WolfCollarImpl(
            @NotNull DyeColor color
    ) implements VariantComponent.Wolf.Collar {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.WOLF_COLLAR, this.color);
        }
    }

    private record WolfVariantImpl(
            org.bukkit.entity.Wolf.Variant variant
    ) implements VariantComponent.Wolf.Variant {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            itemStack.setData(DataComponentTypes.WOLF_VARIANT, this.variant);
        }
    }
}