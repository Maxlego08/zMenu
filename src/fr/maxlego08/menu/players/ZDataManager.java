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

import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.zcore.utils.storage.Persist;

public class ZDataManager implements DataManager {

	private static Map<UUID, ZPlayerData> players = new HashMap<>();

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

	@SuppressWarnings("deprecation")
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

}
