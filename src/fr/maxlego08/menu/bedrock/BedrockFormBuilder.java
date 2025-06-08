package fr.maxlego08.menu.bedrock;

import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

public class BedrockFormBuilder {

    public SimpleForm.Builder createSimpleForm(FloodgatePlayer player, String title) {
        return SimpleForm.builder().title(title);
    }

    public ModalForm.Builder createModalForm(FloodgatePlayer player, String title, String content) {
        return ModalForm.builder().title(title).content(content);
    }

    public CustomForm.Builder createCustomForm(FloodgatePlayer player, String title) {
        return CustomForm.builder().title(title);
    }

}
