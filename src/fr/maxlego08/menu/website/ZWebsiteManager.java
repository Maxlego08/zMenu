package fr.maxlego08.menu.website;

import com.google.gson.JsonObject;
import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.website.WebsiteManager;
import fr.maxlego08.menu.button.loader.NoneLoader;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.placeholder.LocalPlaceholder;
import fr.maxlego08.menu.website.buttons.ButtonMarketplace;
import fr.maxlego08.menu.website.inventories.InventoryMarketplace;
import fr.maxlego08.menu.website.request.HttpRequest;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ZWebsiteManager extends ZUtils implements WebsiteManager {

    private final String API_URL = "http://mib.test/api/v1/";
    private final MenuPlugin plugin;
    private boolean isLogin = false;
    private boolean isDownloadResource = false;
    private long lastResourceUpdate = 0;
    private List<Resource> resources = new ArrayList<>();

    public ZWebsiteManager(MenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public void login(CommandSender sender, String token) {

        if (token == null) {
            message(sender, Message.WEBSITE_LOGIN_ERROR_TOKEN);
            return;
        }

        if (Token.token != null) {
            message(sender, Message.WEBSITE_LOGIN_ERROR_ALREADY);
            return;
        }

        String[] parts = token.split("\\|");
        if (parts.length != 2) {
            message(sender, Message.WEBSITE_LOGIN_ERROR_TOKEN);
            return;
        }

        String code = parts[1];
        if (code.length() != 40) {
            message(sender, Message.WEBSITE_LOGIN_ERROR_TOKEN);
            return;
        }

        if (this.isLogin) {
            message(sender, Message.WEBSITE_LOGIN_PROCESS);
            return;
        }

        this.isLogin = true;

        message(sender, Message.WEBSITE_LOGIN_PROCESS);

        JsonObject data = new JsonObject();
        HttpRequest request = new HttpRequest(this.API_URL + "auth/test", data);
        request.setBearer(token);
        request.submit(this.plugin, map -> {
            this.isLogin = false;
            boolean status = map.getOrDefault("status", false);
            if (status) {
                Token.token = token;
                Token.getInstance().save(this.plugin.getPersist());
                message(sender, Message.WEBSITE_LOGIN_SUCCESS);
            } else {
                message(sender, Message.WEBSITE_LOGIN_ERROR_INFO);
            }
        });

    }

    @Override
    public void disconnect(CommandSender sender) {

        if (Token.token == null) {
            message(sender, Message.WEBSITE_DISCONNECT_ERROR);
            return;
        }

        Token.token = null;
        Token.getInstance().save(this.plugin.getPersist());
        message(sender, Message.WEBSITE_DISCONNECT_SUCCESS);

    }

    @Override
    public void openMarketplace(Player player) {

        if (Token.token == null) {
            message(player, Message.WEBSITE_NOT_CONNECT);
            return;
        }


        // Pas besoin de télécharger les resources
        if (this.lastResourceUpdate > System.currentTimeMillis()) {

            openMarketplaceInventory(player);
        } else {

            message(player, Message.WEBSITE_MARKETPLACE_WAIT);

            if (this.isDownloadResource) return;
            this.isDownloadResource = true;

            JsonObject data = new JsonObject();
            HttpRequest request = new HttpRequest(this.API_URL + "resources", data);
            request.setBearer(Token.token);
            request.setMethod("GET");
            request.submit(this.plugin, map -> {

                this.isDownloadResource = false;
                this.lastResourceUpdate = System.currentTimeMillis() + 1000 * 60 * 15;

                List<Map<String, Object>> maps = (List<Map<String, Object>>) map.get("resources");

                this.resources = maps.stream().map(Resource::new).collect(Collectors.toList());

                this.plugin.getScheduler().runTask(player.getLocation(), () -> openMarketplaceInventory(player));
            });
        }
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void registerPlaceholders() {
        LocalPlaceholder localPlaceholder = LocalPlaceholder.getInstance();
        localPlaceholder.register("marketplace_resources", (a, b) -> String.valueOf(this.resources.size()));
    }

    private void openMarketplaceInventory(Player player) {
        this.plugin.getInventoryManager().openInventory(player, this.plugin, "marketplace");
    }

    public void loadButtons(ButtonManager buttonManager) {
        buttonManager.register(new NoneLoader(this.plugin, ButtonMarketplace.class, "zmenu_marketplace_resources"));
    }

    public void loadInventories(InventoryManager inventoryManager) {
        try {
            inventoryManager.loadInventory(this.plugin, "website/marketplace.yml", InventoryMarketplace.class);
        } catch (InventoryException exception) {
            exception.printStackTrace();
        }
    }
}
