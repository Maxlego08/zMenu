package fr.maxlego08.menu.test.common;

import fr.maxlego08.menu.api.utils.version.PluginVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PluginVersionTest {

    // -------------------------------------------------------------------------
    // toString / parsing
    // -------------------------------------------------------------------------

    @Test
    void testToString() {
        Assertions.assertEquals("1.1.0", PluginVersion.parse("1.1.0").toString());
        Assertions.assertEquals("61.2", PluginVersion.parse("61.2").toString());
        Assertions.assertEquals("0", PluginVersion.parse("0").toString());
        Assertions.assertEquals("1.0.0", PluginVersion.parse("1.0.0").toString());
    }

    @Test
    void testParseNull() {
        Assertions.assertEquals("0", PluginVersion.parse(null).toString());
    }

    @Test
    void testParseBlank() {
        Assertions.assertEquals("0", PluginVersion.parse("").toString());
        Assertions.assertEquals("0", PluginVersion.parse("   ").toString());
    }

    @Test
    void testParseWithBuildSuffix() {
        // Everything after '-' should be ignored
        Assertions.assertEquals("1.2.3", PluginVersion.parse("1.2.3-SNAPSHOT").toString());
        Assertions.assertEquals("2.0", PluginVersion.parse("2.0-beta1").toString());
    }

    @Test
    void testParseWithNonNumericChars() {
        // Non-digit characters inside a segment are stripped
        Assertions.assertEquals("1.2.3", PluginVersion.parse("v1.2.3").toString());
        Assertions.assertEquals("1.2.3", PluginVersion.parse("1.2.3a").toString());
    }

    // -------------------------------------------------------------------------
    // Leading zero stripping  →  0.61.2 == 61.2
    // -------------------------------------------------------------------------

    @Test
    void testLeadingZeroSegmentsStripped() {
        PluginVersion a = PluginVersion.parse("0.61.2");
        PluginVersion b = PluginVersion.parse("61.2");
        Assertions.assertEquals(0, a.compareTo(b), "0.61.2 should equal 61.2");
        Assertions.assertTrue(a.isSameAs(b));
    }

    @Test
    void testMultipleLeadingZeroSegmentsStripped() {
        PluginVersion a = PluginVersion.parse("0.0.1");
        PluginVersion b = PluginVersion.parse("1");
        Assertions.assertTrue(a.isSameAs(b), "0.0.1 should equal 1");
    }

    @Test
    void testSingleZeroVersion() {
        PluginVersion a = PluginVersion.parse("0");
        PluginVersion b = PluginVersion.parse("0.0");
        // 0.0 strips leading zero → [0], same as [0]
        Assertions.assertTrue(a.isSameAs(b), "0 should equal 0.0 after stripping");
    }

    // -------------------------------------------------------------------------
    // Trailing zeros are significant  →  61.2.0 > 61.2
    // -------------------------------------------------------------------------

    @Test
    void testTrailingZeroSegmentIsNewer() {
        PluginVersion base    = PluginVersion.parse("61.2");
        PluginVersion withZero = PluginVersion.parse("61.2.0");
        Assertions.assertTrue(withZero.isNewerThan(base), "61.2.0 should be newer than 61.2");
    }

    @Test
    void testTrailingZeroSegmentMultiple() {
        PluginVersion a = PluginVersion.parse("1.0.0");
        PluginVersion b = PluginVersion.parse("1.0.0.0");
        Assertions.assertTrue(b.isNewerThan(a), "1.0.0.0 should be newer than 1.0.0");
        Assertions.assertTrue(a.isOlderThan(b), "1.0.0 should be older than 1.0.0.0");
    }

    // -------------------------------------------------------------------------
    // compareTo / ordering
    // -------------------------------------------------------------------------

    @Test
    void testComparison() {
        PluginVersion v1 = PluginVersion.parse("1.0.0");
        PluginVersion v2 = PluginVersion.parse("1.1.0");
        PluginVersion v3 = PluginVersion.parse("1.0.0");

        Assertions.assertTrue(v1.compareTo(v2) < 0, "1.0.0 < 1.1.0");
        Assertions.assertTrue(v2.compareTo(v1) > 0, "1.1.0 > 1.0.0");
        Assertions.assertEquals(0, v1.compareTo(v3), "1.0.0 == 1.0.0");
    }

    @Test
    void testMajorVersionDominates() {
        PluginVersion v1 = PluginVersion.parse("2.0.0");
        PluginVersion v2 = PluginVersion.parse("1.99.99");
        Assertions.assertTrue(v1.isNewerThan(v2), "2.0.0 should be newer than 1.99.99");
    }

    @Test
    void testMinorVersionComparison() {
        PluginVersion v1 = PluginVersion.parse("1.2.0");
        PluginVersion v2 = PluginVersion.parse("1.10.0");
        Assertions.assertTrue(v1.isOlderThan(v2), "1.2.0 should be older than 1.10.0");
    }

    @Test
    void testPatchVersionComparison() {
        PluginVersion v1 = PluginVersion.parse("1.0.9");
        PluginVersion v2 = PluginVersion.parse("1.0.10");
        Assertions.assertTrue(v1.isOlderThan(v2), "1.0.9 should be older than 1.0.10");
    }

    @Test
    void testDifferentSegmentCounts() {
        PluginVersion v1 = PluginVersion.parse("1.0");
        PluginVersion v2 = PluginVersion.parse("1.0.1");
        Assertions.assertTrue(v1.isOlderThan(v2), "1.0 should be older than 1.0.1");
    }

    // -------------------------------------------------------------------------
    // isNewerThan / isOlderThan / isSameAs helpers
    // -------------------------------------------------------------------------

    @Test
    void testHelperMethods() {
        PluginVersion low  = PluginVersion.parse("1.0");
        PluginVersion high = PluginVersion.parse("2.0");
        PluginVersion copy = PluginVersion.parse("1.0");

        Assertions.assertTrue(high.isNewerThan(low));
        Assertions.assertFalse(low.isNewerThan(high));
        Assertions.assertTrue(low.isOlderThan(high));
        Assertions.assertFalse(high.isOlderThan(low));
        Assertions.assertTrue(low.isSameAs(copy));
        Assertions.assertFalse(low.isSameAs(high));
    }

    // -------------------------------------------------------------------------
    // equals / hashCode
    // -------------------------------------------------------------------------

    @Test
    void testEqualsSymmetry() {
        PluginVersion a = PluginVersion.parse("1.2.3");
        PluginVersion b = PluginVersion.parse("1.2.3");
        Assertions.assertEquals(a, b);
        Assertions.assertEquals(b, a);
    }

    @Test
    void testEqualsWithLeadingZeros() {
        PluginVersion a = PluginVersion.parse("0.61.2");
        PluginVersion b = PluginVersion.parse("61.2");
        Assertions.assertEquals(a, b, "0.61.2 and 61.2 should be equal");
    }

    @Test
    void testNotEquals() {
        PluginVersion a = PluginVersion.parse("1.0");
        PluginVersion b = PluginVersion.parse("1.0.0");
        Assertions.assertNotEquals(a, b, "1.0 and 1.0.0 should not be equal (trailing zero is significant)");
    }

    @Test
    void testHashCodeConsistentWithEquals() {
        PluginVersion a = PluginVersion.parse("0.1.2");
        PluginVersion b = PluginVersion.parse("1.2");
        Assertions.assertEquals(a, b);
        Assertions.assertEquals(a.hashCode(), b.hashCode(), "Equal versions must have the same hashCode");
    }

    @Test
    void testHashCodeDifferentForDifferentVersions() {
        PluginVersion a = PluginVersion.parse("1.0");
        PluginVersion b = PluginVersion.parse("1.0.0");
        Assertions.assertNotEquals(a.hashCode(), b.hashCode());
    }
}