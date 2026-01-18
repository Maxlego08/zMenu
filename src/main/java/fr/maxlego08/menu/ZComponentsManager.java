package fr.maxlego08.menu;

import fr.maxlego08.common.items.components.BukkitVariantComponent;
import fr.maxlego08.common.loader.components.BukkitVariantItemComponentLoader;
import fr.maxlego08.common.loader.components.VariantItemComponentLoaderFactory;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.PotionDurationScaleItemComponentLoader;
import fr.maxlego08.menu.itemstack.paper.components.PaperVariantComponent;
import fr.maxlego08.menu.loader.components.*;
import fr.maxlego08.menu.zcore.utils.nms.NmsVersion;
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

            this.registerComponent(new BlockStateItemComponentLoader());
            this.registerComponent(new AttributeModifiersItemComponentLoader());
            this.registerComponent(new BannerPatternsItemComponentLoader());
            this.registerComponent(new BaseColorItemComponentLoader());
            this.registerComponent(new BundleContentsItemComponentLoader(plugin));
            this.registerComponent(new ChargedProjectilesItemComponentLoader(plugin));
            this.registerComponent(new ContainerItemComponentLoader(plugin));
            this.registerComponent(new ContainerLootItemComponentLoader());
            this.registerComponent(new CustomDataItemComponentLoader());
            this.registerComponent(new CustomModelDataItemComponentLoader());
            this.registerComponent(new DamageItemComponentLoader());
            this.registerComponent(new DamageResistantItemComponentLoader());
            this.registerComponent(new DyeColorItemComponentLoader());
            this.registerComponent(new EnchantmentGlintOverrideItemComponentLoader());
            this.registerComponent(new FireworkExplosionItemComponentLoader());
            this.registerComponent(new FireworksItemComponentLoader());
            this.registerComponent(new FoodItemComponentLoader());
            this.registerComponent(new InstrumentItemComponentLoader());
            this.registerComponent(new ItemNameItemComponentLoader());
            this.registerComponent(new LodestoneTrackerItemComponentLoader());
            this.registerComponent(new LoreItemComponentLoader(plugin));
            this.registerComponent(new MapColorItemComponentLoader());
            this.registerComponent(new MapIdItemComponentLoader());
            this.registerComponent(new MaxDamageItemComponentLoader());
            this.registerComponent(new MaxStackSizeItemComponentLoader());
            this.registerComponent(new OminousBottleAmplifierItemComponentLoader());
            this.registerComponent(new PotionContentsItemComponentLoader());
            this.registerComponent(new RarityItemComponentLoader());
            this.registerComponent(new RecipesItemComponentLoader());
            this.registerComponent(new RepairCostItemComponentLoader());
            this.registerComponent(new StoredEnchantItemComponentLoader());
            this.registerComponent(new SuspiciousStewEffectsItemComponentLoader());
            this.registerComponent(new ToolItemComponentLoader());
            this.registerComponent(new TrimItemComponentLoader());
            this.registerComponent(new UnbreakableItemComponentLoader());
            this.registerComponent(new WritableBookContentItemComponentLoader());
            this.registerComponent(new WrittenBookContentItemComponentLoader());

            if (isPaperAndMiniMessageEnabled(plugin)){
                this.registerComponent(new PaperCustomNameItemComponentLoader(plugin));
                this.registerComponent(new PaperIntangibleProjectileItemComponentLoader());
                this.registerComponent(new PaperMapDecorationsItemComponentLoader());
                this.registerComponent(new PaperNoteBlockSoundItemComponentLoader());
                this.registerComponent(new PaperPotDecorationsItemComponentLoader());
            }

            if (currentVersion.isNewItemStackAPI()){ // 1.21+
                this.registerComponent(new JukeboxPlayableItemComponentLoader());

                if (currentVersion.is1_21_2OrNewer()){ // 1.21.2+
                    this.registerComponent(new ConsumableItemComponentLoader());
                    this.registerComponent(new EnchantableItemComponentLoader());
                    this.registerComponent(new EquippableItemComponentLoader());
                    this.registerComponent(new GliderItemComponentLoader());
                    this.registerComponent(new ItemModelItemComponentLoader());
                    this.registerComponent(new TooltipStyleItemComponentLoader());
                    this.registerComponent(new UseCooldownItemComponentLoader());
                    this.registerComponent(new UseRemainderItemComponentLoader(plugin));

                    if (isPaperAndMiniMessageEnabled(plugin)){
                        this.registerComponent(new PaperDeathProtectionItemComponentLoader());
                        this.registerComponent(new PaperRepairableItemComponentLoader());
                    }

                    if (currentVersion.is1_21_5OrNewer()){ // 1.21.5+
                        this.registerComponent(new BlocksAttacksItemComponentLoader());
                        this.registerComponent(new BreakSoundItemComponentLoader());
                        this.registerComponent(new PotionDurationScaleItemComponentLoader());
                        this.registerComponent(plugin.isPaper() ? new PaperTooltipDisplayItemComponentLoader() : new BukkitTooltipDisplayComponentLoader()); // Bukkit does not have support for hidden components
                        this.registerComponent(new WeaponItemComponentLoader());

                        if (isPaperAndMiniMessageEnabled(plugin)){
                            this.registerComponent(new PaperProvidesBannerPatternsItemComponentLoader());
                            this.registerComponent(new PaperProvidesTrimMaterialItemComponentLoader());
                        }

                        if (currentVersion.is1_21_9OrNewer()){ // 1.21.9+

                            this.registerComponent(new ProfileItemComponentLoader());

                            if (currentVersion.is1_21_11OrNewer()){ // 1.21.11+
                                this.registerComponent(new AttackRangeItemComponentLoader());
                                this.registerComponent(new DamageTypeItemComponentLoader());
                                this.registerComponent(new KineticWeaponItemComponentLoader());
                                this.registerComponent(new MinimumAttackChargeItemComponentLoader());
                                this.registerComponent(new PiercingWeaponItemComponentLoader());
                                this.registerComponent(new SwingAnimationItemComponentLoader());
                                this.registerComponent(new UseEffectsItemComponentLoader());
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
    public void registerComponent(@NotNull ItemComponentLoader loader) {
        List<String> componentNames = loader.getComponentNames();
        for (String name : componentNames) {
            if (this.components.containsKey(name)) {
                throw new IllegalArgumentException("Component with name '" + name + "' is already registered.");
            }
            this.components.put(name, loader);
        }
    }

    @Override
    public @NotNull Optional<ItemComponentLoader> getLoader(@NotNull String name) {
        return Optional.ofNullable(this.components.get(name));
    }
}
