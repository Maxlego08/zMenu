package fr.maxlego08.menu.api.attribute;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class Attribute implements IAttribute {

	public enum Type {
		/**
		 * Maximum health of an Entity.
		 */
		GENERIC_MAX_HEALTH("generic.max_health"),
		/**
		 * Range at which an Entity will follow others.
		 */
		GENERIC_FOLLOW_RANGE("generic.follow_range"),
		/**
		 * Resistance of an Entity to knockback.
		 */
		GENERIC_KNOCKBACK_RESISTANCE("generic.knockback_resistance"),
		/**
		 * Movement speed of an Entity.
		 */
		GENERIC_MOVEMENT_SPEED("generic.movement_speed"),
		/**
		 * Flying speed of an Entity.
		 */
		GENERIC_FLYING_SPEED("generic.flying_speed"),
		/**
		 * Attack damage of an Entity.
		 */
		GENERIC_ATTACK_DAMAGE("generic.attack_damage"),
		/**
		 * Attack knockback of an Entity.
		 */
		GENERIC_ATTACK_KNOCKBACK("generic.attack_knockback"),
		/**
		 * Attack speed of an Entity.
		 */
		GENERIC_ATTACK_SPEED("generic.attack_speed"),
		/**
		 * Armor bonus of an Entity.
		 */
		GENERIC_ARMOR("generic.armor"),
		/**
		 * Armor durability bonus of an Entity.
		 */
		GENERIC_ARMOR_TOUGHNESS("generic.armor_toughness"),
		/**
		 * Luck bonus of an Entity.
		 */
		GENERIC_LUCK("generic.luck"),
		/**
		 * Maximum absorption of an Entity.
		 */
		GENERIC_MAX_ABSORPTION("generic.max_absorption"),
		/**
		 * Strength with which a horse will jump.
		 */
		HORSE_JUMP_STRENGTH("horse.jump_strength"),
		/**
		 * Chance of a zombie to spawn reinforcements.
		 */
		ZOMBIE_SPAWN_REINFORCEMENTS("zombie.spawn_reinforcements");

		private final String key;

		Type(String key) {
			this.key = key;
		}

		@NotNull
		public String getKey() {
			return key;
		}
	}

	@NotNull
	public static Attribute.Builder builder(Attribute.Type type, double amount) {
		return new Attribute.Builder(type, amount);
	}

	public static final class Builder {
		Attribute.Type type;
		double amount;
		UUID uuid = java.util.UUID.randomUUID();
		String name = "zmenu:modifier";
		EquipmentSlot slot = null;

		Builder(Attribute.Type type, double amount) {
			this.amount = amount;
			this.type = type;
		}

		@NotNull
		public Attribute build() {
			return new Attribute(type, uuid, name, amount, slot);
		}

		public Attribute.Builder withUUID(UUID uuid) {
			this.uuid = uuid;
			return this;
		}

		public Attribute.Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Attribute.Builder withSlot(EquipmentSlot slot) {
			this.slot = slot;
			return this;
		}
	}

	private static final String TYPE = "attribute";
	private static final String UUID = "uuid";
	private static final String NAME = "name";
	private static final String AMOUNT = "amount";
	private static final String SLOT = "slot";

	private final Attribute.Type type;
	private final UUID uuid;
	private final String name;
	private final double amount;
	private final EquipmentSlot slot;

	Attribute(@NotNull Attribute.Type type, UUID uuid, String name, double amount, EquipmentSlot slot) {
		this.type = type;
		this.uuid = uuid;
		this.name = name;
		this.amount = amount;
		this.slot = slot;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public java.util.UUID getUuid() {
		return uuid;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public double getAmount() {
		return amount;
	}

	@Override
	public EquipmentSlot getSlot() {
		return slot;
	}

	@NotNull
	public static Attribute deserialize(@NotNull Map<String, Object> map) {
		if (!map.containsKey(TYPE) || map.get(TYPE) == null) {
			throw new IllegalArgumentException(String.format("The '%s' field is missing or null", TYPE));
		}
		Attribute.Type type = Attribute.Type.valueOf((String) map.get(TYPE));

		if (!map.containsKey(AMOUNT) || map.get(AMOUNT) == null) {
			throw new IllegalArgumentException(String.format("The '%s' field is missing or null", AMOUNT));
		}
		double amount = NumberConversions.toDouble(map.get(AMOUNT));

		Attribute.Builder builder = builder(type, amount);

		if (map.containsKey(NAME) && map.get(NAME) != null) {
			builder.withName((String) map.get(NAME));
		}
		if (map.containsKey(SLOT) && map.get(SLOT) != null) {
			builder.withSlot(EquipmentSlot.valueOf((String) map.get(SLOT)));
		}
		if (map.containsKey(UUID) && map.get(UUID) != null) {
			builder.withUUID(java.util.UUID.fromString((String) map.get(UUID)));
		}

		return builder.build();
	}
}