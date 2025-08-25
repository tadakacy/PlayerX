package ru.tadakacy.playerx;

public interface ModuleManager {
    void onEnable(PlayerX plugin);

    String getModuleName();
    String getVersion();
}
