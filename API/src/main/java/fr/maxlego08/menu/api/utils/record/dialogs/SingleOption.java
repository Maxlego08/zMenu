package fr.maxlego08.menu.api.utils.record.dialogs;

import org.jetbrains.annotations.NotNull;

public record SingleOption(@NotNull String id,@NotNull String display, boolean initialValue) {
}
