package fr.maxlego08.menu.common.utils;

import fr.maxlego08.menu.api.placeholder.Placeholder;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.text.TextService;
import fr.robie.minimessageflow.Template;
import fr.robie.minimessageflow.TemplateList;
import fr.robie.minimessageflow.Values;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;

public abstract class ActionHelper extends Action {

    /**
     * Compiles an action's text once, with zMenu's instance.
     *
     * @param text the text as written, or null
     * @return the template, or null for null text
     */
    protected static @Nullable Template compile(@Nullable String text) {
        return text == null ? null : TextService.get().flow().compile(text);
    }

    /**
     * Compiles an action's lines once, with zMenu's instance.
     *
     * @param lines the lines as written, or null
     * @return the templates, empty for null lines
     */
    protected static @NotNull TemplateList compile(@Nullable List<String> lines) {
        return TextService.get().flow().compileList(lines == null ? List.of() : lines);
    }

    /**
     * Renders a template for a player, an empty component for none.
     */
    protected static @NotNull Component render(@Nullable Template template, @NotNull Values values, @Nullable Player player) {
        return template == null ? Component.empty() : template.render(values, player);
    }

    /**
     * A title rendered for a player, its times given in milliseconds.
     */
    protected static @NotNull Title title(@Nullable Template title, @Nullable Template subtitle, @NotNull Values values, @Nullable Player player,
                                          long start, long duration, long end) {
        Title.Times times = Title.Times.times(Duration.ofMillis(start), Duration.ofMillis(duration), Duration.ofMillis(end));
        return Title.title(render(title, values, player), render(subtitle, values, player), times);
    }

    protected String papi(String string, Player player) {
        if (string == null || !string.contains("%")) return string;
        return Placeholder.Placeholders.getPlaceholder().setPlaceholders(player, string);
    }

    protected List<String> papi(List<String> strings, Player player) {
        return Placeholder.Placeholders.getPlaceholder().setPlaceholders(player, strings);
    }

}
