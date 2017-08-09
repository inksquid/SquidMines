package me.inksquid.squidmines;

import lombok.Getter;
import me.inksquid.squidmines.util.MineUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    @Getter private static ConfigurationSection general;
    @Getter private static ConfigurationSection settings;
    @Getter private static String reloadMessage;
    @Getter private static String noPermMessage;
    @Getter private static String resetSoonMessage;
    @Getter private static String resetStartMessage;
    @Getter private static String resetEndMessage;
    @Getter private static String resetHelpMessage;
    @Getter private static String noMineMessage;
    @Getter private static String resetMessage;
    @Getter private static String resetAllMessage;
    @Getter private static String teleportHelpMessage;
    @Getter private static String teleportMessage;
    @Getter private static String consoleMessage;
    @Getter private static int resetInterval;
    @Getter private static List<String> helpMessage;
    @Getter private static Map<String, Mine> mines = new HashMap<String, Mine>();

    public static void loadConfig(FileConfiguration config) {
        general = config.getConfigurationSection("general");
        settings = config.getConfigurationSection("settings");
        reloadMessage = MineUtil.color(general.getString("reloadMessage"));
        noPermMessage = MineUtil.color(general.getString("noPermMessage"));
        resetSoonMessage = MineUtil.color(general.getString("resetSoonMessage"));
        resetStartMessage = MineUtil.color(general.getString("resetStartMessage"));
        resetEndMessage = MineUtil.color(general.getString("resetEndMessage"));
        resetHelpMessage = MineUtil.color(general.getString("resetHelpMessage"));
        noMineMessage = MineUtil.color(general.getString("noMineMessage"));
        resetMessage = MineUtil.color(general.getString("resetMessage"));
        resetAllMessage = MineUtil.color(general.getString("resetAllMessage"));
        teleportHelpMessage = MineUtil.color(general.getString("teleportHelpMessage"));
        teleportMessage = MineUtil.color(general.getString("teleportMessage"));
        consoleMessage = general.getString("consoleMessage");
        resetInterval = settings.getInt("resetInterval") * 60;
        helpMessage = MineUtil.color(general.getStringList("helpMessage"));

        loadMines(SquidMines.getMines().getConfig());
    }

    public static void loadMines(FileConfiguration config) {
        mines.clear();

        for (String mine : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(mine);

            if (section != null) {
                Location location = MineUtil.deserializeLocation(section.getString("location"));

                if (location != null) {
                    ConfigurationSection materialSection = section.getConfigurationSection("materials");

                    if (materialSection != null) {
                        RandomCollection<BlockMaterial> materials = new RandomCollection<BlockMaterial>();

                        for (String key : materialSection.getKeys(false)) {
                            ConfigurationSection keySection = materialSection.getConfigurationSection(key);

                            if (keySection != null) {
                                Material material = MineUtil.getMaterial(keySection.getString("material"));

                                if (material != null) {
                                    double chance = keySection.getDouble("chance");

                                    if (chance > 0.0) {
                                        materials.add(chance, new BlockMaterial(material, (short) keySection.getInt("damage")));
                                    }
                                }
                            }
                        }

                        if (!materials.isEmpty()) {
                            mines.put(mine, new Mine(section.getString("region"), location, materials));
                        }
                    }
                }
            }
        }
    }
}
