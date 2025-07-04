package fr.maxlego08.menu.zcore.utils.discord;

import java.util.List;

public record DiscordConfigurationComponent(String webhookUrl, String avatarUrl, String username, List<?> json) {

    public void apply(ReturnConsumer<String, String> consumer, DiscordWebhookComponent discordWebhook) {

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
}
