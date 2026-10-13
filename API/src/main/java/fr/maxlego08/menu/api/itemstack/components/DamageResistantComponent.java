package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableDamageTypeTag;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Registry;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class DamageResistantComponent extends ItemComponent {
    private static final Method FACTORY = findFactory();

    private final @NotNull ResolvableDamageTypeTag damageType;

    public DamageResistantComponent(@NotNull ResolvableDamageTypeTag damageType) {
        this.damageType = damageType;
    }

    public @NotNull ResolvableDamageTypeTag getDamageType() {
        return this.damageType;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        this.applyResolvable(context, damageType -> {
            TagKey<DamageType> tagKey = TagKey.create(RegistryKey.DAMAGE_TYPE, damageType.getKey());
            Object value = tagKey;
            if (FACTORY.getParameterTypes()[0] != TagKey.class) {
                Registry<DamageType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE);
                if (!registry.hasTag(tagKey)) return;
                value = registry.getTag(tagKey);
            }
            try {
                itemStack.setData(DataComponentTypes.DAMAGE_RESISTANT, (DamageResistant) FACTORY.invoke(null, value));
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("Could not create the damage-resistant component", exception);
            }
        }, this.damageType);
    }

    private static Method findFactory() {
        for (Method method : DamageResistant.class.getMethods()) {
            if (method.getName().equals("damageResistant") && method.getParameterCount() == 1) return method;
        }
        throw new IllegalStateException("DamageResistant#damageResistant not found");
    }

    @Override
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("types", this.damageType.serialize());
        return map;
    }
}
