package fr.maxlego08.menu.hooks.jobs;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.permissible.JobPermissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;

import java.util.List;

public class ZJobPermissible extends Permissible implements JobPermissible {

    private final String jobName;

    public ZJobPermissible(List<Action> denyActions, List<Action> successActions, String jobName) {
        super(denyActions, successActions);
        this.jobName = jobName;
    }

    @Override
    public boolean hasPermission(Player player, Button button, InventoryEngine inventory, Placeholders placeholders) {
        MenuPlugin plugin = inventory.getPlugin();
        Job job = Jobs.getJob(plugin.parse(player, placeholders.parse(this.jobName)));
        if (job == null) {
            plugin.getLogger().severe("Job " + this.jobName + " was not found !");
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
