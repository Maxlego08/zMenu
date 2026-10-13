package fr.maxlego08.menu.loader.components;

import com.google.common.collect.Multimap;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.attribute.AttributeMergeStrategy;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.AttributeModifiersComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyAttributeModifiersComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableAttributeWrapper;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableDouble;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay;
import org.bukkit.Keyed;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoComponentLoader
public class AttributeModifiersItemComponentLoader extends ItemComponentLoader {
    private final MenuPlugin plugin;

    public AttributeModifiersItemComponentLoader(MenuPlugin plugin){
        super("attribute-modifiers");
        this.plugin = plugin;
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        String mergeStrategyStr = componentSection.getString("attribute-merge-strategy");
        ResolvableEnum<AttributeMergeStrategy> attributeMergeStrategyResolvable = ResolvableEnum.autoOrNull(AttributeMergeStrategy.class, mergeStrategyStr);
        List<Map<?, ?>> mapList = componentSection.getMapList("modifiers");
        List<ResolvableAttributeWrapper> resolvableWrappers = new ArrayList<>();
        for (Map<?, ?> rawMap : mapList) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) rawMap;
            ResolvableAttributeWrapper wrapper = ResolvableAttributeWrapper.fromMap(map);
            if (wrapper != null) {
                resolvableWrappers.add(wrapper);
            }
        }
        if (resolvableWrappers.isEmpty()) return null;
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new AttributeModifiersComponent(this.plugin, resolvableWrappers, attributeMergeStrategyResolvable)
                : new LegacyAttributeModifiersComponent(this.plugin, resolvableWrappers, attributeMergeStrategyResolvable);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        ItemAttributeModifiers attributeModifiers = itemStack.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (attributeModifiers == null || attributeModifiers.modifiers().isEmpty()) return null;
        boolean hasDisplay = MinecraftVersion.isServerAtLeast("1.21.6");
        List<ResolvableAttributeWrapper> wrappers = new ArrayList<>();
        for (ItemAttributeModifiers.Entry entry : attributeModifiers.modifiers()) {
            if (hasDisplay && !(entry.display() instanceof AttributeModifierDisplay.Default)) return null;
            AttributeModifier modifier = entry.modifier();
            wrappers.add(new ResolvableAttributeWrapper(
                    ResolvableString.of(entry.attribute().getKey().asString()),
                    ResolvableEnum.of(AttributeModifier.Operation.class, modifier.getOperation()),
                    ResolvableDouble.of(modifier.getAmount()),
                    ResolvableString.of(modifier.getSlotGroup().toString()),
                    ResolvableNamespacedKey.of(modifier.getKey())
            ));
        }
        return new AttributeModifiersComponent(this.plugin, wrappers, ResolvableEnum.of(AttributeMergeStrategy.class, AttributeMergeStrategy.REPLACE));
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!MinecraftVersion.isServerAtLeast("1.21")) return null;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || !itemMeta.hasAttributeModifiers()) return null;
        Multimap<Attribute, AttributeModifier> attributeModifiers = itemMeta.getAttributeModifiers();
        if (attributeModifiers == null || attributeModifiers.isEmpty()) return null;

        List<ResolvableAttributeWrapper> wrappers = new ArrayList<>();
        for (Map.Entry<Attribute, AttributeModifier> entry : attributeModifiers.entries()) {
            AttributeModifier modifier = entry.getValue();
            Keyed attribute = entry.getKey();
            wrappers.add(new ResolvableAttributeWrapper(
                    ResolvableString.of(attribute.getKey().asString()),
                    ResolvableEnum.of(AttributeModifier.Operation.class, modifier.getOperation()),
                    ResolvableDouble.of(modifier.getAmount()),
                    ResolvableString.of(modifier.getSlotGroup().toString()),
                    ResolvableNamespacedKey.of(modifier.getKey())
            ));
        }
        return new LegacyAttributeModifiersComponent(this.plugin, wrappers, ResolvableEnum.of(AttributeMergeStrategy.class, AttributeMergeStrategy.REPLACE));
    }
}
