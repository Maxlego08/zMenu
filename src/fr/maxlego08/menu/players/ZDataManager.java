package fr.maxlego08.menu.players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.placeholder.LocalPlaceholder;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.utils.builder.TimerBuilder;
import fr.maxlego08.menu.zcore.utils.storage.Persist;

public class ZDataManager implements DataManager {

	private final transient MenuPlugin plugin;
	private transient long lastSave;
	private static Map<UUID, ZPlayerData> players = new HashMap<>();

	/**
	 * @param plugin
	 */
	public ZDataManager(MenuPlugin plugin) {
		super();
		this.plugin = plugin;
	}

	@Override
	public void save(Persist persist) {
		persist.save(this, "players");
	}

	@Override
	public void load(Persist persist) {
		persist.loadOrSaveDefault(this, ZDataManager.class, "players");
	}

	@Override
	public Optional<PlayerData> getPlayer(UUID uniqueId) {
		return Optional.ofNullable(players.getOrDefault(uniqueId, null));
	}

	@Override
	public PlayerData getOrCreate(UUID uniqueId) {

		Optional<PlayerData> optional = this.getPlayer(uniqueId);
		if (optional.isPresent()) {
			return optional.get();
		}

		ZPlayerData data = new ZPlayerData(uniqueId);
		players.put(uniqueId, data);

		this.autoSave();

		return data;
	}

	@Override
	public void addData(UUID uniqueId, Data data) {

		PlayerData playerData = this.getOrCreate(uniqueId);
		playerData.addData(data);

	}

	@Override
	public Optional<Data> getData(UUID uniqueId, String key) {

		Optional<PlayerData> optional = this.getPlayer(uniqueId);
		if (!optional.isPresent()) {
			return Optional.empty();
		}

		PlayerData playerData = optional.get();
		return playerData.getData(key);
	}

	public List<String> getKeys(String[] args) {
		if (args.length != 4) {
			return new ArrayList<String>();
		}
		try {
			String playerName = args[2];
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
			Optional<PlayerData> optional = this.getPlayer(offlinePlayer.getUniqueId());
			if (!optional.isPresent()) {
				return new ArrayList<String>();
			}

			PlayerData playerData = optional.get();
			return playerData.getDatas().stream().map(Data::getKey).collect(Collectors.toList());

		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	@Override
	public void clearAll() {
		players.clear();
		this.save(this.plugin.getPersist());
	}

	@Override
	public void autoSave() {
		if (System.currentTimeMillis() > this.lastSave) {
			Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
				this.save(this.plugin.getPersist());
				this.lastSave = System.currentTimeMillis() + (Config.secondsSavePlayerData * 1000);
			});
		}
	}

	@Override
	public void clearPlayer(UUID uniqueId) {
		players.remove(uniqueId);
		this.autoSave();
	}

	public void registerPlaceholder(LocalPlaceholder localPlaceholder) {

		localPlaceholder.register("player_key_exist_", (player, key) -> {

			Optional<PlayerData> optional = this.getPlayer(player.getUniqueId());
			if (!optional.isPresent()) {
				return "false";
			}

			PlayerData playerData = optional.get();
			return String.valueOf(playerData.containsKey(key));

		});

		localPlaceholder.register("player_value_", (player, key) -> {

			Optional<PlayerData> optional = this.getPlayer(player.getUniqueId());
			if (!optional.isPresent()) {
				return "Key '" + key + "' doesn't exist for this player";
			}

			PlayerData playerData = optional.get();
			if (!playerData.containsKey(key)) {
				return "Key '" + key + "' doesn't exist for this player";
			}

			Data data = playerData.getData(key).get();
			return data.getValue().toString();
		});

		localPlaceholder.register("player_expire_format_", (player, key) -> {

			Optional<PlayerData> optional = this.getPlayer(player.getUniqueId());
			if (!optional.isPresent()) {
				return "Key '" + key + "' doesn't exist for this player";
			}

			PlayerData playerData = optional.get();
			if (!playerData.containsKey(key)) {
				return "Key '" + key + "' doesn't exist for this player";
			}

			Data data = playerData.getData(key).get();

			if (data.getExpiredAt() <= 0) {
				return Message.PLACEHOLDER_NEVER.getMessage();
			}

			long seconds = Math.abs(System.currentTimeMillis() - data.getExpiredAt()) / 1000;
			return TimerBuilder.getStringTime(seconds);
		});

		localPlaceholder.register("player_expire_", (player, key) -> {

			Optional<PlayerData> optional = this.getPlayer(player.getUniqueId());
			if (!optional.isPresent()) {
				return "Key '" + key + "' doesn't exist for this player";
			}

			PlayerData playerData = optional.get();
			if (!playerData.containsKey(key)) {
				return "Key '" + key + "' doesn't exist for this player";
			}

			Data data = playerData.getData(key).get();
			return String.valueOf(data.getExpiredAt());
		});

	}

}
