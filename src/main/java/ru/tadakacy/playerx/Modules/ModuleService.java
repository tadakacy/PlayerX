package ru.tadakacy.playerx.Modules;

import ru.tadakacy.playerx.ModuleManager;
import ru.tadakacy.playerx.PlayerX;
import ru.tadakacy.playerx.Utils.ErrorUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModuleService {
    private final PlayerX plugin;
    private final List<ModuleManager> loadedModules = new ArrayList<>();
    private final CommandRegistry commandRegistry;

    public ModuleService(PlayerX plugin, CommandRegistry commandRegistry) {
        this.plugin = plugin;
        this.commandRegistry = commandRegistry;
    }

    public List<ModuleManager> getLoadedModules() {
        return loadedModules;
    }

    public void loadModulesFromDirectory(File modulesDir) {
        List<ModuleManager> modules = ModuleLoader.loadModulesFromDirectory(modulesDir, plugin.getClass().getClassLoader());
        for (ModuleManager module : modules) {
            enableModule(module);
        }
    }

    public void enableModule(ModuleManager module) {
        loadedModules.add(module);
        module.onEnable(plugin);
        if (module instanceof HasCommands) {
            commandRegistry.registerModuleCommands(module);
        }
    }

    public void disableModule(ModuleManager module) {
        module.onDisable(plugin);
        if (module instanceof HasCommands) {
            commandRegistry.unregisterModuleCommands(module);
        }
        loadedModules.remove(module);
    }

    public boolean reloadModule(String moduleName, File modulesDir) {
        Optional<ModuleManager> optionalModule = loadedModules.stream()
                .filter(m -> m.getModuleName().equalsIgnoreCase(moduleName))
                .findFirst();

        if (optionalModule.isEmpty()) {
            return false;
        }

        ModuleManager oldModule = optionalModule.get();

        disableModule(oldModule);

        ModuleManager newModule = ModuleLoader.loadModuleByName(
                moduleName,
                modulesDir,
                plugin.getClass().getClassLoader(),
                new ErrorUtils(plugin)
        );

        if (newModule != null) {
            enableModule(newModule);
            return true;
        }
        return false;
    }
}
