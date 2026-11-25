package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.buttons.SwitchButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.SwitchCaseButton;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.IntPredicate;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

public class ZSwitchButton extends SwitchButton {

    private final String placeholder;
    private final List<SwitchCaseButton> buttons;

    public ZSwitchButton(String placeholder, List<SwitchCaseButton> buttons) {
        this.placeholder = placeholder;
        this.buttons = buttons;
    }

    @Override
    protected void serializeProperties(@NotNull Section section) {
        section.set("type", "switch");
        section.set("key", this.placeholder);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    protected void serializeProperties(ConfigurationSection section) {
        YamlSections.write(section, copy -> this.serializeProperties(copy));
    }

    @Override
    public void serialize(@NotNull Section section, int inventorySize) {
        super.serialize(section, inventorySize);
        if (this.buttons.isEmpty()) return;
        Section buttonsSection = section.createSection("buttons");
        for (SwitchCaseButton button : this.buttons) {
            button.button().serialize(buttonsSection.createSection(button.value()), inventorySize);
        }
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public void serialize(ConfigurationSection section, int inventorySize) {
        YamlSections.write(section, copy -> this.serialize(copy, inventorySize));
    }

    @Override
    public String getPlaceholder() {
        return this.placeholder;
    }

    @Override
    public List<SwitchCaseButton> getButtons() {
        return this.buttons;
    }

    @Override
    public Button getDisplayButton(InventoryEngine inventoryEngine, Player player) {

        if (this.buttons.isEmpty()) return super.getDisplayButton(inventoryEngine, player);

        if (this.buttons.size() == 1) return this.buttons.getFirst().button();

        String result = inventoryEngine.getPlugin().parse(player, this.placeholder);

        for (SwitchCaseButton button : this.buttons) {

            String value = button.value();

            if (value.startsWith(">=")) {

                if (matches(result, value.substring(2), comparison -> comparison >= 0)) return button.button();

            } else if (value.startsWith("<=")) {

                if (matches(result, value.substring(2), comparison -> comparison <= 0)) return button.button();

            } else if (value.startsWith(">")) {

                if (matches(result, value.substring(1), comparison -> comparison > 0)) return button.button();

            } else if (value.startsWith("<")) {

                if (matches(result, value.substring(1), comparison -> comparison < 0)) return button.button();

            } else if (value.equals(result)) return button.button();
        }

        return super.getDisplayButton(inventoryEngine, player);
    }

    /**
     * Tests the parsed placeholder against a case value.
     * <p>
     * The placeholder can be anything at runtime: an unresolved {@code %placeholder%} when the
     * providing plugin is missing, an empty string, or a decimal such as a money balance. None of
     * those may throw, otherwise the whole inventory fails to render, so a side that is not a
     * number simply never matches.
     */
    private static boolean matches(String result, String value, IntPredicate predicate) {
        Double left = parseOrNull(result);
        Double right = parseOrNull(value);
        return left != null && right != null && predicate.test(Double.compare(left, right));
    }

    private static Double parseOrNull(String value) {
        try {
            return Double.parseDouble(value.trim().replace(",", "."));
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
