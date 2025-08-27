package ru.tadakacy.playerx.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import ru.tadakacy.playerx.ModuleManager;
import ru.tadakacy.playerx.PlayerX;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlayerXTabCompleter implements TabCompleter {

    private final PlayerX playerX;

    public PlayerXTabCompleter(PlayerX playerX) {
        this.playerX = playerX;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!isAdmin(sender)) {
            return completions;
        }

        if (args.length == 1) {
            completions.addAll(List.of("help", "reload", "module"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("module")) {
            completions.addAll(List.of("load", "reload"));
        } else if (args.length == 3 && args[0].equalsIgnoreCase("module")) {
            String action = args[1].toLowerCase();
            if (action.equalsIgnoreCase("reload")) {
                playerX.getLoadedModules().stream()
                        .map(ModuleManager::getModuleName)
                        .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                        .forEach(completions::add);
            } else if (action.equalsIgnoreCase("load")) {
                File modulesFolder = new File(playerX.getDataFolder(), "modules");
                for (File file : modulesFolder.listFiles()) {
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".jar")) {
                        String filename = file.getName();
                        String moduleName = filename.substring(0, filename.length() - 4);
                        if (moduleName.toLowerCase().startsWith(args[2].toLowerCase())) {
                            completions.add(moduleName);
                        }
                    }
                }
            }
        }
        return completions;
    }


    public boolean isAdmin(CommandSender s) {
        return s.hasPermission("playerx.admin") || s.isOp();
    }
}