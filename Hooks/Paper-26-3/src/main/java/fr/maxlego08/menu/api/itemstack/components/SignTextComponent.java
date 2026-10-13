package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableDyeColor;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SignText;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SignTextComponent extends ItemComponent {

    public enum Side {
        FRONT,
        BACK
    }

    public static final int LINES = 4;

    private final @NotNull Side side;
    private final @NotNull List<ResolvableComponent> messages;
    private final @Nullable ResolvableDyeColor color;
    private final @Nullable ResolvableBoolean hasGlowingText;

    public SignTextComponent(@NotNull Side side, @NotNull List<ResolvableComponent> messages, @Nullable ResolvableDyeColor color, @Nullable ResolvableBoolean hasGlowingText) {
        this.side = side;
        this.messages = messages;
        this.color = color;
        this.hasGlowingText = hasGlowingText;
    }

    public @NotNull Side getSide() {
        return this.side;
    }

    public @NotNull List<ResolvableComponent> getMessages() {
        return this.messages;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        SignText.Builder signText = SignText.signText();
        Resolvable.applyResolvable(context, this.messages, lines -> {
            List<Component> signLines = new ArrayList<>(lines);
            while (signLines.size() < LINES) signLines.add(Component.empty());
            signText.lines(signLines.subList(0, LINES));
        });
        Resolvable.applyResolvable(context, this.color, signText::color);
        Resolvable.applyResolvable(context, this.hasGlowingText, signText::hasGlowingText);
        itemStack.setData(this.side == Side.FRONT ? DataComponentTypes.SIGN_TEXT_FRONT : DataComponentTypes.SIGN_TEXT_BACK, signText.build());
    }

    @Override
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("messages", Resolvable.serializeList(this.messages));
        if (this.color != null) map.put("color", this.color.serialize());
        if (this.hasGlowingText != null) map.put("has-glowing-text", this.hasGlowingText.serialize());
        return map;
    }
}
