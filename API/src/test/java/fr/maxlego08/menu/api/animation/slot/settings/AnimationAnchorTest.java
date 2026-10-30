package fr.maxlego08.menu.api.animation.slot.settings;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for the anchors used to place things relative to the groups of an animation.
 */
class AnimationAnchorTest {

    @Test
    void keywordsAreParsed() {
        assertEquals(AnimationAnchor.START, AnimationAnchor.parse("start"));
        assertEquals(AnimationAnchor.END, AnimationAnchor.parse("END"));
        assertEquals(AnimationAnchor.START, AnimationAnchor.parse("  Start  "));
    }

    @Test
    void aPlainNumberIsATimeInMilliseconds() {
        assertEquals(AnimationAnchor.at(250), AnimationAnchor.parse("250"));
        assertEquals(AnimationAnchor.at(0), AnimationAnchor.parse("-40"));
    }

    @Test
    void groupBoundariesAreParsed() {
        assertEquals(AnimationAnchor.groupStart(2), AnimationAnchor.parse("group:2"));
        assertEquals(AnimationAnchor.groupEnd(2), AnimationAnchor.parse("group:2.end"));
        assertEquals(AnimationAnchor.groupStart(10), AnimationAnchor.parse("GROUP:10"));
    }

    @Test
    void bothEndsOfAGroupCanBeWrittenExplicitly() {
        assertEquals(AnimationAnchor.parse("group:2"), AnimationAnchor.parse("group:2.start"));
        assertEquals(AnimationAnchor.groupStart(2), AnimationAnchor.parse("group:2.start"));
        assertEquals(AnimationAnchor.groupEnd(2), AnimationAnchor.parse("GROUP:2.END"));
    }

    @Test
    void theExplicitSectionFormIsParsed() {
        assertEquals(AnimationAnchor.groupStart(2), AnimationAnchor.parseValue(Map.of("group", 2)));
        assertEquals(AnimationAnchor.groupStart(2), AnimationAnchor.parseValue(Map.of("group", 2, "at", "start")));
        assertEquals(AnimationAnchor.groupEnd(2), AnimationAnchor.parseValue(Map.of("group", 2, "at", "END")));
        assertEquals(AnimationAnchor.groupEnd(3), AnimationAnchor.parseValue(Map.of("group", "3", "at", "end")));
    }

    @Test
    void withoutAGroupTheSectionFormAcceptsAnyCompactValue() {
        assertEquals(AnimationAnchor.at(250), AnimationAnchor.parseValue(Map.of("at", 250)));
        assertEquals(AnimationAnchor.START, AnimationAnchor.parseValue(Map.of("at", "start")));
        assertEquals(AnimationAnchor.groupEnd(2), AnimationAnchor.parseValue(Map.of("at", "group:2.end")));
    }

    @Test
    void bothFormsAgree() {
        assertEquals(AnimationAnchor.parse("group:2.end"), AnimationAnchor.parseValue(Map.of("group", 2, "at", "end")));
        assertEquals(AnimationAnchor.parse("250"), AnimationAnchor.parseValue(250));
        assertEquals(AnimationAnchor.parse("end"), AnimationAnchor.parseValue("end"));
    }

    @Test
    void anInvalidSectionIsRejectedRatherThanGuessed() {
        assertNull(AnimationAnchor.parseValue(null));
        assertNull(AnimationAnchor.parseValue(Map.of()));
        assertNull(AnimationAnchor.parseValue(Map.of("group", "x")));
        assertNull(AnimationAnchor.parseValue(Map.of("group", 2, "at", "middle")));
        assertNull(AnimationAnchor.parseValue(Map.of("at", "soon")));
    }

    @Test
    void anInvalidAnchorIsRejectedRatherThanGuessed() {
        assertNull(AnimationAnchor.parse(null));
        assertNull(AnimationAnchor.parse(""));
        assertNull(AnimationAnchor.parse("   "));
        assertNull(AnimationAnchor.parse("group:x"));
        assertNull(AnimationAnchor.parse("group:"));
        assertNull(AnimationAnchor.parse("group:2.middle"));
        assertNull(AnimationAnchor.parse("soon"));
    }
}
