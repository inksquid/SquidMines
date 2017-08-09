package me.inksquid.squidmines.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.*;
import org.bukkit.command.CommandSender;


public class MineUtil {


    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static List<String> color(List<String> list) {
        for (int i = 0, s = list.size(); i < s; i++) {
            list.set(i, color(list.get(i)));
        }

        return list;
    }

    public static String formatTime(int time) {
        if (time > 60) {
            int minutes = time / 60;

            return String.format("%s %s", minutes, "minute" + (minutes == 1 ? "" : "s"));
        } else {
            return String.format("%s %s", time, "second" + (time == 1 ? "" : "s"));
        }
    }

    public static void changeBlock(World world, int x, int y, int z, int id, int data) {
        world.getBlockAt(x, y, z).setTypeIdAndData(id, (byte) data, true);
    }

    public static Set<Chunk> getAllChunks(World world, int minX, int minZ, int maxX, int maxZ) {
        Set<Chunk> chunks = new HashSet<Chunk>();
        minX = minX >> 4;
        minZ = minZ >> 4;
        maxX = maxX >> 4;
        maxZ = maxZ >> 4;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                chunks.add(world.getChunkAt(x, z));
            }
        }

        return chunks;
    }

    public static String serializeLocation(Location location) {
        return location.getWorld().getName() + ", " + location.getX() + ", " + location.getY() + ", " + location.getZ() + ", " + location.getYaw() + ", " + location.getPitch();
    }

    public static Location deserializeLocation(String string) {
        if (string != null) {
            String[] split = string.split(", ");

            if (split.length > 5) {
                World world = Bukkit.getWorld(split[0]);

                if (world != null) {
                    try {
                        return new Location(world, Double.valueOf(split[1]), Double.valueOf(split[2]), Double.valueOf(split[3]), Float.valueOf(split[4]), Float.valueOf(split[5]));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }

    public static Material getMaterial(String string) {
        if (string != null) {
            return Material.getMaterial(string.toUpperCase().replace(" ", "_"));
        } else {
            return null;
        }
    }

    public static void sendMessages(CommandSender sender, List<String> messages) {
        for (String message : messages) {
            sender.sendMessage(message);
        }
    }
}
