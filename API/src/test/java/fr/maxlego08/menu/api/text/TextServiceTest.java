package fr.maxlego08.menu.api.text;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.placeholder.LocalPlaceholder;
import fr.robie.minimessageflow.MiniMessageFlow;
import fr.robie.minimessageflow.Placeholder;
import fr.robie.minimessageflow.Template;
import fr.robie.minimessageflow.Values;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TextServiceTest {

    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    @Test
    @DisplayName("Before zMenu installs its service: one plain instance with zMenu's options")
    void fallback() {
        TextService service = TextService.get();
        assertSame(service, TextService.get());
        MiniMessageFlow flow = service.flow();
        assertSame(TextService.KEYS, flow.keys());
        Values values = flow.newValues().set("name", "steve").set("coins", "abc");
        assertEquals("STEVE Steve []", PLAIN.serialize(flow.compile("%upper_name% %capitalize_name% [%coins.comma%]").render(values, null)));
    }

    @Test
    @DisplayName("Values made by one instance on the shared keys render with another")
    void sharedKeys() {
        MiniMessageFlow.Builder builder = MiniMessageFlow.builder();
        TextService.options(builder);
        MiniMessageFlow other = builder.build();
        Values values = TextService.get().flow().newValues().set("page", 3);
        assertEquals("Page 3", PLAIN.serialize(other.compile("Page %page%").render(values, null)));
    }

    @Test
    @DisplayName("Extra tags replace the previous ones; templates compiled before follow")
    void tags() {
        TextService service = TextService.get();
        Template template = service.flow().compile("<glyph>x");
        service.tags(List.of(TagResolver.resolver("glyph", Tag.styling(NamedTextColor.GOLD))));
        Component rendered = template.render();
        assertEquals(NamedTextColor.GOLD, rendered.color() != null ? rendered.color() : rendered.children().getFirst().color());
        service.tags(List.of());
        assertEquals("<glyph>x", PLAIN.serialize(template.render()));
    }

    @Test
    @DisplayName("number-style is applied by configure")
    void numberStyle() {
        String before = Configuration.numberStyle;
        try {
            Configuration.numberStyle = "european";
            TextService.get().configure();
            Values values = TextService.get().flow().newValues().set("price", "1234,5");
            assertEquals("1.234,50", PLAIN.serialize(TextService.get().flow().compile("%price.comma%").render(values, null)));
        } finally {
            Configuration.numberStyle = before;
            TextService.get().configure();
        }
    }

    @Test
    @DisplayName("install replaces the service get() gives")
    void install() {
        TextService previous = TextService.get();
        MiniMessageFlow.Builder builder = MiniMessageFlow.builder();
        TextService.options(builder);
        TextService own = new AbstractTextService(builder.build()) {
        };
        try {
            TextService.install(own);
            assertSame(own, TextService.get());
        } finally {
            TextService.install(previous);
        }
    }

    @Test
    @DisplayName("zMenu's own %zmenu_...% placeholders are asked directly, for an OfflinePlayer viewer only")
    void zmenuPlaceholders() {
        LocalPlaceholder.getInstance().register("textservicetest_", (player, value) -> "ok-" + value + "-" + player.getName());
        Pointered viewer = (Pointered) Proxy.newProxyInstance(TextServiceTest.class.getClassLoader(),
                new Class<?>[]{Pointered.class, OfflinePlayer.class},
                (proxy, method, arguments) -> method.getName().equals("getName") ? "Steve"
                        : method.isDefault() ? InvocationHandler.invokeDefault(proxy, method, arguments) : null);
        MiniMessageFlow flow = TextService.get().flow();
        assertEquals("ok-a-Steve", PLAIN.serialize(flow.format("%zmenu_textservicetest_a%", Placeholder.empty(), viewer)));
        assertEquals("%zmenu_textservicetest_a%", PLAIN.serialize(flow.format("%zmenu_textservicetest_a%", Placeholder.empty(), null)));
        assertEquals("%zmenu_nothing%", PLAIN.serialize(flow.format("%zmenu_nothing%", Placeholder.empty(), viewer)));
    }
}
