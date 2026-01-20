package fr.maxlego08.menu.api.utils;

import fr.maxlego08.menu.api.enums.MessageType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Abstraction for messages in the zMenu API, supporting typed and multi-line messages and metadata.
 */
public interface IMessage {

    @NotNull
    String getMessage();

    @NotNull
    List<String> getMessages();

    @NotNull
    MessageType getType();

}
