package fr.maxlego08.menu.hooks.dialogs.click;

import io.papermc.paper.dialog.DialogResponseView;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;

public class DialogClicksTest {

    private static final UUID PLAYER = UUID.randomUUID();
    private static final UUID OTHER = UUID.randomUUID();
    private static final long LIFETIME = 60_000;

    private final AtomicLong now = new AtomicLong(1000);
    private final DialogClicks clicks = new DialogClicks(this.now::get);

    private static BiConsumer<Player, DialogResponseView> action() {
        return (player, view) -> {
        };
    }

    private static String payload(ClickEvent event) {
        assertEquals(DialogClicks.KEY, ((ClickEvent.Payload.Custom) event.payload()).key());
        return ((ClickEvent.Payload.Custom) event.payload()).nbt().string();
    }

    private String add(BiConsumer<Player, DialogResponseView> action) {
        return payload(this.clicks.add(PLAYER, action, DialogClicks.UNLIMITED_USES, LIFETIME));
    }

    private static String value(String payload, String key) {
        int start = payload.indexOf(key + "\":") + key.length() + 2;
        int end = start;
        while (end < payload.length() && Character.isDigit(payload.charAt(end))) end++;
        return payload.substring(start, end);
    }

    @Test
    public void aClickFindsItsOwnAreaOnlyForItsPlayer() {
        BiConsumer<Player, DialogResponseView> first = action();
        BiConsumer<Player, DialogResponseView> second = action();
        String firstPayload = this.add(first);
        String secondPayload = this.add(second);

        assertSame(first, this.clicks.use(PLAYER, firstPayload));
        assertSame(second, this.clicks.use(PLAYER, secondPayload));
        assertNull(this.clicks.use(OTHER, firstPayload));
        assertNull(this.clicks.use(PLAYER, "{\"zmenu:t\":999,\"zmenu:a\":0}"));
        assertNull(this.clicks.use(PLAYER, "nonsense"));
    }

    @Test
    public void theKeysOfThePayloadMayComeInAnyOrderNextToTheDialogInputs() {
        BiConsumer<Player, DialogResponseView> action = action();
        String payload = this.add(action);
        String table = value(payload, "zmenu:t");
        String area = value(payload, "zmenu:a");
        String submitted = "{t:\"7\", a: 3b, name: \"zmenu:t\", \"zmenu:a\": " + area + ", 'zmenu:t': " + table + "}";
        assertSame(action, this.clicks.use(PLAYER, submitted));
    }

    @Test
    public void aValueTypedInAnInputCannotPickAnotherArea() {
        BiConsumer<Player, DialogResponseView> pressed = action();
        BiConsumer<Player, DialogResponseView> other = action();
        String payload = this.add(pressed);
        String otherArea = value(this.add(other), "zmenu:a");
        String table = value(payload, "zmenu:t");
        String area = value(payload, "zmenu:a");
        String forged = "{name: \"x\\\",\\\"zmenu:a\\\":" + otherArea + ",\", list: [\"zmenu:a\", {\"zmenu:a\": " + otherArea + "}], \"zmenu:t\": "
                + table + ", \"zmenu:a\": " + area + "}";
        assertSame(pressed, this.clicks.use(PLAYER, forged));
        assertNull(this.clicks.use(PLAYER, "{name: \"\\\"zmenu:t\\\":" + table + ",\\\"zmenu:a\\\":" + otherArea + "\"}"));
    }

    @Test
    public void aDialogBuiltForAnotherPlayerKeepsItsClicksForTheViewer() {
        BiConsumer<Player, DialogResponseView> action = action();
        String[] payload = new String[1];
        this.clicks.open(OTHER, () -> payload[0] = this.add(action));
        assertSame(action, this.clicks.use(OTHER, payload[0]));
        assertNull(this.clicks.use(PLAYER, payload[0]));
    }

    @Test
    public void aDialogThatFailsToOpenKeepsTheClicksOfTheOneShown() {
        String shown = this.add(action());
        assertThrows(IllegalStateException.class, () -> this.clicks.open(PLAYER, () -> {
            this.add(action());
            throw new IllegalStateException("cannot build");
        }));
        assertNotNull(this.clicks.use(PLAYER, shown));
    }

    @Test
    public void openingADialogAgainForgetsTheClicksCreatedBefore() {
        String payload = this.add(action());
        this.clicks.reset(PLAYER);
        assertNull(this.clicks.use(PLAYER, payload));
        assertNotNull(this.clicks.use(PLAYER, this.add(action())));
    }

    @Test
    public void clicksExpireAndLeavingForgetsThem() {
        String payload = this.add(action());
        this.now.addAndGet(LIFETIME + 1);
        assertNull(this.clicks.use(PLAYER, payload));

        String fresh = this.add(action());
        assertNotNull(this.clicks.use(PLAYER, fresh));
        this.clicks.clear(PLAYER);
        assertNull(this.clicks.use(PLAYER, fresh));
        assertEquals(0, this.clicks.size());
    }

    @Test
    public void eachClickKeepsItsOwnLifetime() {
        String shortLived = payload(this.clicks.add(PLAYER, action(), DialogClicks.UNLIMITED_USES, 1_000));
        String longLived = this.add(action());
        this.now.addAndGet(1_001);
        assertNull(this.clicks.use(PLAYER, shortLived));
        assertNotNull(this.clicks.use(PLAYER, longLived));
    }

    @Test
    public void aClickWithUsesStopsWhenTheyAreSpent() {
        String payload = payload(this.clicks.add(PLAYER, action(), 2, LIFETIME));
        assertNotNull(this.clicks.use(PLAYER, payload));
        assertNotNull(this.clicks.use(PLAYER, payload));
        assertNull(this.clicks.use(PLAYER, payload));

        String unlimited = this.add(action());
        for (int index = 0; index < 10; index++) assertNotNull(this.clicks.use(PLAYER, unlimited));
    }

    @Test
    public void aFullTableStartsANewOne() {
        String first = this.add(action());
        for (int index = 1; index < DialogClicks.MAX_AREAS; index++) this.add(action());
        assertNotNull(this.clicks.use(PLAYER, first));
        this.add(action());
        assertNull(this.clicks.use(PLAYER, first));
    }
}
