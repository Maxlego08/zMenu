package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import org.bukkit.entity.Player;
import fr.robie.minimessageflow.Template;
import fr.robie.minimessageflow.TemplateList;
import fr.robie.minimessageflow.Values;
import net.kyori.adventure.inventory.Book;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class OpenBookAction extends ActionHelper {

    private final String title;
    private final String author;
    private final List<String> lines;
    private final transient Template titleTemplate;
    private final transient Template authorTemplate;
    private final transient TemplateList pageTemplates;

    public OpenBookAction(String title, String author, List<String> lines) {
        this.title = title;
        this.author = author;
        this.lines = lines;
        this.titleTemplate = compile(title);
        this.authorTemplate = compile(author);
        this.pageTemplates = compile(lines);
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        Values values = placeholders.values();
        player.openBook(Book.book(render(this.titleTemplate, values, player), render(this.authorTemplate, values, player),
                this.pageTemplates.render(values, player)));
    }

    @Override
    protected void serializeProperties(@NonNull Map<String, Object> map) {
        map.put("type", "book");
        map.put("title", this.title);
        map.put("author", this.author);
        if (this.lines.isEmpty()) return;

        Map<Integer, List<String>> pages = new LinkedHashMap<>();
        for (int index = 0; index < this.lines.size(); index++) {
            pages.put(index + 1, Arrays.asList(this.lines.get(index).split(Pattern.quote("<newline>"), -1)));
        }
        map.put("lines", pages);
    }
}
