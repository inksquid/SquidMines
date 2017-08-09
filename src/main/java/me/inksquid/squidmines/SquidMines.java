package me.inksquid.squidmines;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import lombok.Getter;
import me.inksquid.squidmines.handlers.ConfigHandler;
import me.inksquid.squidmines.util.MineUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;

public final class SquidMines extends JavaPlugin implements Listener {

    @Getter private static SquidMines instance;
    @Getter private static WorldGuardPlugin worldGuard;
    @Getter private static ConfigHandler mines;
    @Getter private static int time = 0;

    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults(true);
        saveConfig();

        worldGuard = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");
        getServer().getPluginManager().registerEvents(this, this);
    }
    @Override
    public void reloadConfig() {
        super.reloadConfig();
        mines = new ConfigHandler(getDataFolder(), "mines.yml");
        Config.loadConfig(getConfig());

        time = Config.getResetInterval();
        FileConfiguration plugin = YamlConfiguration.loadConfiguration(new InputStreamReader(getResource("plugin.yml")));

        for (String command : plugin.getConfigurationSection("commands").getKeys(false)) {
            getCommand(command).setPermissionMessage(Config.getNoPermMessage());
        }

        getServer().getScheduler().cancelTasks(this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                resetCheck();
            }
        }, 0L, 20L);
    }

    public void resetCheck() {
        if (Config.getMines().size() > 0) {
            time--;

            if (time <= 0) {
                time = Config.getResetInterval();

                getServer().broadcastMessage(Config.getResetStartMessage());

                for (Mine mine : Config.getMines().values()) {
                    mine.reset();
                }

                getServer().broadcastMessage(Config.getResetEndMessage());
            } else if ((time <= 3 && time >= 1) || time == 10 || time == 60 || time == 300) {
                getServer().broadcastMessage(Config.getResetSoonMessage().replace("<time>", MineUtil.formatTime(time)));
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length == 0) {
            MineUtil.sendMessages(sender, Config.getHelpMessage());
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("squidmines.reload")) {
                reloadConfig();
                sender.sendMessage(Config.getReloadMessage());
            } else {
                sender.sendMessage(Config.getNoPermMessage());
            }
        } else if (args[0].equalsIgnoreCase("reset")) {
            if (sender.hasPermission("squidmines.reset")) {
                if (args.length < 2) {
                    sender.sendMessage(Config.getResetHelpMessage());
                } else {
                    String name = args[1];
                    Mine mine = Config.getMines().get(name);

                    if (mine != null) {
                        mine.reset();
                        sender.sendMessage(Config.getResetMessage().replace("<mine>", name));
                    } else if (name.equalsIgnoreCase("all")) {
                        for (Mine mine2 : Config.getMines().values()) {
                            mine2.reset();
                        }

                        sender.sendMessage(Config.getResetAllMessage());
                    } else {
                        sender.sendMessage(Config.getNoMineMessage().replace("<mine>", name));
                    }
                }
            } else {
                sender.sendMessage(Config.getNoPermMessage());
            }
        } else if (args[0].equalsIgnoreCase("teleport")) {
            if (sender.hasPermission("squidmines.teleport")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;

                    if (args.length < 2) {
                        player.sendMessage(Config.getTeleportHelpMessage());
                    } else {
                        String name = args[1];
                        Mine mine = Config.getMines().get(name);

                        if (mine != null) {
                            player.teleport(mine.getLocation());
                            player.sendMessage(Config.getTeleportMessage().replace("<mine>", name));
                        } else {
                            player.sendMessage(Config.getNoMineMessage().replace("<mine>", name));
                        }
                    }
                } else {
                    sender.sendMessage(Config.getConsoleMessage());
                }
            } else {
                sender.sendMessage(Config.getNoPermMessage());
            }
        } else if (args[0].equalsIgnoreCase("setspawn")) {
            if (sender.hasPermission("squidmines.setspawn")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (args.length < 2) {
                        return false;
                    } else {
                        String name = args[1];
                        Mine mine = Config.getMines().get(name);
                        if (mine != null) {
                            mines.set(name + ".location", MineUtil.serializeLocation(player.getLocation()));
                            mines.saveConfig();
                            reloadConfig();
                            player.sendMessage(name + " spawn set.");
                        } else {
                            player.sendMessage(Config.getNoMineMessage().replace("<mine>", name));
                        }
                    }
                } else {
                    sender.sendMessage(Config.getConsoleMessage());
                }
            }
        } else {
            MineUtil.sendMessages(sender, Config.getHelpMessage());
        }

        return true;
    }
}
