package fr.maxlego08.menu.api.button.buttons;

import fr.maxlego08.menu.api.button.Button;

/**
 * Represents the default button type in a user interface within a Minecraft plugin, extending
 * the basic {@link Button} interface. This button type is used as a fallback option and is
 * automatically chosen by the plugin's UI handling system when no specific button type is identified
 * for a given UI element.
 * <p>
 * The NoneButton serves as a generic button with standard behavior, suitable for basic interactions
 * where no specialized functionality is required. It provides a baseline implementation that can be
 * used to display a clickable item or option within an inventory GUI without attaching any additional
 * logic or behavior that is specific to the context of the UI being created.
 * </p>
 * <p>
 * This interface is ideal for simple actions or when you want to introduce a button that adheres to
 * the default interaction patterns established by the plugin's UI framework. Implementations might
 * include basic navigation, confirmation actions, or triggers for non-complex operations.
 * </p>
 */
public abstract class NoneButton extends Button {

}
