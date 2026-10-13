package fr.maxlego08.menu.hooks.dialogs.inventory;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import io.papermc.paper.registry.data.dialog.ActionButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

/**
 * Tracks the {@code dialog_list} dialogs being built on the current thread. A list builds the
 * dialogs it references inline, so this is what stops a list that references itself, directly
 * or through another list, and what hands the back button to the dialogs a list builds.
 */
final class DialogListBuildContext {

    private static final ThreadLocal<Deque<Frame>> FRAMES = ThreadLocal.withInitial(ArrayDeque::new);

    private DialogListBuildContext() {
    }

    static boolean isBuilding(@NotNull DialogInventory dialog) {
        for (Frame frame : FRAMES.get()) {
            if (frame.list() == dialog) {
                return true;
            }
        }
        return false;
    }

    static boolean isTooDeep() {
        return FRAMES.get().size() >= Configuration.dialogListMaxDepth;
    }

    /**
     * @return the back button of the list whose entries are being built, or null outside a list
     */
    static @Nullable ActionButton currentBackButton() {
        Frame frame = FRAMES.get().peek();
        return frame == null ? null : frame.backButton();
    }

    static <T> T within(@NotNull DialogInventory list, @Nullable ActionButton backButton, @NotNull Supplier<T> supplier) {
        Deque<Frame> frames = FRAMES.get();
        frames.push(new Frame(list, backButton));
        try {
            return supplier.get();
        } finally {
            frames.pop();
            if (frames.isEmpty()) {
                FRAMES.remove();
            }
        }
    }

    private record Frame(DialogInventory list, @Nullable ActionButton backButton) {
    }
}
