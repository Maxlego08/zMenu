package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.DeathProtectionComponent;
import fr.maxlego08.menu.api.utils.resolvable.paper.PaperResolvableConsumeEffect;
import fr.maxlego08.menu.api.utils.resolvable.paper.PaperResolvableDeathProtection;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoComponentLoader
@SinceVersion("1.21.3")
public class DeathProtectionItemComponentLoader extends AbstractEffectItemComponentLoader {

    public DeathProtectionItemComponentLoader(){
        super("death-protection");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        if (componentSection == null) return null;
        List<PaperResolvableConsumeEffect> resolvablePotionEffects = this.parseEffects(componentSection.getMapList("death-effects"));
        return resolvablePotionEffects.isEmpty() ? null : new DeathProtectionComponent(new PaperResolvableDeathProtection(resolvablePotionEffects));
    }

    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        DeathProtection deathProtection = itemStack.getData(DataComponentTypes.DEATH_PROTECTION);
        if (deathProtection == null || deathProtection.deathEffects().isEmpty()) return null;
        List<PaperResolvableConsumeEffect> effects = new ArrayList<>();
        for (ConsumeEffect consumeEffect : deathProtection.deathEffects()) {
            PaperResolvableConsumeEffect effect = PaperResolvableConsumeEffect.of(consumeEffect);
            if (effect == null) return null;
            effects.add(effect);
        }
        return new DeathProtectionComponent(new PaperResolvableDeathProtection(effects));
    }
}
