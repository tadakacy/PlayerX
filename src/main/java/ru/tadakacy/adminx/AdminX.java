package ru.tadakacy.adminx;

import org.bukkit.plugin.java.JavaPlugin;
import ru.tadakacy.adminx.Utils.Messages;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static ru.tadakacy.adminx.Utils.ColorText.color;

public final class AdminX extends JavaPlugin {
    private List<ModuleManager> loadedModules = new ArrayList<>();
    private Messages msg;
    @Override
    public void onEnable() {
        Messages msg = new Messages(this);

        File modulesDir = new File(getDataFolder(), "modules");
        if (!modulesDir.exists()) {
            modulesDir.mkdirs();
        }

        loadedModules = ru.tadakacy.adminx.Modules.ModuleLoader.loadModulesFromDirectory(
                modulesDir, getClass().getClassLoader());


        getLogger().info(color("&a           __                                    "));
        getLogger().info(color("&a          /\\ \\              __                   "));
        getLogger().info(color("&a   __     \\_\\ \\    ___ ___ /\\_\\    ___    __  _  "));
        getLogger().info(color("&a /'__`\\   /'_` \\ /' __` __`\\/\\ \\ /' _ `\\ /\\ \\/'\\ "));
        getLogger().info(color("&a/\\ \\L\\.\\_/\\ \\L\\ \\/\\ \\/\\ \\/\\ \\ \\ \\/\\ \\/\\ \\\\/>  </ "));
        getLogger().info(color("&a\\ \\__/.\\_\\ \\___,_\\ \\_\\ \\_\\ \\_\\ \\_\\ \\_\\ \\_\\/\\_/\\_\\"));
        getLogger().info(color("&a \\/__/\\/_/\\/__,_ /\\/_/\\/_/\\/_/\\/_/\\/_/\\/_/\\//\\/_/"));
        getLogger().info(color(""));
        getLogger().info(color("&fРазработчик плагина: t.me/dev_tadakacy"));
        getLogger().info(color(""));
        getLogger().info(msg.getMessage("started", "count", String.valueOf(loadedModules.size())));

        for (ModuleManager moduleManager : loadedModules) {
            moduleManager.onEnable(this);
        }
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

    public Messages getCoreMsg() {
        return msg;
    }
}
