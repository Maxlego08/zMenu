package fr.maxlego08.menu.zcore.utils.discord;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DiscordEmbedConfiguration {

    private final String title;
    private final String description;
    private final String url;
    private final Color color;
    private final Footer footer;
    private final Thumbnail thumbnail;
    private final Image image;
    private final Author author;
    private final List<Field> fields;

    public DiscordEmbedConfiguration(String title, String description, String url, Color color, Footer footer,
                                     Thumbnail thumbnail, Image image, Author author, List<Field> fields) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.color = color;
        this.footer = footer;
        this.thumbnail = thumbnail;
        this.image = image;
        this.author = author;
        this.fields = fields;
    }

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

    public void apply(ReturnConsumer<String, String> consumer, DiscordWebhook discordWebhook) {
        DiscordWebhook.EmbedObject embedObject = new DiscordWebhook.EmbedObject();

        if (this.author != null) {
            embedObject.setAuthor(consumer.accept(this.author.getName()),
                    consumer.accept(this.author.getUrl()),
                    consumer.accept(this.author.getIconUrl()));
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
            embedObject.setThumbnail(consumer.accept(this.thumbnail.getUrl()));
        }

        if (this.image != null) {
            embedObject.setImage(consumer.accept(this.image.getUrl()));
        }

        if (this.footer != null) {
            embedObject.setFooter(consumer.accept(this.footer.getText()), consumer.accept(this.footer.getIconUrl()));
        }

        if (!this.fields.isEmpty()) {
            for (Field field : this.fields) {
                embedObject.addField(consumer.accept(field.getName()), consumer.accept(field.getValue()), field.isInline());
            }
        }

        discordWebhook.addEmbed(embedObject);
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public Color getColor() {
        return color;
    }

    public Footer getFooter() {
        return footer;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public Image getImage() {
        return image;
    }

    public Author getAuthor() {
        return author;
    }

    public List<Field> getFields() {
        return fields;
    }

    public static class Footer {
        private final String text;
        private final String iconUrl;

        public Footer(String text, String iconUrl) {
            this.text = text;
            this.iconUrl = iconUrl;
        }

        public String getText() {
            return text;
        }

        public String getIconUrl() {
            return iconUrl;
        }
    }

    public static class Thumbnail {
        private final String url;

        public Thumbnail(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

    public static class Image {
        private final String url;

        public Image(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

    public static class Author {
        private final String name;
        private final String url;
        private final String iconUrl;

        public Author(String name, String url, String iconUrl) {
            this.name = name;
            this.url = url;
            this.iconUrl = iconUrl;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public String getIconUrl() {
            return iconUrl;
        }
    }

    public static class Field {
        private final String name;
        private final String value;
        private final boolean inline;

        public Field(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public boolean isInline() {
            return inline;
        }
    }
}
