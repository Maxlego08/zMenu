package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.itemstack.ZDecorationEntry;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import io.papermc.paper.datacomponent.item.MapDecorations;
import org.bukkit.map.MapCursor;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public final class PaperResolvableMapDecorationEntry implements Resolvable<MapDecorations.DecorationEntry> {
    private final ResolvableRegistryEntry<MapCursor.Type> type;
    private final ResolvableInt x;
    private final ResolvableInt z;
    private final ResolvableFloat rotation;

    public PaperResolvableMapDecorationEntry(ResolvableRegistryEntry<MapCursor.Type> type, ResolvableInt x, ResolvableInt z, ResolvableFloat rotation) {
        this.type = type;
        this.x = x;
        this.z = z;
        this.rotation = rotation;
    }

    @Override
    public MapDecorations.@Nullable DecorationEntry resolve(@NotNull BuildContext context) {
//         MapCursor.Type resolvedType = this.type.resolve(context);
        MapCursor.Type resolvedType = Resolvable.resolve(context, this.type);
        if (resolvedType == null) {
            return null;
        }

        Integer resolvedX = Resolvable.resolve(context, this.x);
        if (resolvedX == null) {
            return null;
        }

        Integer resolvedZ = Resolvable.resolve(context, this.z);
        if (resolvedZ == null) {
            return null;
        }

        Float resolvedRotation = Resolvable.resolve(context, this.rotation);
        if (resolvedRotation == null) {
            return null;
        }

        return new ZDecorationEntry(
            resolvedType,
            resolvedX,
            resolvedZ,
            resolvedRotation
        );
    }
}
