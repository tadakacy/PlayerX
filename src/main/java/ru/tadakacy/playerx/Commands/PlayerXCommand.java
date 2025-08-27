package ru.tadakacy.playerx.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.tadakacy.playerx.ModuleManager;
import ru.tadakacy.playerx.Modules.ModuleService;
import ru.tadakacy.playerx.PlayerX;
import ru.tadakacy.playerx.Utils.ErrorUtils;
import ru.tadakacy.playerx.Utils.Messages;

import java.io.File;
import java.util.List;

public class PlayerXCommand implements CommandExecutor {
    private final PlayerX playerX;
    private Messages messages;

    public PlayerXCommand(PlayerX playerX) {
        this.playerX = playerX;
        this.messages = new Messages(playerX);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!isAdmin(sender)) {
            sender.sendMessage(messages.getMessage("no-perm"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(messages.getMessage("help"));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "help":
                for (String message : messages.getMessageList("info")) {
                    sender.sendMessage(message);
                }
                return true;

            case "reload":
                if (args.length == 1) {
                    playerX.getConfiguration().reloadConfiguration();
                    messages.reloadMessages();
                    sender.sendMessage(messages.getMessage("reload"));
                } else {
                    sender.sendMessage(messages.getMessage("help"));
                }
                return true;

            case "module":
                return handleModuleCommand(sender, args);

            default:
                sender.sendMessage(messages.getMessage("help"));
                return true;
        }
    }

    private boolean handleModuleCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(messages.getMessage("help"));
            return true;
        }

        String action = args[1].toLowerCase();
        String moduleName = args[2];
        File modulesDir = new File(playerX.getDataFolder(), "modules");

        if (action.equals("load")) {
            return loadModule(sender, moduleName, modulesDir);
        } else if (action.equals("reload")) {
            return reloadModule(sender, moduleName);
        } else {
            sender.sendMessage(messages.getMessage("help"));
            return true;
        }
    }

    private boolean loadModule(CommandSender sender, String moduleName, File modulesDir) {
        List<ModuleManager> loadedModules = playerX.getLoadedModules();
        boolean isLoaded = loadedModules.stream().anyMatch(m -> m.getModuleName().equalsIgnoreCase(moduleName));

        if (isLoaded) {
            sender.sendMessage(messages.getMessage("module-already-loaded", "module", moduleName));
            return true;
        }

        ModuleManager module = ModuleService.loadModuleByName(moduleName, modulesDir, playerX.getClass().getClassLoader());
        if (module != null) {
            loadedModules.add(module);
            sender.sendMessage(messages.getMessage("module-loaded", "module", moduleName));
            return true;
        } else {
            new ErrorUtils(playerX).logError("1004", "module", moduleName);
            return false;
        }
    }

    private boolean reloadModule(CommandSender sender, String moduleName) {
        if (playerX.getModuleService().reloadModule(moduleName, playerX.getModulesFolder())) {
            sender.sendMessage(messages.getMessage("reload-module", "module", moduleName));
            return true;
        } else {
            new ErrorUtils(playerX).logError("1005", "module", moduleName);
            return false;
        }
    }

    public boolean isAdmin(CommandSender s) {
        return s.hasPermission("playerx.admin") || s.isOp();
    }

}
