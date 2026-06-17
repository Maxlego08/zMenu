package fr.maxlego08.menu.api.utils.record.dialogs;

import fr.maxlego08.menu.api.utils.dialogs.ZDialogAction;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record ActionButtonRecord(
        @NotNull String label,
        @NotNull String tooltip,
        int width,
        @NotNull ZDialogAction action) {
    public ActionButtonRecord parse(@NotNull Player player) {
        return new ActionButtonRecord(this.parsePlaceholder(this.label,player), this.parsePlaceholder(this.tooltip,player), this.width, this.action);
    }
    private String parsePlaceholder(@NotNull String text,@NotNull Player player) {
        return text.isEmpty() ? "" : PlaceholderAPI.setPlaceholders(player, text);
    }
}
