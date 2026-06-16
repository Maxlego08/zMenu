package fr.maxlego08.menu.zcore.utils;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.enums.PerformanceFilterMode;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private PerformanceDebug(@Nullable String context, boolean enabled) {
        this.context = context;
        this.enabled = enabled;
    }

    @NotNull
    public static PerformanceDebug disabled() {
        return DISABLED_INSTANCE;
    }

    @NotNull
    public static PerformanceDebug create(@NotNull String context) {
        if (!Configuration.enablePerformanceDebug) {
            return DISABLED_INSTANCE;
        }
        return new PerformanceDebug(context, true);
    }

    public void start(@NotNull String operationName) {
        if (!this.enabled) return;
        this.stack.push(new ActiveOperation(operationName, System.nanoTime()));
    }

    public void end() {
        if (!this.enabled) return;
        if (this.stack.isEmpty()) return;
        ActiveOperation active = this.stack.pop();
        long duration = System.nanoTime() - active.startNanos;
        this.records.add(new OperationRecord(active.name, duration));
    }

    public void measure(@NotNull String operationName, @NotNull Runnable action) {
        if (!this.enabled) {
            action.run();
            return;
        }
        this.start(operationName);
        try {
            action.run();
        } finally {
            this.end();
        }
    }

    public void printSummary() {
        if (!this.enabled || this.records.isEmpty()) return;

        long thresholdNanos = Configuration.performanceThresholdMs * 1_000_000L;
        PerformanceFilterMode filterMode = Configuration.performanceFilterMode;
        List<String> filterOperations = Configuration.performanceFilterOperations;

        List<Pattern> filterPatterns = new ArrayList<>(filterOperations.size());
        for (String op : filterOperations) {
            String regex = op.replace(".", "\\.").replace("*", ".*");
            filterPatterns.add(Pattern.compile(regex));
        }

        Logger.info("[PerformanceDebug] === " + this.context + " ===");

        long totalNanos = 0;
        int displayed = 0;

        for (OperationRecord record : this.records) {
            totalNanos += record.durationNanos;

            if (record.durationNanos < thresholdNanos) continue;

            if (!this.matchesFilter(record.name, filterMode, filterPatterns)) continue;

            double ms = record.durationNanos / 1_000_000.0;
            Logger.info(String.format("[PerformanceDebug]   %-40s %8.2f ms", record.name, ms));
            displayed++;
        }

        if (displayed == 0 && thresholdNanos > 0) {
            Logger.info("[PerformanceDebug]   (all operations below threshold of " + Configuration.performanceThresholdMs + " ms)");
        }

        double totalMs = totalNanos / 1_000_000.0;
        Logger.info(String.format("[PerformanceDebug] --- Total: %.2f ms (%d operations recorded) ---", totalMs, this.records.size()));
    }

    private boolean matchesFilter(@NotNull String operationName,@NotNull PerformanceFilterMode mode,@NotNull List<Pattern> patterns) {
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

        return (mode == PerformanceFilterMode.WHITELIST) == matches;
    }

    private record ActiveOperation(@NotNull String name, long startNanos) {
    }

    private record OperationRecord(@NotNull String name, long durationNanos) {
    }
}
