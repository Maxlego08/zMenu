package fr.maxlego08.menu.action;

import fr.maxlego08.menu.api.action.data.ActionPlayerData;
import fr.maxlego08.menu.api.action.data.ActionPlayerDataType;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.players.ZData;

public class ZActionPlayerData implements ActionPlayerData {

	private final String key;
	private final ActionPlayerDataType type;
	private final Object value;
	private final long seconds;

	/**
	 * @param key
	 * @param type
	 * @param value
	 * @param seconds
	 */
	public ZActionPlayerData(String key, ActionPlayerDataType type, Object value, long seconds) {
		super();
		this.key = key;
		this.type = type;
		this.value = value;
		this.seconds = seconds;
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public ActionPlayerDataType getType() {
		return this.type;
	}

	@Override
	public Object getValue() {
		return this.value;
	}

	@Override
	public long getSeconds() {
		return this.seconds;
	}

	@Override
	public Data toData() {
		long expiredAt = this.seconds == 0 ? 0 : System.currentTimeMillis() + (1000 * this.seconds);
		return new ZData(this.key, this.value, expiredAt);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ZActionPlayerData [key=" + key + ", type=" + type + ", value=" + value + ", seconds=" + seconds + "]";
	}
	
	

}
