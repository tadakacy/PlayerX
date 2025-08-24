package ru.tadakacy.adminx.Utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.tadakacy.adminx.PlayerX;

import java.io.File;

import static ru.tadakacy.adminx.Utils.ColorText.color;

public class Messages {
    private final PlayerX plugin;
    private File messagesFile;
    private FileConfiguration messagesConfig;

    public Messages(PlayerX plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    private void loadMessages() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getMessage(String key, String... placeholder) {
        String prefix = messagesConfig.getString("messages.prefix");
        String message = messagesConfig.getString("messages." + key);
        if (message == null) {
            return color("&4Проверьте сообщения в конфигурации (messages.yml).");
        }

        for (int i = 0; i < placeholder.length; i += 2) {
            message = message.replace("%" + placeholder[i] + "%", placeholder[i + 1]);
        }

        return color(prefix + message);
    }
}
