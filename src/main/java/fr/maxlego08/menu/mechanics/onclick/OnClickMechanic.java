package fr.maxlego08.menu.mechanics.onclick;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.mechanic.Mechanic;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Requirement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.ClickType;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class OnClickMechanic extends Mechanic<OnClickMechanicFactory> {

    public enum ClickTarget {
        AIR, BLOCK, BOTH;

        public static ClickTarget fromString(String value) {
            if (value == null) return BOTH;
            return switch (value.toLowerCase(Locale.ROOT)) {
                case "air" -> AIR;
                case "block" -> BLOCK;
                default -> BOTH;
            };
        }
    }

    private final int cooldown;
    private final boolean cancelEvent;
    private final List<ClickType> clickTypes;
    private final List<Requirement> clickRequirements;
    private final List<Action> actions;
    private final ClickTarget clickTarget;

    public OnClickMechanic(String itemId, OnClickMechanicFactory factory, ConfigurationSection section,
                           YamlConfiguration configuration, File file, String path) {
        super(itemId, factory, section);

        MenuPlugin plugin = factory.getPlugin();

        this.cooldown = section.getInt("cooldown", 0);
        this.cancelEvent = section.getBoolean("cancel-event", false);
        this.clickTarget = ClickTarget.fromString(section.getString("click-target"));
        this.clickTypes = plugin.getInventoryManager().loadClicks(section.getStringList("click-types"));

        ConfigurationSection reqSection = section.getConfigurationSection("click-requirements");
        try {
            this.clickRequirements = reqSection != null
                    ? plugin.getButtonManager().loadRequirements(configuration, path + reqSection.getName(), file)
                    : List.of();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load click requirements for item: " + itemId, e);
        }

        this.actions = plugin.getButtonManager().loadActions(configuration, path + "actions", file);
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public boolean shouldCancelEvent() {
        return this.cancelEvent;
    }

    public ClickTarget getClickTarget() {
        return this.clickTarget;
    }

    public List<ClickType> getClickTypes() {
        return this.clickTypes;
    }

    public List<Requirement> getClickRequirements() {
        return this.clickRequirements;
    }

    public List<Action> getActions() {
        return this.actions;
    }
}
