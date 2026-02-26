package fr.maxlego08.menu.zcore.utils;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.enums.PerformanceFilterMode;
import fr.maxlego08.menu.zcore.logger.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Pattern;

public class PerformanceDebug {

    private static final PerformanceDebug DISABLED_INSTANCE = new PerformanceDebug(null, false);

    private final String context;
    private final boolean enabled;
    private final List<OperationRecord> records = new ArrayList<>();
    private final Deque<ActiveOperation> stack = new ArrayDeque<>();

    private PerformanceDebug(String context, boolean enabled) {
        this.context = context;
        this.enabled = enabled;
    }

    public static PerformanceDebug disabled() {
        return DISABLED_INSTANCE;
    }

    public static PerformanceDebug create(String context) {
        if (!Configuration.enablePerformanceDebug) {
            return DISABLED_INSTANCE;
        }
        return new PerformanceDebug(context, true);
    }

    public void start(String operationName) {
        if (!enabled) return;
        this.stack.push(new ActiveOperation(operationName, System.nanoTime()));
    }

    public void end() {
        if (!enabled) return;
        if (this.stack.isEmpty()) return;
        ActiveOperation active = this.stack.pop();
        long duration = System.nanoTime() - active.startNanos;
        this.records.add(new OperationRecord(active.name, duration));
    }

    public void measure(String operationName, Runnable action) {
        if (!enabled) {
            action.run();
            return;
        }
        start(operationName);
        try {
            action.run();
        } finally {
            end();
        }
    }

    public void printSummary() {
        if (!enabled || records.isEmpty()) return;

        long thresholdNanos = Configuration.performanceThresholdMs * 1_000_000L;
        PerformanceFilterMode filterMode = Configuration.performanceFilterMode;
        List<String> filterOperations = Configuration.performanceFilterOperations;

        List<Pattern> filterPatterns = new ArrayList<>(filterOperations.size());
        for (String op : filterOperations) {
            String regex = op.replace(".", "\\.").replace("*", ".*");
            filterPatterns.add(Pattern.compile(regex));
        }

        Logger.info("[PerformanceDebug] === " + context + " ===");

        long totalNanos = 0;
        int displayed = 0;

        for (OperationRecord record : records) {
            totalNanos += record.durationNanos;

            if (record.durationNanos < thresholdNanos) continue;

            if (!matchesFilter(record.name, filterMode, filterPatterns)) continue;

            double ms = record.durationNanos / 1_000_000.0;
            Logger.info(String.format("[PerformanceDebug]   %-40s %8.2f ms", record.name, ms));
            displayed++;
        }

        if (displayed == 0 && thresholdNanos > 0) {
            Logger.info("[PerformanceDebug]   (all operations below threshold of " + Configuration.performanceThresholdMs + " ms)");
        }

        double totalMs = totalNanos / 1_000_000.0;
        Logger.info(String.format("[PerformanceDebug] --- Total: %.2f ms (%d operations recorded) ---", totalMs, records.size()));
    }

    private boolean matchesFilter(String operationName, PerformanceFilterMode mode, List<Pattern> patterns) {
        if (mode == PerformanceFilterMode.DISABLED || patterns.isEmpty()) {
            return true;
        }

        boolean matches = false;
        for (Pattern pattern : patterns) {
            if (pattern.matcher(operationName).matches()) {
                matches = true;
                break;
            }
        }

        return mode == PerformanceFilterMode.WHITELIST ? matches : !matches;
    }

    private record ActiveOperation(String name, long startNanos) {
    }

    private record OperationRecord(String name, long durationNanos) {
    }
}
