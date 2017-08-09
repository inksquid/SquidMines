package me.inksquid.squidmines.handlers;

import lombok.Getter;
import me.inksquid.squidmines.SquidMines;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigHandler {

    @Getter private FileConfiguration config;
    @Getter private File file;

    public ConfigHandler(File folder, String name) {
        this.file = new File(folder, name);

        reloadConfig();
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (Exception e) {
            SquidMines.getInstance().getLogger().severe(String.format("Couldn't save '%s', because: '%s'", file.getName(), e.getMessage()));
        }
    }

    public <T> void set(String path, T value, boolean save) {
        config.set(path, value);

        if (save) saveConfig();
    }

    public <T> void set(String path, T value) {
        set(path, value, false);
    }
}
