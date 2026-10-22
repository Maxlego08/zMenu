package fr.maxlego08.menu.inventory.inventories;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.animation.compile.AnimationCompiler;
import fr.maxlego08.menu.animation.compile.AnimationStep;
import fr.maxlego08.menu.animation.compile.CompiledAnimation;
import fr.maxlego08.menu.animation.compile.StepAction;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.animation.slot.Animation;
import fr.maxlego08.menu.api.animation.slot.AnimationContext;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayMode;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayback;
import fr.maxlego08.menu.api.animation.slot.AnimationSlots;
import fr.maxlego08.menu.api.animation.slot.settings.AnimationVariant;
import fr.maxlego08.menu.api.animation.slot.settings.ButtonAnimationSettings;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationGroup;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import fr.maxlego08.menu.api.animation.slot.settings.idle.ButtonIdleAnimation;
import fr.maxlego08.menu.api.animation.slot.settings.idle.IdleFrame;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.InventoryResult;
import fr.maxlego08.menu.api.engine.ItemButton;
import fr.maxlego08.menu.api.engine.OpenCause;
import fr.maxlego08.menu.api.event.events.AnimationEndEvent;
import fr.maxlego08.menu.api.event.events.AnimationStartEvent;
import fr.maxlego08.menu.api.exceptions.InventoryOpenException;
import fr.maxlego08.menu.api.inventory.ChestInventory;
import fr.maxlego08.menu.api.inventory.ContainerInventory;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.RefreshRequirement;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.VInventory;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.PerformanceDebug;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author Maxlego08
 */
public class InventoryDefault extends VInventory implements InventoryEngine {

    private final Map<Integer, WrappedTask> timers = new ConcurrentHashMap<>();
    private final Map<Integer, Button> buttonsByAnimationSlot = new ConcurrentHashMap<>();
    private final Set<Integer> slotsOwnedByAnimation = ConcurrentHashMap.newKeySet();
    private final Map<Integer, WrappedTask> idleTasksBySlot = new ConcurrentHashMap<>();
    private final Map<Integer, IdleCycle> idleCycleBySlot = new ConcurrentHashMap<>();
    private final Map<Integer, IdleCycle> idleCyclesAwaitingRebuild = new ConcurrentHashMap<>();
    private volatile boolean useSharedIdleTimer;
    private final Set<IdleCycle> sharedIdleCycles = ConcurrentHashMap.newKeySet();
    private final Object sharedIdleLock = new Object();
    private WrappedTask sharedIdleTimer;

    private volatile RunningAnimation runningAnimation;
    private volatile Placeholders openPlaceholders = new Placeholders();
    private PerformanceDebug perfDebug;
    private Inventory inventory;
    private List<Inventory> oldInventories = new ArrayList<>();
    private List<Button> buttons = Collections.emptyList();
    private int maxPage = 1;
    private boolean openAsync = false;

    private List<Button> updatedButtons = Collections.emptyList();

    @Override
    public InventoryResult openInventory(MenuPlugin main, Player player, int page, Object... args) throws InventoryOpenException {

        this.inventory = (Inventory) args[0];
        this.perfDebug = PerformanceDebug.create("inventory:" + this.inventory.getFileName());

        this.perfDebug.start("openInventory.permissionCheck");
        InventoryResult result = this.inventory.openInventory(player, this);
        this.perfDebug.end();
        if (result != InventoryResult.SUCCESS) {
            return result;
        }

        if (this.inventory instanceof ContainerInventory containerInventory) {
            super.setClearInvType(containerInventory.getClearInvType());
            super.setClickLimiterEnabled(containerInventory.isClickLimiterEnabled());
        }

        this.oldInventories = this.extractOldInventories(args);

        Collection<Pattern> patterns = this.inventory.getPatterns();

        this.perfDebug.start("openInventory.getMaxPage");
        this.maxPage = Math.max(1, this.inventory.getMaxPage(patterns, player, args));
        this.perfDebug.end();

        List<Button> computedButtons = new ArrayList<>();
        this.perfDebug.start("openInventory.sortPatterns");
        for (Pattern pattern : patterns) {
            computedButtons.addAll(this.inventory.sortPatterns(pattern, page, args));
        }
        this.perfDebug.end();

        this.perfDebug.start("openInventory.sortButtons");
        computedButtons.addAll(this.inventory.sortButtons(page, args));
        this.perfDebug.end();
        this.buttons = computedButtons;

        List<Button> clickableUpdates = new ArrayList<>();
        boolean async = false;
        int idleButtons = 0;
        for (Button button : computedButtons) {
            if (button.updateOnClick()) {
                clickableUpdates.add(button);
            }
            if (button.getIdleAnimation() != null) {
                idleButtons++;
            }
            if (!async && button.isOpenAsync()) {
                async = true;
            }
        }
        this.updatedButtons = clickableUpdates;
        this.useSharedIdleTimer = idleButtons > 0 && idleButtons >= Configuration.sharedIdleTimerThreshold;
        this.openAsync = async;
        InventoryManager manager = this.plugin.getInventoryManager();
        manager.setPlayerPage(player, page, this.maxPage);

        var scheduler = this.plugin.getScheduler();
        Consumer<WrappedTask> runnable = w -> {
            Placeholders placeholders = new Placeholders();
            this.openPlaceholders = placeholders;

            placeholders.register("page", String.valueOf(page));
            placeholders.register("maxPage", String.valueOf(this.maxPage));
            placeholders.register("max-page", String.valueOf(this.maxPage));

            this.perfDebug.start("openInventory.onInventoryOpen");
            for (Button button : this.buttons) {
                button.onInventoryOpen(player, this, placeholders);
            }
            this.perfDebug.end();

            this.perfDebug.start("openInventory.resolveInventoryName");
            String inventoryName = this.plugin.getFontImage().replace(this.inventory.getName(player, this, placeholders));
            Player targetPlayer = this.getTargetPlayer();
            this.perfDebug.end();

            this.perfDebug.start("openInventory.papiInventoryName");
            String parsedName = super.papi(placeholders.parse(inventoryName), targetPlayer, false);
            this.perfDebug.end();

            if (this.inventory instanceof ContainerInventory containerInventory) {
                this.perfDebug.start("openInventory.createMetaInventory");
                if (containerInventory.getType() == InventoryType.CHEST && containerInventory instanceof ChestInventory chestInventory) {
                    super.createMetaInventory(parsedName, chestInventory.size());
                } else {
                    super.createMetaInventory(parsedName, containerInventory.getType());
                }
                this.perfDebug.end();


                super.setTitleAnimation(containerInventory.getTitleAnimation());

                // Display fill items
                this.perfDebug.start("openInventory.fillItems");
                MenuItemStack fillItemStack = containerInventory.getFillItemStack();
                if (fillItemStack != null) {
                    ItemStack builtItem = fillItemStack.build(player, containerInventory.isFillItemUseCache());
                    if (builtItem != null) {
                        ItemStack[] contents = super.getSpigotInventory().getContents();
                        for (int slot = 0; slot < contents.length; slot++) {
                            this.addItem(slot, builtItem.clone());
                        }
                    }
                }
                this.perfDebug.end();
            }


            // Display buttons
            for (Button button : this.buttons) {
                this.perfDebug.start("buildButton." + button.getName());
                this.buildButton(button, placeholders);
                this.perfDebug.end();
            }

            if (this.openAsync) {
                scheduler.runAtEntity(player, w2 -> {
                    this.playOpenAnimation();
                    player.openInventory(this.getSpigotInventory());
                });
            } else {
                this.playOpenAnimation();
            }

            this.perfDebug.printSummary();
        };

        if (this.openAsync) {

            scheduler.runAsync(runnable);
            return InventoryResult.SUCCESS_ASYNC;
        } else {

            runnable.accept(null);
            return InventoryResult.SUCCESS;
        }
    }

