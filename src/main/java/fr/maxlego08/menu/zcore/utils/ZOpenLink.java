package fr.maxlego08.menu.zcore.utils;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.OpenLink;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.List;

public class ZOpenLink extends ZUtils implements OpenLink {

    private final MenuPlugin plugin;
    private final Action action;
    private final String message;
    private final String link;
    private final String replace;
    private final List<String> hover;

    /**
     * @param action  - The action
     * @param message - The message
     * @param link    - The link
     * @param replace - The replacement
     * @param hover   - The hover messages
     */
    public ZOpenLink(MenuPlugin plugin, Action action, String message, String link, String replace, List<String> hover) {
        super();
        this.plugin = plugin;
        this.action = action;
        this.message = message;
        this.link = link;
        this.replace = replace;
        this.hover = hover;
    }

    public ZOpenLink() {
        this(null, null, null, null, null, null);
    }

    public String getMessage() {
        return message;
    }

    public String getLink() {
        return link;
    }

    public String getReplace() {
        return replace;
    }

    public List<String> getHover() {
        return hover;
    }

    @Override
    public Action getAction() {
        return this.action;
    }

    @Override
    public void send(Player player, List<String> messages) {
        if (this.isValid()) {

            messages.forEach(message -> {

                String finalMessage = this.papi(message, player, true);

                if (finalMessage.contains(this.replace)) {

                    String[] splitMessages = finalMessage.split(this.replace);

                    TextComponent component = buildTextComponent(splitMessages[0]);

                    TextComponent clickComponent = buildTextComponent(color(this.message));
                    setClickAction(clickComponent, Action.OPEN_URL, this.link);
                    setHoverMessage(clickComponent, color(this.hover));

                    component.addExtra(clickComponent);
                    if (splitMessages.length == 2) {
                        component.addExtra(buildTextComponent(splitMessages[1]));
                    }

                    player.spigot().sendMessage(component);

                } else {

                    this.plugin.getMetaUpdater().sendMessage(player, finalMessage);
                }
            });
        } else {

            messages.forEach(message -> this.plugin.getMetaUpdater().sendMessage(player, this.papi(message, player, true)));
        }
    }

    @Override
    public boolean isValid() {
        return this.action != null && this.replace != null && this.hover != null && this.link != null
                && this.message != null && this.plugin != null;
    }
}
