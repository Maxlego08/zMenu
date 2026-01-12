package fr.maxlego08.menu.api.utils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public enum OpGrantMethod {
    ATTACHMENT {
        @Override
        public void execute(@NotNull Player player, @NotNull Plugin plugin, @NotNull Runnable action) {
            var attachment = player.addAttachment(plugin);
            try {
                attachment.setPermission("*", true);
                action.run();
            } finally {
                player.removeAttachment(attachment);
            }
        }
    },
    SET_OP {
        @Override
        public void execute(@NotNull Player player, @NotNull Plugin plugin, @NotNull Runnable action) {
            boolean wasOp = player.isOp();
            try {
                player.setOp(true);
                action.run();
            } finally {
                player.setOp(wasOp);
            }
        }
    },
    BOTH {
        @Override
        public void execute(@NotNull Player player, @NotNull Plugin plugin, @NotNull Runnable action) {
            boolean wasOp = player.isOp();
            var attachment = player.addAttachment(plugin);
            try {
                player.setOp(true);
                attachment.setPermission("*", true);
                action.run();
            } finally {
                player.removeAttachment(attachment);
                player.setOp(wasOp);
            }
        }
    }
    ;

    public abstract void execute(@NotNull Player player,@NotNull Plugin plugin,@NotNull Runnable action);
}
