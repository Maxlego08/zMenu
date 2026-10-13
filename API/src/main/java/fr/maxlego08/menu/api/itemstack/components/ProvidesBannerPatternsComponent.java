package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ProvidesBannerPatternsComponent extends ItemComponent {
    private static final boolean TAKES_TAG_KEY = findTakesTagKey();

    private final TagKey<PatternType> patterns;

    public ProvidesBannerPatternsComponent(@NotNull TagKey<PatternType> patterns) {
        this.patterns = patterns;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Object value = this.patterns;
        if (!TAKES_TAG_KEY) {
            Registry<PatternType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN);
            if (!registry.hasTag(this.patterns)) return;
            value = registry.getTag(this.patterns);
        }
        DataComponentType.Valued<Object> type = (DataComponentType.Valued<Object>) (DataComponentType.Valued<?>) DataComponentTypes.PROVIDES_BANNER_PATTERNS;
        itemStack.setData(type, value);
    }

    private static boolean findTakesTagKey() {
        try {
            Type type = DataComponentTypes.class.getField("PROVIDES_BANNER_PATTERNS").getGenericType();
            Type valueType = ((ParameterizedType) type).getActualTypeArguments()[0];
            Type rawValueType = valueType instanceof ParameterizedType parameterized ? parameterized.getRawType() : valueType;
            return rawValueType == TagKey.class;
        } catch (NoSuchFieldException exception) {
            throw new IllegalStateException("DataComponentTypes#PROVIDES_BANNER_PATTERNS not found", exception);
        }
    }

    @Override
    public @Nullable Object serialize() {
        return this.patterns.key().asString();
    }
}