    @Override
    public void postOpen(MenuPlugin plugin, Player player, int page, Object[] objects) {
        this.inventory.postOpenInventory(player, this);
    }

    @Override
    protected void onClose(InventoryCloseEvent event, MenuPlugin plugin, Player player) {
        this.stopScheduledTasks();
        this.inventory.closeInventory(player, this);
        this.buttons.forEach(button -> button.onInventoryClose(player, this));
    }

    public void stopScheduledTasks() {
        this.idleCycleBySlot.clear();
        this.idleCyclesAwaitingRebuild.clear();
        this.idleTasksBySlot.values().forEach(WrappedTask::cancel);
        this.idleTasksBySlot.clear();

        synchronized (this.sharedIdleLock) {
            this.sharedIdleCycles.clear();
            if (this.sharedIdleTimer != null) {
                this.sharedIdleTimer.cancel();
                this.sharedIdleTimer = null;
            }
        }

        this.cancelTimers();
    }

    @Override
    protected void onDrag(InventoryDragEvent event, MenuPlugin plugin, Player player) {
        this.buttons.forEach(button -> button.onDrag(event, player, this));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event, MenuPlugin plugin, Player player) {
        this.buttons.forEach(button -> button.onInventoryClick(event, player, this));
    }

    /**
     * Allows displaying a button
     *
     * @param button The button
     */
    @Override
    public void buildButton(Button button) {
        this.buildButton(button, new Placeholders());
    }

    @Override
    public void buildButton(Button button, @NonNull Placeholders placeholders) {
        if (button == null) {
            return;
        }
        final Player targetPlayer = this.getTargetPlayer();

        this.perfDebug.start("getDisplayButton." + button.getName());
        button = button.getDisplayButton(this, targetPlayer);
        this.perfDebug.end();
        if (button == null) {
            return;
        }

        // If the button has a permission or a placeholder to check
        if (button.hasPermission()) {

            this.perfDebug.start("checkPermission." + button.getName());
            boolean hasPermission = button.checkPermission(targetPlayer, this, placeholders);
            this.perfDebug.end();

            // We will check if the player has the permission to display the
            // button
            if (!hasPermission) {

                // If there is an ElseButton we will display it
                if (button.hasElseButton()) {

                    Button elseButton = button.getElseButton();
                    this.buildButton(elseButton, placeholders);
                }
            } else {

                // If the player has the permission, the button
                if (button.hasCustomRender()) {
                    this.perfDebug.start("onRender." + button.getName());
                    button.onRender(targetPlayer, this);
                    this.perfDebug.end();
                } else {
                    this.displayButton(button, placeholders);
                }
            }

        } else {

            // If there is no permission, then the button
            if (button.hasCustomRender()) {
                this.perfDebug.start("onRender." + button.getName());
                button.onRender(targetPlayer, this);
                this.perfDebug.end();
            } else {
                this.displayButton(button, placeholders);
            }
        }
    }

    /**
     * Allows displaying the button in the inventory
     */
    @Override
    public void displayButton(@NonNull Button button) {
        this.displayButton(button, new Placeholders());
    }

    @Override
    public void displayButton(@NotNull Button button, @NotNull Placeholders placeholders) {
        final Player targetPlayer = this.getTargetPlayer();
        var scheduler = this.plugin.getScheduler();
        Consumer<WrappedTask> runnable;
        if (button.hasSpecialRender()) {

            runnable = w -> {
                this.perfDebug.start("specialRender." + button.getName());
                button.onRender(targetPlayer, this);
                this.perfDebug.end();
            };

        } else {

            runnable = w -> {
                this.perfDebug.start("getRealSlot." + button.getName());
                int slot;
                if (button.isPlayerInventory()) {
                    slot = button.getRealSlot(36, this.page);
                } else {
                    if (this.inventory instanceof ChestInventory chestInventory) {
                        slot = button.getRealSlot(chestInventory.size(), this.page);
                    } else {
                        //noinspection deprecation
                        slot = button.getRealSlot(this.inventory.size(), this.page);
                    }
                }

                this.perfDebug.end();
                this.displayFinalButton(button, placeholders, slot);
            };

        }
        if (this.openAsync) scheduler.runAtEntity(this.player, runnable);
        else runnable.accept(null);
    }

    /**
     * Allows displaying the button and putting the actions on the clicks
     */
    @Override
    public void displayFinalButton(@NonNull Button button, int... slots) {
        this.displayFinalButton(button, new Placeholders(), slots);
    }

