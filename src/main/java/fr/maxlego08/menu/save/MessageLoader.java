package fr.maxlego08.menu.save;

import fr.maxlego08.menu.zcore.logger.Logger;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.enums.MessageType;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.zcore.utils.VersionedYamlFile;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

    public void save() {

        File file = new File(this.plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException exception) {
                Logger.error(exception);
            }
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        this.addMissingMessages(configuration);

        try {
            configuration.save(file);
        } catch (IOException exception) {
            Logger.error(exception);
        }

        this.loadMessages(configuration);
    }

    private void addMissingMessages(YamlConfiguration configuration) {
        for (Message message : Message.values()) {

            String path = this.getPath(message);

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
            Set<String> messages = Arrays.stream(Message.values()).map(this::getPath).collect(Collectors.toSet());
            VersionedYamlFile.open(file, this::getDefaults, this.plugin.getLogger(), messages, Map.of());
        } catch (Exception exception) {
            Logger.error(exception);
        }
        if (!file.exists()) {
            this.save();
            return;
        }

        this.loadMessages(YamlConfiguration.loadConfiguration(file));
    }

    /**
     * There is no messages.yml in the jar, the defaults are the messages of the enum.
     */
    private InputStream getDefaults() {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set(VersionedYamlFile.VERSION_ROUTE, 1);
        this.addMissingMessages(configuration);
        return new ByteArrayInputStream(configuration.saveToString().getBytes(StandardCharsets.UTF_8));
    }

    private String getPath(Message message) {
        return message.name().toLowerCase(Locale.ROOT).replace("_", "-");
    }

    private void loadMessages(YamlConfiguration configuration) {

        this.loadedMessages.clear();

        for (String key : configuration.getKeys(false)) {
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
    private void loadMessage(YamlConfiguration configuration, String key) {
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
