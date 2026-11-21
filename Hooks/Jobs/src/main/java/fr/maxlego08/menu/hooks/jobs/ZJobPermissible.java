package fr.maxlego08.menu.hooks.jobs;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.permissible.JobPermissible;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.robie.minimessageflow.TextTemplate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

public class ZJobPermissible extends JobPermissible {

    private final TextTemplate jobName;

    public ZJobPermissible(List<Action> denyActions, List<Action> successActions, String jobName) {
        super(denyActions, successActions);
        this.jobName = TextService.text(jobName);
    }

    @Override
    public boolean hasPermission(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        Job job = Jobs.getJob(TextService.render(this.jobName, placeholders, player));
        if (job == null) {
            Logger.info("Job " + TextService.source(this.jobName) + " was not found !", Logger.LogType.ERROR);
            return true;
        }
        return Jobs.getPlayerManager().getJobsPlayer(player.getUniqueId()).isInJob(job);
    }

    @Override
    protected void serializeProperties(@NonNull Map<String, Object> map) {
        map.put("type", "job");
        if (this.jobName != null) map.put("job", TextService.source(this.jobName));
    }

    @Override
    public boolean isValid() {
        return this.jobName != null;
    }

    @Override
    public @Nullable String getJobName() {
        return TextService.source(this.jobName);
    }
}
