package fr.maxlego08.menu.players;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public Optional<Data> getData(UUID uniqueId, String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
