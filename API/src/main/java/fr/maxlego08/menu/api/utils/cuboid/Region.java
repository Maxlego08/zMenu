package fr.maxlego08.menu.api.utils.cuboid;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;


/**
 * Represents a group of cuboids as a region, useful for area grouping, protection, or bulk operations.
 */
public class Region {
    private final List<Cuboid> cuboids;

    public Region() {
        this.cuboids = new ArrayList<>();
    }

    public Region(List<Cuboid> cuboids) {
        this.cuboids = cuboids;
    }

    public Region(Cuboid... cuboids) {
        this.cuboids = Arrays.asList(cuboids);
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

    public List<Cuboid> getCuboids() {
        return this.cuboids;
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
