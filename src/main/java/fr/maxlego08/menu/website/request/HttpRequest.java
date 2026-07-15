package fr.maxlego08.menu.website.request;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class HttpRequest {

    // Never let a connection hang forever (default HttpURLConnection timeout is infinite): a stalled
    // download during a folder-sync burst would otherwise pin its async thread and leak the socket.
    private static final int CONNECT_TIMEOUT_MS = 10_000;
    private static final int READ_TIMEOUT_MS = 30_000;

    // A folder sync pulls one YAML per inventory through a per-token rate limit, so a brief 429/5xx/timeout
    // is expected on big folders: retry it (bounded) instead of silently dropping the inventory.
    private static final int MAX_DOWNLOAD_ATTEMPTS = 3;
    private static final long BACKOFF_BASE_MS = 1_000L;
    private static final long BACKOFF_MAX_MS = 15_000L;

    private final String url;
    private final JsonObject data;
    private String bearer;

    private String method = "POST";

    public HttpRequest(String url, JsonObject data) {
        this.url = url;
        this.data = data;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setBearer(String bearer) {
        this.bearer = bearer;
    }

    public void submit(ZMenuPlugin plugin, Consumer<Response> consumer) {
        plugin.getScheduler().runAsync(w -> {
            Map map = new HashMap<>();
            HttpURLConnection connection;
            int responseCode = -1;

            try {
                URL url = new URI(this.url).toURL();
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
                connection.setReadTimeout(READ_TIMEOUT_MS);

                connection.setRequestMethod(this.method);
                connection.addRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json");

                if (this.bearer != null) {
                    connection.setRequestProperty("Authorization", "Bearer " + this.bearer);
                }

                if ("POST".equals(this.method) || "PUT".equals(this.method)) {
                    connection.setDoOutput(true);
                    try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                        String jsonInputString = this.data.toString();
                        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                        outputStream.write(input, 0, input.length);
                    }
                }

                responseCode = connection.getResponseCode();

                try (InputStream inputStream = connection.getInputStream(); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        builder.append(line);
                    }
                    Gson gson = new Gson();
                    map = gson.fromJson(builder.toString(), Map.class);
                }

            } catch (Exception exception) {
                if (Configuration.enableDebug) {
                    exception.printStackTrace();
                }
            }

            Response response = new Response(responseCode, map);
            consumer.accept(response);
        });
    }

    /**
     * Download a file, reporting only pass/fail. Backed by the same bounded-retry engine as
     * {@link #submitForFileDownloadDetailed} so every download (live sync + marketplace) survives a
     * transient 429/5xx/timeout instead of dropping on the first failure.
     */
    public void submitForFileDownload(ZMenuPlugin plugin, File fileOut, Consumer<Boolean> consumer) {
        plugin.getScheduler().runAsync(w -> consumer.accept(this.downloadWithRetry(fileOut).success));
    }

    /**
     * Download a file, reporting the final {@link DownloadResult} (success + last HTTP status code) so the
     * caller can tell apart a rate-limit (429) from an auth failure (401/403) or a transport error (-1).
     */
    public void submitForFileDownloadDetailed(ZMenuPlugin plugin, File fileOut, Consumer<DownloadResult> consumer) {
        plugin.getScheduler().runAsync(w -> consumer.accept(this.downloadWithRetry(fileOut)));
    }

    /**
     * Download with bounded retry + exponential backoff (with jitter). Retries transient failures
     * (HTTP 429 - honouring Retry-After -, 5xx, connect/read timeouts, connection resets) so a folder sync
     * that briefly trips the website's per-token rate limit recovers instead of silently dropping
     * inventories. Terminal failures (401/403/404 and other 4xx) are not retried. Runs entirely on the
     * caller's async thread (the backoff sleeps within this one task, so it never spawns extra threads).
     */
    private DownloadResult downloadWithRetry(File fileOut) {
        int lastCode = -1;
        for (int attempt = 1; attempt <= MAX_DOWNLOAD_ATTEMPTS; attempt++) {
            Attempt result = this.downloadOnce(fileOut);
            lastCode = result.code;

            if (result.success) {
                return new DownloadResult(true, result.code);
            }
            if (!isRetryable(result.code) || attempt == MAX_DOWNLOAD_ATTEMPTS) {
                return new DownloadResult(false, result.code);
            }

            try {
                Thread.sleep(backoffMillis(attempt, result.retryAfterMs));
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                return new DownloadResult(false, result.code);
            }
        }
        return new DownloadResult(false, lastCode);
    }

    /**
     * A single download attempt. On a 2xx the body is streamed to {@code fileOut} and the FileOutputStream
     * is fully closed before returning - so the caller's subsequent atomic move never hits a Windows file
     * lock ("the file is used by another process"). On a non-2xx the error stream is drained/closed and
     * success stays false; the HTTP status (or -1 on a transport error) is returned so the retry layer can
     * decide whether to back off and try again.
     */
    private Attempt downloadOnce(File fileOut) {
        HttpURLConnection connection = null;
        try {
            URL url = new URI(this.url).toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.setRequestMethod(this.method);
            connection.addRequestProperty("Accept", "application/yaml");

            if (this.bearer != null) connection.setRequestProperty("Authorization", "Bearer " + this.bearer);

            if ("POST".equals(this.method) || "PUT".equals(this.method)) {
                connection.setDoOutput(true);
                try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                    byte[] input = this.data.toString().getBytes(StandardCharsets.UTF_8);
                    outputStream.write(input, 0, input.length);
                }
            }

            int code = connection.getResponseCode();
            if (code < 200 || code >= 300) {
                long retryAfterMs = parseRetryAfterMs(connection);
                drainQuietly(connection.getErrorStream());
                return new Attempt(false, code, retryAfterMs);
            }

            try (InputStream inputStream = connection.getInputStream(); FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
            }
            // FileOutputStream is now closed (try-with-resources) - safe for the caller's atomic move.
            return new Attempt(true, code, 0L);

        } catch (Exception exception) {
            if (Configuration.enableDebug) {
                exception.printStackTrace();
            }
            // Connect/read timeout, connection reset, DNS failure, etc. - a retryable transport error.
            return new Attempt(false, -1, 0L);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static boolean isRetryable(int code) {
        return code == -1 || code == 429 || (code >= 500 && code <= 599);
    }

    private static long backoffMillis(int attempt, long retryAfterMs) {
        // Honour an explicit Retry-After (429) when present; otherwise exponential backoff with jitter.
        long base = retryAfterMs > 0 ? retryAfterMs : (long) (BACKOFF_BASE_MS * Math.pow(2, attempt - 1));
        long jitter = ThreadLocalRandom.current().nextLong(BACKOFF_BASE_MS);
        return Math.min(BACKOFF_MAX_MS, base + jitter);
    }

    private static long parseRetryAfterMs(HttpURLConnection connection) {
        String header = connection.getHeaderField("Retry-After");
        if (header == null) {
            return 0L;
        }
        try {
            // Retry-After is most commonly a number of seconds.
            return Long.parseLong(header.trim()) * 1000L;
        } catch (NumberFormatException ignored) {
            return 0L; // HTTP-date form not handled here; fall back to exponential backoff.
        }
    }

    private static void drainQuietly(InputStream stream) {
        if (stream == null) {
            return;
        }
        try (InputStream s = stream) {
            byte[] buffer = new byte[4096];
            while (s.read(buffer) != -1) {
                // discard - draining the error body lets the JVM reuse the keep-alive socket.
            }
        } catch (IOException ignored) {
        }
    }

    /**
         * Final outcome of a download: whether it succeeded and the last HTTP status code seen
         * (or -1 for a transport-level error such as a timeout or connection reset).
         */
        public record DownloadResult(boolean success, int code) {
    }

    private record Attempt(boolean success, int code, long retryAfterMs) {
    }

}
