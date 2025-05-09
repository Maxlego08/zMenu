package fr.maxlego08.menu.save;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.enums.MessageType;
import fr.maxlego08.menu.zcore.enums.Message;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * The MessageLoader class extends YamlUtils and implements Savable to manage message configurations.
 * This class is responsible for loading and saving custom messages to a YAML file for a Bukkit plugin.
 */
public class MessageLoader {

    private final List<Message> loadedMessages = new ArrayList<>();
    private final MenuPlugin plugin;

    public MessageLoader(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    public void save() {

        File file = new File(this.plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        for (Message message : Message.values()) {

            String path = message.name().toLowerCase().replace("_", "-");

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

        try {
            configuration.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        loadMessages(configuration);
    }

    public void load() {

        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            this.save();
            return;
        }

        loadMessages(YamlConfiguration.loadConfiguration(file));
    }

    private void loadMessages(YamlConfiguration configuration) {

        this.loadedMessages.clear();

        for (String key : configuration.getKeys(false)) {
            loadMessage(configuration, key);
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
    private void loadMessage(YamlConfiguration configuration, String key) {
        try {

            Message message = Message.valueOf(key.toUpperCase().replace("-", "_"));

            if (configuration.contains(key + ".type")) {

                MessageType messageType = MessageType.valueOf(configuration.getString(key + ".type", "TCHAT").toUpperCase());
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
