package fr.maxlego08.menu.website;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.zcore.utils.ElapsedTime;
import fr.maxlego08.menu.zcore.utils.ZUtils;

public class DownloadFile extends ZUtils {

	public void download(Plugin plugin, CommandSender sender, String link) {

		String fileName = generateRandomString(5);
		File folder = this.getFolder(plugin);
		File output = new File(folder, fileName + ".zip");

		ElapsedTime downloadTime = new ElapsedTime("download");
		downloadTime.start();

		try {

			URL url = new URL(link);

			BufferedInputStream in = new BufferedInputStream(url.openStream());
			FileOutputStream fileOutputStream = new FileOutputStream(output);

			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				fileOutputStream.write(dataBuffer, 0, bytesRead);
			}
			fileOutputStream.close();
			downloadTime.endDisplay(true);

			sender.sendMessage("§fTéléchargement terminé. Unzip en cours.");

			ElapsedTime unzipFile = new ElapsedTime("unzip");
			unzipFile.start();

			File folderOutput = new File(folder, fileName);
			folderOutput.mkdir();
			this.unzipFolder(output.toPath(), folderOutput.toPath());

			unzipFile.endDisplay(true);

			sender.sendMessage("§fUnzip terminé.");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public File getFolder(Plugin plugin) {
		File folder = new File(plugin.getDataFolder(), "download");
		if (!folder.exists()) {
			folder.mkdir();
		}
		return folder;
	}

	public void unzipFolder(Path source, Path target) throws IOException {

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {

			ZipEntry zipEntry = zis.getNextEntry();

			while (zipEntry != null) {

				boolean isDirectory = zipEntry.getName().endsWith(File.separator) || zipEntry.getName().endsWith("/");
				Path newPath = zipSlipProtect(zipEntry, target);
				
				if (isDirectory) {
					Files.createDirectories(newPath);
				} else {

					if (newPath.getParent() != null) {
						if (Files.notExists(newPath.getParent())) {
							System.out.println("ICI!");
							Files.createDirectories(newPath.getParent());
						}
					}
					Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
				}

				zipEntry = zis.getNextEntry();

			}
			zis.closeEntry();
		}

	}

	private Path zipSlipProtect(ZipEntry zipEntry, Path targetDir) throws IOException {

		Path targetDirResolved = targetDir.resolve(zipEntry.getName());
		Path normalizePath = targetDirResolved.normalize();
		if (!normalizePath.startsWith(targetDir)) {
			throw new IOException("Bad zip entry: " + zipEntry.getName());
		}

		return normalizePath;
	}

}
