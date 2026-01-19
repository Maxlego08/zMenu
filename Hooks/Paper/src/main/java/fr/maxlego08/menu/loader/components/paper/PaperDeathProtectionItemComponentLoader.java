package fr.maxlego08.menu.loader.components.paper;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.common.enums.ConsumeEffectType;
import fr.maxlego08.menu.itemstack.components.paper.DeathProtectionComponent;
import fr.maxlego08.menu.loader.components.AbstractEffectItemComponentLoader;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaperDeathProtectionItemComponentLoader extends AbstractEffectItemComponentLoader {

    public PaperDeathProtectionItemComponentLoader(){
        super("death_protection");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        List<ConsumeEffect> effects = new ArrayList<>();
        List<Map<?, ?>> mapList = componentSection.getMapList("death_effects");
        for (Map<?, ?> map : mapList) {
            @SuppressWarnings("unchecked")
            Map<String, Object> effectMap = (Map<String, Object>) map;
            ConsumeEffectType type;
            try {
                type = ConsumeEffectType.valueOf(((String) effectMap.get("type")).toUpperCase());
            } catch (Exception e) {
                continue;
            }
            switch (type) {
                case PLAY_SOUND -> {
                    String soundString = (String) effectMap.get("sound");
                    if (soundString == null) continue;

                    effects.add(ConsumeEffect.playSoundConsumeEffect(Key.key(soundString)));
                }
                case APPLY_EFFECTS -> {
                    List<Map<?, ?>> potionEffectsRaw = (List<Map<?, ?>>) effectMap.get("effects");
                    if (potionEffectsRaw == null) continue;
                    List<PotionEffect> potionEffects = parsePotionEffects(potionEffectsRaw);
                    float probability = parseFloat(effectMap, "probability", 1.0f);
                    effects.add(ConsumeEffect.applyStatusEffects(potionEffects, probability));
                }
                case TELEPORT_RANDOMLY -> {
                    float diameter = parseFloat(effectMap, "diameter", 16.0f);
                    effects.add(ConsumeEffect.teleportRandomlyEffect(diameter));
                }
                case CLEAR_ALL_EFFECTS -> {
                    effects.add(ConsumeEffect.clearAllStatusEffects());
                }
                case REMOVE_EFFECTS -> {
                    List<PotionEffectType> potionEffectTypes = parsePotionEffectTypes(effectMap.get("effects"));

                    List<TypedKey<PotionEffectType>> keys = new ArrayList<>();
                    for (PotionEffectType pet : potionEffectTypes) {
                        keys.add(TypedKey.create(RegistryKey.MOB_EFFECT, pet.key()));
                    }

                    RegistryKeySet<PotionEffectType> effectTypes = RegistrySet.keySet(RegistryKey.MOB_EFFECT, keys);

                    effects.add(ConsumeEffect.removeEffects(effectTypes));
                }
            }
        }
        return effects.isEmpty() ? null : new DeathProtectionComponent(effects);
    }
}
