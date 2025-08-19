package ru.tadakacy.adminx.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorText {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([a-fA-F\\d]{6})");
    public static final char COLOR_CHAR = '§';

    public static String color(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 32);

        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x" +
                    COLOR_CHAR + group.charAt(0) +
                    COLOR_CHAR + group.charAt(1) +
                    COLOR_CHAR + group.charAt(2) +
                    COLOR_CHAR + group.charAt(3) +
                    COLOR_CHAR + group.charAt(4) +
                    COLOR_CHAR + group.charAt(5));
        }

        matcher.appendTail(buffer);

        return translateAlternateColorCodes('&', buffer.toString());
    }

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();

        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && isValidColorCharacter(b[i + 1])) {
                b[i] = COLOR_CHAR;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }

    private static boolean isValidColorCharacter(char c) {
        return "0123456789abcdefklmnor".indexOf(c) != -1;
    }
}
