package me.inksquid.squidmines;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.inksquid.squidmines.util.MineUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

@AllArgsConstructor
public class Mine {

    @Getter private String region;
    @Getter private Location location;
    @Getter private RandomCollection<BlockMaterial> materials;

    public boolean isInside(Location location, String world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return location != null && location.getWorld().getName().equals(world) && location.getBlockX() >= minX && location.getBlockX() <= maxX && location.getBlockY() >= minY && location.getBlockY() <= maxY && location.getBlockZ() >= minZ && location.getBlockZ() <= maxZ;
    }

    public void clean(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        Set<Chunk> chunks = MineUtil.getAllChunks(world, minX, minZ, maxX, maxZ);

        for (Chunk chunk : chunks) {
            for (Entity entity : chunk.getEntities()) {
                if (entity instanceof Item || entity instanceof Player) {
                    if (isInside(entity.getLocation(), world.getName(), minX, Math.max(0, minY - 2), minZ, maxX, Math.min(255, maxY + 2), maxZ)) {
                        if (entity instanceof Item) {
                            entity.remove();
                        } else {
                            ((Player) entity).teleport(location);
                        }
                    }
                }
            }
        }
    }

    public void reset() {
        World world = location.getWorld();
        ProtectedRegion protection = SquidMines.getWorldGuard().getRegionManager(world).getRegion(region);

        if (protection != null) {
            BlockVector minimum = protection.getMinimumPoint();
            BlockVector maximum = protection.getMaximumPoint();
            int minX = minimum.getBlockX();
            int minY = minimum.getBlockY();
            int minZ = minimum.getBlockZ();
            int maxX = maximum.getBlockX();
            int maxY = maximum.getBlockY();
            int maxZ = maximum.getBlockZ();

            clean(world, minX, minY, minZ, maxX, maxY, maxZ);

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        BlockMaterial material = materials.next();

                        MineUtil.changeBlock(world, x, y, z, material.getMaterial().getId(), material.getDamage());
                    }
                }
            }
        } else {
            SquidMines.getInstance().getLogger().warning(String.format("The region '%s' doesn't exist!", region));
        }
    }
}
