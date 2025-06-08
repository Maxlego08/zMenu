package fr.maxlego08.menu.hooks;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.forms.FormImage;
import org.geysermc.floodgate.api.forms.SimpleForm;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.List;
import java.util.Map;

public class FloodgateBuilder {

    /**
     * Send a SimpleForm to a Floodgate player based on the provided menu configuration.
     *
     * @param player Player to whom the menu will be sent. This should be a Floodgate player.
     * @param menuConfig Menu YAML configuration section containing the Floodgate-specific settings.
     */
    public SimpleForm.Builder buildSimpleForm(Player player, ConfigurationSection menuConfig) {
        ConfigurationSection floodgateConfig = menuConfig.getConfigurationSection("floodgate");

        String title = floodgateConfig.getString("title", "Menu");
        String content = floodgateConfig.getString("content", "");

        SimpleForm.Builder formBuilder = SimpleForm.builder()
                .title(title)
                .content(content);

        List<Map<?, ?>> buttonsConfig = floodgateConfig.getMapList("buttons");
        if (buttonsConfig != null && !buttonsConfig.isEmpty()) {
            for (Map<?, ?> buttonMap : buttonsConfig) {
                String buttonText = buttonMap.get("text") instanceof String ? (String) buttonMap.get("text") : "Bouton";

                if (buttonMap.containsKey("imageType") && buttonMap.containsKey("imageData")) {
                    String imageTypeStr = String.valueOf(buttonMap.get("imageType"));
                    String imageData = String.valueOf(buttonMap.get("imageData"));
                    try {
                        FormImage.Type imageType = FormImage.Type.valueOf(imageTypeStr.toUpperCase());
                        formBuilder.button(buttonText, imageType, imageData);
                    } catch (IllegalArgumentException e) {
                        formBuilder.button(buttonText);
                    }
                } else {
                    formBuilder.button(buttonText);
                }
            }
        } else {
            formBuilder.content(content + "\n\n(No buttons configured)");
        }
        
        return formBuilder;
    }
}
