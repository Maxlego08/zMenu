package fr.maxlego08.menu.itemstack;

import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.itemstack.LoreSimilar;
import fr.maxlego08.menu.itemstack.MaterialSimilar;
import fr.maxlego08.menu.itemstack.ModelIdSimilar;
import fr.maxlego08.menu.itemstack.NameSimilar;

/**
 * Represents a type of verification for comparing two item stacks
 * This enumeration is used to specify the criteria for comparing properties of item stacks.
 */
public enum ItemStackVerification {
	MATERIAL(new MaterialSimilar()),
	NAME(new NameSimilar()),
	LORE(new LoreSimilar()),
	MODEL_ID(new ModelIdSimilar());

	private final ItemStackSimilar itemStackSimilar;

	public ItemStackSimilar getItemStackSimilar() {
		return itemStackSimilar;
	}

	ItemStackVerification(ItemStackSimilar itemStackSimilar) {
		this.itemStackSimilar = itemStackSimilar;
	}
}

