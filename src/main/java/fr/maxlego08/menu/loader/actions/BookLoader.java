package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.OpenBookAction;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookLoader extends ActionLoader {

    public BookLoader() {
        super("book");
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {

        String title = accessor.getString("title");
        String author = accessor.getString("author");
        List<String> lines = new ArrayList<>();

        Map<?, List<String>> maps = (Map<?, List<String>>) accessor.getObject("lines", new HashMap<>());
        maps.forEach((page, currentLine) -> lines.add(String.join("<newline>", currentLine)));

        return new OpenBookAction(title, author, lines);
    }
}
