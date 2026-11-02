package fr.maxlego08.menu.hooks.dialogs.canvas.state;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.dialog.canvas.CanvasManager;
import fr.maxlego08.menu.api.dialog.canvas.CanvasStateService;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.hooks.dialogs.canvas.CanvasTestSupport;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Proxy;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CanvasStateTest {

    private static final UUID PLAYER_ID = UUID.randomUUID();

    private final ZCanvasStateService state = new ZCanvasStateService();
    private final List<String> reopened = new ArrayList<>();

    @BeforeAll
    static void logger() {
        CanvasTestSupport.installLogger();
    }

    @SuppressWarnings("unchecked")
    private static <T> T stub(Class<T> type, Map<String, Object> answers) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (instance, method, arguments) -> {
            Object answer = answers.get(method.getName());
            if (answer instanceof java.util.function.Function<?, ?> function) return ((java.util.function.Function<Object[], Object>) function).apply(arguments);
            if (answer != null) return answer;
            Class<?> returned = method.getReturnType();
            if (returned == boolean.class) return false;
            if (returned == int.class || returned == long.class) return 0;
            return null;
        }));
    }

    private MenuPlugin plugin() {
        java.util.function.Function<Object[], Object> reopen = arguments -> this.reopened.add((String) arguments[1]);
        java.util.function.Function<Object[], Object> parse = arguments -> arguments[1];
        CanvasManager canvasManager = stub(CanvasManager.class, Map.of("getStateService", this.state, "reopen", reopen));
        return stub(MenuPlugin.class, Map.of("getCanvasManager", canvasManager, "parse", parse));
    }

    private void run(MenuPlugin plugin, Placeholders placeholders, String type, Map<String, Object> keys) {
        ActionLoader loader = CanvasStateActions.loaders(plugin).stream().filter(candidate -> candidate.getKeys().getFirst().equals(type)).findFirst().orElseThrow();
        Map<String, Object> map = new HashMap<>(keys);
        map.put("type", type);
        Action action = loader.load("test", new TypedMapAccessor(map), new File("test.yml"));
        Player player = stub(Player.class, Map.of("getUniqueId", PLAYER_ID, "getName", "Steve"));
        action.setChance(100);
        action.preExecute(player, null, stub(InventoryEngine.class, Map.of()), placeholders);
    }

    private Placeholders dialog(String name) {
        Placeholders placeholders = new Placeholders();
        placeholders.register(CanvasStateActions.DIALOG_PLACEHOLDER, name);
        placeholders.registerLookup(CanvasStateActions.PREFIX, key -> CanvasStateActions.lookup(this.state.get(PLAYER_ID, name, key), key));
        return placeholders;
    }

    @Test
    public void openValuesResetAtAFreshOpenButNotAtAReopen() {
        this.state.set(PLAYER_ID, "shop", "tab", "profile", CanvasStateService.Keep.OPEN);
        this.state.set(PLAYER_ID, "shop", "visits", "3", CanvasStateService.Keep.SESSION);

        this.state.markReopen(PLAYER_ID, "shop");
        this.state.onOpen(PLAYER_ID, "shop");
        assertEquals("profile", this.state.get(PLAYER_ID, "SHOP", "tab"));

        this.state.onOpen(PLAYER_ID, "shop");
        assertNull(this.state.get(PLAYER_ID, "shop", "tab"));
        assertEquals("3", this.state.get(PLAYER_ID, "shop", "visits"));

        this.state.clear(PLAYER_ID);
        assertEquals(Map.of(), this.state.all(PLAYER_ID));
    }

    @Test
    public void aReopenThatDidNotOpenDoesNotProtectTheNextOpening() {
        this.state.set(PLAYER_ID, "shop", "tab", "profile", CanvasStateService.Keep.OPEN);
        this.state.markReopen(PLAYER_ID, "shop");
        this.state.cancelReopen(PLAYER_ID, "shop");

        this.state.onOpen(PLAYER_ID, "shop");
        assertNull(this.state.get(PLAYER_ID, "shop", "tab"));
    }

    @Test
    public void theActionsChangeTheStateOfTheirDialog() {
        MenuPlugin plugin = this.plugin();
        Placeholders placeholders = this.dialog("shop");

        this.run(plugin, placeholders, "canvas-set", Map.of("key", "tab", "value", "profile"));
        this.run(plugin, placeholders, "canvas-toggle", Map.of("key", "notify"));
        this.run(plugin, placeholders, "canvas-add", Map.of("key", "count", "amount", 7, "max", 10));
        this.run(plugin, placeholders, "canvas-add", Map.of("key", "count", "amount", 7, "max", 10));
        this.run(plugin, placeholders, "canvas-cycle", Map.of("key", "size", "values", List.of("s", "m", "l")));
        this.run(plugin, placeholders, "canvas-cycle", Map.of("key", "size", "values", List.of("s", "m", "l")));

        assertEquals(Map.of("tab", "profile", "notify", "true", "count", "10", "size", "m"), this.state.all(PLAYER_ID, "shop"));
        assertEquals("10 true", placeholders.parse("%canvas_count% %canvas_notify%"));

        this.run(plugin, placeholders, "canvas-remove", Map.of("key", "tab"));
        assertEquals("shop", placeholders.parse("%canvas_tab.default:shop%"));

        this.run(plugin, placeholders, "canvas-refresh", Map.of());
        assertEquals(List.of("shop"), this.reopened);
    }

    @Test
    public void anActionOutsideADialogNamesIt() {
        MenuPlugin plugin = this.plugin();
        this.run(plugin, new Placeholders(), "canvas-set", Map.of("key", "tab", "value", "shop", "dialog", "menu"));
        this.run(plugin, new Placeholders(), "canvas-set", Map.of("key", "lost", "value", "x"));

        assertEquals(Map.of("menu", Map.of("tab", "shop")), this.state.all(PLAYER_ID));
    }
}
