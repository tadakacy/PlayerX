package ru.tadakacy.playerx.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.tadakacy.playerx.ModuleManager;
import ru.tadakacy.playerx.Modules.ModuleInfo;
import ru.tadakacy.playerx.Modules.ModuleLoader;
import ru.tadakacy.playerx.PlayerX;
import ru.tadakacy.playerx.Utils.Configuration;
import ru.tadakacy.playerx.Utils.ErrorUtils;
import ru.tadakacy.playerx.Utils.Messages;

import java.io.File;
import java.util.List;

public class PlayerXCommand implements CommandExecutor {
    private final PlayerX playerX;
    private Configuration configuration;
    private Messages messages;
    private final ModuleLoader moduleLoader;
    private ErrorUtils error;

    public PlayerXCommand(PlayerX playerX) {
        this.playerX = playerX;
        this.moduleLoader = new ModuleLoader(playerX);
        this.configuration = new Configuration(playerX);
        this.messages = new Messages(playerX);
        this.error = new ErrorUtils(playerX);
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
                    configuration.reloadConfiguration();
                    messages.reloadMessages();
                    sender.sendMessage(messages.getMessage("reload"));
                } else {
                    sender.sendMessage(messages.getMessage("help"));
                }
                return true;

            case "module":
                if (args.length < 3) {
                    sender.sendMessage(messages.getMessage("help"));
                    return true;
                }
                String action = args[1].toLowerCase();
                String moduleNameInput = args[2];

                File modulesDir = new File(playerX.getDataFolder(), "modules");
                List<ModuleManager> loadedModules = playerX.getLoadedModules();
                ModuleManager module = null;

                boolean isLoaded = loadedModules.stream()
                        .anyMatch(m -> m.getModuleName().equalsIgnoreCase(moduleNameInput));

                if (action.equals("load")) {
                    if (isLoaded) {
                        sender.sendMessage(messages.getMessage("module-already-loaded", "module", moduleNameInput));
                        return true;
                    }
                    module = ModuleLoader.loadModuleByName(
                            moduleNameInput,
                            modulesDir,
                            playerX.getClass().getClassLoader(),
                            error
                    );
                    if (module instanceof ModuleInfo) {
                        String moduleName = ((ModuleInfo) module).getModuleName();
                        String version = ((ModuleInfo) module).getVersion();
                    }
                    if (module != null) {
                        playerX.getLoadedModules().add(module);
                        sender.sendMessage(messages.getMessage("module-loaded", "module", moduleNameInput));
                    } else {
                        error.logError("1004", "module", moduleNameInput);
                    }
                } else if (action.equals("reload")) {
                    if (moduleLoader.reloadModule(moduleNameInput, modulesDir)) {
                        sender.sendMessage(messages.getMessage("reload-module", "module", moduleNameInput));
                    } else {
                        error.logError("1005", "module", moduleNameInput);
                    }
                } else {
                    sender.sendMessage(messages.getMessage("help"));
                }
                return true;

            default:
                sender.sendMessage(messages.getMessage("help"));
                return true;
        }
    }


    public boolean isAdmin(CommandSender s) {
        return s.hasPermission("playerx.admin") || s.isOp();
    }

}
