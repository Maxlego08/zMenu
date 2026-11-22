package fr.maxlego08.menu.save;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import fr.maxlego08.menu.zcore.logger.Logger;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.enums.MessageType;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.zcore.utils.VersionedYamlFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * The MessageLoader class extends YamlUtils and implements Savable to manage message configurations.
 * This class is responsible for loading and saving custom messages to a YAML file for a Bukkit plugin.
 */
public class MessageLoader {

    private final List<Message> loadedMessages = new ArrayList<>();
    private final ZMenuPlugin plugin;

    public MessageLoader(ZMenuPlugin plugin) {
        this.plugin = plugin;
    }

    private YamlDocument document;

    /**
     * Adds the messages the file does not have yet.
     */
    public void save() {
        if (this.document == null) return;
        addMissingMessages(this.document);
        try {
            this.document.save();
        } catch (IOException exception) {
            Logger.error(exception);
        }
        this.loadMessages(this.document);
    }

    /**
     * Writes every message of the enum the section does not have yet, in the form messages.yml uses.
     */
    public static void addMissingMessages(Section configuration) {
        for (Message message : Message.values()) {

            String path = getPath(message);

            if (configuration.contains(path)) continue;

            if (message.getType() != MessageType.TCHAT) {
                configuration.set(path + ".type", message.getType().name());
                configuration.set(path + ".message", message.getMessage());
            }
            if (message.getType().equals(MessageType.TCHAT) || message.getType().equals(MessageType.ACTION) || message.getType().equals(MessageType.CENTER)) {

                if (message.isMessage()) {
                    if (message.getType() != MessageType.TCHAT) {
                        configuration.set(path + ".messages", message.getMessages());
                    } else {
                        configuration.set(path, message.getMessages());
                    }
                } else {
                    if (message.getType() != MessageType.TCHAT) {
                        configuration.set(path + ".message", message.getMessage());
                    } else {
                        configuration.set(path, message.getMessage());
                    }
                }
            }
        }
    }

    public void load() {

        File file = new File(this.plugin.getDataFolder(), "messages.yml");
        try {
            // Each message keeps the form the user gave it, only the messages zMenu no longer has are removed
            Set<String> messages = Arrays.stream(Message.values()).map(MessageLoader::getPath).collect(Collectors.toSet());
            this.document = VersionedYamlFile.open(file, () -> this.plugin.getResource("messages.yml"), this.plugin.getLogger(), messages, false, Map.of());
        } catch (Exception exception) {
            Logger.error(exception);
            return;
        }

        this.loadMessages(this.document);
    }

    public static String getPath(Message message) {
        return message.name().toLowerCase(Locale.ROOT).replace("_", "-");
    }

    private void loadMessages(YamlDocument configuration) {

        this.loadedMessages.clear();

        for (String key : configuration.getRoutesAsStrings(false)) {
            this.loadMessage(configuration, key);
        }

        boolean canSave = false;
        for (Message message : Message.values()) {

            if (!this.loadedMessages.contains(message)) {
                canSave = true;
                break;
            }
        }

        // Allows you to save new parameters
        if (canSave) {
            this.save();
        }
    }

    /**
     * Loads a single message from the given YAML configuration.
     *
     * @param configuration The YAML configuration to load the message from.
     * @param key           The key under which the message is stored.
     */
    private void loadMessage(YamlDocument configuration, String key) {
        try {

            Message message = Message.valueOf(key.toUpperCase(Locale.ROOT).replace("-", "_"));

            if (configuration.contains(key + ".type")) {

                MessageType messageType = MessageType.valueOf(configuration.getString(key + ".type", "TCHAT").toUpperCase(Locale.ROOT));
                message.setType(messageType);
                switch (messageType) {
                    case ACTION:
                    case TCHAT_AND_ACTION: {
                        message.setMessage(configuration.getString(key + ".message"));
                        break;
                    }
                    case CENTER:
                    case TCHAT:
                    case WITHOUT_PREFIX: {
                        List<String> messages = configuration.getStringList(key + ".messages");
                        if (messages.isEmpty()) {
                            message.setMessage(configuration.getString(key + ".message"));
                        } else message.setMessages(messages);
                        break;
                    }
                }

            } else {
                message.setType(MessageType.TCHAT);
                List<String> messages = configuration.getStringList(key);
                if (messages.isEmpty()) {
                    message.setMessage(configuration.getString(key));
                } else message.setMessages(messages);
            }

            this.loadedMessages.add(message);
        } catch (Exception ignored) {
        }
    }

}
