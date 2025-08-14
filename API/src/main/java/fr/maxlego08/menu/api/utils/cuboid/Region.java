package fr.maxlego08.menu.api.utils.cuboid;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


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
        cuboids.add(cube);
    }

    public boolean contains(Block block) {
        for (Cuboid cuboid : cuboids) {
            if (cuboid.contains(block)) {
                return true;
            }
        }
        return false;
    }

    public List<Cuboid> getCuboids() {
        return cuboids;
    }

    public Collection<Player> getPlayers() {
        return this.cuboids.stream().map(Cuboid::getPlayers).flatMap(Collection::stream).distinct().collect(Collectors.toList());
    }

    public Collection<LivingEntity> getEntities() {
        return this.cuboids.stream().map(Cuboid::getEntities).flatMap(Collection::stream).distinct().collect(Collectors.toList());
    }

    public boolean contains(Location location) {
        for (Cuboid cuboid : cuboids) {
            if (cuboid.contains(location)) {
                return true;
            }
        }
        return false;
    }
}
