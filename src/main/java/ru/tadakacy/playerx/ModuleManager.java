package ru.tadakacy.playerx;

import ru.tadakacy.playerx.Modules.CommandRegistry;
import ru.tadakacy.playerx.Modules.ModuleCommand;
import ru.tadakacy.playerx.Utils.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public interface ModuleManager {
    void onEnable(PlayerX playerX);
    void onDisable(PlayerX playerX);

    String getModuleName();
    String getVersion();

    void saveConfig(Configuration config, File modulesDir);

    void registerCommands(CommandRegistry registry);
}