    @Override
    public void displayFinalButton(@NotNull Button button, @NotNull Placeholders placeholders, int... slots) {
        final Player targetPlayer = this.getTargetPlayer();

        this.perfDebug.start("getCustomItemStack." + button.getName());
        ItemStack itemStack = button.getCustomItemStack(targetPlayer, button.isUseCache(), placeholders);
        this.perfDebug.end();

        int maxSlotSize = button.isPlayerInventory() ? 36 : this.inventory.size();

        if (button.getPlayerHead() != null && itemStack.getItemMeta() instanceof SkullMeta) {
            this.plugin.getScheduler().runAsync(w -> {
                var updatedItemStack = this.plugin.getInventoryManager().postProcessSkullItemStack(itemStack, button, this.player, placeholders);
                this.plugin.getScheduler().runAtEntity(this.player, w2 -> {
                    for (int slot : slots) {
                        if (slot < 0 || slot >= maxSlotSize) continue;

                        int animationSlot = this.toAnimationSlot(button.isPlayerInventory(), slot);

                        if (!this.replaceItemToReveal(animationSlot, updatedItemStack)) {
                            this.setAnimationSlotItem(animationSlot, updatedItemStack);
                        }
                    }
                });
            });
        }

        List<Integer> idleSlots = new ArrayList<>(slots.length);

        for (int slot : slots) {

            if (slot < 0) {
                Logger.info("slot is negative ! (" + slot + ") Button: " + button.getName() + " in inventory " + this.inventory.getFileName(), Logger.LogType.ERROR);
                continue;
            }

            if (slot >= maxSlotSize) {
                Logger.info("slot is out of range ! (" + slot + ") Button: " + button.getName() + " in inventory " + this.inventory.getFileName(), Logger.LogType.ERROR);
                continue;
            }

            this.perfDebug.start("addItem." + button.getName() + "[" + slot + "]");
            ItemButton itemButton = this.addItem(button.isPlayerInventory(), slot, itemStack);
            this.perfDebug.end();

            this.buttonsByAnimationSlot.put(this.toAnimationSlot(button.isPlayerInventory(), slot), button);

            if (button.isClickable()) {
                itemButton.setClick(event -> {

                    if (event.getClick() == ClickType.DOUBLE_CLICK) return;

                    if (!this.isClickTypeAllowed(button, event.getClick())) {
                        event.setCancelled(true);
                        return;
                    }

                    event.setCancelled(true);
                    button.onClick(this.player, event, this, slot, new Placeholders());

                    if (button.isRefreshOnClick()) {
                        this.cancel(slot);
                        this.buildButton(button.getMasterParentButton(), placeholders);
                    }

                    // Update buttons that need to be updated
                    this.updatedButtons.forEach(updatedButton -> this.buildButton(updatedButton, placeholders));
                });
                itemButton.setLeftClick(event -> button.onLeftClick(this.player, event, this, slot));
                itemButton.setRightClick(event -> button.onRightClick(this.player, event, this, slot));
                itemButton.setMiddleClick(event -> button.onMiddleClick(this.player, event, this, slot));
            }

            if (button.isDraggable()) {
                //If one button is draggable enable click
                super.setDisablePlayerInventoryClick(false);
            }

            int animationSlot = this.toAnimationSlot(button.isPlayerInventory(), slot);
            idleSlots.add(animationSlot);

            if (button.hasRefreshRequirement() || button.isUpdated()) {

                RefreshRequirement refreshRequirement = button.hasRefreshRequirement() ? button.getRefreshRequirement() : null;
                this.perfDebug.start("refreshCheck." + button.getName() + "[" + slot + "]");
                boolean needRefresh = refreshRequirement != null && refreshRequirement.needRefresh(this.player, button, this, new Placeholders());
                this.perfDebug.end();

                if (needRefresh || button.isUpdated()) {
                    long interval = refreshRequirement != null ? refreshRequirement.getUpdateInterval() : this.inventory.getUpdateInterval();
                    WrappedTask timerTask = this.plugin.getScheduler().runAtEntityTimer(this.player, () -> {
                        if (this.isClose()) {
                            this.cancelTimers();
                            return;
                        }

                        if (this.slotsOwnedByAnimation.contains(animationSlot)) {
                            return;
                        }

                        if (refreshRequirement != null && refreshRequirement.canRefresh(this.player, button, this, new Placeholders())) {
                            this.cancel(slot);
                            this.updateItemMeta(itemStack, button, refreshRequirement, slot, placeholders);
                        } else if (button.isUpdated()) {
                            this.handleUpdatedButton(button, itemStack, slot, placeholders);
                        }
                    }, interval, interval, TimeUnit.MILLISECONDS);

                    WrappedTask previousTask = this.timers.put(slot, timerTask);
                    if (previousTask != null) {
                        previousTask.cancel();
                    }
                }
            }
        }

        ButtonIdleAnimation idleAnimation = button.getIdleAnimation();
        if (idleAnimation != null && !idleSlots.isEmpty()) {
            this.startIdleAnimation(idleAnimation, idleSlots, itemStack, button.isUseCache());
        }
    }

    /**
     * Plays the opening animation of the inventory, if it has one and this open is a genuine one.
     */
    private void playOpenAnimation() {
        if (!(this.inventory instanceof ContainerInventory containerInventory)) return;

        SlotAnimationSettings settings = containerInventory.getSlotAnimation();
        if (settings == null) return;

        if (this.getOpenCause() == OpenCause.REFRESH) return;

        AnimationPlayMode playMode = settings.play();
        boolean tracksPlays = playMode.isOnce() || settings.cooldown() > 0;
        String historyKey = tracksPlays ? this.getAnimationHistoryKey() : null;
        InventoryManager inventoryManager = this.plugin.getInventoryManager();

        boolean historyInDatabase = settings.needsPersistence();
        if (historyInDatabase && !inventoryManager.isAnimationHistoryLoaded(this.player)) return;

        if (tracksPlays) {
            Long lastPlayedAt = historyInDatabase
                    ? inventoryManager.getAnimationPlayedAt(this.player, historyKey)
                    : (inventoryManager.hasPlayedAnimation(this.player, historyKey) ? 0L : null);

            if (!playMode.canPlay(lastPlayedAt, System.currentTimeMillis(), settings.cooldown())) return;
        }

        long duration = this.playAnimation(new Animation(this.inventory.key(), settings), AnimationPlayback.REVEAL, settings.freeze());

        if (!tracksPlays || duration <= 0) return;

        if (historyInDatabase) {
            inventoryManager.markAnimationPlayedForever(this.player, historyKey, System.currentTimeMillis());
        } else {
            inventoryManager.markAnimationPlayed(this.player, historyKey);
        }
    }

