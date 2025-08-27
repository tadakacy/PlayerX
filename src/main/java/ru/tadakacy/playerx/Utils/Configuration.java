package ru.tadakacy.playerx.Utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.tadakacy.playerx.ModuleManager;
import ru.tadakacy.playerx.PlayerX;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private Map<String, FileConfiguration> moduleConfigs = new HashMap<>();
    private final PlayerX playerX;
    private File configFile;
    private FileConfiguration moduleConfig;
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

    public FileConfiguration getModuleConfig(String moduleName) {
        return moduleConfigs.get(moduleName.toLowerCase());
    }

    public void loadModuleConfiguration(ModuleManager module) {
        String nameFile = (module.getModuleName() + ".yml").toLowerCase();
        File mConfig = new File(playerX.getConfigFolder(), nameFile);
        if(!mConfig.exists()) {
            saveResourceToFile("cfg/" + nameFile, mConfig);
        }
        moduleConfig = YamlConfiguration.loadConfiguration(mConfig);
        moduleConfigs.put(module.getModuleName().toLowerCase(), moduleConfig);
    }

    public void saveResourceToFile(String resourcePath, File outputFile) {
        if (!outputFile.exists()) {
            try (InputStream in = playerX.getResource(resourcePath)) {
                if (in != null) {
                    outputFile.getParentFile().mkdirs();
                    try (OutputStream out = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = in.read(buffer)) > 0) {
                            out.write(buffer, 0, len);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
