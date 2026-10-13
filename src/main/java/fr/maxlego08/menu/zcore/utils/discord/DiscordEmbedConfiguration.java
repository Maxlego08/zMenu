package fr.maxlego08.menu.zcore.utils.discord;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record DiscordEmbedConfiguration(String title, String description, String url, Color color,
                                        DiscordEmbedConfiguration.Footer footer,
                                        DiscordEmbedConfiguration.Thumbnail thumbnail,
                                        DiscordEmbedConfiguration.Image image,
                                        DiscordEmbedConfiguration.Author author, List<Field> fields) {

    public static List<DiscordEmbedConfiguration> convertToEmbedObjects(List<Map<?, ?>> data) {
        List<DiscordEmbedConfiguration> embedObjects = new ArrayList<>();

        for (Map<?, ?> map : data) {
            String title = getString(map, "title");
            String description = getString(map, "description");
            String url = getString(map, "url");
            Color color = hexToColor(getString(map, "color"));

            Footer footer = null;
            Map<?, ?> footerMap = getMap(map, "footer");
            if (footerMap != null) {
                String footerText = getString(footerMap, "text");
                String footerIconUrl = getString(footerMap, "icon-url");
                footer = new Footer(footerText, footerIconUrl);
            }

            Thumbnail thumbnail = null;
            Map<?, ?> thumbnailMap = getMap(map, "thumbnail");
            if (thumbnailMap != null) {
                String thumbnailUrl = getString(thumbnailMap, "url");
                thumbnail = new Thumbnail(thumbnailUrl);
            }

            Image image = null;
            Map<?, ?> imageMap = getMap(map, "image");
            if (imageMap != null) {
                String imageUrl = getString(imageMap, "url");
                image = new Image(imageUrl);
            }

            Author author = null;
            Map<?, ?> authorMap = getMap(map, "author");
            if (authorMap != null) {
                String authorName = getString(authorMap, "name");
                String authorUrl = getString(authorMap, "url");
                String authorIconUrl = getString(authorMap, "icon-url");
                author = new Author(authorName, authorUrl, authorIconUrl);
            }

            List<Field> fields = new ArrayList<>();
            List<Map<String, Object>> fieldsList = getList(map, "fields");
            if (fieldsList != null) {
                for (Map<String, Object> fieldMap : fieldsList) {
                    String fieldName = getString(fieldMap, "name");
                    String fieldValue = getString(fieldMap, "value");
                    Boolean inline = getBoolean(fieldMap, "inline");
                    fields.add(new Field(fieldName, fieldValue, inline != null ? inline : false));
                }
            }

            DiscordEmbedConfiguration embedObject = new DiscordEmbedConfiguration(title, description, url, color, footer, thumbnail, image, author, fields);

            embedObjects.add(embedObject);
        }

        return embedObjects;
    }

    private static String getString(Map<?, ?> map, String key) {
        Object value = map.get(key);
        return value instanceof String ? (String) value : null;
    }

    private static Map<?, ?> getMap(Map<?, ?> map, String key) {
        Object value = map.get(key);
        return value instanceof Map<?, ?> ? (Map<?, ?>) value : null;
    }

    private static List<Map<String, Object>> getList(Map<?, ?> map, String key) {
        Object value = map.get(key);
        return value instanceof List<?> ? (List<Map<String, Object>>) value : null;
    }

    private static Boolean getBoolean(Map<?, ?> map, String key) {
        Object value = map.get(key);
        return value instanceof Boolean ? (Boolean) value : null;
    }

    private static Color hexToColor(String hex) {
        if (hex == null) return null;

        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        if (hex.length() != 6 && hex.length() != 8) {
            throw new IllegalArgumentException("Invalid hex color string");
        }

        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        int a = 255;

        if (hex.length() == 8) {
            a = Integer.parseInt(hex.substring(6, 8), 16);
        }

        return new Color(r, g, b, a);
    }

    /**
     * Writes this embed in the format {@link #convertToEmbedObjects} reads.
     *
     * @return The embed as a map, ready to be put in the embeds list of a discord action.
     */
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        if (this.title != null) map.put("title", this.title);
        if (this.description != null) map.put("description", this.description);
        if (this.url != null) map.put("url", this.url);
        if (this.color != null) {
            String hex = String.format("#%02X%02X%02X", this.color.getRed(), this.color.getGreen(), this.color.getBlue());
            map.put("color", this.color.getAlpha() == 255 ? hex : hex + String.format("%02X", this.color.getAlpha()));
        }
        if (this.footer != null) map.put("footer", withoutNulls("text", this.footer.text(), "icon-url", this.footer.iconUrl()));
        if (this.thumbnail != null) map.put("thumbnail", withoutNulls("url", this.thumbnail.url()));
        if (this.image != null) map.put("image", withoutNulls("url", this.image.url()));
        if (this.author != null) map.put("author", withoutNulls("name", this.author.name(), "url", this.author.url(), "icon-url", this.author.iconUrl()));
        if (this.fields != null && !this.fields.isEmpty()) {
            List<Map<String, Object>> serializedFields = new ArrayList<>(this.fields.size());
            for (Field field : this.fields) {
                Map<String, Object> fieldMap = withoutNulls("name", field.name(), "value", field.value());
                if (field.inline()) fieldMap.put("inline", true);
                serializedFields.add(fieldMap);
            }
            map.put("fields", serializedFields);
        }
        return map;
    }

    /**
     * Builds a map from key/value pairs, leaving out the null values.
     */
    private static Map<String, Object> withoutNulls(Object... keysAndValues) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int index = 0; index + 1 < keysAndValues.length; index += 2) {
            if (keysAndValues[index + 1] != null) map.put((String) keysAndValues[index], keysAndValues[index + 1]);
        }
        return map;
    }

    public void apply(ReturnConsumer<String, String> consumer, DiscordWebhook discordWebhook) {
        DiscordWebhook.EmbedObject embedObject = new DiscordWebhook.EmbedObject();

        if (this.author != null) {
            embedObject.setAuthor(consumer.accept(this.author.name()),
                    consumer.accept(this.author.url()),
                    consumer.accept(this.author.iconUrl()));
        }

        if (this.description != null) {
            embedObject.setDescription(consumer.accept(this.description));
        }

        if (this.color != null) {
            embedObject.setColor(this.color);
        }

        if (this.title != null) {
            embedObject.setTitle(consumer.accept(this.title));
        }

        if (this.thumbnail != null) {
            embedObject.setThumbnail(consumer.accept(this.thumbnail.url()));
        }

        if (this.image != null) {
            embedObject.setImage(consumer.accept(this.image.url()));
        }

        if (this.footer != null) {
            embedObject.setFooter(consumer.accept(this.footer.text()), consumer.accept(this.footer.iconUrl()));
        }

        if (!this.fields.isEmpty()) {
            for (Field field : this.fields) {
                embedObject.addField(consumer.accept(field.name()), consumer.accept(field.value()), field.inline());
            }
        }

        discordWebhook.addEmbed(embedObject);
    }

    public record Footer(String text, String iconUrl) {}

    public record Thumbnail(String url) {}

    public record Image(String url) {}

    public record Author(String name, String url, String iconUrl) {}

    public record Field(String name, String value, boolean inline) {}
}
