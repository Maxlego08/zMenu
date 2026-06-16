package fr.maxlego08.menu.api.utils;

public record TextChange(
        TextChangeType type,
        String oldText,
        String newText,
        Character changedChar
) {
    public static TextChange compute(String oldText, String newText) {
        if (oldText.equals(newText))
            return new TextChange(TextChangeType.EQUAL, oldText, newText, null);
        if (newText.isEmpty())
            return new TextChange(TextChangeType.CLEARED, oldText, newText, null);

        int delta = newText.length() - oldText.length();

        if (delta == 1 && newText.startsWith(oldText))
            return new TextChange(TextChangeType.ADDED, oldText, newText, newText.charAt(newText.length() - 1));

        if (delta == -1 && oldText.startsWith(newText))
            return new TextChange(TextChangeType.REMOVED, oldText, newText, oldText.charAt(oldText.length() - 1));

        return new TextChange(TextChangeType.REPLACED, oldText, newText, null);
    }
}