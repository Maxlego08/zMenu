package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.UseCooldownComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseCooldown;
import org.bukkit.NamespacedKey;
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
@SinceVersion("1.21.3")
public class UseCooldownItemComponentLoader extends ItemComponentLoader {

    public UseCooldownItemComponentLoader(){
        super("use-cooldown");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        if (componentSection == null) return null;
        ResolvableFloat cooldownSeconds = this.asResolvableFloat(componentSection, "seconds");
        String cooldownGroupStr = componentSection.getString("cooldown-group");
        ResolvableNamespacedKey resolvableNamespacedKey = ResolvableNamespacedKey.autoOrNull(cooldownGroupStr);
        return new UseCooldownComponent(cooldownSeconds != null ? cooldownSeconds : ResolvableFloat.of(0), resolvableNamespacedKey);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        UseCooldown cooldown = itemStack.getData(DataComponentTypes.USE_COOLDOWN);
        if (cooldown == null) return null;
        NamespacedKey group = cooldown.cooldownGroup() == null ? null : NamespacedKey.fromString(cooldown.cooldownGroup().asString());
        return new UseCooldownComponent(ResolvableFloat.of(cooldown.seconds()), group == null ? null : ResolvableNamespacedKey.of(group));
    }
}
