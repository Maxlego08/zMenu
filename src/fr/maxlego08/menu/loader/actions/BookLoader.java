package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.OpenBookAction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookLoader implements ActionLoader {

    @Override
    public String getKey() {
        return "book";
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {

        String title = accessor.getString("title");
        String author = accessor.getString("subtitle");
        List<String> lines = new ArrayList<>();

        Map<?, List<String>> maps = (Map<?, List<String>>) accessor.getObject("lines", new HashMap<>());
        maps.forEach((page, currentLine) -> lines.add(String.join("<newline>", currentLine)));

        return new OpenBookAction(title, author, lines);
    }
}
