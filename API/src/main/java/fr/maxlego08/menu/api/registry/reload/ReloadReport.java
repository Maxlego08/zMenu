package fr.maxlego08.menu.api.registry.reload;

import fr.maxlego08.menu.api.registry.RegistryType;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * What a reload of a reloadable registry found.
 *
 * <p>When there is an error, the new snapshot is rejected and the previous one stays live.</p>
 *
 * @param type       the registry that was reloaded
 * @param issues     everything providers and validators reported
 * @param applied    whether the new snapshot replaced the previous one
 * @param generation the generation of the registry after the reload
 */
public record ReloadReport(@NotNull RegistryType<?> type, @NotNull List<Issue> issues, boolean applied, long generation) {

    public ReloadReport {
        issues = List.copyOf(issues);
    }

    public enum Severity {
        WARNING,
        ERROR
    }

    /**
     * @param severity how serious it is
     * @param owner    the provider or validator that reported it
     * @param subject  the entry it is about, if any
     * @param message  what is wrong, meant for whoever wrote the file
     */
    public record Issue(@NotNull Severity severity, @NotNull String owner, @Nullable Key subject, @NotNull String message) {
    }

    public boolean hasErrors() {
        return this.issues.stream().anyMatch(issue -> issue.severity() == Severity.ERROR);
    }

    public @NotNull List<Issue> errors() {
        return this.issues.stream().filter(issue -> issue.severity() == Severity.ERROR).toList();
    }

    public @NotNull List<Issue> warnings() {
        return this.issues.stream().filter(issue -> issue.severity() == Severity.WARNING).toList();
    }

    /**
     * @return one line per issue, ready to be logged
     */
    public @NotNull List<String> format() {
        return this.issues.stream()
                .map(issue -> "[" + issue.severity() + "] " + issue.owner() + (issue.subject() == null ? "" : " " + issue.subject().asString()) + ": " + issue.message())
                .toList();
    }
}
