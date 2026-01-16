package fr.maxlego08.menu.zcore.utils.itemstack;

import org.bukkit.World;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ZMapView implements MapView {
    private final int id;
    private @Nullable World world;
    private @NonNull Scale scale = Scale.NORMAL;
    private int centerX = 0;
    private int centerZ = 0;
    private final List<MapRenderer> renderers = new ArrayList<>();
    private boolean trackingPosition = false;
    private boolean unlimitedTracking = false;
    private boolean locked = false;

    public ZMapView(int id) {
        this.id = id;
    }

    public ZMapView(int id, @NotNull MapView original) {
        this.id = id;
        this.world = original.getWorld();
        this.scale = original.getScale();
        this.centerX = original.getCenterX();
        this.centerZ = original.getCenterZ();
        this.renderers.addAll(original.getRenderers());
        this.trackingPosition = original.isTrackingPosition();
        this.unlimitedTracking = original.isUnlimitedTracking();
        this.locked = original.isLocked();
    }

    @Override
    @Contract(pure = true)
    public int getId() {
        return this.id;
    }

    @Override
    @Contract(pure = true)
    public boolean isVirtual() {
        return false;
    }

    @Override
    @Contract(pure = true)
    public @NonNull Scale getScale() {
        return this.scale;
    }

    @Override
    public void setScale(MapView.@NonNull Scale scale) {
        this.scale = scale;
    }

    @Override
    @Contract(pure = true)
    public int getCenterX() {
        return this.centerX;
    }

    @Override
    @Contract(pure = true)
    public int getCenterZ() {
        return this.centerZ;
    }

    @Override
    public void setCenterX(int x) {
        this.centerX = x;
    }

    @Override
    public void setCenterZ(int z) {
        this.centerZ = z;
    }

    @Override
    @Contract(pure = true)
    public @Nullable World getWorld() {
        return this.world;
    }

    @Override
    public void setWorld(@NonNull World world) {
        this.world = world;
    }

    @Override
    @Contract(pure = true)
    public @NonNull List<MapRenderer> getRenderers() {
        return Collections.unmodifiableList(this.renderers);
    }

    @Override
    public void addRenderer(@NonNull MapRenderer renderer) {
        this.renderers.add(renderer);
    }

    @Override
    public boolean removeRenderer(@Nullable MapRenderer renderer) {
        return this.renderers.remove(renderer);
    }

    @Override
    @Contract(pure = true)
    public boolean isTrackingPosition() {
        return this.trackingPosition;
    }

    @Override
    public void setTrackingPosition(boolean trackingPosition) {
        this.trackingPosition = trackingPosition;
    }

    @Override
    @Contract(pure = true)
    public boolean isUnlimitedTracking() {
        return this.unlimitedTracking;
    }

    @Override
    public void setUnlimitedTracking(boolean unlimited) {
        this.unlimitedTracking = unlimited;
    }

    @Override
    @Contract(pure = true)
    public boolean isLocked() {
        return this.locked;
    }

    @Override
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
