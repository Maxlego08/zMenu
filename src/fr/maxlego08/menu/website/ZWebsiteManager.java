package fr.maxlego08.menu.website;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import com.google.gson.JsonObject;

import fr.maxlego08.menu.api.website.WebsiteManager;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.utils.ZUtils;

public class ZWebsiteManager extends ZUtils implements WebsiteManager {

	// private final String API_URL = "https://mib.groupez.dev/api/";
	private final String API_URL = "http://mib.test/api/";
	private final Plugin plugin;
	private boolean isLogin = false;

	/**
	 * @param plugin
	 */
	public ZWebsiteManager(Plugin plugin) {
		super();
		this.plugin = plugin;
	}

	@Override
	public void login(CommandSender sender, String email, String password) {

		if (Token.token != null) {
			message(sender, Message.WEBSITE_LOGIN_ERROR_ALREADY);
			return;
		}

		if (this.isLogin) {
			message(sender, Message.WEBSITE_LOGIN_PROCESS);
			return;
		}

		this.isLogin = true;

		message(sender, Message.WEBSITE_LOGIN_PROCESS);

		JsonObject data = new JsonObject();
		data.addProperty("email", email);
		data.addProperty("password", password);
		HttpRequest request = new HttpRequest(this.API_URL + "auth/login", data);
		request.submit(this.plugin, map -> {
			this.isLogin = false;
			boolean status = (boolean) map.get("status");
			if (status) {
				Token.token = (String) map.get("token");
				message(sender, Message.WEBSITE_LOGIN_SUCCESS);
			} else {
				message(sender, Message.WEBSITE_LOGIN_ERROR_INFO, "%message%", map.get("message"));
			}
		});

	}

}
