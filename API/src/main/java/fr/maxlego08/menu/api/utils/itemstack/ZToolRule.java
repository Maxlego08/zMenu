package fr.maxlego08.menu.api.utils.itemstack;

/**
 * @deprecated The tool component now keeps its rules as
 * {@link fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableToolRule}, in order and with optional values.
 */
@Deprecated
public record ZToolRule<T>(T data, float speed, boolean correctForDrop) {
}
