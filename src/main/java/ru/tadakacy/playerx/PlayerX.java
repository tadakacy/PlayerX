package ru.tadakacy.playerx;

import org.bukkit.plugin.java.JavaPlugin;
import ru.tadakacy.playerx.Commands.PlayerXCommand;
import ru.tadakacy.playerx.Commands.PlayerXTabCompleter;
import ru.tadakacy.playerx.Modules.ModuleLoader;
import ru.tadakacy.playerx.Utils.Configuration;
import ru.tadakacy.playerx.Utils.Messages;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static ru.tadakacy.playerx.Utils.ColorText.color;

public final class PlayerX extends JavaPlugin {

    private List<ModuleManager> loadedModules = new ArrayList<>();
    private Messages msg;
    private Configuration cfg;
    private ModuleManager moduleManager;
    private ModuleLoader moduleLoader;

    @Override
    public void onEnable() {
        msg = new Messages(this);
        cfg = new Configuration(this);
        moduleLoader = new ModuleLoader(this);

        getCommand("playerx").setExecutor(new PlayerXCommand(this));
        getCommand("playerx").setTabCompleter(new PlayerXTabCompleter(this));

        File modulesDir = new File(getDataFolder(), "modules");
        if (!modulesDir.exists()) {
            modulesDir.mkdirs();
        }

        loadedModules = ModuleLoader.loadModulesFromDirectory(
                modulesDir, getClass().getClassLoader());


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

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public List<ModuleManager> getLoadedModules() {
        return loadedModules;
    }

    public ModuleLoader getModuleLoader() {
        return moduleLoader;
    }

    public File getModulesFolder() {
        File modulesFolder = new File(getDataFolder(), "modules");
        if (!modulesFolder.exists()) {
            modulesFolder.mkdirs();
        }
        return modulesFolder;
    }

    public void saveResourceToFile(String resourcePath, File outputFile) {
        if (!outputFile.exists()) {
            try (InputStream in = getResource(resourcePath)) {
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
