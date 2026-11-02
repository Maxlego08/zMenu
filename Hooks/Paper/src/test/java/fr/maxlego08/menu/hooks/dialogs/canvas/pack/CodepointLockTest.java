package fr.maxlego08.menu.hooks.dialogs.canvas.pack;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class CodepointLockTest {

    @TempDir
    Path folder;

    @Test
    public void startsInPlaneFifteen() throws IOException {
        CodepointLock lock = CodepointLock.load(this.folder.resolve("codepoints.lock"));
        assertEquals(0xF0000, lock.codepoint("a"));
        assertEquals(0xF0001, lock.codepoint("b"));
        assertEquals(0xF0000, lock.codepoint("a"));
    }

    @Test
    public void keepsCharactersAcrossLoads() throws IOException {
        Path file = this.folder.resolve("codepoints.lock");
        CodepointLock first = CodepointLock.load(file);
        int a = first.codepoint("a");
        int b = first.codepoint("b");
        first.save();

        CodepointLock second = CodepointLock.load(file);
        int c = second.codepoint("c");
        assertEquals(a, second.codepoint("a"));
        assertEquals(b, second.codepoint("b"));
        assertNotEquals(a, c);
        assertNotEquals(b, c);
    }

    @Test
    public void neverGivesAnUnusedCharacterBack() throws IOException {
        Path file = this.folder.resolve("codepoints.lock");
        CodepointLock first = CodepointLock.load(file);
        first.codepoint("a");
        int removed = first.codepoint("b");
        first.save();

        CodepointLock second = CodepointLock.load(file);
        assertNotEquals(removed, second.codepoint("c"));
    }

    @Test
    public void savesOnlyWhenSomethingChanged() throws IOException {
        Path file = this.folder.resolve("codepoints.lock");
        CodepointLock lock = CodepointLock.load(file);
        lock.save();
        assertFalse(Files.exists(file));
    }
}
