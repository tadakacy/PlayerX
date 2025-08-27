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
            List<String> commands = new ArrayList<>();
            commands.add("help");
            commands.add("reload");
            commands.add("module");
            for (String cmd : commands) {
                if (cmd.startsWith(args[0].toLowerCase())) {
                    completions.add(cmd);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("module")) {
            List<String> actions = new ArrayList<>();
            actions.add("load");
            actions.add("reload");
            for (String act : actions) {
                if (act.startsWith(args[1].toLowerCase())) {
                    completions.add(act);
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("module")) {
            String action = args[1].toLowerCase();
            if (action.equals("load") || action.equals("reload")) {
                for (ModuleManager module : playerX.getLoadedModules()) {
                    String moduleName = module.getModuleName();
                    if (moduleName.toLowerCase().startsWith(args[2].toLowerCase())) {
                        completions.add(moduleName);
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