package ru.tadakacy.playerx.Modules;

import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import ru.tadakacy.playerx.ModuleManager;
import ru.tadakacy.playerx.PlayerX;
import ru.tadakacy.playerx.Utils.ErrorUtils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

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
                Reflections reflections = new Reflections(new ConfigurationBuilder()
                        .setUrls(jarUrl)
                        .addClassLoaders(loader));
                Set<Class<? extends ModuleManager>> classes = reflections.getSubTypesOf(ModuleManager.class);
                for (Class<? extends ModuleManager> clazz : classes) {
                    ModuleManager moduleInstance = clazz.getDeclaredConstructor().newInstance();
                    if (!loadedModules.contains(moduleInstance)) {
                        modules.add(moduleInstance);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return modules;
    }

    public void enableModule(ModuleManager module) {
        if (!loadedModules.contains(module)) {
            module.onEnable(plugin);
            plugin.getConfiguration().loadModuleConfiguration(module);
            if (module instanceof HasCommands) {
                commandRegistry.registerModuleCommands(module);
            }
            loadedModules.add(module);
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
        try {
            plugin.getLogger().info("Перезагрузка модуля " + moduleName);
            Optional<ModuleManager> optionalModule = loadedModules.stream()
                    .filter(m -> m.getModuleName().equalsIgnoreCase(moduleName))
                    .findFirst();

            if (optionalModule.isEmpty()) {
                plugin.getLogger().info("Модуль не найден " + moduleName);
                return false;
            }

            ModuleManager oldModule = optionalModule.get();
            plugin.getLogger().info("Отключаю старый модуль " + moduleName);
            disableModule(oldModule);
            plugin.getLogger().info("Загружаю новый модуль " + moduleName);
            ModuleManager newModule = ModuleService.loadModuleByName(
                    moduleName,
                    modulesDir,
                    plugin.getClass().getClassLoader(),
                    plugin
            );

            if (newModule != null) {
                plugin.getLogger().info("Активирую новый модуль " + newModule.getModuleName());
                enableModule(newModule);
                plugin.getLogger().info("Модуль успешно запущен " + newModule.getModuleName());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return false;
    }

    public static ModuleManager loadModuleByName(String moduleName, File directory, ClassLoader parentClassLoader, PlayerX playerX) {

        for(ModuleManager existModule : playerX.getModuleService().getLoadedModules()) {
            if(existModule.getModuleName().equalsIgnoreCase(moduleName))
                return existModule;
        }

        if (!directory.exists() || !directory.isDirectory()) {
            error.logError("1001");
            return null;
        }

        File[] exactMatchFiles = directory.listFiles((dir, name) -> name.equalsIgnoreCase(moduleName + ".jar"));

        File selectedFile = null;

        if (exactMatchFiles != null && exactMatchFiles.length > 0) {
            selectedFile = exactMatchFiles[0];
        } else {
            File[] candidateFiles = directory.listFiles((dir, name) -> {
                String lowerName = name.toLowerCase();
                return lowerName.startsWith(moduleName.toLowerCase()) && lowerName.endsWith(".jar");
            });

            if (candidateFiles == null || candidateFiles.length == 0) {
                return null;
            }

            List<File> candidates = new ArrayList<>();
            Collections.addAll(candidates, candidateFiles);

            candidates.sort((f1, f2) -> {
                String v1 = extractVersion(f1.getName());
                String v2 = extractVersion(f2.getName());
                return compareVersions(v1, v2);
            });

            selectedFile = candidates.get(candidates.size() - 1);
        }

        if (selectedFile == null) {
            return null;
        }

        try {
            URL jarUrl = selectedFile.toURI().toURL();
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

    private static String extractVersion(String filename) {
        String name = filename.replace(".jar", "");
        int dashIndex = name.indexOf('-');
        if (dashIndex >= 0 && dashIndex < name.length() - 1) {
            return name.substring(dashIndex + 1);
        }
        return ""; // без версии
    }

    private static int compareVersions(String v1, String v2) {
        try {
            double d1 = Double.parseDouble(v1);
            double d2 = Double.parseDouble(v2);
            return Double.compare(d1, d2);
        } catch (NumberFormatException e) {
            return v1.compareTo(v2);
        }
    }
}
