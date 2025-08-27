package ru.tadakacy.playerx.Utils;

import org.bukkit.configuration.file.YamlConfiguration;
import ru.tadakacy.playerx.ModuleManager;
import ru.tadakacy.playerx.PlayerX;

import java.io.File;
import java.io.IOException;

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

    public void saveModuleConfig(ModuleManager module, File modulesDir) {
        File moduleConfigFile = new File(modulesDir, module.getModuleName() + ".yml");
        if (!moduleConfigFile.exists()) {
            try {
                if (moduleConfigFile.createNewFile()) {
                    // можно записать туда настройки по умолчанию
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(moduleConfigFile);
        yaml.set("moduleName", module.getModuleName());
        yaml.set("version", module.getVersion());
        try {
            yaml.save(moduleConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
