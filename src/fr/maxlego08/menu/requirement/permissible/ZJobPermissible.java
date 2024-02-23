package fr.maxlego08.menu.requirement.permissible;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.permissible.JobPermissible;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.requirement.ZPermissible;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;

import java.util.List;

public class ZJobPermissible extends ZPermissible implements JobPermissible {

    private final String jobName;

    public ZJobPermissible(List<Action> denyActions, List<Action> successActions, String jobName) {
        super(denyActions, successActions);
        this.jobName = jobName;
    }

    @Override
    public boolean hasPermission(Player player, Button button, InventoryDefault inventory) {
        Job job = Jobs.getJob(this.jobName);
        if (job == null) {
            Logger.info("Job " + this.jobName +" was not found !", Logger.LogType.ERROR);
            return true;
        }
        return Jobs.getPlayerManager().getJobsPlayer(player.getUniqueId()).isInJob(job);
    }

    @Override
    public boolean isValid() {
        return this.jobName != null;
    }

    @Override
    public String getJobName() {
        return this.jobName;
    }
}
