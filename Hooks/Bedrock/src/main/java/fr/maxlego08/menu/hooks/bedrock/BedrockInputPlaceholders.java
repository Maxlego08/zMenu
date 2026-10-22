package fr.maxlego08.menu.hooks.bedrock;

import fr.maxlego08.menu.api.utils.Placeholders;
import org.geysermc.cumulus.component.*;

import java.util.List;

public final class BedrockInputPlaceholders {

    private BedrockInputPlaceholders() {}

    public static void register(Placeholders p, String key, Object raw, Component component) {
        if (raw == null) return;

        switch (component) {
            case SliderComponent slider when raw instanceof Number n -> slider(p, key, n.doubleValue(), slider);
            case DropdownComponent dropdown when raw instanceof Number n -> choice(p, key, n.intValue(), dropdown.options());
            case StepSliderComponent step when raw instanceof Number n -> choice(p, key, n.intValue(), step.steps());
            case ToggleComponent ignored when raw instanceof Boolean b -> toggle(p, key, b);
            case null, default -> generic(p, key, raw);
        }
    }

    private static void generic(Placeholders p, String key, Object raw) {
        if (raw instanceof Boolean b) toggle(p, key, b);
        else if (raw instanceof Number n) p.register(key, format(n.doubleValue()));
        else p.register(key, raw.toString());
    }

    private static void slider(Placeholders p, String key, double v, SliderComponent slider) {
        double min = slider.minValue(), max = slider.maxValue(), range = max - min;
        p.register(key, format(v));
        put(p, key, "min", format(min));
        put(p, key, "max", format(max));
        put(p, key, "percent", Math.round(range == 0 ? 0 : (v - min) / range * 100.0));
    }

    private static void choice(Placeholders p, String key, int index, List<String> options) {
        p.register(key, String.valueOf(index));
        put(p, key, "index", index);
        put(p, key, "count", options.size());
        if (index >= 0 && index < options.size()) {
            put(p, key, "display", options.get(index));
        }
    }

    private static void toggle(Placeholders p, String key, boolean b) {
        p.register(key, String.valueOf(b));
        put(p, key, "bool", b);
        put(p, key, "int", b ? 1 : 0);
        put(p, key, "yesno", b ? "yes" : "no");
        put(p, key, "onoff", b ? "on" : "off");
        put(p, key, "not", !b);
    }

    private static void put(Placeholders p, String key, String suffix, Object value) {
        p.register(key + "." + suffix, String.valueOf(value));
    }

    private static String format(double d) {
        return d == Math.rint(d) && Math.abs(d) < 1e15 ? String.valueOf((long) d) : String.valueOf(d);
    }
}