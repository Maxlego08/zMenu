package fr.maxlego08.menu.zcore.utils.discord;

import java.util.List;

public class DiscordConfiguration {

    private final String webhookUrl;
    private final String avatarUrl;
    private final String content;
    private final String username;
    private final List<DiscordEmbedConfiguration> embeds;

    public DiscordConfiguration(String webhookUrl, String avatarUrl, String content, String username, List<DiscordEmbedConfiguration> embeds) {
        this.webhookUrl = webhookUrl;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.username = username;
        this.embeds = embeds;
    }

    public void apply(ReturnConsumer<String, String> consumer, DiscordWebhook discordWebhook) {

        if (this.username != null) {
            discordWebhook.setUsername(consumer.accept(this.username));
        }

        if (this.avatarUrl != null) {
            discordWebhook.setAvatarUrl(consumer.accept(this.avatarUrl));
        }

        if (this.content != null) {
            discordWebhook.setContent(consumer.accept(this.content));
        }

        this.embeds.forEach(embed -> embed.apply(consumer, discordWebhook));
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }

    public List<DiscordEmbedConfiguration> getEmbeds() {
        return embeds;
    }
}
