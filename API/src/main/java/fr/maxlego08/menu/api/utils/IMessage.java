package fr.maxlego08.menu.api.utils;

import fr.maxlego08.menu.api.enums.MessageType;

import java.util.List;

public interface IMessage {

    String getMessage();

    List<String> getMessages();

    MessageType getType();

}
