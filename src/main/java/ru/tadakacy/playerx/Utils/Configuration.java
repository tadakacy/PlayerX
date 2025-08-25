package ru.tadakacy.playerx.Utils;

import org.bukkit.configuration.file.YamlConfiguration;
import ru.tadakacy.playerx.PlayerX;

import java.io.File;

public class Configuration {
    private final PlayerX playerX;
    private File configFile;
    private YamlConfiguration cfgConfig;
    private ErrorUtils error;

    public Configuration(PlayerX playerX) {
        this.playerX = playerX;
        loadConfiguration();
    }

    private void loadConfiguration() {
        configFile = new File(playerX.getDataFolder(), "config.yml");
        if(!configFile.exists()) {
            playerX.saveResource("config.yml", false);
        }
        cfgConfig = YamlConfiguration.loadConfiguration(configFile);
    }

    public void reloadConfiguration() {
        if(configFile == null) {
            error.logError("1002");
            playerX.saveResource("config.yml", false);
        }
        cfgConfig = YamlConfiguration.loadConfiguration(configFile);
    }
}
