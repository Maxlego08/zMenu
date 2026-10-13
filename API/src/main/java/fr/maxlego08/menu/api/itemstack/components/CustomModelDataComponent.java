package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomModelDataComponent extends ItemComponent {
    private final List<ResolvableFloat> floats;
    private final List<ResolvableBoolean> booleans;
    private final List<ResolvableString> strings;
    private final List<ResolvableColor> colorList;

    public CustomModelDataComponent(List<ResolvableFloat> floats, List<ResolvableBoolean> booleans, List<ResolvableString> strings, List<ResolvableColor> colorList) {
        this.floats = floats;
        this.booleans = booleans;
        this.strings = strings;
        this.colorList = colorList;
    }

    public List<ResolvableFloat> getFloats() {
        return this.floats;
    }

    public List<ResolvableBoolean> getFlags() {
        return this.booleans;
    }

    public List<ResolvableString> getStrings() {
        return this.strings;
    }

    public List<ResolvableColor> getColors() {
        return this.colorList;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {

        CustomModelData.Builder builder = CustomModelData.customModelData();

        Resolvable.applyResolvable(context, this.floats, builder::addFloats);
        Resolvable.applyResolvable(context, this.booleans, builder::addFlags);
        Resolvable.applyResolvable(context, this.strings, builder::addStrings);
        Resolvable.applyResolvable(context, this.colorList, builder::addColors);

        itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, builder.build());
    }

    @Override
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        if (!this.floats.isEmpty()) map.put("floats", Resolvable.serializeList(this.floats));
        if (!this.booleans.isEmpty()) map.put("flags", Resolvable.serializeList(this.booleans));
        if (!this.strings.isEmpty()) map.put("strings", Resolvable.serializeList(this.strings));
        if (!this.colorList.isEmpty()) map.put("colors", Resolvable.serializeList(this.colorList));
        return map;
    }
}
