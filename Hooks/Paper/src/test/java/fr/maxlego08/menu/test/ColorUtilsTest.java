package fr.maxlego08.menu.test;

import fr.maxlego08.menu.hooks.MiniMessageColorUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ColorUtilsTest extends MiniMessageColorUtils {

    // ========================================
    // Legacy Hex Pattern Tests (§x§r§g§b§2§f§3)
    // ========================================

    @Test
    @DisplayName("Convert legacy hex format to MiniMessage format")
    void testLegacyHexConversion() {
        String input = "§x§a§1§b§2§c§3Hello";
        String expected = "<#a1b2c3>Hello";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Convert multiple legacy hex colors")
    void testMultipleLegacyHexConversion() {
        String input = "§x§f§f§0§0§0§0Red§x§0§0§f§f§0§0Blue";
        String expected = "<#ff0000>Red<#00ff00>Blue";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Convert legacy hex with uppercase letters")
    void testLegacyHexUppercase() {
        String input = "§x§A§B§C§D§E§FText";
        String expected = "<#ABCDEF>Text";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Convert legacy hex with mixed case")
    void testLegacyHexMixedCase() {
        String input = "§x§a§B§c§D§e§FText";
        String expected = "<#aBcDeF>Text";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    // ========================================
    // Short Legacy Hex Pattern Tests (&#a1b2c3)
    // ========================================

    @Test
    @DisplayName("Convert short legacy hex format with ampersand")
    void testShortLegacyHexConversion() {
        String input = "&#55ff55Hello";
        String expected = "<#55ff55>Hello";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Convert multiple short legacy hex colors")
    void testMultipleShortLegacyHexConversion() {
        String input = "&#ff0000Red&#00ff00Green&#0000ffBlue";
        String expected = "<#ff0000>Red<#00ff00>Green<#0000ff>Blue";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Convert short legacy hex with uppercase")
    void testShortLegacyHexUppercase() {
        String input = "&#ABCDEF Text";
        String expected = "<#ABCDEF> Text";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Test original example from existing test")
    void testColorParsing() {
        String coloredMessage = colorMiniMessage("&#55ff55&lAAA&#55ff8e&lBBB&#55ffc6&lCCC&#55ffff&lDDD");
        Assertions.assertEquals("<#55ff55><bold>AAA<#55ff8e><bold>BBB<#55ffc6><bold>CCC<#55ffff><bold>DDD", coloredMessage);
    }

    // ========================================
    // Simple Hex Pattern Tests (#a1b2c3)
    // ========================================

    @Test
    @DisplayName("Convert simple hex format without prefix")
    void testSimpleHexConversion() {
        String input = "#55ff55Hello";
        String expected = "<#55ff55>Hello";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Convert multiple simple hex colors")
    void testMultipleSimpleHexConversion() {
        String input = "#ff0000Red#00ff00Green#0000ffBlue";
        String expected = "<#ff0000>Red<#00ff00>Green<#0000ff>Blue";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Simple hex should not convert when preceded by <")
    void testSimpleHexNoConversionAfterLessThan() {
        String input = "<#55ff55Hello";
        String expected = "<#55ff55Hello";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Simple hex should not convert when preceded by &")
    void testSimpleHexNoConversionAfterAmpersand() {
        // This is already handled by &#pattern, so plain # after & should convert
        String input = "Test#55ff55Hello";
        String expected = "Test<#55ff55>Hello";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    // ========================================
    // Legacy Color Code Tests (&a, §c, etc.)
    // ========================================

    @Test
    @DisplayName("Convert ampersand color codes to MiniMessage")
    void testAmpersandColorCodes() {
        String input = "&aGreen&cRed&9Blue";
        String expected = "<green>Green<red>Red<blue>Blue";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Convert section symbol color codes to MiniMessage")
    void testSectionColorCodes() {
        String input = "§aGreen§cRed§9Blue";
        String expected = "<green>Green<red>Red<blue>Blue";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Convert all color codes (0-9, a-f)")
    void testAllColorCodes() {
        String input = "&0&1&2&3&4&5&6&7&8&9&a&b&c&d&e&f";
        String expected = "<black><dark_blue><dark_green><dark_aqua><dark_red><dark_purple><gold><gray><dark_gray><blue><green><aqua><red><light_purple><yellow><white>";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Convert uppercase color codes")
    void testUppercaseColorCodes() {
        String input = "&AHello&CWorld";
        String expected = "<green>Hello<red>World";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Convert mixed case color codes")
    void testMixedCaseColorCodes() {
        String input = "&aHello&CWorld&Etest";
        String expected = "<green>Hello<red>World<yellow>test";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    // ========================================
    // Format Code Tests (bold, italic, etc.)
    // ========================================

    @Test
    @DisplayName("Convert bold format code")
    void testBoldFormatCode() {
        String input = "&lBold Text";
        String expected = "<bold>Bold Text";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Convert italic format code")
    void testItalicFormatCode() {
        String input = "&oItalic Text";
        String expected = "<italic>Italic Text";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Convert underlined format code")
    void testUnderlinedFormatCode() {
        String input = "&nUnderlined Text";
        String expected = "<underlined>Underlined Text";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Convert strikethrough format code")
    void testStrikethroughFormatCode() {
        String input = "&mStrikethrough Text";
        String expected = "<strikethrough>Strikethrough Text";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Convert obfuscated format code")
    void testObfuscatedFormatCode() {
        String input = "&kObfuscated Text";
        String expected = "<obfuscated>Obfuscated Text";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Convert reset format code")
    void testResetFormatCode() {
        String input = "&rReset Text";
        String expected = "<reset>Reset Text";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Convert all format codes")
    void testAllFormatCodes() {
        String input = "&k&l&m&n&o&r";
        String expected = "<obfuscated><bold><strikethrough><underlined><italic><reset>";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Convert format codes with section symbol")
    void testSectionFormatCodes() {
        String input = "§lBold§oItalic§nUnderlined";
        String expected = "<bold>Bold<italic>Italic<underlined>Underlined";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Convert uppercase format codes")
    void testUppercaseFormatCodes() {
        String input = "&LBold&OItalic";
        String expected = "<bold>Bold<italic>Italic";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    // ========================================
    // Combined Pattern Tests
    // ========================================

    @Test
    @DisplayName("Combine hex color with format codes")
    void testHexWithFormatCodes() {
        String input = "&#ff0000&lBold Red";
        String expected = "<#ff0000><bold>Bold Red";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Combine legacy color with format codes")
    void testLegacyColorWithFormatCodes() {
        String input = "&a&lBold Green&r&cRed";
        String expected = "<green><bold>Bold Green<reset><red>Red";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Combine all three hex patterns")
    void testAllHexPatterns() {
        String input = "§x§f§f§0§0§0§0A&#00ff00B#0000ffC";
        String expected = "<#ff0000>A<#00ff00>B<#0000ff>C";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Complex combination with all patterns")
    void testComplexCombination() {
        String input = "&a&lGreen Bold §x§f§f§0§0§0§0&#00ff00Hex&r#0000ff&oBlue Italic";
        String expected = "<green><bold>Green Bold <#ff0000><#00ff00>Hex<reset><#0000ff><italic>Blue Italic";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Mix section symbol and ampersand")
    void testMixedSymbols() {
        String input = "&aAmpersand§cSection&lBold§oItalic";
        String expected = "<green>Ampersand<red>Section<bold>Bold<italic>Italic";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    // ========================================
    // Edge Cases and Special Scenarios
    // ========================================

    @Test
    @DisplayName("Empty string should return empty")
    void testEmptyString() {
        String input = "";
        String expected = "";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("String without color codes should remain unchanged")
    void testNoColorCodes() {
        String input = "Plain text without colors";
        String expected = "Plain text without colors";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Invalid hex code should not be converted")
    void testInvalidHexCode() {
        String input = "&#gghhii Invalid";
        String expected = "&#gghhii Invalid";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Incomplete hex code should not be converted")
    void testIncompleteHexCode() {
        String input = "&#ff00 Incomplete";
        String expected = "&#ff00 Incomplete";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Already converted MiniMessage should not double convert")
    void testAlreadyConverted() {
        String input = "<#ff0000>Already converted";
        String expected = "<#ff0000>Already converted";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Multiple spaces between color codes")
    void testMultipleSpaces() {
        String input = "&a   Green   &c   Red";
        String expected = "<green>   Green   <red>   Red";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Newlines with color codes")
    void testNewlines() {
        String input = "&aGreen\n&cRed\n&9Blue";
        String expected = "<green>Green\n<red>Red\n<blue>Blue";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Consecutive color codes")
    void testConsecutiveColorCodes() {
        String input = "&a&l&n&oText";
        String expected = "<green><bold><underlined><italic>Text";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Color codes at end of string")
    void testColorCodesAtEnd() {
        String input = "Text&a&l";
        String expected = "Text<green><bold>";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Short hex at start of string")
    void testShortHexAtStart() {
        String input = "&#ff0000Start";
        String expected = "<#ff0000>Start";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Short hex at end of string")
    void testShortHexAtEnd() {
        String input = "End&#ff0000";
        String expected = "End<#ff0000>";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Special characters with color codes")
    void testSpecialCharacters() {
        String input = "&aHello!@#$%^&*()_+-=[]{}|;':\",./<>?";
        String expected = "<green>Hello!@#$%^&*()_+-=[]{}|;':\",./<>?";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Unicode characters with color codes")
    void testUnicodeCharacters() {
        String input = "&aHéllo Wörld 你好 مرحبا";
        String expected = "<green>Héllo Wörld 你好 مرحبا";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Very long string with multiple colors")
    void testLongString() {
        StringBuilder input = new StringBuilder();
        StringBuilder expected = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            input.append("&a").append(i).append(" ");
            expected.append("<green>").append(i).append(" ");
        }
        Assertions.assertEquals(expected.toString(), colorMiniMessage(input.toString()));
    }

    @Test
    @DisplayName("Only color codes without text")
    void testOnlyColorCodes() {
        String input = "&a&l&n&#ff0000§x§0§0§f§f§0§0";
        String expected = "<green><bold><underlined><#ff0000><#00ff00>";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Real world example - gradient text")
    void testGradientText() {
        String input = "&#ff0000R&#ff3300a&#ff6600i&#ff9900n&#ffcc00b&#ffff00o&#ccff00w";
        String expected = "<#ff0000>R<#ff3300>a<#ff6600>i<#ff9900>n<#ffcc00>b<#ffff00>o<#ccff00>w";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Real world example - server MOTD")
    void testServerMOTD() {
        String input = "&#55ff55&l✦ &#55ff8e&lMY SERVER &#55ffc6&l✦\n&7Join us for fun!";
        String expected = "<#55ff55><bold>✦ <#55ff8e><bold>MY SERVER <#55ffc6><bold>✦\n<gray>Join us for fun!";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Real world example - chat prefix")
    void testChatPrefix() {
        String input = "&#ff0000[&#ff9900Admin&#ff0000]&r &aPlayerName&f: Message";
        String expected = "<#ff0000>[<#ff9900>Admin<#ff0000>]<reset> <green>PlayerName<white>: Message";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }

    @Test
    @DisplayName("Preserve existing MiniMessage color tags")
    void testMiniMessageColorTagsPreserved() {
        String input = "<color:#FF5555>This is a <color:#55FF55>test!";
        String expected = "<color:#FF5555>This is a <color:#55FF55>test!";
        Assertions.assertEquals(expected, colorMiniMessage(input));
    }
}