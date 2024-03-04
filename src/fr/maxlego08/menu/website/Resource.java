package fr.maxlego08.menu.website;

import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.zcore.utils.ZUtils;

import java.util.Map;

public class Resource extends ZUtils {
    private final int id;
    private final String name;
    private final User user;
    private final String version;
    private final String category;
    private final double price;
    private final String currency;
    private final String tag;
    private final int download;
    private final String link;

    public Resource(Map<String, Object> map) {
        this.id = ((Number) map.get("id")).intValue();
        this.name = (String) map.get("name");
        Map<String, Object> userMap = (Map<String, Object>) map.get("user");
        this.user = new User(((Number) userMap.get("id")).intValue(), (String) userMap.get("name"));
        this.version = (String) map.get("version");
        this.category = (String) map.get("category");
        this.price = ((Number) map.get("price")).doubleValue();
        this.currency = (String) map.get("currency");
        this.tag = (String) map.get("tag");
        this.download = Integer.parseInt((String) map.get("download"));
        this.link = (String) map.get("link");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public User getUser() {
        return user;
    }

    public String getVersion() {
        return version;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public String getTag() {
        return tag;
    }

    public int getDownload() {
        return download;
    }

    public String getLink() {
        return link;
    }

    public boolean isFree() {
        return this.price == 0.0;
    }

    public Placeholders getPlaceholders() {
        Placeholders placeholders = new Placeholders();
        placeholders.register("resource_name", this.name);
        placeholders.register("resource_tag", this.tag);
        placeholders.register("resource_price", isFree() ? "FREE" : format(this.price) + this.currency);
        placeholders.register("resource_author", this.user.getName());
        placeholders.register("resource_version", this.version);
        placeholders.register("resource_category", this.category);
        placeholders.register("resource_download", String.valueOf(this.download));
        return placeholders;
    }

    public static class User {
        private int id;
        private String name;

        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
