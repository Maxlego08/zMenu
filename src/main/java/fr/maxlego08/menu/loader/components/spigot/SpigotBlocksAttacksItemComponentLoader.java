package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.ResolvableDamageReduction;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.BlocksAttacksComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableSound;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoComponentLoader
@SinceVersion("1.21.5")
public class SpigotBlocksAttacksItemComponentLoader extends ItemComponentLoader {

    public SpigotBlocksAttacksItemComponentLoader(){
        super("blocks-attacks");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) {
            return null;
        }

        ResolvableFloat blockDelaySeconds = this.asResolvableFloat(componentSection, "block-delay-seconds",0);
        ResolvableFloat disableCooldownScale = this.asResolvableFloat(componentSection, "disable-cooldown-scale",1);
        ResolvableFloat itemDamageThreshold = this.asResolvableFloat(componentSection, "item-damage.threshold",0);
        ResolvableFloat itemDamageBase = this.asResolvableFloat(componentSection, "item-damage.base",0);
        ResolvableFloat itemDamageFactor = this.asResolvableFloat(componentSection, "item-damage.factor",1.5f);
        ResolvableSound blockSound = this.asResolvableSound(componentSection, "block-sound");
        ResolvableSound disabledSound = this.asResolvableSound(componentSection, "disabled-sound");

        List<Map<?, ?>> rawList = componentSection.getMapList("damage-reductions");
        List<ResolvableDamageReduction> resolvableRecords = new ArrayList<>();
        for (var rawMap : rawList) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) rawMap;
            ResolvableDamageReduction record = ResolvableDamageReduction.fromMap(map);
            if (record != null) {
                resolvableRecords.add(record);
            }
        }

        return new BlocksAttacksComponent(
                blockDelaySeconds,
                disableCooldownScale,
                resolvableRecords,
                itemDamageThreshold,
                itemDamageBase,
                itemDamageFactor,
                blockSound,
                disabledSound
        );
    }
}
