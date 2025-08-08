package fr.maxlego08.menu.zcore.utils.discord;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used to execute Discord Webhooks with low effort
 */
public class DiscordWebhook {

    private final String url;
    private final List<EmbedObject> embeds = new ArrayList<>();
    private String content;
    private String username;
    private String avatarUrl;
    private boolean tts;

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

    public void setTts(boolean tts) {
        this.tts = tts;
    }

    public void addEmbed(EmbedObject embed) {
        this.embeds.add(embed);
    }

    public void execute() throws IOException {
        if (this.content == null && this.embeds.isEmpty()) {
            throw new IllegalArgumentException("Set content or add at least one EmbedObject");
        }

        JsonObject json = new JsonObject();

        if (this.content != null) json.addProperty("content", this.content);
        if (this.username != null) json.addProperty("username", this.username);
        if (this.avatarUrl != null) json.addProperty("avatar_url", this.avatarUrl);
        json.addProperty("tts", this.tts);

        if (!this.embeds.isEmpty()) {
            JsonArray embedArray = new JsonArray();

            for (EmbedObject embed : this.embeds) {
                JsonObject jsonEmbed = getJsonEmbed(embed);

                EmbedObject.Footer footer = embed.getFooter();
                if (footer != null && (footer.text() != null || footer.iconUrl() != null)) {
                    JsonObject jsonFooter = new JsonObject();
                    if (footer.text() != null) jsonFooter.addProperty("text", footer.text());
                    if (footer.iconUrl() != null) jsonFooter.addProperty("icon_url", footer.iconUrl());
                    jsonEmbed.add("footer", jsonFooter);
                }

                EmbedObject.Image image = embed.getImage();
                if (image != null && image.url() != null) {
                    JsonObject jsonImage = new JsonObject();
                    jsonImage.addProperty("url", image.url());
                    jsonEmbed.add("image", jsonImage);
                }

                EmbedObject.Thumbnail thumbnail = embed.getThumbnail();
                if (thumbnail != null && thumbnail.url() != null) {
                    JsonObject jsonThumbnail = new JsonObject();
                    jsonThumbnail.addProperty("url", thumbnail.url());
                    jsonEmbed.add("thumbnail", jsonThumbnail);
                }

                EmbedObject.Author author = embed.getAuthor();
                if (author != null && (author.name() != null || author.url() != null || author.iconUrl() != null)) {
                    JsonObject jsonAuthor = new JsonObject();
                    if (author.name() != null) jsonAuthor.addProperty("name", author.name());
                    if (author.url() != null) jsonAuthor.addProperty("url", author.url());
                    if (author.iconUrl() != null) jsonAuthor.addProperty("icon_url", author.iconUrl());
                    jsonEmbed.add("author", jsonAuthor);
                }

                List<EmbedObject.Field> fields = embed.getFields();
                if (fields != null && !fields.isEmpty()) {
                    JsonArray jsonFields = getJsonArrayFields(fields);
                    if (!jsonFields.isEmpty())
                        jsonEmbed.add("fields", jsonFields);
                }

                embedArray.add(jsonEmbed);
            }

            json.add("embeds", embedArray);
        }

        URL url = URI.create(this.url).toURL();
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        int responseCode = DiscordWebhookComponent.sendDiscordHttpRequest(json, connection);
        if (responseCode == 400) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            System.out.println("Error response: " + response);
        }

        connection.getInputStream().close();
        connection.disconnect();
    }

    private static @NotNull JsonArray getJsonArrayFields(List<EmbedObject.Field> fields) {
        JsonArray jsonFields = new JsonArray();
        for (EmbedObject.Field field : fields) {
            if (field.name() != null && field.value() != null) {
                JsonObject jsonField = new JsonObject();
                jsonField.addProperty("name", field.name());
                jsonField.addProperty("value", field.value());
                jsonField.addProperty("inline", field.inline());
                jsonFields.add(jsonField);
            }
        }
        return jsonFields;
    }

    private static @NotNull JsonObject getJsonEmbed(EmbedObject embed) {
        JsonObject jsonEmbed = new JsonObject();

        if (embed.getTitle() != null) jsonEmbed.addProperty("title", embed.getTitle());
        if (embed.getDescription() != null) jsonEmbed.addProperty("description", embed.getDescription());
        if (embed.getUrl() != null) jsonEmbed.addProperty("url", embed.getUrl());

        if (embed.getColor() != null) {
            Color color = embed.getColor();
            int rgb = (color.getRed() << 16) + (color.getGreen() << 8) + color.getBlue();
            jsonEmbed.addProperty("color", rgb);
        }
        return jsonEmbed;
    }


    public static class EmbedObject {
        private final List<Field> fields = new ArrayList<>();
        private String title;
        private String description;
        private String url;
        private Color color;
        private Footer footer;
        private Thumbnail thumbnail;
        private Image image;
        private Author author;

        public String getTitle() {
            return title;
        }

        public EmbedObject setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public EmbedObject setDescription(String description) {
            this.description = description;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public EmbedObject setUrl(String url) {
            this.url = url;
            return this;
        }

        public Color getColor() {
            return color;
        }

        public EmbedObject setColor(Color color) {
            this.color = color;
            return this;
        }

        public Footer getFooter() {
            return footer;
        }

        public Thumbnail getThumbnail() {
            return thumbnail;
        }

        public EmbedObject setThumbnail(String url) {
            this.thumbnail = new Thumbnail(url);
            return this;
        }

        public Image getImage() {
            return image;
        }

        public EmbedObject setImage(String url) {
            this.image = new Image(url);
            return this;
        }

        public Author getAuthor() {
            return author;
        }

        public List<Field> getFields() {
            return fields;
        }

        public EmbedObject setFooter(String text, String icon) {
            this.footer = new Footer(text, icon);
            return this;
        }

        public EmbedObject setAuthor(String name, String url, String icon) {
            this.author = new Author(name, url, icon);
            return this;
        }

        public EmbedObject addField(String name, String value, boolean inline) {
            this.fields.add(new Field(name, value, inline));
            return this;
        }

        public record Footer(String text, String iconUrl) {}

        public record Thumbnail(String url) {}

        public record Image(String url) {}

        public record Author(String name, String url, String iconUrl) {}

        public record Field(String name, String value, boolean inline) {}
    }
}