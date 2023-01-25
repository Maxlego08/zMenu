package fr.maxlego08.menu.zcore.utils.meta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.zcore.utils.nms.NMSUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;

public class ComponentMeta implements MetaUpdater {

	private final MiniMessage MINI_MESSAGE = MiniMessage.builder()
			.tags(TagResolver.builder().resolver(StandardTags.defaults()).build()).build();
	private final Map<String, String> COLORS_MAPPINGS = new HashMap<String, String>();

	public ComponentMeta() {
		this.COLORS_MAPPINGS.put("0", "black");
		this.COLORS_MAPPINGS.put("1", "dark_blue");
		this.COLORS_MAPPINGS.put("2", "dark_green");
		this.COLORS_MAPPINGS.put("3", "dark_aqua");
		this.COLORS_MAPPINGS.put("4", "dark_red");
		this.COLORS_MAPPINGS.put("5", "dark_purple");
		this.COLORS_MAPPINGS.put("6", "gold");
		this.COLORS_MAPPINGS.put("7", "gray");
		this.COLORS_MAPPINGS.put("8", "dark_gray");
		this.COLORS_MAPPINGS.put("9", "blue");
		this.COLORS_MAPPINGS.put("a", "green");
		this.COLORS_MAPPINGS.put("b", "aqua");
		this.COLORS_MAPPINGS.put("c", "red");
		this.COLORS_MAPPINGS.put("d", "light_purple");
		this.COLORS_MAPPINGS.put("e", "yellow");
		this.COLORS_MAPPINGS.put("f", "white");
		this.COLORS_MAPPINGS.put("k", "obfuscated");
		this.COLORS_MAPPINGS.put("l", "bold");
		this.COLORS_MAPPINGS.put("m", "strikethrough");
		this.COLORS_MAPPINGS.put("n", "underlined");
		this.COLORS_MAPPINGS.put("o", "italic");
		this.COLORS_MAPPINGS.put("r", "reset");
	}

	@Override
	public void updateDisplayName(ItemMeta itemMeta, String text, Player player) {
		Component component = this.MINI_MESSAGE.deserialize(colorMiniMessage(text));
		itemMeta.displayName(component);
	}

	@Override
	public void updateLore(ItemMeta itemMeta, List<String> lore, Player player) {
		List<Component> components = lore.stream().map(text -> {
			return this.MINI_MESSAGE.deserialize(colorMiniMessage(text));
		}).collect(Collectors.toList());
		itemMeta.lore(components);
	}

	private String colorMiniMessage(String message) {

		String newMessage = message;

		/*if (NMSUtils.isHexColor()) {
			Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
			Matcher matcher = pattern.matcher(message);
			while (matcher.find()) {
				String color = message.substring(matcher.start(), matcher.end());
				newMessage = newMessage.replace(color, "<" + color + ">");
				message = message.replace(color, "");
				matcher = pattern.matcher(message);
			}
		}*/

		for (Entry<String, String> entry : this.COLORS_MAPPINGS.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			newMessage = newMessage.replace("&" + key, "<" + value + ">");
			newMessage = newMessage.replace("§" + key, "<" + value + ">");
		}
		return newMessage;
	}

}
