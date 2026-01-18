package fr.maxlego08.menu.common.interfaces;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.Art;
import org.bukkit.DyeColor;

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

    interface Axolotl extends ItemComponent {
        org.bukkit.entity.Axolotl.Variant variant();
    }

    interface Cat {
        interface Collar extends ItemComponent {
            DyeColor color();
        }

        interface Variant extends ItemComponent {
            org.bukkit.entity.Cat.Type variant();
        }
    }

    interface Chicken extends ItemComponent {
        org.bukkit.entity.Chicken.Variant variant();
    }

    interface Cow extends ItemComponent {
        org.bukkit.entity.Cow.Variant variant();
    }

    interface Fox extends ItemComponent {
        org.bukkit.entity.Fox.Type variant();
    }

    interface Frog extends ItemComponent {
        org.bukkit.entity.Frog.Variant variant();
    }

    interface Horse extends ItemComponent {
        org.bukkit.entity.Horse.Color variant();
    }

    interface Llama extends ItemComponent {
        org.bukkit.entity.Llama.Color variant();
    }

    interface MushroomCow extends ItemComponent {
        org.bukkit.entity.MushroomCow.Variant variant();
    }

    interface Painting extends ItemComponent {
        Art variant();
    }

    interface Parrot extends ItemComponent {
        org.bukkit.entity.Parrot.Variant variant();
    }

    interface Pig extends ItemComponent {
        org.bukkit.entity.Pig.Variant variant();
    }

    interface Rabbit extends ItemComponent {
        org.bukkit.entity.Rabbit.Type variant();
    }

    interface Salmon extends ItemComponent {
        org.bukkit.entity.Salmon.Variant variant();
    }

    interface Sheep extends ItemComponent {
        DyeColor color();
    }

    interface ShulkerBox extends ItemComponent {
        DyeColor color();
    }

    interface TropicalFish {
        interface BaseColor extends ItemComponent {
            DyeColor color();
        }

        interface PatternColor extends ItemComponent {
            DyeColor color();
        }
    }

    interface Villager extends ItemComponent {
        org.bukkit.entity.Villager.Type variant();
    }

    interface Wolf {
        interface Collar extends ItemComponent {
            DyeColor color();
        }

        interface Variant extends ItemComponent {
            org.bukkit.entity.Wolf.Variant variant();
        }
    }
}