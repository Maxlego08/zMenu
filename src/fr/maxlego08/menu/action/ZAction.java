package fr.maxlego08.menu.action;

import java.util.List;

import org.bukkit.event.inventory.ClickType;

import fr.maxlego08.menu.api.action.Action;
import fr.maxlego08.menu.api.action.permissible.Permissible;

public class ZAction implements Action {

	private final List<ClickType> clickTypes;
	private final Action allowAction;
	private final Action denyAction;
	private final List<Permissible> permissibles;

	/**
	 * @param clickTypes
	 * @param allowAction
	 * @param denyAction
	 * @param permissibles
	 */
	public ZAction(List<ClickType> clickTypes, Action allowAction, Action denyAction, List<Permissible> permissibles) {
		super();
		this.clickTypes = clickTypes;
		this.allowAction = allowAction;
		this.denyAction = denyAction;
		this.permissibles = permissibles;
	}

	@Override
	public List<ClickType> getClickType() {
		return this.clickTypes;
	}

	@Override
	public Action getAllowAction() {
		return this.allowAction;
	}

	@Override
	public Action getDenyAction() {
		return this.denyAction;
	}

	@Override
	public List<Permissible> getPermissibles() {
		return this.permissibles;
	}

}