    @Override
    public long playAnimation(@NotNull Animation animation, @NotNull AnimationPlayback playback) {
        return this.playAnimation(animation, playback, playback == AnimationPlayback.REVEAL && animation.settings().freeze());
    }

    @Override
    public void stopAnimation() {
        RunningAnimation running = this.runningAnimation;
        if (running != null) running.skipToEnd();
    }

    @Override
    public void closeWithAnimation(@NotNull Runnable whenDone) {
        if (this.isClose() || !(this.inventory instanceof ContainerInventory containerInventory)) {
            whenDone.run();
            return;
        }

        SlotAnimationSettings settings = containerInventory.getCloseAnimation();
        if (settings == null) {
            whenDone.run();
            return;
        }

        this.stopAnimation();

        Runnable onEnd = () -> {
            if (this.isClose()) return;
            whenDone.run();
        };

        long duration = this.playAnimation(new Animation(this.inventory.key(), settings),
                AnimationPlayback.CONCEAL, settings.freeze(), onEnd);

        if (duration <= 0) whenDone.run();
    }

    /**
     * Compiles an animation and starts playing it.
     *
     * <p>In {@link AnimationPlayback#REVEAL} the items that were just built are hidden and put back
     * step by step; the buttons are built normally first, so click handlers and refresh tasks are
     * registered as usual and only the displayed ItemStacks are withheld. In
     * {@link AnimationPlayback#OVERLAY} nothing is hidden and only the temporary items play.</p>
     */
    @Override
    public long playAnimation(@NotNull Animation animation, @NotNull AnimationPlayback playback, boolean freeze) {
        return this.playAnimation(animation, playback, freeze, null);
    }

    /**
     * Plays an animation, running {@code onEnd} exactly once when it ends however it ends. It is not
     * run when the animation refuses to play, which the returned duration of 0 says.
     */
    private long playAnimation(@NotNull Animation animation, @NotNull AnimationPlayback playback, boolean freeze,
                               @Nullable Runnable onEnd) {
        SlotAnimationSettings settings = animation.settings();
        ItemStack[] contents = this.readAnimationSlotItems();

        int size = this.inventory.size();
        List<Integer> filledSlots = new ArrayList<>();
        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack itemStack = contents[slot];
            if (itemStack == null || itemStack.getType().isAir()) continue;

            if (AnimationSlots.isPlayer(size, slot) && !this.buttonsByAnimationSlot.containsKey(slot)) continue;

            filledSlots.add(slot);
        }

        Map<Integer, ButtonAnimationSettings> buttonSettingsBySlot = new HashMap<>();
        this.buttonsByAnimationSlot.forEach((slot, button) -> buttonSettingsBySlot.put(slot, button.getAnimationSettings()));

        AnimationContext context = new AnimationContext(contents.length, filledSlots, this.player, this.inventory,
                settings.seed().create(this.player == null ? null : this.player.getUniqueId(), animation.name()));
        if (!context.roll(settings.chance())) return 0;

        Requirement requirement = settings.requirement();
        if (requirement != null && !requirement.execute(this.player, null, this, new Placeholders())) return 0;

        AnimationVariant variant = context.pickVariant(settings.variants());
        if (variant != null) settings = variant.settings();

        this.perfDebug.start("animation.compile");
        CompiledAnimation compiled = AnimationCompiler.compile(contents.length, settings, filledSlots, buttonSettingsBySlot, playback, context);
        this.perfDebug.end();
        if (compiled.isEmpty()) return 0;

        long duration = compiled.duration();
        if (duration > Configuration.maxAnimationDuration) {
            Logger.info("The animation " + animation.name() + " lasts " + duration + "ms, over the configured maximum of " + Configuration.maxAnimationDuration + "ms. It plays, but it will not freeze the inventory.", Logger.LogType.WARNING);
            freeze = false;
        }

        AnimationStartEvent startEvent = new AnimationStartEvent(this.player, this, animation, playback, freeze);
        startEvent.call();
        if (startEvent.isCancelled()) return 0;
        freeze = startEvent.isFreeze();

        this.stopAnimation();

        List<AnimationStep> steps = compiled.steps();
        ItemStack[] itemsToReveal = contents.clone();

        RunningAnimation running = new RunningAnimation(animation, playback, steps, itemsToReveal, settings.endActions(), onEnd);
        this.runningAnimation = running;

        this.perfDebug.start("animation.hide");
        for (AnimationStep step : steps) {
            this.slotsOwnedByAnimation.add(step.slot());
            if (playback.hidesButtons() && step.action() == StepAction.REVEAL) {
                this.setAnimationSlotItem(step.slot(), null);
            }
        }
        this.perfDebug.end();

        if (freeze) {
            this.setFrozen(true);
        }

        this.runAnimationActions(settings.startActions());

        long startedAt = System.currentTimeMillis();
        long periodTicks = toTicks(this.getTimerPeriodMillis(settings, steps));

        running.timer = this.plugin.getScheduler().runAtEntityTimer(this.player, () -> {

            if (this.isClose()) {
                running.stop(false);
                return;
            }

            running.applyStepsDueBy(System.currentTimeMillis() - startedAt);
        }, 1, periodTicks);

