package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.AttackRangeComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.AttackRange;
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
public class AttackRangeItemComponentLoader extends AbstractAttackRangeItemComponentLoader {

    @Override
    public @Nullable ItemComponent load(
            @NotNull MenuItemStackContext context,
            @NotNull File file,
            @NotNull YamlDocument configuration,
            @NotNull String path,
            @Nullable Section componentSection
    ) {

        if (componentSection == null) {
            return null;
        }

        return new AttackRangeComponent(
                this.getMinReachResolvable(componentSection, path),
                this.getMaxReachResolvable(componentSection, path),
                this.getMinCreativeReachResolvable(componentSection, path),
                this.getMaxCreativeReachResolvable(componentSection, path),
                this.getHitboxMarginResolvable(componentSection, path),
                this.getMobFactorResolvable(componentSection, path)
        );
    }

    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        AttackRange attackRange = itemStack.getData(DataComponentTypes.ATTACK_RANGE);
        if (attackRange == null) return null;
        return new AttackRangeComponent(
                ResolvableFloat.of(attackRange.minReach()),
                ResolvableFloat.of(attackRange.maxReach()),
                ResolvableFloat.of(attackRange.minCreativeReach()),
                ResolvableFloat.of(attackRange.maxCreativeReach()),
                ResolvableFloat.of(attackRange.hitboxMargin()),
                ResolvableFloat.of(attackRange.mobFactor())
        );
    }
}
