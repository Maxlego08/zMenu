package fr.maxlego08.menu.api.loader;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The ButtonLoader interface defines methods for loading buttons and related configurations.
 * Examples of implementations can be found in:
 * <ul>
 *     <li><a href="https://github.com/Maxlego08/zMenu/blob/master/src/fr/maxlego08/menu/button/loader/NoneLoader.java">NoneLoader</a></li>
 *     <li><a href="https://github.com/Maxlego08/zMenu/blob/master/src/fr/maxlego08/menu/button/loader/InventoryLoader.java">InventoryLoader</a></li>
 *     <li><a href="https://github.com/Maxlego08/zMenu/blob/master/src/fr/maxlego08/menu/button/loader/HomeLoader.java">HomeLoader</a></li>
 *     <li><a href="https://github.com/Maxlego08/zMenu/blob/master/src/fr/maxlego08/menu/button/loader/NextLoader.java">NextLoader</a></li>
 *     <li><a href="https://github.com/Maxlego08/zMenu/blob/master/src/fr/maxlego08/menu/button/loader/PreviousLoader.java">PreviousLoader</a></li>
 * </ul>
 * You must specify a unique name for your button, typically in the format 'your plugin name'_'button name'.
 * For example, if your plugin is called "test" and your button is called "magic", the name should be 'test_magic'.
 * You can then return a new instance of your button with the essential elements initialized.
 */
public interface ButtonLoader {

    /**
     * Loads a list of strings and transforms it into integers, supporting various slot formats.
     * More information <a href="https://zmenu.groupez.dev/configurations/buttons#none_slot">here</a>.
     *
     * @param slotsAsString List of slot strings
     * @return List of slots as integers
     */
    static List<Integer> loadSlot(List<String> slotsAsString) {
        List<Integer> slots = new ArrayList<>();
        if (slotsAsString.size() > 0) {
            for (String line : slotsAsString) {
                if (line.contains("-")) {
                    try {
                        String[] values = line.split("-");
                        int from = Integer.parseInt(values[0]);
                        int to = Integer.parseInt(values[1]) + 1;
                        slots.addAll(IntStream.range(Math.min(from, to), Math.max(from, to)).boxed()
                                .collect(Collectors.toList()));
                    } catch (Exception ignored) {
                    }
                } else {
                    try {
                        slots.add(Integer.parseInt(line));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return slots;
    }

    /**
     * Gets the class that will be used for the button.
     *
     * @return The button class.
     */
    Class<? extends Button> getButton();

    /**
     * Gets the name of the button.
     *
     * @return The button name.
     */
    String getName();

    /**
     * Gets the plugin from which the button loader originates.
     *
     * @return The plugin.
     */
    Plugin getPlugin();

    /**
     * Loads a button based on the provided configuration.
     *
     * @param configuration      The current file configuration.
     * @param path               The current button path.
     * @param defaultButtonValue Default button values.
     * @return The loaded button.
     */
    Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue);
}
