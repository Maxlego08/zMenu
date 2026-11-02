package fr.maxlego08.menu.hooks.dialogs.canvas;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CanvasRenderContextTest {

    private final List<String> parsed = new ArrayList<>();

    private CanvasRenderContext context() {
        MenuPlugin plugin = (MenuPlugin) Proxy.newProxyInstance(MenuPlugin.class.getClassLoader(), new Class<?>[]{MenuPlugin.class}, (proxy, method, arguments) -> {
            if (!method.getName().equals("parse")) return null;
            String text = (String) arguments[1];
            this.parsed.add(text);
            return text.replace("%player_name%", "Steve");
        });
        return new CanvasRenderContext(plugin, null, null, null, null, new Placeholders(), null);
    }

    @Test
    public void childContextsShareThePlaceholdersOfTheCanvas() {
        CanvasRenderContext root = this.context();
        assertEquals("Steve", root.resolve("%player_name%"));
        CanvasRenderContext first = root.with(Map.of("entry", "bow"));
        CanvasRenderContext second = root.with(Map.of("entry", "apple"));

        assertEquals("Steve", first.resolve("%player_name%"));
        assertEquals("Steve", second.resolve("%player_name%"));
        assertEquals(List.of("%player_name%"), this.parsed);
    }

    @Test
    public void theirOwnPlaceholdersStayApart() {
        CanvasRenderContext root = this.context();
        CanvasRenderContext first = root.with(Map.of("entry", "bow"));
        CanvasRenderContext second = root.with(Map.of("entry", "apple"));

        assertEquals("bow", first.resolve("%entry%"));
        assertEquals("apple", second.resolve("%entry%"));
        assertEquals("Steve: apple", second.resolve("%player_name%: %entry%"));
        assertEquals("%entry%", root.resolve("%entry%"));

        CanvasRenderContext nested = first.with(Map.of("value", "3"));
        assertEquals("bow 3", nested.resolve("%entry% %value%"));
        assertEquals("bow", nested.resolve("%entry%"));
    }
}
