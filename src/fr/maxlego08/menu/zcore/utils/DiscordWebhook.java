package fr.maxlego08.menu.zcore.utils;

import fr.maxlego08.menu.api.dupe.DupeItem;
import fr.maxlego08.ztranslator.api.Translator;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Class used to execute Discord Webhooks with low effort
 */
public class DiscordWebhook extends ZUtils {

    private final String url;
    private transient final Pattern STRIP_EXTRAS_PATTERN = Pattern.compile("(?i)§[0-9A-FK-ORX]");
    private String content;
    private String username;
    private String avatarUrl;

    /**
     * Constructs a new DiscordWebhook instance
     *
     * @param url The webhook URL obtained in Discord
     */
    public DiscordWebhook(String url) {
        this.url = url;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String replaceString(String string, DupeItem dupeItem) {

        if (string == null) return string;

        ItemStack itemStack = dupeItem.getItemStack();
        string = string.replace("%player%", dupeItem.getPlayer().getName());
        string = string.replace("%itemname%", getItemName(itemStack));
        string = string.replace("%amount%", String.valueOf(itemStack.getAmount()));
        return string;
    }

    public void execute(DupeItem dupeItem) throws IOException {
        if (this.content == null) {
            throw new IllegalArgumentException("Set content or add at least one EmbedObject");
        }

        JSONObject json = new JSONObject();

        json.put("content", this.replaceString(this.content, dupeItem));
        json.put("username", this.replaceString(this.username, dupeItem));
        json.put("avatar_url", this.avatarUrl);

        URL url = new URL(this.url);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "Java-DiscordWebhook-BY-Gelox_");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        OutputStream stream = connection.getOutputStream();
        stream.write(json.toString().getBytes(StandardCharsets.UTF_8));
        stream.flush();
        stream.close();

        connection.getInputStream().close();
        connection.disconnect();
    }

    @Override
    protected String getItemName(ItemStack itemStack) {
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            return this.STRIP_EXTRAS_PATTERN.matcher(itemStack.getItemMeta().getDisplayName()).replaceAll("");
        }

        // Translation
        if (Bukkit.getPluginManager().isPluginEnabled("zTranslator")) {

            RegisteredServiceProvider<Translator> provider = Bukkit.getServer().getServicesManager().getRegistration(Translator.class);
            Translator translator = provider.getProvider();
            return translator.translate(itemStack);
        }

        String name = itemStack.serialize().get("type").toString().replace("_", " ").toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private static class JSONObject {

        private final HashMap<String, Object> map = new HashMap<>();

        void put(String key, Object value) {
            if (value != null) {
                map.put(key, value);
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            Set<Map.Entry<String, Object>> entrySet = map.entrySet();
            builder.append("{");

            int i = 0;
            for (Map.Entry<String, Object> entry : entrySet) {
                Object val = entry.getValue();
                builder.append(quote(entry.getKey())).append(":");

                if (val instanceof String) {
                    builder.append(quote(String.valueOf(val)));
                } else if (val instanceof Integer) {
                    builder.append(Integer.valueOf(String.valueOf(val)));
                } else if (val instanceof Boolean) {
                    builder.append(val);
                } else if (val instanceof JSONObject) {
                    builder.append(val);
                } else if (val.getClass().isArray()) {
                    builder.append("[");
                    int len = Array.getLength(val);
                    for (int j = 0; j < len; j++) {
                        builder.append(Array.get(val, j).toString()).append(j != len - 1 ? "," : "");
                    }
                    builder.append("]");
                }

                builder.append(++i == entrySet.size() ? "}" : ",");
            }

            return builder.toString();
        }

        private String quote(String string) {
            return "\"" + string + "\"";
        }
    }

}