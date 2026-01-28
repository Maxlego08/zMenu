package fr.maxlego08.menu.common.factory;

import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.jetbrains.annotations.NotNull;

/**
 * Factory interface for platform-specific ItemComponentLoaders for all supported variants.
 * Each method returns a loader for a specific entity type/variant. 
 * If a loader cannot be provided, it may return null but all supported loaders should be implemented in factories.
 **/
public interface VariantItemComponentLoaderFactory {
    /**
     * Loader for Axolotl variant. Never null if the platform supports Axolotl.
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderAxolotl();
    /**
     * Loader for Cat collar color. May be null if not supported.
     *
     * @return @Nullable ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderCatCollar();
    /**
     * Loader for Cat variant. Never null if supported by the platform.
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderCatVariant();
    /**
     * Loader for Chicken variant (regardless of underlying Paper/Bukkit differences).
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderChicken();
    /**
     * Loader for Cow variant (handles Paper/Bukkit differences).
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderCow();
    /**
     * Loader for Fox variant.
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderFox();
    /**
     * Loader for Frog variant.
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderFrog();
    /**
     * Loader for Horse variant.
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderHorse();
    /**
     * Loader for Llama variant.
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderLlama();
    /**
     * Loader for MushroomCow (Mooshroom) variant.
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderMushroomCow();
    /**
     * Loader for Painting variant.
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderPainting();
    /**
     * Loader for Parrot variant.
     *
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderParrot();
    /**
     * Loader for Pig variant.
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderPig();
    /**
     * Loader for Rabbit variant.
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderRabbit();
    /**
     * Loader for Salmon variant.
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderSalmon();
    /**
     * Loader for Sheep variant (dye color).
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderSheep();
    /**
     * Loader for ShulkerBox variant (dye color).
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderShulkerBox();
    /**
     * Loader for TropicalFish base color.
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderTropicalFishBaseColor();
    /**
     * Loader for TropicalFish pattern color.
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderTropicalFishPatternColor();
    /**
     * Loader for Villager variant.
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderVillager();
    /**
     * Loader for Wolf collar color.
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderWolfCollar();
    /**
     * Loader for Wolf variant type.
     * @return @NotNull ItemComponentLoader
     */
    @NotNull ItemComponentLoader getLoaderWolfVariant();
}
