package fr.maxlego08.menu.zcore.utils;

import io.papermc.paper.datacomponent.item.MapDecorations;
import org.bukkit.map.MapCursor;

public record ZDecorationEntry(
    MapCursor.Type type,
    double x,
    double z,
    float rotation
) implements MapDecorations.DecorationEntry {
    @Override
    public MapCursor.Type type() {
        return this.type;
    }

    @Override
    public double x() {
        return this.x;
    }

    @Override
    public double z() {
        return this.z;
    }

    @Override
    public float rotation() {
        return this.rotation;
    }
}
