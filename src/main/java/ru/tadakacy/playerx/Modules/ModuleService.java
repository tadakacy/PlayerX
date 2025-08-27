package ru.tadakacy.playerx.Modules;

import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import ru.tadakacy.playerx.ModuleManager;
import ru.tadakacy.playerx.PlayerX;
import ru.tadakacy.playerx.Utils.ErrorUtils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ModuleService {
    private final PlayerX plugin;
    private final List<ModuleManager> loadedModules = new ArrayList<>();
    private final CommandRegistry commandRegistry;
    private static ErrorUtils error;

    public ModuleService(PlayerX plugin, CommandRegistry commandRegistry) {
        this.plugin = plugin;
        this.commandRegistry = commandRegistry;
        this.error = new ErrorUtils(plugin);
    }

    public List<ModuleManager> getLoadedModules() {
        return loadedModules;
    }

    public List<ModuleManager> loadModulesFromDirectory(File modulesDir) {
        List<ModuleManager> modules = loadModules(modulesDir);
        for (ModuleManager module : modules) {
            enableModule(module);
            plugin.getConfiguration().loadModuleConfiguration(module);
        }
        return modules;
    }

    private List<ModuleManager> loadModules(File directory) {
        List<ModuleManager> modules = new ArrayList<>();
        if (!directory.exists() || !directory.isDirectory()) {
            error.logError("1001");
            return modules;
        }
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null) return modules;
        for (File jarFile : files) {
            try {
                URL jarUrl = jarFile.toURI().toURL();
                URLClassLoader loader = new URLClassLoader(new URL[]{jarUrl}, plugin.getClass().getClassLoader());
                Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(jarUrl).addClassLoaders(loader));
                Set<Class<? extends ModuleManager>> classes = reflections.getSubTypesOf(ModuleManager.class);
                for (Class<? extends ModuleManager> clazz : classes) {
                    ModuleManager moduleInstance = clazz.getDeclaredConstructor().newInstance();
                    modules.add(moduleInstance);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return modules;
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

        ModuleManager newModule = ModuleService.loadModuleByName(
                moduleName,
                modulesDir,
                plugin.getClass().getClassLoader()
        );

        if (newModule != null) {
            enableModule(newModule);
            return true;
        }
        return false;
    }

    public static ModuleManager loadModuleByName(String moduleName, File directory, ClassLoader parentClassLoader) {
        if (!directory.exists() || !directory.isDirectory()) {
            error.logError("1001");
            return null;
        }
        File[] files = directory.listFiles((dir, name) -> name.equalsIgnoreCase(moduleName + ".jar"));
        if (files == null || files.length == 0) {
            return null;
        }
        File jarFile = files[0];
        try {
            URL jarUrl = jarFile.toURI().toURL();
            URLClassLoader loader = new URLClassLoader(new URL[]{jarUrl}, parentClassLoader);
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .setUrls(jarUrl)
                    .addClassLoaders(loader));
            Set<Class<? extends ModuleManager>> classes = reflections.getSubTypesOf(ModuleManager.class);
            for (Class<? extends ModuleManager> clazz : classes) {
                return clazz.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
            error.logError("1006", "module", moduleName);
        }
        return null;
    }
}
