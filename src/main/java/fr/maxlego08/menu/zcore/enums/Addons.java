package fr.maxlego08.menu.zcore.enums;

public enum Addons {
    ZSHOP("zShop", "Maxlego08", "https://www.spigotmc.org/resources/74073/", "9,99€"),
    ZACTIONHOUSE("zAuctionHouse", "Maxlego08", "https://www.spigotmc.org/resources/63010","12,99€"),
    ZVOTEPARTY("zVoteParty", "Maxlego08", "https://www.spigotmc.org/resources/95603"),
    ZESSENTIALS("zEssentials", "Maxlego08", "https://www.spigotmc.org/resources/118014","19,99€"),
    ZJOBS("zJobs", "Maxlego08", "https://groupez.dev/resources/zjobs.336","9,99€"),
    ZQUESTS("zQuests", "Maxlego08", "https://groupez.dev/resources/zquests.335","14,99€"),
    RECIPEBOOK("RecipeBook","1robie","https://www.spigotmc.org/resources/recipebook-zmenu-%E2%80%93-addon-for-zmenu.123600/"),
    SUPERIORSKYBLOCK("SuperiorSkyblock2-Addon","Maxlego08","https://modrinth.com/plugin/superiorskyblock2-zmenu-fork"),


    ;

    private final String pluginName;
    private final String authorName;
    private final String url;
    private final String price;

    Addons(String pluginName, String authorName, String url) {
        this.pluginName = pluginName;
        this.authorName = authorName;
        this.url = url;
        this.price = "Free";
    }

    Addons (String pluginName, String authorName, String url, String price) {
        this.pluginName = pluginName;
        this.authorName = authorName;
        this.url = url;
        this.price = price;
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getUrl() {
        return url;
    }
    public String getPrice() {
        return price;
    }
}
