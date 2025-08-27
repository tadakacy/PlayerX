package ru.tadakacy.playerx.Modules;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public interface ModuleCommand {
    String getCommandName();
    CommandExecutor getExecutor();
    TabCompleter getTabCompleter();
}
