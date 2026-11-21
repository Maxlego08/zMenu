package fr.maxlego08.menu.api.context;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.attribute.AttributeMergeStrategy;
import fr.maxlego08.menu.api.attribute.AttributeWrapper;
import fr.maxlego08.menu.api.enums.AmountType;
import fr.maxlego08.menu.api.enums.MenuItemRarity;
import fr.maxlego08.menu.api.itemstack.*;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.LoreType;
import fr.robie.minimessageflow.Template;
import fr.robie.minimessageflow.TemplateList;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface MenuItemStackContext {

    /**
     * Retrieves the {@link InventoryManager} responsible for managing this menu item stack.
     *
     * @return The inventory manager that manages this menu item stack.
     */
    InventoryManager getInventoryManager();

    /**
     * Returns the path of the configuration file associated with the menu item stack.
     *
     * @return The path of the configuration file.
     */
    String getFilePath();

    /**
     * Returns the path of the configuration file associated with the menu item stack,
     * relative to the root of the configuration folder.
     *
     * @return The path of the configuration file relative to the root of the configuration folder.
     */
    String getPath();

    /**
     * Returns the material of the item stack.
     *
     * @return The material of the item stack.
     */
    String getMaterial();

    /**
     * Retrieves the target player placeholder used in the menu item stack,
     * or {@code null} if no target player placeholder is set.
     *
     * @return The target player placeholder, or {@code null} if none is set.
     */
    String getTargetPlayer();

    /**
     * Retrieves the configured amount placeholder for the item stack.
     *
     * @return the amount expression as a string.
     */
    String getAmount();

    /**
     * Retrieves the amount type for the item stack.
     *
     * @return the amount type.
     */
    AmountType getAmountType();

    /**
     * Returns the skull texture URL associated with this item stack, when applicable.
     *
     * @return the skull URL or {@code null} if none is set.
     */
    String getUrl();

    /**
     * Retrieves the legacy data value configured for this item stack.
     *
     * @return the data value.
     */
    String getData();

    /**
     * Returns the custom durability configured for this item stack.
     *
     * @return the durability value.
     */
    int getDurability();

    /**
     * Retrieves the potion configuration applied to this item stack.
     *
     * @return the potion descriptor, or {@code null} when none is set.
     */
    Potion getPotion();

    /**
     * Returns the lore lines attached to this item stack.
     *
     * @return the lore lines.
     * @deprecated the lore as written; render {@link #getLoreTemplate(String)}. Changing the list returned no longer changes the item: use {@code setLore}.
     */
    @Deprecated(since = "1.1.2.2")
    List<String> getLore();

    /**
     * Retrieves the item flags applied to this item stack.
     *
     * @return the list of item flags.
     */
    List<ItemFlag> getFlags();

    /**
     * Returns the display name configured for the item stack.
     *
     * @return the display name.
     * @deprecated the name as written; render {@link #getDisplayNameTemplate(String)}.
     */
    @Deprecated(since = "1.1.2.2")
    String getDisplayName();

    /**
     * Indicates whether the item should appear with a glowing effect.
     *
     * @return {@code true} if the glowing effect is enabled.
     */
    boolean isGlowing();

    /**
     * Retrieves the custom model data identifier as a string.
     *
     * @return the model identifier.
     */
    String getModelID();

    /**
     * Returns the enchantments configured for this item stack.
     *
     * @return the enchantments map.
     */
    Map<Enchantment, Integer> getEnchantments();

    /**
     * Retrieves the attributes configured for this item stack.
     *
     * @return the list of attributes.
     */
    List<AttributeWrapper> getAttributes();

    /**
     * Retrieves the strategy for merging attribute modifiers when applying custom attributes to items.
     *
     * @return the strategy for merging attribute modifiers.
     * @see AttributeMergeStrategy
     */
    AttributeMergeStrategy getAttributeMergeStrategy();

    /**
     * Returns the banner configuration attached to this item stack.
     *
     * @return the banner descriptor, or {@code null} when none is set.
     */
    Banner getBanner();

    /**
     * Retrieves the firework configuration attached to this item stack.
     *
     * @return the firework descriptor, or {@code null} when none is set.
     */
    Firework getFirework();

    /**
     * Retrieves the leather armor configuration attached to this item stack.
     *
     * @return the leather armor descriptor, or {@code null} when none is set.
     */
    LeatherArmor getLeatherArmor();

    /**
     * Returns the tooltip style identifier applied to this item stack.
     *
     * @return the tooltip style key.
     */
    String getToolTipStyle();

    /**
     * Retrieves the translated display names available for this item stack.
     *
     * @return the map of locale to display name.
     * @deprecated the names as written; render {@link #getDisplayNameTemplate(String)}. Changing the map returned no longer changes the item: use {@code setTranslatedDisplayName}.
     */
    @Deprecated(since = "1.1.2.2")
    Map<String, String> getTranslatedDisplayName();

    /**
     * Retrieves the translated lore entries available for this item stack.
     *
     * @return the map of locale to lore lines.
     * @deprecated the lore as written; render {@link #getLoreTemplate(String)}. Changing the map returned no longer changes the item: use {@code setTranslatedLore}.
     */
    @Deprecated(since = "1.1.2.2")
    Map<String, List<String>> getTranslatedLore();

    /**
     * The display name of a locale, compiled: the translated one, or the default one when the locale has none.
     *
     * @param locale the player's locale ({@code Player#getLocale()}), or null for the default name
     * @return the template, not italic unless the text asks for it, or null when the item has no name
     */
    @SuppressWarnings("deprecation")
    default @Nullable Template getDisplayNameTemplate(@Nullable String locale) {
        String name = locale == null ? this.getDisplayName() : this.getTranslatedDisplayName().getOrDefault(locale, this.getDisplayName());
        return name == null ? null : TextService.compileItemLine(name);
    }

    /**
     * The lore of a locale, compiled: the translated one, or the default one when the locale has none.
     *
     * @param locale the player's locale ({@code Player#getLocale()}), or null for the default lore
     * @return the templates, one per line, empty without lore
     */
    @SuppressWarnings("deprecation")
    default @NotNull TemplateList getLoreTemplate(@Nullable String locale) {
        List<String> lore = locale == null ? this.getLore() : this.getTranslatedLore().getOrDefault(locale, this.getLore());
        return TextService.compileItemLines(lore == null ? List.of() : lore);
    }

    /**
     * Indicates whether PlaceholderAPI is required for this item stack.
     *
     * @return {@code true} if PlaceholderAPI is needed.
     * @deprecated use {@link #isStatic()}: true when the item is not static.
     */
    @Deprecated(since = "1.1.2.2")
    boolean isNeedPlaceholderAPI();

    /**
     * Whether the built item is the same for every player and every build, so it can be kept and given again: all its
     * texts, values and components are static, it has no translations and no target player.
     *
     * @return true when the item never changes
     */
    default boolean isStatic() {
        return !this.isNeedPlaceholderAPI();
    }

    /**
     * Retrieves the cached item stack when caching is enabled.
     *
     * @return the cached ItemStack, or {@code null} when none exists.
     */
    ItemStack getCacheItemStack();

    /**
     * Retrieves the configured maximum stack size for the item.
     *
     * @return the maximum stack size.
     */
    int getMaxStackSize();

    /**
     * Retrieves the configured maximum damage for the item.
     *
     * @return the maximum damage value.
     */
    int getMaxDamage();

    /**
     * Retrieves the current damage value for the item.
     *
     * @return the damage value.
     */
    int getDamage();

    /**
     * Retrieves the repair cost associated with the item.
     *
     * @return the repair cost.
     */
    int getRepairCost();

    /**
     * Indicates whether the item is unbreakable.
     *
     * @return {@code true} if unbreakable is enabled.
     */
    boolean isUnbreakableEnabled();

    /**
     * Indicates whether the unbreakable tooltip should be shown.
     *
     * @return {@code true} if the tooltip is shown.
     */
    boolean isUnbreakableShowInTooltip();

    /**
     * Indicates whether the item is fire resistant.
     *
     * @return {@code true} if fire resistance is enabled.
     */
    boolean isFireResistant();

    /**
     * Indicates whether the tooltip should be hidden.
     *
     * @return {@code true} if the tooltip is hidden.
     */
    boolean isHideTooltip();

    /**
     * Indicates whether additional tooltip information should be hidden.
     *
     * @return {@code true} if additional tooltip content is hidden.
     */
    boolean isHideAdditionalTooltip();

    /**
     * Indicates whether the enchantment glint is enabled.
     *
     * @return {@code true} if the glint is enabled.
     */
    boolean isEnchantmentGlint();

    /**
     * Indicates whether enchantments should be visible in the tooltip.
     *
     * @return {@code true} if enchantments are visible.
     */
    boolean isEnchantmentShowInTooltip();

    /**
     * Indicates whether attributes should be visible in the tooltip.
     *
     * @return {@code true} if attributes are visible.
     */
    boolean isAttributeShowInTooltip();

    /**
     * Retrieves the trim configuration applied to armor items.
     *
     * @return the trim configuration, or {@code null} when none is set.
     */
    TrimConfiguration getTrimConfiguration();

    /**
     * Retrieves the rarity assigned to this item stack.
     *
     * @return the item rarity value.
     */
    MenuItemRarity getItemRarity();

    /**
     * Indicates whether the lore should be centered when displayed.
     *
     * @return {@code true} if lore centering is enabled.
     */
    boolean isCenterLore();

    /**
     * Indicates whether the display name should be centered when displayed.
     *
     * @return {@code true} if name centering is enabled.
     */
    boolean isCenterName();

    /**
     * Retrieves how lore should be merged when applied.
     *
     * @return the lore merge strategy.
     */
    LoreType getLoreType();

    /**
     * Retrieves the item model identifier used for newer rendering APIs.
     *
     * @return the item model identifier.
     */
    NamespacedKey getItemModel();

    /**
     * Retrieves the item model identifier as a string, if applicable.
     * @return the item model identifier as a string, or {@code null} if not set or not applicable.
     */
    String getItemModelString();

    /**
     * Retrieves the equipped model identifier used for wearable items.
     *
     * @return the equipped model identifier.
     */
    String getEquippedModel();

    /**
     * Indicates whether default attributes should be cleared on the item.
     *
     * @return {@code true} if default attributes are cleared.
     */
    boolean isClearDefaultAttributes();

    /**
     * Retrieves the collection of item components attached to this item stack.
     *
     * @return the collection of item components.
     */
    Collection<@NotNull ItemComponent> getItemComponents();
}
