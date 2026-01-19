package fr.maxlego08.menu.itemstack.components.spigot;

import fr.maxlego08.menu.api.configuration.Configuration;
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

public class BukkitVariantComponent implements VariantComponent {

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


    private record AxolotlImpl(
        org.bukkit.entity.Axolotl.Variant variant
    ) implements VariantComponent.Axolotl {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean applyBucket = ItemUtil.editMeta(itemStack, AxolotlBucketMeta.class, meta -> {
                meta.setVariant(this.variant);
            });
            boolean applySpawnEgg = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.AXOLOTL, org.bukkit.entity.Axolotl.class, axolotl -> axolotl.setVariant(this.variant)));
            if (!applyBucket && !applySpawnEgg && Configuration.enableDebug)
                Logger.info("Could not apply Axolotl variant to item: " + itemStack.getType().name());
        }
    }

    private record CatCollarImpl(
            @NotNull DyeColor color
    ) implements VariantComponent.Cat.Collar {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.CAT, org.bukkit.entity.Cat.class, c -> c.setCollarColor(this.color)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Cat collar to item: " + itemStack.getType().name());
        }
    }

    private record CatVariantImpl(
            org.bukkit.entity.Cat.Type variant
    ) implements VariantComponent.Cat.Variant {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.CAT, org.bukkit.entity.Cat.class, c -> c.setCatType(this.variant)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Cat variant to item: " + itemStack.getType().name());
        }
    }

    private record ChickenImpl(
            org.bukkit.entity.Chicken.Variant variant
    ) implements VariantComponent.Chicken {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.CHICKEN, org.bukkit.entity.Chicken.class, c -> c.setVariant(this.variant)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Chicken variant to item: " + itemStack.getType().name());
        }
    }

    private record CowImpl(
            org.bukkit.entity.Cow.Variant variant
    ) implements VariantComponent.Cow {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.COW, org.bukkit.entity.Cow.class, c -> c.setVariant(this.variant)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Cow variant to item: " + itemStack.getType().name());
        }
    }

    private record FoxImpl(
            org.bukkit.entity.Fox.Type variant
    ) implements VariantComponent.Fox {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.FOX, org.bukkit.entity.Fox.class, f -> f.setFoxType(this.variant)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Fox variant to item: " + itemStack.getType().name());
        }
    }

    private record FrogImpl(
            org.bukkit.entity.Frog.Variant variant
    ) implements VariantComponent.Frog {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.FROG, org.bukkit.entity.Frog.class, f -> f.setVariant(this.variant)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Frog variant to item: " + itemStack.getType().name());
        }
    }

    private record HorseImpl(
            org.bukkit.entity.Horse.Color variant
    ) implements VariantComponent.Horse {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.HORSE, org.bukkit.entity.Horse.class, h -> h.setColor(this.variant)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Horse variant to item: " + itemStack.getType().name());
        }
    }

    private record LlamaImpl(
            org.bukkit.entity.Llama.Color variant
    ) implements VariantComponent.Llama {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.LLAMA, org.bukkit.entity.Llama.class, l -> l.setColor(this.variant)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Llama variant to item: " + itemStack.getType().name());
        }
    }

    private record MushroomCowImpl(
            org.bukkit.entity.MushroomCow.Variant variant
    ) implements VariantComponent.MushroomCow {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.MOOSHROOM, org.bukkit.entity.MushroomCow.class, mc -> mc.setVariant(this.variant)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply MushroomCow variant to item: " + itemStack.getType().name());
        }
    }

    private record PaintingImpl(
            Art variant
    ) implements VariantComponent.Painting {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.PAINTING, org.bukkit.entity.Painting.class, p -> p.setArt(this.variant)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Painting variant to item: " + itemStack.getType().name());
        }
    }

    private record ParrotImpl(
            org.bukkit.entity.Parrot.Variant variant
    ) implements VariantComponent.Parrot {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.PARROT, org.bukkit.entity.Parrot.class, p -> p.setVariant(this.variant)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Parrot variant to item: " + itemStack.getType().name());
        }
    }

    private record PigImpl(
            org.bukkit.entity.Pig.Variant variant
    ) implements VariantComponent.Pig {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.PIG, org.bukkit.entity.Pig.class, p -> p.setVariant(this.variant)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Pig variant to item: " + itemStack.getType().name());
        }
    }

    private record RabbitImpl(
            org.bukkit.entity.Rabbit.Type variant
    ) implements VariantComponent.Rabbit {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.RABBIT, org.bukkit.entity.Rabbit.class, r -> r.setRabbitType(this.variant)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Rabbit variant to item: " + itemStack.getType().name());
        }
    }

    private record SalmonImpl(
            org.bukkit.entity.Salmon.Variant variant
    ) implements VariantComponent.Salmon {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.SALMON, org.bukkit.entity.Salmon.class, s -> s.setVariant(this.variant)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Salmon variant to item: " + itemStack.getType().name());
        }
    }

    private record SheepImpl(
            @NotNull DyeColor color
    ) implements VariantComponent.Sheep {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.SHEEP, org.bukkit.entity.Sheep.class, s -> s.setColor(this.color)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Sheep color to item: " + itemStack.getType().name());
        }
    }

    private record ShulkerBoxImpl(
            @NotNull DyeColor color
    ) implements VariantComponent.ShulkerBox {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.SHULKER, org.bukkit.entity.Shulker.class, s -> s.setColor(this.color)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Shulker color to item: " + itemStack.getType().name());
        }
    }

    private record TropicalFishBaseColorImpl(
            @NotNull DyeColor color
    ) implements VariantComponent.TropicalFish.BaseColor {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.TROPICAL_FISH, org.bukkit.entity.TropicalFish.class, tf -> tf.setBodyColor(this.color)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply TropicalFish base color to item: " + itemStack.getType().name());
        }
    }

    private record TropicalFishPatternColorImpl(
            @NotNull DyeColor color
    ) implements VariantComponent.TropicalFish.PatternColor {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.TROPICAL_FISH, org.bukkit.entity.TropicalFish.class, tf -> tf.setPatternColor(this.color)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply TropicalFish pattern color to item: " + itemStack.getType().name());
        }
    }

    private record VillagerImpl(
            org.bukkit.entity.Villager.Type variant
    ) implements VariantComponent.Villager {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.VILLAGER, org.bukkit.entity.Villager.class, v -> v.setVillagerType(this.variant)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Villager variant to item: " + itemStack.getType().name());
        }
    }

    private record WolfCollarImpl(
            @NotNull DyeColor color
    ) implements VariantComponent.Wolf.Collar {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.WOLF, org.bukkit.entity.Wolf.class, w -> w.setCollarColor(this.color)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Wolf collar to item: " + itemStack.getType().name());
        }
    }

    private record WolfVariantImpl(
            org.bukkit.entity.Wolf.Variant variant
    ) implements VariantComponent.Wolf.Variant {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> applyVariantToSpawnEgg(meta, player, EntityType.WOLF, org.bukkit.entity.Wolf.class, w -> w.setVariant(this.variant)));
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Wolf variant to item: " + itemStack.getType().name());
        }
    }
}