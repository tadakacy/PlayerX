package ru.tadakacy.playerx.Modules;

import ru.tadakacy.playerx.ModuleManager;
import ru.tadakacy.playerx.PlayerX;

public class CommandRegistry {
    private final PlayerX plugin;

    public CommandRegistry(PlayerX plugin) {
        this.plugin = plugin;
    }

    public void registerModuleCommands(ModuleManager module) {
        if (!(module instanceof HasCommands)) return;
        for (ModuleCommand cmd : ((HasCommands) module).getCommands()) {
            plugin.getCommand(cmd.getCommandName()).setExecutor(cmd.getExecutor());
            plugin.getCommand(cmd.getCommandName()).setTabCompleter(cmd.getTabCompleter());
        }
    }

    public void unregisterModuleCommands(ModuleManager module) {
        if (!(module instanceof HasCommands)) return;
        for (ModuleCommand cmd : ((HasCommands) module).getCommands()) {
            plugin.getCommand(cmd.getCommandName()).setExecutor(null);
            plugin.getCommand(cmd.getCommandName()).setTabCompleter(null);
        }
    }
}
