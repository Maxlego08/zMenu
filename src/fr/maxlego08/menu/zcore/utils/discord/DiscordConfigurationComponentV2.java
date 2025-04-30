package fr.maxlego08.menu.zcore.utils.discord;

import java.util.List;

public class DiscordConfigurationComponentV2 {
    private final String webhookUrl;
    private final String avatarUrl;
    private final String username;
    private final List<?> json;

    public DiscordConfigurationComponentV2(String webhookUrl, String avatarUrl, String username, List<?> json) {
        this.webhookUrl = webhookUrl;
        this.avatarUrl = avatarUrl;
        this.username = username;
        this.json = json;
    }

    public void apply(ReturnConsumer<String, String> consumer, DiscordWebhookComponentV2 discordWebhook) {

        if (this.username != null) {
            discordWebhook.setUsername(consumer.accept(this.username));
        }

        if (this.avatarUrl != null) {
            discordWebhook.setAvatarUrl(consumer.accept(this.avatarUrl));
        }

        if (this.json != null) {
            discordWebhook.setJson(this.json);
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
}
