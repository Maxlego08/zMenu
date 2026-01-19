package fr.maxlego08.menu;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.exceptions.ItemComponentAlreadyRegisterException;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.common.factory.VariantItemComponentLoaderFactory;
import fr.maxlego08.menu.common.utils.nms.NmsVersion;
import fr.maxlego08.menu.itemstack.components.SpigotPotionDurationScaleItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.paper.PaperVariantComponent;
import fr.maxlego08.menu.itemstack.components.spigot.BukkitVariantComponent;
import fr.maxlego08.menu.loader.components.paper.*;
import fr.maxlego08.menu.loader.components.spigot.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ZComponentsManager implements ComponentsManager {
    private final Map<String, ItemComponentLoader> components = new HashMap<>();

    private boolean isPaperAndMiniMessageEnabled(MenuPlugin plugin){
        return plugin.isPaper() && Configuration.enableMiniMessageFormat;
    }

    @Override
    public void initializeDefaultComponents(MenuPlugin plugin) {
        NmsVersion currentVersion = NmsVersion.getCurrentVersion();
        if (currentVersion.isAttributItemStack()){ // 1.20.5+
            this.initializeVariantComponents(plugin);

            this.registerComponent(new SpigotBlockStateItemComponentLoader());
            this.registerComponent(new SpigotAttributeModifiersItemComponentLoader());
            this.registerComponent(new SpigotBannerPatternsItemComponentLoader());
            this.registerComponent(new SpigotBaseColorItemComponentLoader());
            this.registerComponent(new SpigotBundleContentsItemComponentLoader(plugin));
            this.registerComponent(new SpigotChargedProjectilesItemComponentLoader(plugin));
            this.registerComponent(new SpigotContainerItemComponentLoader(plugin));
            this.registerComponent(new SpigotContainerLootItemComponentLoader());
            this.registerComponent(new SpigotCustomDataItemComponentLoader());
            this.registerComponent(new SpigotCustomModelDataItemComponentLoader());
            this.registerComponent(new SpigotDamageItemComponentLoader());
            this.registerComponent(new SpigotDamageResistantItemComponentLoader());
            this.registerComponent(new SpigotDyeColorItemComponentLoader());
            this.registerComponent(new SpigotEnchantmentGlintOverrideItemComponentLoader());
            this.registerComponent(new SpigotFireworkExplosionItemComponentLoader());
            this.registerComponent(new SpigotFireworksItemComponentLoader());
            this.registerComponent(new SpigotFoodItemComponentLoader());
            this.registerComponent(new SpigotInstrumentItemComponentLoader());
            this.registerComponent(new SpigotItemNameItemComponentLoader());
            this.registerComponent(new SpigotLodestoneTrackerItemComponentLoader());
            this.registerComponent(new SpigotLoreItemComponentLoader(plugin));
            this.registerComponent(new SpigotMapColorItemComponentLoader());
            this.registerComponent(new SpigotMapIdItemComponentLoader());
            this.registerComponent(new SpigotMaxDamageItemComponentLoader());
            this.registerComponent(new SpigotMaxStackSizeItemComponentLoader());
            this.registerComponent(new SpigotOminousBottleAmplifierItemComponentLoader());
            this.registerComponent(new SpigotPotionContentsItemComponentLoader());
            this.registerComponent(new SpigotRarityItemComponentLoader());
            this.registerComponent(new SpigotRecipesItemComponentLoader());
            this.registerComponent(new SpigotRepairCostItemComponentLoader());
            this.registerComponent(new SpigotStoredEnchantItemComponentLoader());
            this.registerComponent(new SpigotSuspiciousStewEffectsItemComponentLoader());
            this.registerComponent(new SpigotToolItemComponentLoader());
            this.registerComponent(new SpigotTrimItemComponentLoader());
            this.registerComponent(new SpigotUnbreakableItemComponentLoader());
            this.registerComponent(new SpigotWritableBookContentItemComponentLoader());
            this.registerComponent(new SpigotWrittenBookContentItemComponentLoader());
            this.registerComponent(new SpigotEnchantmentsItemComponentLoader());

            if (isPaperAndMiniMessageEnabled(plugin)) {
                this.registerComponent(new PaperCustomNameItemComponentLoader(plugin));
            }
            if (plugin.isPaper()) {
                this.registerComponent(new PaperIntangibleProjectileItemComponentLoader());
                this.registerComponent(new PaperMapDecorationsItemComponentLoader());
                this.registerComponent(new PaperNoteBlockSoundItemComponentLoader());
                this.registerComponent(new PaperPotDecorationsItemComponentLoader());
            }

            if (currentVersion.isNewItemStackAPI()){ // 1.21+
                this.registerComponent(new SpigotJukeboxPlayableItemComponentLoader());

                if (currentVersion.is1_21_2OrNewer()){ // 1.21.2+
                    this.registerComponent(new SpigotConsumableItemComponentLoader());
                    this.registerComponent(new SpigotEnchantableItemComponentLoader());
                    this.registerComponent(new SpigotEquippableItemComponentLoader());
                    this.registerComponent(new SpigotGliderItemComponentLoader());
                    this.registerComponent(new SpigotItemModelItemComponentLoader());
                    this.registerComponent(new SpigotTooltipStyleItemComponentLoader());
                    this.registerComponent(new SpigotUseCooldownItemComponentLoader());
                    this.registerComponent(new SpigotUseRemainderItemComponentLoader(plugin));

                    if (plugin.isPaper()){
                        this.registerComponent(new PaperDeathProtectionItemComponentLoader());
                        this.registerComponent(new PaperRepairableItemComponentLoader());
                    }

                    if (currentVersion.is1_21_5OrNewer()){ // 1.21.5+
                        this.registerComponent(new SpigotBlocksAttacksItemComponentLoader());
                        this.registerComponent(new SpigotBreakSoundItemComponentLoader());
                        this.registerComponent(new SpigotPotionDurationScaleItemComponentLoader());
                        this.registerComponent(plugin.isPaper() ? new PaperTooltipDisplayItemComponentLoader() : new SpigotTooltipDisplayComponentLoader()); // Bukkit does not have support for hidden components
                        this.registerComponent(new SpigotWeaponItemComponentLoader());

                        if (isPaperAndMiniMessageEnabled(plugin)){
                            this.registerComponent(new PaperProvidesBannerPatternsItemComponentLoader());
                            this.registerComponent(new PaperProvidesTrimMaterialItemComponentLoader());
                        }

                        if (currentVersion.is1_21_9OrNewer()){ // 1.21.9+

                            this.registerComponent(new SpigotProfileItemComponentLoader());

                            if (currentVersion.is1_21_11OrNewer()){ // 1.21.11+
                                this.registerComponent(new SpigotAttackRangeItemComponentLoader());
                                this.registerComponent(new SpigotDamageTypeItemComponentLoader());
                                this.registerComponent(new SpigotKineticWeaponItemComponentLoader());
                                this.registerComponent(new SpigotMinimumAttackChargeItemComponentLoader());
                                this.registerComponent(new SpigotPiercingWeaponItemComponentLoader());
                                this.registerComponent(new SpigotSwingAnimationItemComponentLoader());
                                this.registerComponent(new SpigotUseEffectsItemComponentLoader());
                            }
                        }

                    }
                }
            }
        }
    }

    private void initializeVariantComponents(MenuPlugin plugin) {
        VariantItemComponentLoaderFactory loaderFactory =
            plugin.isPaper() ? new PaperVariantItemComponentLoader(new PaperVariantComponent())
                             : new BukkitVariantItemComponentLoader(new BukkitVariantComponent());

        this.registerComponent(loaderFactory.getLoaderAxolotl());
        this.registerComponent(loaderFactory.getLoaderCatCollar());
        this.registerComponent(loaderFactory.getLoaderCatVariant());
        this.registerComponent(loaderFactory.getLoaderChicken());
        this.registerComponent(loaderFactory.getLoaderCow());
        this.registerComponent(loaderFactory.getLoaderFox());
        this.registerComponent(loaderFactory.getLoaderFrog());
        this.registerComponent(loaderFactory.getLoaderHorse());
        this.registerComponent(loaderFactory.getLoaderLlama());
        this.registerComponent(loaderFactory.getLoaderMushroomCow());
        this.registerComponent(loaderFactory.getLoaderPainting());
        this.registerComponent(loaderFactory.getLoaderParrot());
        this.registerComponent(loaderFactory.getLoaderPig());
        this.registerComponent(loaderFactory.getLoaderRabbit());
        this.registerComponent(loaderFactory.getLoaderSalmon());
        this.registerComponent(loaderFactory.getLoaderSheep());
        this.registerComponent(loaderFactory.getLoaderShulkerBox());
        this.registerComponent(loaderFactory.getLoaderTropicalFishBaseColor());
        this.registerComponent(loaderFactory.getLoaderTropicalFishPatternColor());
        this.registerComponent(loaderFactory.getLoaderVillager());
        this.registerComponent(loaderFactory.getLoaderWolfCollar());
        this.registerComponent(loaderFactory.getLoaderWolfVariant());
    }


    @Override
    public void registerComponent(@NotNull ItemComponentLoader loader) throws ItemComponentAlreadyRegisterException {
        List<String> componentNames = loader.getComponentNames();
        for (String name : componentNames) {
            if (this.components.containsKey(name)) {
                throw new ItemComponentAlreadyRegisterException("Component with name '" + name + "' is already registered.");
            }
            this.components.put(name, loader);
        }
    }

    @Override
    public @NotNull Optional<ItemComponentLoader> getLoader(@NotNull String name) {
        return Optional.ofNullable(this.components.get(name));
    }
}
