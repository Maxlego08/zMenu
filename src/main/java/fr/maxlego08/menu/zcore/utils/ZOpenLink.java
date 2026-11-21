package fr.maxlego08.menu.zcore.utils;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.api.text.TextService;
import fr.robie.minimessageflow.MiniMessageFlow;
import fr.robie.minimessageflow.Placeholder;
import fr.robie.minimessageflow.Template;
import fr.robie.minimessageflow.TemplateList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ZOpenLink extends ZUtils implements OpenLink {

    private final MenuPlugin plugin;
    private final Action action;
    private final String message;
    private final String link;
    private final String replace;
    private final List<String> hover;
    /**
     * The clickable text and its hover, compiled once; null when the link is not valid.
     */
    private final transient Template messageTemplate;
    private final transient TemplateList hoverTemplates;

    /**
     * @param action  - The action
     * @param message - The message
     * @param link    - The link
     * @param replace - The replacement
     * @param hover   - The hover messages
     */
    public ZOpenLink(MenuPlugin plugin, Action action, String message, String link, String replace, List<String> hover) {
        this.plugin = plugin;
        this.action = action;
        this.message = message;
        this.link = link;
        this.replace = replace;
        this.hover = hover;
        boolean valid = this.isValid();
        this.messageTemplate = valid ? TextService.get().flow().compile(message) : null;
        this.hoverTemplates = valid ? TextService.get().flow().compileList(hover) : null;
    }

    public String getMessage() {
        return this.message;
    }

    public String getLink() {
        return this.link;
    }

    public String getReplace() {
        return this.replace;
    }

    public List<String> getHover() {
        return this.hover;
    }

    @Override
    public Action getAction() {
        return this.action;
    }

    /**
     * Sends the messages, each rendered for the player (PlaceholderAPI included). In a message holding the
     * {@code replace} text, it is replaced by the clickable {@code message}, which opens {@code link} and shows
     * {@code hover}; the parts before and after it are formatted on their own.
     */
    @Override
    public void send(@NonNull Player player, @NonNull List<String> messages) {
        MiniMessageFlow flow = TextService.get().flow();
        for (String message : messages) {
            int at = this.messageTemplate == null ? -1 : message.indexOf(this.replace);
            if (at < 0) {
                player.sendMessage(flow.format(message, Placeholder.empty(), player));
                continue;
            }
            Component click = this.messageTemplate.render(Placeholder.empty(), player)
                    .clickEvent(ClickEvent.openUrl(this.link))
                    .hoverEvent(HoverEvent.showText(Component.join(JoinConfiguration.newlines(), this.hoverTemplates.render(Placeholder.empty(), player))));
            player.sendMessage(Component.text()
                    .append(flow.format(message.substring(0, at), Placeholder.empty(), player))
                    .append(click)
                    .append(flow.format(message.substring(at + this.replace.length()), Placeholder.empty(), player))
                    .build());
        }
    }

    @Override
    public boolean isValid() {
        return this.action != null && this.replace != null && this.hover != null && this.link != null
                && this.message != null && this.plugin != null;
    }
}
