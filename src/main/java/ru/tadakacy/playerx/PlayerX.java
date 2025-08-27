package ru.tadakacy.playerx;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tadakacy.playerx.Modules.CommandRegistry;
import ru.tadakacy.playerx.Commands.PlayerXCommand;
import ru.tadakacy.playerx.Commands.PlayerXTabCompleter;
import ru.tadakacy.playerx.Modules.ModuleService;
import ru.tadakacy.playerx.Utils.Configuration;
import ru.tadakacy.playerx.Utils.Messages;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static ru.tadakacy.playerx.Utils.ColorText.color;

public final class PlayerX extends JavaPlugin {

    ///  List загруженных модулей
    private List<ModuleManager> loadedModules = new ArrayList<>();

    ///  всякие штучки дрючки :)
    private Messages msg;
    private Configuration cfg;
    private ModuleManager moduleManager;
    private ModuleService moduleService;
    private CommandRegistry commandRegistry;

    @Override
    public void onEnable() {
        /// важно!!!
        msg = new Messages(this);
        cfg = new Configuration(this);
        commandRegistry = new CommandRegistry(this);
        moduleService = new ModuleService(this, commandRegistry);

        File modulesDir = getModulesFolder();
        moduleService.loadModulesFromDirectory(modulesDir);
        loadedModules = moduleService.getLoadedModules();

        registerMainCommands();

        getLogger().info(color("&a ______   __       ________   __  __   ______   ______    __     __     "));
        getLogger().info(color("&a/_____/\\ /_/\\     /_______/\\ /_/\\/_/\\ /_____/\\ /_____/\\  /__/\\ /__/\\    "));
        getLogger().info(color("&a\\:::_ \\ \\\\:\\ \\    \\::: _  \\ \\\\ \\ \\ \\ \\\\::::_\\/_\\:::_ \\ \\ \\ \\::\\\\:.\\ \\   "));
        getLogger().info(color("&a \\:(_) \\ \\\\:\\ \\    \\::(_)  \\ \\\\:\\_\\ \\ \\\\:\\/___/\\\\:(_) ) )_\\_\\::_\\:_\\/   "));
        getLogger().info(color("&a  \\: ___\\/ \\:\\ \\____\\:: __  \\ \\\\::::_\\/ \\::___\\/_\\: __ `\\ \\ _\\/__\\_\\_/\\ "));
        getLogger().info(color("&a   \\ \\ \\    \\:\\/___/\\\\:.\\ \\  \\ \\ \\::\\ \\  \\:\\____/\\\\ \\ `\\ \\ \\\\ \\ \\ \\::\\ \\"));
        getLogger().info(color("&a    \\_\\/     \\_____\\/ \\__\\/\\__\\/  \\__\\/   \\_____\\/ \\_\\/ \\_\\/ \\_\\/  \\__\\/"));
        getLogger().info(color(""));
        getLogger().info(color("&fGitHub: github.com/tadakacy/PlayerX/"));
        getLogger().info(color(""));
        getLogger().info(msg.getMessage("started", "count", String.valueOf(loadedModules.size())));

        for (ModuleManager moduleManager : loadedModules) {
            moduleManager.onEnable(this);
        }
    }

    @Override
    public void onDisable() {
        loadedModules.clear();
    }

    private void registerMainCommands() {
        String cmd = "playerx";
        getCommand(cmd).setExecutor(new PlayerXCommand(this));
        getCommand(cmd).setTabCompleter(new PlayerXTabCompleter(this));
    }

    public Configuration getConfiguration() {return cfg;}
    public ModuleService getModuleService() {return moduleService;}

    public List<ModuleManager> getLoadedModules() {
        return loadedModules;
    }

    public File getModulesFolder() {
        File modulesFolder = new File(getDataFolder(), "modules");
        if (!modulesFolder.exists()) {
            modulesFolder.mkdirs();
        }
        return modulesFolder;
    }

    public File getConfigFolder() {
        File configFolder = new File(getModulesFolder(), "cfg");
        if(!configFolder.exists()) {
            configFolder.mkdirs();
        }
        return configFolder;
    }
}
