package fr.maxlego08.menu.api.utils.dialogs.record;

import fr.maxlego08.menu.api.requirement.Requirement;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.temporal.TemporalAmount;
import java.util.List;

public record ActionButtonRecord(@NotNull String label, @NotNull String tooltip, int width, @NotNull List<Requirement> actions, int usageLimit, @Nullable TemporalAmount actionDurationLimit) {
    public ActionButtonRecord parse(@NotNull Player player) {
        return new ActionButtonRecord(this.parsePlaceholder(this.label,player), this.parsePlaceholder(this.tooltip,player), this.width, this.actions, this.usageLimit, this.actionDurationLimit);
    }
    private String parsePlaceholder(@NotNull String text,@NotNull Player player) {
        return text.isEmpty() ? "" : PlaceholderAPI.setPlaceholders(player, text);
    }
}
