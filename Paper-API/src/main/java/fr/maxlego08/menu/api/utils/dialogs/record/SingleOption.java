package fr.maxlego08.menu.api.utils.dialogs.record;

import org.jetbrains.annotations.NotNull;

public record SingleOption(@NotNull String id,@NotNull String display, boolean initialValue) {
}
