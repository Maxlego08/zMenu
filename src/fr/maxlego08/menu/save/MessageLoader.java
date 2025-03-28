package fr.maxlego08.menu.save;

import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.api.enums.MessageType;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.storage.Persist;
import fr.maxlego08.menu.zcore.utils.storage.Savable;
import fr.maxlego08.menu.zcore.utils.yaml.YamlUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The MessageLoader class extends YamlUtils and implements Savable to manage message configurations.
 * This class is responsible for loading and saving custom messages to a YAML file for a Bukkit plugin.
 */
public class MessageLoader extends YamlUtils implements Savable {

    private final List<Message> loadedMessages = new ArrayList<>();

    /**
     * Constructor for MessageLoader.
     *
     * @param plugin The JavaPlugin instance associated with this loader.
     */
    public MessageLoader(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Saves messages to the configuration file.
     *
     * @param persist The persist instance used for saving the data.
     */
    @Override
    public void save(Persist persist) {

        if (persist != null) return;

        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration configuration = getConfig(file);
        for (Message message : Message.values()) {

            if (!message.isUse()) continue;

            String path = "messages." + message.name().toLowerCase().replace("_", ".");

            if (configuration.contains(path)) continue;

            if (message.getType() != MessageType.TCHAT) {
                configuration.set(path + ".type", message.getType().name());
            }
            if (message.getType().equals(MessageType.TCHAT) || message.getType().equals(MessageType.ACTION) || message.getType().equals(MessageType.CENTER)) {

                if (message.isMessage()) {
                    configuration.set(path + ".messages", colorReverse(message.getMessages()));
                } else {
                    configuration.set(path + ".message", colorReverse(message.getMessage()));
                }
            } else if (message.getType().equals(MessageType.TITLE)) {

                configuration.set(path + ".title", colorReverse(message.getTitle()));
                configuration.set(path + ".subtitle", colorReverse(message.getSubTitle()));
                configuration.set(path + ".fade-in-time", message.getStart());
                configuration.set(path + ".show-time", message.getTime());
                configuration.set(path + ".fade-out-time", message.getEnd());
            }
        }

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Loads messages from the configuration file.
     *
     * @param persist The persist instance used for loading the data.
     */
    @Override
    public void load(Persist persist) {

        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            this.save(null);
            return;
        }

        YamlConfiguration configuration = getConfig(file);

        if (!configuration.contains("messages")) {
            this.save(null);
            return;
        }

        this.loadedMessages.clear();

        for (String key : configuration.getConfigurationSection("messages.").getKeys(false)) {
            loadMessage(configuration, "messages." + key);
        }

        boolean canSave = false;
        for (Message message : Message.values()) {

            if (!this.loadedMessages.contains(message) && message.isUse()) {
                canSave = true;
                break;
            }
        }

        // Allows you to save new parameters
        if (canSave) {
            Logger.info("Save the message file, add new settings");
            this.save(null);
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

            MessageType messageType = MessageType.valueOf(configuration.getString(key + ".type", "TCHAT").toUpperCase());
            String keys = key.substring("messages.".length());
            Message enumMessage = Message.valueOf(keys.toUpperCase().replace(".", "_"));
            enumMessage.setType(messageType);

            this.loadedMessages.add(enumMessage);

            switch (messageType) {
                case ACTION: {
                    String message = configuration.getString(key + ".message");
                    enumMessage.setMessage(color(message));
                    break;
                }
                case CENTER:
                case TCHAT: {
                    if (configuration.contains(key + ".messages")) {
                        List<String> messages = configuration.getStringList(key + ".messages");
                        enumMessage.setMessages(color(messages));
                        enumMessage.setMessage(null);
                    } else {
                        String message = configuration.getString(key + ".message");
                        enumMessage.setMessage(color(message));
                        enumMessage.setMessages(new ArrayList<String>());
                    }
                    break;
                }
                case TITLE: {
                    String title = configuration.getString(key + ".title");
                    String subtitle = configuration.getString(key + ".subtitle");
                    int fadeInTime = configuration.getInt(key + ".fadeInTime", configuration.getInt(key + ".fade-in-time"));
                    int showTime = configuration.getInt(key + ".showTime", configuration.getInt(key + ".show-time"));
                    int fadeOutTime = configuration.getInt(key + ".fadeOutTime", configuration.getInt(key + ".fade-out-time"));
                    Map<String, Object> titles = new HashMap<String, Object>();
                    titles.put("title", color(title));
                    titles.put("subtitle", color(subtitle));
                    titles.put("start", fadeInTime);
                    titles.put("time", showTime);
                    titles.put("end", fadeOutTime);
                    titles.put("isUse", true);
                    enumMessage.setTitles(titles);
                    break;
                }
                default:
                    break;
            }

        } catch (Exception ignored) {
        }

        if (configuration.isConfigurationSection(key + ".")) {
            for (String newKey : configuration.getConfigurationSection(key + ".").getKeys(false)) {
                loadMessage(configuration, key + "." + newKey);
            }
        }
    }

}
