package fr.maxlego08.menu.website;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import com.google.gson.JsonObject;

import fr.maxlego08.menu.api.website.WebsiteManager;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.utils.ZUtils;

public class ZWebsiteManager extends ZUtils implements WebsiteManager {

	private final String API_URL = "https://mib.test/api/";
	private final Plugin plugin;

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

		JsonObject data = new JsonObject();
		data.addProperty("email", email);
		data.addProperty("password", password);
		HttpRequest request = new HttpRequest(this.API_URL + "auth/login", data);
		request.submit(this.plugin, map -> {
			System.out.println("ici");
			System.out.println(map);
		});

	}

}
