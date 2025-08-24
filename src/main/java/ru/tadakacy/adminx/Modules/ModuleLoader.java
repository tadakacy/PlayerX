package ru.tadakacy.adminx.Modules;

import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import ru.tadakacy.adminx.PlayerX;
import ru.tadakacy.adminx.ModuleManager;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ModuleLoader {

    private static PlayerX plugin;
    public static List<ru.tadakacy.adminx.ModuleManager> loadModulesFromDirectory(File directory, ClassLoader parentClassLoader) {
        List<ru.tadakacy.adminx.ModuleManager> modules = new ArrayList<>();

        if (!directory.exists() || !directory.isDirectory()) {
            plugin.getLogger().info("Папка не найдена: " + directory.getAbsolutePath());
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
                    modules.add(moduleInstance);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return modules;
    }
}

