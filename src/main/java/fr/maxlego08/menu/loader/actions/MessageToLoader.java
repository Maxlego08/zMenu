package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.MessageToAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class MessageToLoader extends ActionLoader {

    public MessageToLoader() {
        super("message-to", "messages-to");
    }

    @Override
    public @Nullable Action load(@NotNull String path, @NotNull TypedMapAccessor accessor, @NotNull File file) {
        boolean miniMessage = accessor.getBoolean("minimessage", accessor.getBoolean("mini-message", true));
        List<String> messages = MessageLoader.extractMessages(accessor);
        String targetPlayer = accessor.getString("target-player", "");
        return new MessageToAction(messages, miniMessage, targetPlayer);
    }
}
