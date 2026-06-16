package fr.maxlego08.menu.api.context;

import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import org.bukkit.entity.Player;

import java.util.List;

public class DialogRenderContext<T> {
    private final List<T> content;
    private final Player player;
    private final DialogInventory dialogInventory;
    private final PaperMetaUpdater paperMetaUpdater;

    public DialogRenderContext(List<T> content, Player player, DialogInventory dialogInventory, PaperMetaUpdater paperMetaUpdater) {
        this.content = content;
        this.player = player;
        this.dialogInventory = dialogInventory;
        this.paperMetaUpdater = paperMetaUpdater;
    }

    public List<T> getContent() {
        return content;
    }

    public Player getPlayer() {
        return player;
    }

    public DialogInventory getDialogInventory() {
        return dialogInventory;
    }

    public PaperMetaUpdater getPaperMetaUpdater() {
        return paperMetaUpdater;
    }
}
