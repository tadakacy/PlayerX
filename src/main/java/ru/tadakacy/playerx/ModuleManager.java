package ru.tadakacy.playerx;

import ru.tadakacy.playerx.Modules.CommandRegistry;
import ru.tadakacy.playerx.Utils.Configuration;

import java.io.File;

public interface ModuleManager {
    void onEnable(PlayerX playerX);
    void onDisable(PlayerX playerX);

    String getModuleName();
    String getVersion();

    void saveConfig(Configuration config);
    void registerCommands(CommandRegistry registry);
}
