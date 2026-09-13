package fr.maxlego08.menu.zcore.utils.discord;

import fr.maxlego08.menu.zcore.logger.Logger;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Checks that a Discord webhook exists, without ever blocking the thread that asks.
 * <p>
 * The check used to run inline while the action was being loaded, so a reload with an unreachable
 * Discord froze the server for up to ten seconds per distinct webhook. The action is now always
 * built and the webhook is verified in the background, purely to warn the administrator.
 * <p>
 * Only successful checks are remembered: a webhook that failed once is checked again on the next
 * reload, instead of staying disabled until the server restarts.
 */
public final class DiscordWebhookChecker {

    private static final Set<String> VALID_WEBHOOKS = ConcurrentHashMap.newKeySet();
    private static final Map<String, Boolean> PENDING = new ConcurrentHashMap<>();

    private DiscordWebhookChecker() {
    }

    /**
     * Verifies the webhook in the background and logs a warning if it does not answer.
     *
     * @param webhookUrl the webhook to check, may be null
     * @param file       the file the webhook was declared in, used in the warning
     */
    public static void verifyAsync(String webhookUrl, String file) {
        if (webhookUrl == null || webhookUrl.isBlank()) return;
        if (VALID_WEBHOOKS.contains(webhookUrl)) return;
        if (PENDING.putIfAbsent(webhookUrl, Boolean.TRUE) != null) return;

        CompletableFuture.runAsync(() -> {
            try {
                if (check(webhookUrl)) {
                    VALID_WEBHOOKS.add(webhookUrl);
                } else {
                    Logger.info("The discord webhook used in " + file + " does not answer, the action will fail: " + webhookUrl, Logger.LogType.WARNING);
                }
            } finally {
                PENDING.remove(webhookUrl);
            }
        });
    }

    private static boolean check(String webhookUrl) {
        HttpURLConnection connection = null;
        try {
            URL url = new URI(webhookUrl).toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            return connection.getResponseCode() == 200;
        } catch (Exception exception) {
            return false;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }
}
