package fr.maxlego08.menu.api.utils.record.dialogs;

import org.jetbrains.annotations.NotNull;

/**
 * One button of a {@code dialog_list} dialog.
 *
 * @param reference the zMenu dialog name ({@code name} or {@code plugin:name}) or the namespaced key of a registry dialog
 * @param source    where the reference is looked up
 */
public record DialogListEntry(@NotNull String reference, @NotNull Source source) {

    public static DialogListEntry dialog(@NotNull String name) {
        return new DialogListEntry(name, Source.ZMENU);
    }

    public static DialogListEntry registry(@NotNull String key) {
        return new DialogListEntry(key, Source.REGISTRY);
    }

    public enum Source {
        /**
         * A dialog loaded by zMenu, found with {@code DialogManager#getDialog(String)}.
         */
        ZMENU,
        /**
         * A vanilla or datapack dialog from the server's dialog registry, such as {@code minecraft:server_links}.
         */
        REGISTRY
    }
}
