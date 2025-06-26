package fr.maxlego08.menu.zcore.utils.discord;

import java.util.List;

public record DiscordConfiguration(String webhookUrl, String avatarUrl, String content, String username, List<DiscordEmbedConfiguration> embeds) {

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
}
