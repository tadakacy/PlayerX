package ru.tadakacy.playerx.Utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.tadakacy.playerx.PlayerX;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ru.tadakacy.playerx.Utils.ColorText.color;

public class Messages {
    private final PlayerX playerX;
    private ErrorUtils error;
    private File messagesFile;
    private static FileConfiguration messagesConfig;

    public Messages(PlayerX playerX) {
        this.playerX = playerX;
        loadMessages();
    }

    private void loadMessages() {
        messagesFile = new File(playerX.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            playerX.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reloadMessages() {
        if(messagesFile == null) {
            error.logError("1003");
            playerX.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public static String getMessage(String key, String... placeholder) {
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

    public static List<String> getMessageList(String key, String... placeholder) {
        List<String> messageList = messagesConfig.getStringList("list." + key);
        if (messageList == null || messageList.isEmpty()) {
            List<String> fallback = new ArrayList<>();
            fallback.add(color("&4Проверьте сообщения в конфигурации (messages.yml)."));
            return fallback;
        }
        List<String> processedList = new ArrayList<>();
        for (String message : messageList) {
            for (int i = 0; i < placeholder.length; i += 2) {
                message = message.replace("%" + placeholder[i] + "%", placeholder[i + 1]);
            }
            processedList.add(color(message));
        }
        return processedList;
    }
}
