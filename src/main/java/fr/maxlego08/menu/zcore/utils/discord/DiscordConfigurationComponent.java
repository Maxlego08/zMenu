package fr.maxlego08.menu.zcore.utils.discord;

import java.util.List;

public class DiscordConfigurationComponent {
    private final String webhookUrl;
    private final String avatarUrl;
    private final String username;
    private final List<?> json;

    public DiscordConfigurationComponent(String webhookUrl, String avatarUrl, String username, List<?> json) {
        this.webhookUrl = webhookUrl;
        this.avatarUrl = avatarUrl;
        this.username = username;
        this.json = json;
    }

    public void apply(ReturnConsumer<String, String> consumer, DiscordWebhookComponent discordWebhook) {

        if (this.username != null) {
            discordWebhook.setUsername(consumer.accept(this.username));
        }

        if (this.avatarUrl != null) {
            discordWebhook.setAvatarUrl(consumer.accept(this.avatarUrl));
        }

        if (this.json != null) {
            Object parsed = parseRecursively(this.json, consumer);
            if (parsed instanceof List<?>) {
                discordWebhook.setJson((List<?>) parsed);
            } else {
                discordWebhook.setJson(java.util.Collections.emptyList());
            }
        }
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getUsername() {
        return username;
    }

    public List<?> getJson() {
        return json;
    }
    private Object parseRecursively(Object obj, ReturnConsumer<String, String> consumer) {
        if (obj instanceof String) {
            return consumer.accept((String) obj);
        }
        if (obj instanceof List<?> list) {
            List<Object> newList = new java.util.ArrayList<>();
            for (Object item : list) {
                newList.add(parseRecursively(item, consumer));
            }
            return newList;
        }
        if (obj instanceof java.util.Map<?, ?>) {
            java.util.Map<Object, Object> map = new java.util.HashMap<>();
            for (var entry : ((java.util.Map<?, ?>) obj).entrySet()) {
                map.put(entry.getKey(), parseRecursively(entry.getValue(), consumer));
            }
            return map;
        }
        return obj;
    }
}
