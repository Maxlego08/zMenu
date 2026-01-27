package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;

public class CommandDumplog extends VCommand {
    public CommandDumplog(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("dumplog");
        this.setDescription(Message.DESCRIPTION_DUMPLOG);
        this.setPermission(Permission.ZMENU_DUMPLOG);
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {
        Path logPath = Path.of("logs/latest.log");
        if (!Files.exists(logPath)) {
            message(plugin, this.sender, Message.DUMPLOG_ERROR, "%error%", "Log file not found.");
            return CommandType.SUCCESS;
        }
        plugin.getScheduler().runAsync(var -> {
            try {
                String url = uploadLog(logPath).replace("\\","");
                message(plugin, this.sender, Message.DUMPLOG_SUCCESS, "%url%", plugin.isSpigot() ? url : "<click:open_url:'" + url + "'><green>" + url + "</green></click>");
                if (Configuration.enableInformationMessage) {
                    Logger.info("Log uploaded: " + url);
                }
            } catch (IOException e) {
                message(plugin, this.sender, Message.DUMPLOG_ERROR, "%error%", e.getMessage());
                if (Configuration.enableInformationMessage) {
                    Logger.info("Error uploading log: " + e.getMessage());
                }
            }
        });
        return CommandType.SUCCESS;
    }

    public String uploadLog(Path logPath) throws IOException {
        String logContent = Files.readString(logPath);
        String data = "content=" + URLEncoder.encode(logContent, "UTF-8");

        URL url = new URL("https://api.mclo.gs/1/log");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(data.getBytes());
        }

        try (InputStream is = conn.getInputStream()) {
            String response = new String(is.readAllBytes());
            if (response.contains("\"success\":true")) {
                int urlIndex = response.indexOf("\"url\":\"");
                if (urlIndex != -1) {
                    int start = urlIndex + 7;
                    int end = response.indexOf("\"", start);
                    if (end != -1) {
                        return response.substring(start, end);
                    }
                }
                throw new IOException("Upload succeeded but URL not found in response.");
            } else {
                int errorIndex = response.indexOf("\"error\":\"");
                if (errorIndex != -1) {
                    int start = errorIndex + 9;
                    int end = response.indexOf("\"", start);
                    String errorMsg = (end != -1) ? response.substring(start, end) : "Unknown error";
                    throw new IOException("Upload failed: " + errorMsg);
                }
                throw new IOException("Upload failed: Unknown error");
            }
        }
    }
}
