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
import java.util.Set;

public class ModuleLoader {

    private final PlayerX playerX;
    private static ErrorUtils error;

    public ModuleLoader(PlayerX playerX) {
        this.playerX = playerX;
        this.error = new ErrorUtils(playerX);
    }

    public static List<ModuleManager> loadModulesFromDirectory(File directory, ClassLoader parentClassLoader) {
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
                URLClassLoader loader = new URLClassLoader(new URL[]{jarUrl}, parentClassLoader);

                Reflections reflections = new Reflections(new ConfigurationBuilder()
                        .setUrls(jarUrl)
                        .addClassLoaders(loader));

                Set<Class<? extends ModuleManager>> classes = reflections.getSubTypesOf(ModuleManager.class);

                for (Class<? extends ModuleManager> clazz : classes) {
                    ModuleManager moduleInstance = clazz.getDeclaredConstructor().newInstance();

                    if (moduleInstance instanceof ModuleInfo) {
                        String moduleName = ((ModuleInfo) moduleInstance).getModuleName();
                        String version = ((ModuleInfo) moduleInstance).getVersion();
                    }

                    modules.add(moduleInstance);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return modules;
    }

    public static ModuleManager loadModuleByName(String moduleName, File directory, ClassLoader parentClassLoader, ErrorUtils errorUtils) {
        if (!directory.exists() || !directory.isDirectory()) {
            errorUtils.logError("1001");
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
                ModuleManager moduleInstance = clazz.getDeclaredConstructor().newInstance();
                return moduleInstance;
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorUtils.logError("1006", "module", moduleName);
        }
        return null;
    }

    public boolean reloadModule(String moduleName, File modulesDir) {
        if (playerX == null) {
            error.logError("PlayerX is null in ModuleLoader");
            return false;
        }

        List<ModuleManager> loadedModules = playerX.getLoadedModules();
        ModuleManager moduleToReload = null;

        for (ModuleManager module : loadedModules) {
            if (module.getClass().getSimpleName().equalsIgnoreCase(moduleName)) {
                moduleToReload = module;
                break;
            }
        }

        if (moduleToReload == null) {
            error.logError("1004", "module", moduleName);
            return false;
        }

        loadedModules.remove(moduleToReload);

        List<ModuleManager> newModules = loadModulesFromDirectory(modulesDir, playerX.getClass().getClassLoader());

        for (ModuleManager module : newModules) {
            if (module.getClass().getSimpleName().equalsIgnoreCase(moduleName)) {
                loadedModules.add(module);
                return true;
            }
        }

        return false;
    }
}

