package fr.maxlego08.menu.command.commands.dialogs;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.DialogManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.buttons.dialogs.body.DialogCanvasBody;
import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.dialog.canvas.CanvasManager;
import fr.maxlego08.menu.api.dialog.canvas.GeneratedPack;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommandDialogCanvas extends SubCommand<ZMenuPlugin> {

    public CommandDialogCanvas(ZMenuPlugin plugin) {
        super(plugin, "canvas");
        this.setPermission(Permission.ZMENU_DIALOG_CANVAS.getPermission());
        this.addOptionalArgument("player", ArgumentTypes.player());
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        CommandSender sender = commandDispatch.getSender();
        CanvasManager canvasManager = this.plugin.getCanvasManager();
        if (canvasManager == null) {
            MessageUtils.message(this.plugin, sender, Message.DIALOG_CANVAS_DISABLED);
            return CommandResultType.SUCCESS;
        }

        Optional<Player> target = commandDispatch.resolvePlayer("player");
        if (target.isPresent()) {
            this.sendPlayer(sender, canvasManager, target.get());
            return CommandResultType.SUCCESS;
        }

        int dialogs = 0;
        int canvases = 0;
        int staticCanvases = 0;
        DialogManager dialogManager = this.plugin.getDialogManager();
        if (dialogManager != null) {
            for (DialogInventory dialog : dialogManager.getDialogs()) {
                int before = canvases;
                for (BodyButton body : dialog.getBodyButtons()) {
                    for (Button button = body; button != null; button = button.getElseButton()) {
                        if (!(button instanceof DialogCanvasBody canvas)) continue;
                        canvases++;
                        if (canvas.isStatic()) staticCanvases++;
                    }
                }
                if (canvases > before) dialogs++;
            }
        }

        Map<String, List<String>> problems = canvasManager.getProblems();
        int problemCount = problems.values().stream().mapToInt(List::size).sum();
        MessageUtils.message(this.plugin, sender, Message.DIALOG_CANVAS_INFO,
                "%target%", canvasManager.getActiveTarget().map(packTarget -> packTarget.key().asString()).orElse("none, add the files by hand"),
                "%pack%", canvasManager.getGeneratedPack().map(CommandDialogCanvas::describe).orElse("not generated yet"),
                "%canvases%", canvases, "%dialogs%", dialogs, "%static%", staticCanvases, "%problems%", problemCount);
        problems.forEach((dialog, list) -> list.forEach(problem -> MessageUtils.message(this.plugin, sender, Message.DIALOG_CANVAS_PROBLEM,
                "%dialog%", dialog, "%problem%", problem)));
        return CommandResultType.SUCCESS;
    }

    private static @NotNull String describe(@NotNull GeneratedPack pack) {
        if (pack.isEmpty()) return "empty, no dialog uses a canvas";
        return "SHA-1 " + pack.sha1().substring(0, Math.min(12, pack.sha1().length())) + (pack.changed() ? ", changed at the last generation" : "");
    }

    private void sendPlayer(@NotNull CommandSender sender, @NotNull CanvasManager canvasManager, @NotNull Player player) {
        String pack = canvasManager.getResourcePackStatus(player.getUniqueId())
                .map(loaded -> loaded ? "loaded" : "declined or failed")
                .orElse("nothing reported");
        MessageUtils.message(this.plugin, sender, Message.DIALOG_CANVAS_PLAYER,
                "%player%", player.getName(),
                "%version%", this.plugin.getClientVersionManager().getClientVersion(player),
                "%bedrock%", canvasManager.isBedrockPlayer(player) ? "yes" : "no",
                "%pack%", pack,
                "%canvas%", canvasManager.canSeeCanvas(player) ? "yes" : "no, the fallback is shown",
                "%state%", describe(canvasManager.getStateService().all(player.getUniqueId())));
    }

    private static @NotNull String describe(@NotNull Map<String, Map<String, String>> state) {
        if (state.isEmpty()) return "none";
        return String.join("; ", state.entrySet().stream()
                .map(dialog -> dialog.getKey() + " (" + String.join(", ", dialog.getValue().entrySet().stream()
                        .map(value -> value.getKey() + "=" + value.getValue()).toList()) + ")")
                .toList());
    }
}
