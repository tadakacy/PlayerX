package ru.tadakacy.playerx.Utils;

import ru.tadakacy.playerx.PlayerX;

import static ru.tadakacy.playerx.Utils.ColorText.color;

public class ErrorUtils {

    private final PlayerX playerX;

    public ErrorUtils(PlayerX playerX) {
        this.playerX = playerX;
    }

    public void logError(String errorCode, String... placeholder) {
        String prefix = color("&8{&cPlayerX&8} ");
        String message = getErrorMessage(errorCode);

        message = replacePlaceholders(message, placeholder);

        playerX.getLogger().severe(color(prefix + message));
    }

    private String replacePlaceholders(String message, String... placeholder) {
        for (int i = 0; i < placeholder.length; i += 2) {
            message = message.replace("%" + placeholder[i] + "%", placeholder[i + 1]);
        }
        return message;
    }

    private String getErrorMessage(String errorCode) {
        switch (errorCode) {
            case "1001":
                return "&cПапка с модулями не найдена.";
            case "1002":
                return "&cФайл config.yml не найден.";
            case "1003":
                return "&cФайл messages.yml не найден.";
            case "1004":
                return "&cМодуль %module% не найден.";
            case "1005":
                return "&cНе удалось перезагрузить модуль: %module%";
            case "1006":
                return "&cНе удалось загрузить модуль: %module%";
            default:
                return "&cНеизвестная ошибка: " + errorCode;
        }
    }
}
