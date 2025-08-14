package fr.maxlego08.menu.zcore.utils.toast;

import fr.maxlego08.menu.api.utils.toast.ToastType;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author therealthread
 * <a href="https://github.com/therealthread/aneToastAPI">AneToastAPI</a>
 */
public class ToastBuilder {
    private final AdvancementHandler advancementHandler;
    private String icon = "paper";
    private String message = "Toast Message";
    private ToastType style = ToastType.TASK;
    private Object modelData;
    private Boolean glowing = false;
    private Collection<? extends Player> players;
    private boolean isToAll = false;
    private String modelDataType = null;

    ToastBuilder(AdvancementHandler advancementHandler) {
        this.advancementHandler = advancementHandler;
    }

    /**
     * Determines the players to display the notification
     *
     * @param players Players to display the notification
     * @return This builder
     */
    public ToastBuilder to(Collection<? extends Player> players) {
        this.players = players;
        this.isToAll = false;
        return this;
    }

    /**
     * Determines the players to display the notification
     *
     * @param players Players to display the notification
     * @return This builder
     */
    public ToastBuilder to(Player... players) {
        this.players = Arrays.asList(players);
        this.isToAll = false;
        return this;
    }

    /**
     * Determines whether the notification will be displayed to all players on the server
     *
     * @return This builder
     **/
    public ToastBuilder toAll() {
        this.isToAll = true;
        return this;
    }

    /**
     * Determines the icon to be displayed in the notification
     *
     * @param icon Minecraft item ID ("diamond", "paper" etc.)
     * @return This builder
     */
    public ToastBuilder withIcon(String icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Determines the message to be displayed in the notification
     * | character can be used for line break
     *
     * @param message Message to be displayed
     * @return This builder
     */
    public ToastBuilder withMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Determines the style of the notification
     *
     * @param style Toast style
     * @return This builder
     */
    public ToastBuilder withStyle(ToastType style) {
        this.style = style;
        return this;
    }

    /**
     * Determines the glowing of the notifying icon
     *
     * @param glowing Icon glowing
     * @return This builder
     */
    public ToastBuilder setGlowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    public ToastBuilder withModelData(Object modelData) {
        this.modelData = modelData;

        if (modelData instanceof String) {
            this.modelDataType = "string";
        } else if (modelData instanceof Float || modelData instanceof Double) {
            this.modelDataType = "float";
        } else {
            this.modelDataType = "integer";
        }
        return this;
    }

    /**
     * Sets the model data and its type explicitly
     *
     * @param modelData     The model data (String, Float, or Integer)
     * @param modelDataType The type of model data ("string", "float", or "integer")
     * @return This builder
     */
    public ToastBuilder withModelData(Object modelData, String modelDataType) {
        this.modelData = modelData;
        this.modelDataType = modelDataType;
        return this;
    }

    /**
     * Notifications are generated and displayed to specified players
     */
    public void show() {
        if (isToAll) {
            advancementHandler.showToastToAll(icon, message, style, modelData, modelDataType, glowing);
        } else {
            if (players == null || players.isEmpty()) {
                throw new IllegalStateException("[ERROR TOAST POP-UP] No player found!");
            }
            advancementHandler.showToast(players, icon, message, style, modelData, modelDataType, glowing);
        }
    }
}