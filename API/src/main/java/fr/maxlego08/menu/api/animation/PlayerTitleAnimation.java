package fr.maxlego08.menu.api.animation;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public abstract class PlayerTitleAnimation {
    private final MenuPlugin plugin;
    protected final int inventoryId;
    protected PaperMetaUpdater metaUpdater;

    protected final InventoryType inventoryType;
    protected final int size;

    protected final TitleAnimationSettings settings;

    protected int currentIndex = 0;
    protected int currentCycle = 0;
    protected boolean isStarted = false;

    protected WrappedTask wrappedTask;

    protected PlayerTitleAnimation(@NotNull MenuPlugin plugin,@NotNull TitleAnimationSettings settings, int inventoryId, InventoryType type, int size) {
        this.plugin = plugin;
        this.settings = settings;
        this.inventoryId = inventoryId;
        this.inventoryType = type;
        this.size = size;
        if (!(this.plugin.getMetaUpdater() instanceof PaperMetaUpdater paperMetaUpdater)) {
            throw new UnsupportedOperationException("Title animations are only supported on Paper servers.");
        }
        this.metaUpdater = paperMetaUpdater;
    }

    public void start(@NotNull Player player,@NotNull List<ItemStack> inventoryContents){
        if (this.settings.titles().isEmpty()) {
            return;
        }
        this.currentCycle = 0;
        this.currentIndex = 0;
        this.wrappedTask = this.plugin.getScheduler().runAtEntityTimer(player, () -> {
            if (this.currentIndex >= this.settings.titles().size()) {
                this.currentIndex = 0;
                this.currentCycle++;
                if (this.settings.cycles() > 0 && this.currentCycle >= this.settings.cycles()) {
                    if (this.settings.showItemsAfterAnimation()) {
                        this.sendInventoryContent(player, inventoryContents);
                    }
                    this.stop();
                    return;
                }
            }
            String title = this.settings.titles().get(this.currentIndex++);
            this.sendTitle(player, title);
            if (!this.settings.showItemsAfterAnimation()) {
                if (this.settings.itemUpdateInterval() > 0 && this.currentIndex % this.settings.itemUpdateInterval() == 0) {
                    this.sendInventoryContent(player, inventoryContents);
                }
            }
        }, this.settings.initialDelay(), this.settings.interval(), this.settings.timeUnit());
        this.isStarted = true;
    }

    public void stop(){
        if(this.wrappedTask != null){
            this.wrappedTask.cancel();
        }
        this.isStarted = false;
        this.currentIndex = 0;
    }

    public abstract void sendTitle(@NotNull Player player,@NotNull String title);

    public abstract void sendInventoryContent(@NotNull Player player, @NotNull List<ItemStack> inventoryContents);

    public int getCurrentIndex() {
        return this.currentIndex;
    }

    @Contract(pure = true)
    public @NotNull MenuPlugin getPlugin() {
        return this.plugin;
    }

    @Contract(pure = true)
    public int getInventoryId() {
        return this.inventoryId;
    }

    @Contract(pure = true)
    public @NotNull PaperMetaUpdater getMetaUpdater() {
        return this.metaUpdater;
    }

    @Contract(pure = true)
    public @NotNull InventoryType getInventoryType() {
        return this.inventoryType;
    }

    @Contract(pure = true)
    public int getSize() {
        return this.size;
    }

    @Contract(pure = true)
    public @NotNull TitleAnimationSettings getSettings() {
        return this.settings;
    }

    @Contract(pure = true)
    public int getCurrentCycle() {
        return this.currentCycle;
    }

    @Contract(pure = true)
    public boolean isStarted() {
        return this.isStarted;
    }

    @Contract(pure = true)
    public @Nullable WrappedTask getWrappedTask() {
        return this.wrappedTask;
    }
}
