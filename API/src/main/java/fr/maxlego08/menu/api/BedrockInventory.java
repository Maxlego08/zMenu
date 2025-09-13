package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.button.bedrock.BedrockButton;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.bedrock.BedrockType;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.OpenWithItem;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

public interface BedrockInventory {
    String getName(Player player);

    String getContent(Player player);

    String getFileName();

    MenuPlugin getPlugin();

    File getFile();

    void setFile(File file);

    void setBedrockButtons(List<BedrockButton> bedrockButtons);

    List<BedrockButton> getBedrockButtons();

    List<BedrockButton> getBedrockButtons(Player player);

    void setInputButtons(List<InputButton> inputButtons);

    List<InputButton> getInputButtons();

    List<InputButton> getInputButtons(Player player);

    void setOpenRequirement(Requirement openRequirement);

    Requirement getOpenRequirement();

    boolean hasOpenRequirement(Player player);

    void setBedrockType(BedrockType bedrockType);

    void setRequirements(List<Requirement> actions);

    List<Requirement> getRequirements();

    BedrockType getBedrockType();
}
