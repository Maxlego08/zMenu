package fr.maxlego08.menu.api.registry.reload;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Collects what goes wrong while a snapshot is being built. An error rejects the whole snapshot,
 * a warning only gets reported.
 */
public interface ReloadReporter {

    void report(@NotNull ReloadReport.Severity severity, @Nullable Key subject, @NotNull String message);

    default void error(@NotNull String message) {
        this.report(ReloadReport.Severity.ERROR, null, message);
    }

    default void error(@NotNull Key subject, @NotNull String message) {
        this.report(ReloadReport.Severity.ERROR, subject, message);
    }

    default void warn(@NotNull String message) {
        this.report(ReloadReport.Severity.WARNING, null, message);
    }

    default void warn(@NotNull Key subject, @NotNull String message) {
        this.report(ReloadReport.Severity.WARNING, subject, message);
    }
}
