package fr.maxlego08.menu.benchmarks;

import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.hooks.MiniMessageColorUtils;
import fr.robie.minimessageflow.MiniMessageFlow;
import fr.robie.minimessageflow.Template;
import fr.robie.minimessageflow.ValueKey;
import fr.robie.minimessageflow.Values;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * The text of one item (a name and six lore lines, three placeholders), formatted the way zMenu does it today and
 * the ways the MiniMessageFlow plan replaces it. A full {@code ZMenuItemStack.build} needs a server; this is the part
 * of it the plan changes.
 *
 * <ul>
 *   <li>{@code legacy*}: {@code Placeholders.parse}, the legacy-to-MiniMessage regex chain, then
 *   {@code MiniMessage.deserialize}, with the component cache keyed by the resolved text ({@code ComponentMeta}).</li>
 *   <li>{@code flowFormat*}: the same resolved text given to {@code MiniMessageFlow.format} (plan P2).</li>
 *   <li>{@code flowTemplates*}: each line compiled once, rendered from per-player {@code Values} (plan P3).</li>
 * </ul>
 * {@code *Stable}: 50 players in turn, each with its own values that do not change (a menu refreshed).
 * {@code *Changing}: a value changes at each build (a balance, a timer), so a cache keyed by resolved text misses.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ItemTextBenchmark {

    private static final int PLAYERS = 50;
    private static final String NAME = "&6&lShop &8» &e%item%";
    private static final List<String> LORE = List.of(
            "&7Price: &a%price%$",
            "&7Stock: &f%stock%",
            "&7Seller: &b%seller%",
            "",
            "&8» &7Click to buy",
            "<gradient:#ff5555:#55ff55>Limited offer</gradient>");

    private final Legacy legacy = new Legacy();
    private final Map<String, Component> legacyCache = new ConcurrentHashMap<>();
    private final Component reset = Component.empty().decoration(TextDecoration.ITALIC, false);

    private final MiniMessageFlow flow;
    private final ValueKey item;
    private final ValueKey price;
    private final ValueKey stock;
    private final ValueKey seller;
    private final Template nameTemplate;
    private final List<Template> loreTemplates = new ArrayList<>();

    private final Placeholders[] placeholders = new Placeholders[PLAYERS];
    private final Values[] values = new Values[PLAYERS];
    private final Values[] remembered = new Values[PLAYERS];
    private final Placeholders changingPlaceholders = new Placeholders();
    private final Values changingValues;
    private int next;
    private long counter;

    public ItemTextBenchmark() {
        MiniMessageFlow.Builder builder = MiniMessageFlow.builder();
        this.item = builder.key("item");
        this.price = builder.key("price");
        this.stock = builder.key("stock");
        this.seller = builder.key("seller");
        this.flow = builder.build();
        this.nameTemplate = this.flow.compile(NAME);
        for (String line : LORE) {
            this.loreTemplates.add(this.flow.compile(line));
        }
        for (int i = 0; i < PLAYERS; i++) {
            Placeholders p = new Placeholders();
            p.register("item", "Diamond Sword");
            p.register("price", String.valueOf(100 + i));
            p.register("stock", String.valueOf(i));
            p.register("seller", "Player" + i);
            this.placeholders[i] = p;
            this.values[i] = this.fill(this.flow.newValues(), i);
            this.remembered[i] = this.fill(this.flow.newValues().rememberRenders(true), i);
        }
        this.changingPlaceholders.register("item", "Diamond Sword");
        this.changingPlaceholders.register("stock", "7");
        this.changingPlaceholders.register("seller", "Steve");
        this.changingValues = this.flow.newValues().set(this.item, "Diamond Sword").set(this.stock, 7L).set(this.seller, "Steve");
    }

    private Values fill(Values values, int i) {
        return values.set(this.item, "Diamond Sword").set(this.price, 100L + i).set(this.stock, (long) i).set(this.seller, "Player" + i);
    }

    private int nextPlayer() {
        int i = this.next;
        this.next = i + 1 == PLAYERS ? 0 : i + 1;
        return i;
    }

    private Placeholders changingPlaceholders() {
        this.changingPlaceholders.register("price", String.valueOf(this.counter++));
        return this.changingPlaceholders;
    }

    private Values changingValues() {
        return this.changingValues.set(this.price, this.counter++);
    }

    @Benchmark
    public List<Component> legacyStable() {
        return this.legacyItem(this.placeholders[this.nextPlayer()], true);
    }

    @Benchmark
    public List<Component> legacyChanging() {
        return this.legacyItem(this.changingPlaceholders(), false);
    }

    @Benchmark
    public List<Component> flowFormatStable() {
        return this.formatItem(this.placeholders[this.nextPlayer()]);
    }

    @Benchmark
    public List<Component> flowFormatChanging() {
        return this.formatItem(this.changingPlaceholders());
    }

    @Benchmark
    public List<Component> flowTemplatesStable() {
        return this.templateItem(this.values[this.nextPlayer()]);
    }

    @Benchmark
    public List<Component> flowTemplatesStableRemembered() {
        return this.templateItem(this.remembered[this.nextPlayer()]);
    }

    @Benchmark
    public List<Component> flowTemplatesChanging() {
        return this.templateItem(this.changingValues());
    }

    private List<Component> legacyItem(Placeholders placeholders, boolean cache) {
        List<Component> out = new ArrayList<>(LORE.size() + 1);
        out.add(this.legacyLine(placeholders.parse(NAME), cache));
        for (String line : LORE) {
            out.add(this.legacyLine(placeholders.parse(line), cache));
        }
        return out;
    }

    private Component legacyLine(String resolved, boolean cache) {
        if (!cache) {
            return this.legacy.parse(resolved, this.reset);
        }
        return this.legacyCache.computeIfAbsent("item:" + resolved, key -> this.legacy.parse(resolved, this.reset));
    }

    private List<Component> formatItem(Placeholders placeholders) {
        List<Component> out = new ArrayList<>(LORE.size() + 1);
        out.add(this.reset.append(this.flow.format(placeholders.parse(NAME))));
        for (String line : LORE) {
            out.add(this.reset.append(this.flow.format(placeholders.parse(line))));
        }
        return out;
    }

    private List<Component> templateItem(Values values) {
        List<Component> out = new ArrayList<>(LORE.size() + 1);
        out.add(this.reset.append(this.nameTemplate.render(values, null)));
        for (Template template : this.loreTemplates) {
            out.add(this.reset.append(template.render(values, null)));
        }
        return out;
    }

    /**
     * zMenu's current conversion, as {@code ComponentMeta} does it for an item line.
     */
    private static final class Legacy extends MiniMessageColorUtils {
        private final MiniMessage miniMessage = MiniMessage.miniMessage();

        Component parse(String text, Component reset) {
            return reset.append(this.miniMessage.deserialize(this.colorMiniMessage(text)).decoration(TextDecoration.ITALIC, false));
        }
    }
}
