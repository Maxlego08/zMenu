package fr.maxlego08.menu.website;

import org.bukkit.command.CommandSender;

import com.google.gson.JsonObject;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.website.WebsiteManager;
import fr.maxlego08.menu.website.request.HttpRequest;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.utils.ZUtils;

public class ZWebsiteManager extends ZUtils implements WebsiteManager {

	// private final String API_URL = "https://mib.groupez.dev/api/v1";
	private final String API_URL = "http://mib.test/api/v1/";
	private final MenuPlugin plugin;
	private boolean isLogin = false;

	/**
	 * @param plugin
	 */
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
		if (parts.length != 2){
			message(sender, Message.WEBSITE_LOGIN_ERROR_TOKEN);
			return;
		}
		
		String code = parts[1];
		if (code.length() != 40){
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

}
