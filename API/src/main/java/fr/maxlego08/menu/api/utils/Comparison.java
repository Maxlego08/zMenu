package fr.maxlego08.menu.api.utils;

public enum Comparison {
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL_TO,
    LESS_THAN,
    LESS_THAN_OR_EQUAL_TO,
    EQUAL,
    NOT_EQUAL;

    public boolean compare(int result) {
        return switch (this) {
            case GREATER_THAN -> result > 0;
            case GREATER_THAN_OR_EQUAL_TO -> result >= 0;
            case LESS_THAN -> result < 0;
            case LESS_THAN_OR_EQUAL_TO -> result <= 0;
            case EQUAL -> result == 0;
            case NOT_EQUAL -> result != 0;
        };
    }
}