        return duration;
    }

    private static long toTicks(long milliseconds) {
        return Math.max(1, milliseconds / 50);
    }

    private final class RunningAnimation {

        private final Animation animation;
        private final AnimationPlayback playback;
        private final List<AnimationStep> steps;
        private final ItemStack[] itemsToReveal;
        private final ItemStack[] drawnItems;

        private final AtomicInteger nextStepIndex = new AtomicInteger();

        private volatile WrappedTask timer;
        private final AtomicBoolean stopped = new AtomicBoolean();
        private final ProtectedItemCache protectedItems = new ProtectedItemCache();

        private final List<Action> endActions;
        
        private final Runnable onEnd;

        private RunningAnimation(Animation animation, AnimationPlayback playback, List<AnimationStep> steps,
                                 ItemStack[] itemsToReveal, List<Action> endActions, @Nullable Runnable onEnd) {
            this.onEnd = onEnd;
            this.animation = animation;
            this.playback = playback;
            this.steps = steps;
            this.itemsToReveal = itemsToReveal;
            this.drawnItems = playback.hidesButtons() ? new ItemStack[itemsToReveal.length] : itemsToReveal.clone();
            this.endActions = endActions;
        }

        /**
         * Records what the animation has just drawn in a slot.
         */
        private void recordDrawn(int slot, ItemStack itemStack) {
            if (slot >= 0 && slot < this.drawnItems.length) this.drawnItems[slot] = itemStack;
        }

        /**
         * Puts back whatever the animation is showing in a slot, over anything else written there.
         */
        private void redrawSlot(int slot) {
            if (slot >= 0 && slot < this.drawnItems.length) InventoryDefault.this.setAnimationSlotItem(slot, this.drawnItems[slot]);
        }

        /**
         * Returns whether a slot still holds what the animation last drew there.
         *
         * <p>It does not when a player has put something in it, which they can do on a draggable
         * button of a menu that does not freeze. Their item is theirs: the animation leaves the slot
         * alone rather than replacing it with the one it was going to reveal.</p>
         */
        private boolean isSlotAsDrawn(int slot) {
            if (slot < 0 || slot >= this.drawnItems.length) return true;

            ItemStack current = InventoryDefault.this.getAnimationSlotItem(slot);
            ItemStack expected = this.drawnItems[slot];

            if (current == null || current.getType().isAir()) return expected == null;
            return expected != null && current.isSimilar(expected);
        }

        /**
         * Replaces what will be displayed for a slot this animation has not revealed yet.
         *
         * @return true if the slot belongs to this animation
         */
        private boolean replaceItemToReveal(int slot, ItemStack itemStack) {
            if (this.stopped.get() || slot < 0 || slot >= this.itemsToReveal.length) return false;
            if (!InventoryDefault.this.slotsOwnedByAnimation.contains(slot)) return false;

            this.itemsToReveal[slot] = itemStack;
            return true;
        }

        /**
         * Applies every step whose time has come.
         */
        private void applyStepsDueBy(long elapsed) {
            if (this.stopped.get()) return;

            boolean soundPlayed = false;

            int index = this.nextStepIndex.get();
            while (index < this.steps.size() && this.steps.get(index).time() <= elapsed) {
                soundPlayed |= InventoryDefault.this.applyAnimationStep(this.steps.get(index), this.itemsToReveal, !soundPlayed);
                index++;
            }
            this.nextStepIndex.set(index);

            if (index >= this.steps.size()) this.stop(true);
        }

        /**
         * Applies everything that is left, at once, and stops.
         */
        private void skipToEnd() {
            if (this.stopped.get()) return;

            for (int index = this.nextStepIndex.getAndSet(this.steps.size()); index < this.steps.size(); index++) {
                InventoryDefault.this.applyAnimationStep(this.steps.get(index), this.itemsToReveal, false);
            }
            this.stop(false);
        }

        private void stop(boolean completed) {
            if (!this.stopped.compareAndSet(false, true)) return;

            if (this.timer != null) {
                this.timer.cancel();
            }
            if (InventoryDefault.this.runningAnimation == this) {
                InventoryDefault.this.runningAnimation = null;
            }

            InventoryDefault.this.slotsOwnedByAnimation.clear();
            InventoryDefault.this.setFrozen(false);

            new AnimationEndEvent(InventoryDefault.this.player, InventoryDefault.this, this.animation, this.playback, completed).call();
            InventoryDefault.this.runAnimationActions(this.endActions);

            if (this.onEnd != null) this.onEnd.run();
        }
    }

    /**
     * Cycles the items of an idle animation on one slot, for as long as the menu stays open.
     *
     * <p>This has nothing to do with the opening animation: there is no timeline and no freeze, the
     * button keeps working throughout. It only waits for the opening animation to hand the slot over
     * before touching it.</p>
     *
     * <p>Each frame lasts its own duration rather than the cycle running on one fixed period, so a
     * frame may be held while the next one only flashes past. A menu with few idle buttons gives
     * each a task of its own; one with {@link Configuration#sharedIdleTimerThreshold} or more drives
     * them all from a single timer, so a busy menu costs one task per viewer however many cycle.</p>
     *
     * <p>A button runs one cycle across all of its slots, so they change together and each frame
     * is built, protected and heard once, however many slots the button covers.</p>
     *
     * @param idleAnimation the animation to play
     * @param buttonSlots         the slots of the button, in the space of the animation
     * @param buttonItem     the real item of the button, put back when a cycle that does not loop ends
     */
    private void startIdleAnimation(ButtonIdleAnimation idleAnimation, List<Integer> buttonSlots, ItemStack buttonItem, boolean useCache) {
        IdleCycle previousCycle = this.findPreviousCycle(idleAnimation, buttonSlots);

        IdleCycle cycle = new IdleCycle(idleAnimation, List.copyOf(buttonSlots), buttonItem, useCache, previousCycle);
        for (int slot : cycle.buttonSlots) {
            this.releaseIdleSlot(slot);
            this.idleCyclesAwaitingRebuild.remove(slot);
            this.idleCycleBySlot.put(slot, cycle);
        }

        long firstFrameDelayMillis;
        if (previousCycle != null) {
            cycle.repaintLastFrame();
            firstFrameDelayMillis = Math.max(0, previousCycle.nextFrameAt - System.currentTimeMillis());
        } else {
            firstFrameDelayMillis = ThreadLocalRandom.current().nextLong(Math.max(1, idleAnimation.interval()));
        }
        this.scheduleIdleFrame(cycle, firstFrameDelayMillis);
    }

    /**
     * Finds the cycle a button ran before it was rebuilt, if it is the same animation.
     */
    @Nullable
    private IdleCycle findPreviousCycle(ButtonIdleAnimation idleAnimation, List<Integer> slots) {
        for (int slot : slots) {
            IdleCycle running = this.idleCycleBySlot.get(slot);
            if (running != null && running.animation == idleAnimation) return running;

            IdleCycle awaitingRebuild = this.idleCyclesAwaitingRebuild.get(slot);
            if (awaitingRebuild != null && awaitingRebuild.animation == idleAnimation) return awaitingRebuild;
        }
        return null;
    }

    /**
     * Runs the next frame of a cycle after a delay, on a task of its own or on the timer the menu
     * shares between its idle buttons.
     *
     * @param delayMillis milliseconds before the next frame
     */
    private void scheduleIdleFrame(IdleCycle cycle, long delayMillis) {
        cycle.nextFrameAt = System.currentTimeMillis() + delayMillis;

        if (this.useSharedIdleTimer) {
            this.addToSharedIdleTimer(cycle);
            return;
        }

        WrappedTask task = this.plugin.getScheduler().runAtEntityLater(this.player, () -> {
            long millisToNextFrame = cycle.showNextFrame();
            if (millisToNextFrame >= 0) this.scheduleIdleFrame(cycle, millisToNextFrame);
        }, toTicks(delayMillis));

        for (int slot : cycle.ownedSlots()) this.idleTasksBySlot.put(slot, task);
    }

    private void addToSharedIdleTimer(IdleCycle cycle) {
        synchronized (this.sharedIdleLock) {
            this.sharedIdleCycles.add(cycle);
            if (this.sharedIdleTimer == null) {
                this.sharedIdleTimer = this.plugin.getScheduler().runAtEntityTimer(this.player, this::tickSharedIdleCycles, 1, 1);
            }
        }
    }

    /**
     * One tick of the timer shared by the idle buttons of a busy menu: each cycle whose frame is due
     * moves on, the others wait. Frames land on the tick rather than to the millisecond.
     */
    private void tickSharedIdleCycles() {
        long now = System.currentTimeMillis();

        for (IdleCycle cycle : this.sharedIdleCycles) {
            if (now < cycle.nextFrameAt) continue;

            long millisToNextFrame = cycle.showNextFrame();
            if (millisToNextFrame < 0) {
                this.sharedIdleCycles.remove(cycle);
                continue;
            }
            long dueOnSchedule = cycle.nextFrameAt + millisToNextFrame;
            cycle.nextFrameAt = dueOnSchedule > now ? dueOnSchedule : now + millisToNextFrame;
        }

        synchronized (this.sharedIdleLock) {
            if (this.sharedIdleCycles.isEmpty() && this.sharedIdleTimer != null) {
                this.sharedIdleTimer.cancel();
                this.sharedIdleTimer = null;
            }
        }
    }

    /**
     * Takes a slot away from whichever idle cycle owns it. The cycle keeps running for the other
     * slots of its button, and is only cancelled once it owns none.
     */
    private void releaseIdleSlot(int slot) {
        IdleCycle owner = this.idleCycleBySlot.remove(slot);
        WrappedTask task = this.idleTasksBySlot.remove(slot);
        if (task != null && (owner == null || !this.idleCycleBySlot.containsValue(owner))) task.cancel();
    }

    private final class IdleCycle {

        private final ButtonIdleAnimation animation;
        private final List<Integer> buttonSlots;
        private final ItemStack buttonItem;
        private final boolean useCache;
        private final ProtectedItemCache protectedItems;

        private volatile int framesShown;
        private volatile long nextFrameAt;
        private volatile ItemStack paintedItem;

        private IdleCycle(ButtonIdleAnimation animation, List<Integer> buttonSlots, ItemStack buttonItem, boolean useCache,
                          @Nullable IdleCycle previousCycle) {
            this.animation = animation;
            this.buttonSlots = buttonSlots;
            this.buttonItem = buttonItem;
            this.useCache = useCache;

            if (previousCycle != null) {
                this.framesShown = previousCycle.framesShown;
                this.paintedItem = previousCycle.paintedItem;
                this.protectedItems = previousCycle.protectedItems;
            } else {
                this.protectedItems = new ProtectedItemCache();
            }
        }

        /**
         * Shows the next frame.
         *
         * @return milliseconds until the one after, or -1 when the cycle is over
         */
        private long showNextFrame() {
            InventoryDefault menu = InventoryDefault.this;

            List<Integer> owned = this.ownedSlots();
            if (owned.isEmpty()) return -1;

            if (menu.isClose()) {
                this.releaseSlots(owned);
                return -1;
            }

            for (int slot : owned) {
                if (menu.slotsOwnedByAnimation.contains(slot)) {
                    this.paintedItem = null;
                    return this.animation.interval();
                }
            }

            if (this.animation.isOver(this.framesShown)) {
                this.releaseSlots(owned);
                for (int slot : owned) menu.setAnimationSlotItem(slot, this.buttonItem);
                this.paintedItem = null;
                return -1;
            }

            IdleFrame frame = this.animation.frame(this.framesShown, ThreadLocalRandom.current());

            ItemStack built = frame.item().build(menu.getTargetPlayer(), this.useCache, menu.openPlaceholders);
            ItemStack protectedItem = this.protectedItems.protect(frame, built);

            if (protectedItem != this.paintedItem) {
                for (int slot : owned) menu.setAnimationSlotItem(slot, protectedItem);
                this.paintedItem = protectedItem;
            }

            if (frame.sound() != null && menu.player != null) {
                frame.sound().play(menu.player);
            }

            this.framesShown++;
            return frame.duration(this.animation.interval());
        }

        /**
         * Puts the frame last shown back on the slots, after a rebuild painted over it.
         */
        private void repaintLastFrame() {
            ItemStack lastFrame = this.paintedItem;
            if (lastFrame == null) return;

            List<Integer> owned = this.ownedSlots();
            for (int slot : owned) {
                if (InventoryDefault.this.slotsOwnedByAnimation.contains(slot)) {
                    this.paintedItem = null;
                    return;
                }
            }
            for (int slot : owned) InventoryDefault.this.setAnimationSlotItem(slot, lastFrame);
        }

        private List<Integer> ownedSlots() {
            List<Integer> owned = new ArrayList<>(this.buttonSlots.size());
            for (int slot : this.buttonSlots) {
                if (InventoryDefault.this.idleCycleBySlot.get(slot) == this) owned.add(slot);
            }
            return owned;
        }

        private void releaseSlots(List<Integer> owned) {
            for (int slot : owned) {
                InventoryDefault.this.idleCycleBySlot.remove(slot, this);
                InventoryDefault.this.idleTasksBySlot.remove(slot);
            }
        }
    }

    /**
     * The slot an animation uses for a slot of the menu, or of the player inventory.
     */
    private int toAnimationSlot(boolean playerInventory, int slot) {
        return playerInventory ? AnimationSlots.fromPlayer(this.inventory.size(), slot) : slot;
    }

    /**
     * The contents the animation is laid out over: the menu, then the player inventory as it is
     * displayed under it.
     */
    private ItemStack[] readAnimationSlotItems() {
        int size = this.inventory.size();
        ItemStack[] contents = new ItemStack[AnimationSlots.totalSize(size)];

        ItemStack[] menu = this.getSpigotInventory().getContents();
        System.arraycopy(menu, 0, contents, 0, Math.min(menu.length, size));

        if (this.player != null) {
            ItemStack[] player = this.player.getInventory().getContents();
            for (int slot = 0; slot < Math.min(player.length, AnimationSlots.PLAYER_SIZE); slot++) {
                contents[AnimationSlots.fromPlayer(size, slot)] = player[slot];
            }
        }
        return contents;
    }

    /**
     * Reads a slot of the animation space, from whichever inventory it belongs to.
     */
    private ItemStack getAnimationSlotItem(int slot) {
        int size = this.inventory.size();

        if (!AnimationSlots.isPlayer(size, slot)) return this.getSpigotInventory().getItem(slot);
        return this.player == null ? null : this.player.getInventory().getItem(AnimationSlots.toPlayer(size, slot));
    }

    /**
     * Writes to a slot of the animation space, in whichever inventory it belongs to.
     */
    private void setAnimationSlotItem(int slot, ItemStack itemStack) {
        int size = this.inventory.size();

        if (!AnimationSlots.isPlayer(size, slot)) {
            this.getSpigotInventory().setItem(slot, itemStack);
            return;
        }
        if (this.player == null) return;

        this.player.getInventory().setItem(AnimationSlots.toPlayer(size, slot), itemStack);
    }

    /**
     * Replaces what the animation will display for a slot it currently owns.
     *
     * <p>An item may be finished after the animation started: the texture of a head is looked up off
     * the main thread and lands whenever it lands. Without this the animation would reveal the copy
     * it took before that happened.</p>
     *
     * @param slot      the slot, in the space of the animation
     * @param itemStack the item to display when the slot is revealed
     * @return true if the animation owns the slot and was updated
     */
    private boolean replaceItemToReveal(int slot, ItemStack itemStack) {
        RunningAnimation running = this.runningAnimation;
        return running != null && running.replaceItemToReveal(slot, itemStack);
    }

    @Override
    public ItemButton addItem(boolean inPlayerInventory, int slot, ItemStack itemStack, boolean enableAntiDupe) {
        ItemButton button = super.addItem(inPlayerInventory, slot, itemStack, enableAntiDupe);
        if (button == null || itemStack == null) return button;

        int animationSlot = this.toAnimationSlot(inPlayerInventory, slot);

        RunningAnimation running = this.runningAnimation;
        if (running != null && running.replaceItemToReveal(animationSlot, itemStack)) {
            running.redrawSlot(animationSlot);
        }
        return button;
    }

    /**
     * Runs the actions an animation attached to its start or to its end.
     *
     * <p>Always on the thread of the player, since an animation may end from a timer, from a click
     * taking over, or from the inventory closing.</p>
     */
    private void runAnimationActions(List<Action> actions) {
        if (actions.isEmpty() || this.player == null) return;

        this.plugin.getScheduler().runAtEntity(this.player, w -> {
            Placeholders placeholders = new Placeholders();
            actions.forEach(action -> action.preExecute(this.player, null, this, placeholders));
        });
    }

    /**
     * Applies one step of the animation.
     *
     * @param allowSound whether the step may play its sound
     * @return true if it played one
     */
    private boolean applyAnimationStep(AnimationStep step, ItemStack[] itemsToReveal, boolean allowSound) {
        int slot = step.slot();

        boolean soundPlayed = false;
        if (allowSound && step.sound() != null && this.player != null) {
            step.sound().play(this.player);
            soundPlayed = true;
        }

        RunningAnimation running = this.runningAnimation;

        boolean slotAsDrawn = running == null || running.isSlotAsDrawn(slot);

        switch (step.action()) {
            case REVEAL -> {
                if (slotAsDrawn) {
                    this.setAnimationSlotItem(slot, itemsToReveal[slot]);
                    if (running != null) running.recordDrawn(slot, itemsToReveal[slot]);
                }
                this.slotsOwnedByAnimation.remove(slot);
            }
            case SET -> {
                MenuItemStack itemStack = step.item();
                if (itemStack != null && slotAsDrawn) {
                    ItemStack built = itemStack.build(this.getTargetPlayer(), step.useCache(), this.openPlaceholders);
                    ItemStack protectedItem = running != null
                            ? running.protectedItems.protect(itemStack, built)
                            : this.plugin.getDupeManager().protectItem(built);

                    this.setAnimationSlotItem(slot, protectedItem);
                    if (running != null) running.recordDrawn(slot, protectedItem);
                }
            }
            case CLEAR -> {
                if (slotAsDrawn) {
                    this.setAnimationSlotItem(slot, null);
                    if (running != null) running.recordDrawn(slot, null);
                }
            }
        }
        return soundPlayed;
    }

    /**
     * Protects built items, skipping the ones already protected.
     *
     * <p>Protecting copies an item's meta and writes it back. A cached build hands back the very
     * same instance each time, whether it lands on nine slots at once or on every turn of an idle
     * cycle, and protecting it again changes nothing. Items rebuilt each time are new instances,
     * and are always protected.</p>
     */
    private final class ProtectedItemCache {

        private final Map<Object, ItemStack[]> lastBuiltAndProtectedBySource = new ConcurrentHashMap<>();

        private ItemStack protect(Object source, ItemStack built) {
            ItemStack[] lastBuiltAndProtected = this.lastBuiltAndProtectedBySource.get(source);
            if (lastBuiltAndProtected != null && lastBuiltAndProtected[0] == built) return lastBuiltAndProtected[1];

            ItemStack protectedItem = InventoryDefault.this.plugin.getDupeManager().protectItem(built);
            this.lastBuiltAndProtectedBySource.put(source, new ItemStack[]{built, protectedItem});
            return protectedItem;
        }
    }

    /**
     * Gets why this inventory was opened. The cause is passed among the opening arguments by the
     * inventory manager, and is missing when the inventory is opened by older API calls.
     */
    private OpenCause getOpenCause() {
        if (this.args == null) return OpenCause.OPEN;
        for (Object arg : this.args) {
            if (arg instanceof OpenCause openCause) return openCause;
        }
        return OpenCause.OPEN;
    }

    /**
     * Gets the key identifying this inventory in the per player animation state. The owning plugin
     * is part of it, so two plugins may ship a file with the same name.
     */
    private String getAnimationHistoryKey() {
        return this.inventory.getPlugin().getName() + ":" + this.inventory.getFileName();
    }

    /**
     * Gets how often the animation task runs. Steps carry an absolute time, so this only decides the
     * granularity: it follows the shortest gap the animation actually asked for.
     *
     * <p>The groups are not enough on their own: the keyframes of a temporary item may be far closer
     * together than the interval of any group, and would otherwise arrive in clumps.</p>
     */
    private long getTimerPeriodMillis(SlotAnimationSettings settings, List<AnimationStep> steps) {
        long shortestGap = settings.defaults().interval();
        for (SlotAnimationGroup group : settings.groups().values()) {
            shortestGap = Math.min(shortestGap, group.interval());
        }

        long previousStepTime = -1;
        for (AnimationStep step : steps) {
            if (previousStepTime >= 0 && step.time() > previousStepTime) {
                shortestGap = Math.min(shortestGap, step.time() - previousStepTime);
            }
            previousStepTime = step.time();
        }
        return Math.max(1, shortestGap);
    }

    @Override
    public void cancel(int slot) {
        WrappedTask task = this.timers.remove(slot);
        if (task != null) {
            task.cancel();
        }

        IdleCycle cycle = this.idleCycleBySlot.get(slot);
        if (cycle != null) this.idleCyclesAwaitingRebuild.put(slot, cycle);
        this.releaseIdleSlot(slot);
    }

    private void cancelTimers() {
        this.timers.values().forEach(WrappedTask::cancel);
        this.timers.clear();
    }

    @Override
    public @NotNull PerformanceDebug getPerformanceDebug() {
        return this.perfDebug != null ? this.perfDebug : PerformanceDebug.disabled();
    }

    @Override
    public Inventory getMenuInventory() {
        return this.inventory;
    }

    @Override
    public @NonNull List<Inventory> getOldInventories() {
        return this.oldInventories == null ? new ArrayList<>() : this.oldInventories;
    }

    @Override
    public int getMaxPage() {
        return this.maxPage;
    }

    @Override
    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    @Override
    public @NonNull List<Button> getButtons() {
        return this.buttons;
    }

    private void updateItemMeta(ItemStack itemStack, Button button, RefreshRequirement refreshRequirement, int slot, @NotNull Placeholders placeholders) {
        boolean updated = this.applyMetaUpdates(button, itemStack, refreshRequirement.isRefreshLore(), refreshRequirement.isRefreshName());
        if (!updated) {
            return;
        }

        this.getSpigotInventory().setItem(slot, itemStack);

        if (refreshRequirement.isRefreshButton()) {
            this.buildButton(button.getMasterParentButton(), placeholders);
        }
    }

    private void handleUpdatedButton(Button button, ItemStack itemStack, int slot, @NotNull Placeholders placeholders) {
        Button masterButton = button.getMasterParentButton();

        if (button.isUpdatedMasterButton()) {
            this.cancel(slot);
            this.buildButton(masterButton, placeholders);
            return;
        }

        if (!this.applyMetaUpdates(button, itemStack, true, true)) {
            return;
        }

        this.getSpigotInventory().setItem(slot, itemStack);
    }

    private boolean applyMetaUpdates(Button button, ItemStack itemStack, boolean refreshLore, boolean refreshName) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        }

        final Player targetPlayer = this.getTargetPlayer();
        if (targetPlayer == null) {
            return false;
        }

        if (refreshLore) {
            List<String> lore = button.buildLore(targetPlayer);
            if (!lore.isEmpty()) {
                this.plugin.getMetaUpdater().updateLore(itemMeta, this.papi(lore, targetPlayer, false), button.getItemStack().getLoreType());
            }
        }

        if (refreshName) {
            String displayName = button.buildDisplayName(targetPlayer);
            if (displayName != null) {
                this.plugin.getMetaUpdater().updateDisplayName(itemMeta, this.papi(displayName, targetPlayer, false), targetPlayer);
            }
        }

        itemStack.setItemMeta(itemMeta);
        return true;
    }

    private List<Inventory> extractOldInventories(Object[] args) {
        if (args.length < 2 || !(args[1] instanceof List<?> inventories)) {
            return new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        List<Inventory> castedInventories = (List<Inventory>) inventories;
        return new ArrayList<>(castedInventories);
    }

    private Player getTargetPlayer() {
        if (this.inventory == null || this.player == null) {
            return this.player;
        }
        String targetPlaceholder = this.inventory.getTargetPlayerNamePlaceholder();
        if (targetPlaceholder == null || targetPlaceholder.isEmpty()) {
            return this.player;
        }
        String targetName = this.papi(targetPlaceholder, this.player, false);
        if (targetName == null || targetName.isEmpty()) {
            return this.player;
        }
        Player targetPlayer = Bukkit.getPlayer(targetName);
        return targetPlayer != null ? targetPlayer : this.player;
    }

    /**
     * Whether a button should react to a given click type.
     *
     * <p>A click type is allowed when it is listed in {@code all-clicks-type}, or when one of the
     * button's own click-requirements names it explicitly. The second case matters so that a
     * requirement written with {@code click_type: [DROP]} keeps working: the admin asked for that
     * click type, so the button has a requirement covering it.</p>
     *
     * @param button    The button being clicked.
     * @param clickType The click type received.
     * @return True when the click should be dispatched to the button.
     */
    private boolean isClickTypeAllowed(Button button, ClickType clickType) {
        if (!Configuration.enforceClickTypes) return true;

        if (Configuration.allClicksType.contains(clickType)) return true;

        List<Requirement> requirements = button.getClickRequirements();
        if (requirements != null) {
            for (Requirement requirement : requirements) {
                if (requirement.getClickTypes().contains(clickType)) return true;
            }
        }

        return false;
    }
}
