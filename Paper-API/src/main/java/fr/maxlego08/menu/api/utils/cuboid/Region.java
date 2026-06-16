package fr.maxlego08.menu.api.utils.cuboid;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;


/**
 * Represents a group of cuboids as a region, useful for area grouping, protection, or bulk operations.
 */
public record Region(List<Cuboid> cuboids) {
    public Region() {
        this(new ArrayList<>());
    }

    public Region(Cuboid... cuboids) {
        this(Arrays.asList(cuboids));
    }

    public void addCube(Cuboid cube) {
        this.cuboids.add(cube);
    }

    public boolean contains(Block block) {
        for (Cuboid cuboid : this.cuboids) {
            if (cuboid.contains(block)) {
                return true;
            }
        }
        return false;
    }

    public Collection<Player> getPlayers() {
        Collection<Player> players = new LinkedHashSet<>();
        for (Cuboid cuboid : this.cuboids) {
            players.addAll(cuboid.getPlayers());
        }
        return new ArrayList<>(players);
    }

    public Collection<LivingEntity> getEntities() {
        Collection<LivingEntity> entities = new LinkedHashSet<>();
        for (Cuboid cuboid : this.cuboids) {
            entities.addAll(cuboid.getEntities());
        }
        return new ArrayList<>(entities);
    }

    public boolean contains(Location location) {
        for (Cuboid cuboid : this.cuboids) {
            if (cuboid.contains(location)) {
                return true;
            }
        }
        return false;
    }
}
