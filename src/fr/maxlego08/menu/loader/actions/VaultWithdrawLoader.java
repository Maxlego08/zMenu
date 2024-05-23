package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.VaultWithdrawAction;

import java.io.File;

public class VaultWithdrawLoader implements ActionLoader {

    @Override
    public String getKey() {
        return "withdraw,money remove";
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        double amount = accessor.getDouble("amount");
        return new VaultWithdrawAction(amount);
    }
}
