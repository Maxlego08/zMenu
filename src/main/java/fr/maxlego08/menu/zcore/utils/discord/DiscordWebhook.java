package fr.maxlego08.menu.zcore.utils.discord;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
                JsonObject jsonEmbed = new JsonObject();

                if (embed.getTitle() != null) jsonEmbed.addProperty("title", embed.getTitle());
                if (embed.getDescription() != null) jsonEmbed.addProperty("description", embed.getDescription());
                if (embed.getUrl() != null) jsonEmbed.addProperty("url", embed.getUrl());

                if (embed.getColor() != null) {
                    Color color = embed.getColor();
                    int rgb = (color.getRed() << 16) + (color.getGreen() << 8) + color.getBlue();
                    jsonEmbed.addProperty("color", rgb);
                }

                EmbedObject.Footer footer = embed.getFooter();
                if (footer != null && (footer.getText() != null || footer.getIconUrl() != null)) {
                    JsonObject jsonFooter = new JsonObject();
                    if (footer.getText() != null) jsonFooter.addProperty("text", footer.getText());
                    if (footer.getIconUrl() != null) jsonFooter.addProperty("icon_url", footer.getIconUrl());
                    jsonEmbed.add("footer", jsonFooter);
                }

                EmbedObject.Image image = embed.getImage();
                if (image != null && image.getUrl() != null) {
                    JsonObject jsonImage = new JsonObject();
                    jsonImage.addProperty("url", image.getUrl());
                    jsonEmbed.add("image", jsonImage);
                }

                EmbedObject.Thumbnail thumbnail = embed.getThumbnail();
                if (thumbnail != null && thumbnail.getUrl() != null) {
                    JsonObject jsonThumbnail = new JsonObject();
                    jsonThumbnail.addProperty("url", thumbnail.getUrl());
                    jsonEmbed.add("thumbnail", jsonThumbnail);
                }

                EmbedObject.Author author = embed.getAuthor();
                if (author != null && (author.getName() != null || author.getUrl() != null || author.getIconUrl() != null)) {
                    JsonObject jsonAuthor = new JsonObject();
                    if (author.getName() != null) jsonAuthor.addProperty("name", author.getName());
                    if (author.getUrl() != null) jsonAuthor.addProperty("url", author.getUrl());
                    if (author.getIconUrl() != null) jsonAuthor.addProperty("icon_url", author.getIconUrl());
                    jsonEmbed.add("author", jsonAuthor);
                }

                List<EmbedObject.Field> fields = embed.getFields();
                if (fields != null && !fields.isEmpty()) {
                    JsonArray jsonFields = new JsonArray();
                    for (EmbedObject.Field field : fields) {
                        if (field.getName() != null && field.getValue() != null) {
                            JsonObject jsonField = new JsonObject();
                            jsonField.addProperty("name", field.getName());
                            jsonField.addProperty("value", field.getValue());
                            jsonField.addProperty("inline", field.isInline());
                            jsonFields.add(jsonField);
                        }
                    }
                    if (!jsonFields.isEmpty()) {
                        jsonEmbed.add("fields", jsonFields);
                    }
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

        private class Footer {
            private final String text;
            private final String iconUrl;

            private Footer(String text, String iconUrl) {
                this.text = text;
                this.iconUrl = iconUrl;
            }

            private String getText() {
                return text;
            }

            private String getIconUrl() {
                return iconUrl;
            }
        }

        private class Thumbnail {
            private final String url;

            private Thumbnail(String url) {
                this.url = url;
            }

            private String getUrl() {
                return url;
            }
        }

        private class Image {
            private final String url;

            private Image(String url) {
                this.url = url;
            }

            private String getUrl() {
                return url;
            }
        }

        private class Author {
            private final String name;
            private final String url;
            private final String iconUrl;

            private Author(String name, String url, String iconUrl) {
                this.name = name;
                this.url = url;
                this.iconUrl = iconUrl;
            }

            private String getName() {
                return name;
            }

            private String getUrl() {
                return url;
            }

            private String getIconUrl() {
                return iconUrl;
            }
        }

        private class Field {
            private final String name;
            private final String value;
            private final boolean inline;

            private Field(String name, String value, boolean inline) {
                this.name = name;
                this.value = value;
                this.inline = inline;
            }

            private String getName() {
                return name;
            }

            private String getValue() {
                return value;
            }

            private boolean isInline() {
                return inline;
            }
        }
    }
}