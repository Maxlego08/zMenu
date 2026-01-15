package fr.maxlego08.menu;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.loader.components.*;
import fr.maxlego08.menu.zcore.utils.nms.NmsVersion;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ZComponentsManager implements ComponentsManager {
    private final Map<String, ItemComponentLoader> components = new HashMap<>();

    ZComponentsManager(MenuPlugin plugin){
        NmsVersion currentVersion = NmsVersion.getCurrentVersion();
        if (currentVersion.isAttributItemStack()){

            this.registerComponent(new BlockStateItemComponentLoader());
            this.registerComponent(new AttributeModifiersItemComponentLoader());
            this.registerComponent(new BannerPatternsItemComponentLoader());
            this.registerComponent(new BaseColorItemComponentLoader());
            this.registerComponent(new BundleContentsItemComponentLoader(plugin));
            this.registerComponent(new ChargedProjectilesItemComponentLoader(plugin));
            this.registerComponent(new ContainerItemComponentLoader(plugin));
            this.registerComponent(new ContainerLootItemComponentLoader());
            this.registerComponent(new CustomDataItemComponentLoader());

            if (currentVersion.is1_21_2OrNewer()){
                this.registerComponent(new ConsumableItemComponentLoader());

                if (currentVersion.is1_21_5OrNewer()){
                    this.registerComponent(new BlocksAttacksItemComponentLoader());
                    this.registerComponent(new BreakSoundItemComponentLoader());

                    if (currentVersion.is1_21_11OrNewer()){
                        this.registerComponent(new AttackRangeItemComponentLoader());
                    }
                }
            }
        }
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
