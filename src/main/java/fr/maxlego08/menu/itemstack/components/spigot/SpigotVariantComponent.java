package fr.maxlego08.menu.itemstack.components.spigot;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SpigotVariantComponent implements VariantComponent {

    @Override
    public Axolotl createAxolotl(org.bukkit.entity.Axolotl.Variant variant) {
        return new AxolotlImpl(variant);
    }

    @Override
    public Cat.Collar createCatCollar(DyeColor color) {
        return new CatCollarImpl(color);
    }

    @Override
    public Cat.Variant createCatVariant(org.bukkit.entity.Cat.Type variant) {
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
    public TropicalFish.BaseColor createTropicalFishBaseColor(DyeColor color) {
        return new TropicalFishBaseColorImpl(color);
    }

    @Override
    public TropicalFish.PatternColor createTropicalFishPatternColor(DyeColor color) {
        return new TropicalFishPatternColorImpl(color);
    }

    @Override
    public Villager createVillager(org.bukkit.entity.Villager.Type variant) {
        return new VillagerImpl(variant);
    }

    @Override
    public Wolf.Collar createWolfCollar(DyeColor color) {
        return new WolfCollarImpl(color);
    }

    @Override
    public Wolf.Variant createWolfVariant(org.bukkit.entity.Wolf.Variant variant) {
        return new WolfVariantImpl(variant);
    }

    private static <T extends Entity> void applyVariantToSpawnEgg(@NotNull SpawnEggMeta meta, @Nullable Player player, @NotNull EntityType entityType, Class<T> entityClass, Consumer<T> variantApplier) {
        EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
        if (spawnedEntity == null){
            spawnedEntity = Bukkit.getEntityFactory().createEntitySnapshot("{id:\"minecraft:" + entityType.name().toLowerCase() + "\"}");
        }
        if (player == null) {
            return;
        }
        Entity entity = spawnedEntity.createEntity(player.getWorld());
        if (entityClass.isInstance(entity)) {
            T typedEntity = entityClass.cast(entity);
            variantApplier.accept(typedEntity);
            EntitySnapshot entitySnapshot = typedEntity.createSnapshot();
            if (entitySnapshot != null) {
                meta.setSpawnedEntity(entitySnapshot);
            }
        }
    }


    private static class AxolotlImpl extends VariantComponent.Axolotl {

        protected AxolotlImpl(org.bukkit.entity.Axolotl.@NotNull Variant variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean applyBucket = ItemUtil.editMeta(itemStack, AxolotlBucketMeta.class, meta -> {
                meta.setVariant(this.getVariant());
            });
            boolean applySpawnEgg = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.AXOLOTL, org.bukkit.entity.Axolotl.class, axolotl -> axolotl.setVariant(this.getVariant())));
            if (!applyBucket && !applySpawnEgg && Configuration.enableDebug)
                Logger.info("Could not apply Axolotl variant to item: " + itemStack.getType().name());
        }
    }

    private static class CatCollarImpl extends VariantComponent.Cat.Collar {
        protected CatCollarImpl(DyeColor color) {
            super(color);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.CAT, org.bukkit.entity.Cat.class, c -> c.setCollarColor(this.getColor())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Cat collar to item: " + itemStack.getType().name());
        }
    }


    private static class CatVariantImpl extends VariantComponent.Cat.Variant {
        protected CatVariantImpl(org.bukkit.entity.Cat.Type variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.CAT, org.bukkit.entity.Cat.class, c -> c.setCatType(this.getVariant())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Cat variant to item: " + itemStack.getType().name());
        }
    }

    private static class ChickenImpl extends VariantComponent.Chicken {
        protected ChickenImpl(org.bukkit.entity.Chicken.Variant variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.CHICKEN, org.bukkit.entity.Chicken.class, c -> c.setVariant(this.getVariant())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Chicken variant to item: " + itemStack.getType().name());
        }
    }

    private static class CowImpl extends VariantComponent.Cow {
        protected CowImpl(org.bukkit.entity.Cow.Variant variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.COW, org.bukkit.entity.Cow.class, c -> c.setVariant(this.getVariant())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Cow variant to item: " + itemStack.getType().name());
        }
    }

    private static class FoxImpl extends VariantComponent.Fox {
        protected FoxImpl(org.bukkit.entity.Fox.Type variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.FOX, org.bukkit.entity.Fox.class, f -> f.setFoxType(this.getVariant())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Fox variant to item: " + itemStack.getType().name());
        }
    }

    private static class FrogImpl extends VariantComponent.Frog {
        protected FrogImpl(org.bukkit.entity.Frog.Variant variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.FROG, org.bukkit.entity.Frog.class, f -> f.setVariant(this.getVariant())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Frog variant to item: " + itemStack.getType().name());
        }
    }

    private static class HorseImpl extends VariantComponent.Horse {
        protected HorseImpl(org.bukkit.entity.Horse.Color variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.HORSE, org.bukkit.entity.Horse.class, h -> h.setColor(this.getVariant())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Horse variant to item: " + itemStack.getType().name());
        }
    }

    private static class LlamaImpl extends VariantComponent.Llama {
        protected LlamaImpl(org.bukkit.entity.Llama.Color variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.LLAMA, org.bukkit.entity.Llama.class, l -> l.setColor(this.getVariant())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Llama variant to item: " + itemStack.getType().name());
        }
    }

    private static class MushroomCowImpl extends VariantComponent.MushroomCow {
        protected MushroomCowImpl(org.bukkit.entity.MushroomCow.Variant variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.MOOSHROOM, org.bukkit.entity.MushroomCow.class, mc -> mc.setVariant(this.getVariant())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply MushroomCow variant to item: " + itemStack.getType().name());
        }
    }

    private static class PaintingImpl extends VariantComponent.Painting {
        protected PaintingImpl(Art variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.PAINTING, org.bukkit.entity.Painting.class, p -> p.setArt(this.getVariant())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Painting variant to item: " + itemStack.getType().name());
        }
    }

    private static class ParrotImpl extends VariantComponent.Parrot {
        protected ParrotImpl(org.bukkit.entity.Parrot.Variant variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.PARROT, org.bukkit.entity.Parrot.class, p -> p.setVariant(this.getVariant())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Parrot variant to item: " + itemStack.getType().name());
        }
    }

    private static class PigImpl extends VariantComponent.Pig {
        protected PigImpl(org.bukkit.entity.Pig.Variant variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.PIG, org.bukkit.entity.Pig.class, p -> p.setVariant(this.getVariant())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Pig variant to item: " + itemStack.getType().name());
        }
    }

    private static class RabbitImpl extends VariantComponent.Rabbit {
        protected RabbitImpl(org.bukkit.entity.Rabbit.Type variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.RABBIT, org.bukkit.entity.Rabbit.class, r -> r.setRabbitType(this.getVariant())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Rabbit variant to item: " + itemStack.getType().name());
        }
    }

    private static class SalmonImpl extends VariantComponent.Salmon {
        protected SalmonImpl(org.bukkit.entity.Salmon.Variant variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.SALMON, org.bukkit.entity.Salmon.class, s -> s.setVariant(this.getVariant())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Salmon variant to item: " + itemStack.getType().name());
        }
    }

    private static class SheepImpl extends VariantComponent.Sheep {
        protected SheepImpl(DyeColor color) {
            super(color);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.SHEEP, org.bukkit.entity.Sheep.class, s -> s.setColor(this.getColor())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Sheep color to item: " + itemStack.getType().name());
        }
    }

    private static class ShulkerBoxImpl extends VariantComponent.ShulkerBox {
        protected ShulkerBoxImpl(DyeColor color) {
            super(color);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.SHULKER, org.bukkit.entity.Shulker.class, s -> s.setColor(this.getColor())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Shulker color to item: " + itemStack.getType().name());
        }
    }

    private static class TropicalFishBaseColorImpl extends VariantComponent.TropicalFish.BaseColor {
        protected TropicalFishBaseColorImpl(DyeColor color) {
            super(color);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.TROPICAL_FISH, org.bukkit.entity.TropicalFish.class, tf -> tf.setBodyColor(this.getColor())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply TropicalFish base color to item: " + itemStack.getType().name());
        }
    }

    private static class TropicalFishPatternColorImpl extends VariantComponent.TropicalFish.PatternColor {
        protected TropicalFishPatternColorImpl(DyeColor color) {
            super(color);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.TROPICAL_FISH, org.bukkit.entity.TropicalFish.class, tf -> tf.setPatternColor(this.getColor())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply TropicalFish pattern color to item: " + itemStack.getType().name());
        }
    }

    private static class VillagerImpl extends VariantComponent.Villager {
        protected VillagerImpl(org.bukkit.entity.Villager.Type variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.VILLAGER, org.bukkit.entity.Villager.class, v -> v.setVillagerType(this.getVariant())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Villager variant to item: " + itemStack.getType().name());
        }
    }

    private static class WolfCollarImpl extends VariantComponent.Wolf.Collar {
        protected WolfCollarImpl(DyeColor color) {
            super(color);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.WOLF, org.bukkit.entity.Wolf.class, w -> w.setCollarColor(this.getColor())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Wolf collar to item: " + itemStack.getType().name());
        }
    }

    private static class WolfVariantImpl extends VariantComponent.Wolf.Variant {
        protected WolfVariantImpl(org.bukkit.entity.Wolf.Variant variant) {
            super(variant);
        }

        @Override
        public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.WOLF, org.bukkit.entity.Wolf.class, w -> w.setVariant(this.getVariant())));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Wolf variant to item: " + itemStack.getType().name());
        }
    }
}
