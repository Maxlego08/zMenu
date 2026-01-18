package fr.maxlego08.common.items.components;

import fr.maxlego08.common.interfaces.VariantComponent;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Art;
import org.bukkit.DyeColor;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    // Implementation records
    private record AxolotlImpl(
            org.bukkit.entity.Axolotl.Variant variant
    ) implements VariantComponent.Axolotl {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean applyBucket = ItemUtil.editMeta(itemStack, AxolotlBucketMeta.class, meta -> {
                meta.setVariant(this.variant);
            });
            boolean applySpawnEgg = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.Axolotl axolotl) {
                    axolotl.setVariant(this.variant);
                }
            });
            if (!applyBucket && !applySpawnEgg && Configuration.enableDebug)
                Logger.info("Could not apply Axolotl variant to item: " + itemStack.getType().name());
        }
    }

    private record CatCollarImpl(
            @NotNull DyeColor color
    ) implements VariantComponent.Cat.Collar {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.Cat cat) {
                    cat.setCollarColor(this.color);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Cat collar to item: " + itemStack.getType().name());
        }
    }

    private record CatVariantImpl(
            org.bukkit.entity.Cat.Type variant
    ) implements VariantComponent.Cat.Variant {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.Cat cat) {
                    cat.setCatType(this.variant);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Cat variant to item: " + itemStack.getType().name());
        }
    }

    private record ChickenImpl(
            org.bukkit.entity.Chicken.Variant variant
    ) implements VariantComponent.Chicken {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.Chicken chicken) {
                    chicken.setVariant(this.variant);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Chicken variant to item: " + itemStack.getType().name());
        }
    }

    private record CowImpl(
            org.bukkit.entity.Cow.Variant variant
    ) implements VariantComponent.Cow {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.Cow cow) {
                    cow.setVariant(this.variant);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Cow variant to item: " + itemStack.getType().name());
        }
    }

    private record FoxImpl(
            org.bukkit.entity.Fox.Type variant
    ) implements VariantComponent.Fox {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.Fox fox) {
                    fox.setFoxType(this.variant);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Fox variant to item: " + itemStack.getType().name());
        }
    }

    private record FrogImpl(
            org.bukkit.entity.Frog.Variant variant
    ) implements VariantComponent.Frog {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.Frog frog) {
                    frog.setVariant(this.variant);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Frog variant to item: " + itemStack.getType().name());
        }
    }

    private record HorseImpl(
            org.bukkit.entity.Horse.Color variant
    ) implements VariantComponent.Horse {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.Horse horse) {
                    horse.setColor(this.variant);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Horse variant to item: " + itemStack.getType().name());
        }
    }

    private record LlamaImpl(
            org.bukkit.entity.Llama.Color variant
    ) implements VariantComponent.Llama {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.Llama llama) {
                    llama.setColor(this.variant);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Llama variant to item: " + itemStack.getType().name());
        }
    }

    private record MushroomCowImpl(
            org.bukkit.entity.MushroomCow.Variant variant
    ) implements VariantComponent.MushroomCow {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.MushroomCow mushroomCow) {
                    mushroomCow.setVariant(this.variant);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply MushroomCow variant to item: " + itemStack.getType().name());
        }
    }

    private record PaintingImpl(
            Art variant
    ) implements VariantComponent.Painting {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.Painting painting) {
                    painting.setArt(this.variant);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Painting variant to item: " + itemStack.getType().name());
        }
    }

    private record ParrotImpl(
            org.bukkit.entity.Parrot.Variant variant
    ) implements VariantComponent.Parrot {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.Parrot parrot) {
                    parrot.setVariant(this.variant);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Parrot variant to item: " + itemStack.getType().name());
        }
    }

    private record PigImpl(
            org.bukkit.entity.Pig.Variant variant
    ) implements VariantComponent.Pig {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.Pig pig) {
                    pig.setVariant(this.variant);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Pig variant to item: " + itemStack.getType().name());
        }
    }

    private record RabbitImpl(
            org.bukkit.entity.Rabbit.Type variant
    ) implements VariantComponent.Rabbit {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.Rabbit rabbit) {
                    rabbit.setRabbitType(this.variant);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Rabbit variant to item: " + itemStack.getType().name());
        }
    }

    private record SalmonImpl(
            org.bukkit.entity.Salmon.Variant variant
    ) implements VariantComponent.Salmon {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.Salmon salmon) {
                    salmon.setVariant(this.variant);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Salmon variant to item: " + itemStack.getType().name());
        }
    }

    private record SheepImpl(
            @NotNull DyeColor color
    ) implements VariantComponent.Sheep {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.Sheep sheep) {
                    sheep.setColor(this.color);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Sheep color to item: " + itemStack.getType().name());
        }
    }

    private record ShulkerBoxImpl(
            @NotNull DyeColor color
    ) implements VariantComponent.ShulkerBox {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.Shulker shulker) {
                    shulker.setColor(this.color);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Shulker color to item: " + itemStack.getType().name());
        }
    }

    private record TropicalFishBaseColorImpl(
            @NotNull DyeColor color
    ) implements VariantComponent.TropicalFish.BaseColor {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.TropicalFish tropicalFish) {
                    tropicalFish.setBodyColor(this.color);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply TropicalFish base color to item: " + itemStack.getType().name());
        }
    }

    private record TropicalFishPatternColorImpl(
            @NotNull DyeColor color
    ) implements VariantComponent.TropicalFish.PatternColor {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.TropicalFish tropicalFish) {
                    tropicalFish.setPatternColor(this.color);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply TropicalFish pattern color to item: " + itemStack.getType().name());
        }
    }

    private record VillagerImpl(
            org.bukkit.entity.Villager.Type variant
    ) implements VariantComponent.Villager {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.Villager villager) {
                    villager.setVillagerType(this.variant);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Villager variant to item: " + itemStack.getType().name());
        }
    }

    private record WolfCollarImpl(
            @NotNull DyeColor color
    ) implements VariantComponent.Wolf.Collar {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.Wolf wolf) {
                    wolf.setCollarColor(this.color);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Wolf collar to item: " + itemStack.getType().name());
        }
    }

    private record WolfVariantImpl(
            org.bukkit.entity.Wolf.Variant variant
    ) implements VariantComponent.Wolf.Variant {
        @Override
        public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
            boolean apply = ItemUtil.editMeta(itemStack, SpawnEggMeta.class, meta -> {
                EntitySnapshot spawnedEntity = meta.getSpawnedEntity();
                if (spawnedEntity instanceof org.bukkit.entity.Wolf wolf) {
                    wolf.setVariant(this.variant);
                }
            });
            if (!apply && Configuration.enableDebug)
                Logger.info("Could not apply Wolf variant to item: " + itemStack.getType().name());
        }
    }
}