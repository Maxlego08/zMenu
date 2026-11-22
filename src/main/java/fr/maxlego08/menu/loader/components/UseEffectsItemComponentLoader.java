package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.UseEffectsComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseEffects;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoComponentLoader
@SinceVersion("1.21.11")
public class UseEffectsItemComponentLoader extends ItemComponentLoader {

    public UseEffectsItemComponentLoader(){
        super("use-effects");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        if (componentSection == null) return null;
        ResolvableBoolean canSprint = this.asResolvableBoolean(componentSection, "can-sprint", false);
        ResolvableFloat speedMultiplier = this.asResolvableFloat(componentSection, "speed-multiplier", 0.2f);
        ResolvableBoolean interactVibrations = this.asResolvableBoolean(componentSection, "interact-vibrations", true);
        return new UseEffectsComponent(canSprint, speedMultiplier, interactVibrations);
    }

    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        UseEffects useEffects = itemStack.getData(DataComponentTypes.USE_EFFECTS);
        if (useEffects == null) return null;
        return new UseEffectsComponent(ResolvableBoolean.of(useEffects.canSprint()), ResolvableFloat.of(useEffects.speedMultiplier()), ResolvableBoolean.of(useEffects.interactVibrations()));
    }
}
