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
 * <p>The BoutonLoader is the class that will allow the loading of a Button</p>
 * <p>Examples:</p>
 * <ul>
 *     <li><a href="https://github.com/Maxlego08/zMenu/blob/master/src/fr/maxlego08/menu/button/loader/NoneLoader.java">NoneLoader</a></li>
 *     <li><a href="https://github.com/Maxlego08/zMenu/blob/master/src/fr/maxlego08/menu/button/loader/InventoryLoader.java">InventoryLoader</a></li>
 *     <li><a href="https://github.com/Maxlego08/zMenu/blob/master/src/fr/maxlego08/menu/button/loader/HomeLoader.java">HomeLoader</a></li>
 *     <li><a href="https://github.com/Maxlego08/zMenu/blob/master/src/fr/maxlego08/menu/button/loader/NextLoader.java">NextLoader</a></li>
 *     <li><a href="https://github.com/Maxlego08/zMenu/blob/master/src/fr/maxlego08/menu/button/loader/PreviousLoader.java">PreviousLoader</a></li>
 * </ul>
 * <p>You must specify a unique name for your button, it is recommended to do this <strong>'your plugin name'_'button name'</strong></p>
 * <p>For example, if your plugin is called "test" and your button is called "magic" you should put this: <strong>test_magic</strong></p>
 * <p>You can then return a new instance of your button with the important elements in it.</p>
 */
public interface ButtonLoader {

    /**
     * Allows to load a list of string to transform it into integer
     * Allows to have several formats for the slots
     * More information <a href="https://zmenu.groupez.dev/configurations/buttons#none_slot">here</a>
     *
     * @param slotsAsString List of slot as string
     * @return slots
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
     * Returns the class that will be used for the button
     *
     * @return classz
     */
    Class<? extends Button> getButton();

    /**
     * Returns the name of the button
     *
     * @return name
     */
    String getName();

    /**
     * Return the plugin where the loader button comes from
     *
     * @return plugin
     */
    Plugin getPlugin();

    /**
     * Allows you to load a button
     *
     * @param configuration Current file configuration
     * @param path Current button path
     * @param defaultButtonValue Default button values
     * @return Button
     */
    Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue);
}
