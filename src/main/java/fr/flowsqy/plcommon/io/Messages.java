package fr.flowsqy.plcommon.io;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class Messages {

    private final Map<String, String> messages;
    private final Map<String, String[]> listMessages;
    private final String prefix;

    public Messages(YamlConfiguration yamlConfiguration, String defaultPrefix, String... ignoredKeys) {
        this(yamlConfiguration, defaultPrefix, generateIgnorePattern(ignoredKeys));
    }

    public Messages(YamlConfiguration yamlConfiguration, String defaultPrefix, Pattern ignorePattern) {
        this.messages = new HashMap<>();
        this.listMessages = new HashMap<>();
        final String originalPrefix = yamlConfiguration.getString("prefix", defaultPrefix);
        Objects.requireNonNull(originalPrefix);
        this.prefix = ChatColor.translateAlternateColorCodes('&', originalPrefix);
        initMessages(yamlConfiguration, ignorePattern);
    }

    public static Pattern generateIgnorePattern(String... ignoredKeys) {
        if (ignoredKeys == null || ignoredKeys.length == 0)
            return null;
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        int count = 0;
        for (String ignoredKey : ignoredKeys) {
            if (ignoredKey.isBlank()) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                sb.append("|");
            }
            sb
                    .append("(")
                    .append(ignoredKey)
                    .append("(\\.(.)+)?") // Exclude sub sections
                    .append(")");
            count++;
        }
        if (count == 0) {
            return null;
        }
        if (count > 1) {
            sb.insert(0, "(").append(")");
        }
        sb.insert(0, "^");
        sb.append("$");

        return Pattern.compile(sb.toString());
    }

    protected void initMessages(YamlConfiguration configuration, Pattern ignorePattern) {
        for (Map.Entry<String, Object> entry : configuration.getValues(true).entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            // Skip ignored values
            if (ignorePattern != null && ignorePattern.matcher(key).matches()) {
                continue;
            }
            if (value instanceof String message) {
                messages.put(
                        key,
                        ChatColor.translateAlternateColorCodes('&',
                                message.replace("%prefix%", prefix)
                        )
                );
            } else if (value instanceof List objectList) {
                final List<String> messageList = new ArrayList<>();
                for (Object element : objectList) {
                    if (element instanceof String message) {
                        messageList.add(
                                ChatColor.translateAlternateColorCodes('&',
                                        message.replace("%prefix%", prefix)
                                )
                        );
                    }
                }
                listMessages.put(key, messageList.toArray(new String[0]));
            }
        }
    }

    public String getPrefix() {
        return prefix;
    }

    protected String getRawMessage(String message, String[] replacement) {
        if (message == null)
            return null;

        final int middle = (replacement.length - replacement.length % 2) / 2;
        for (int index = 0; index < middle; index++) {
            message = message.replace(replacement[index], replacement[index + middle]);
        }

        return message;
    }

    public String getMessage(String path, String... replacement) {
        return getRawMessage(messages.get(path), replacement);
    }

    public String useMessage(String path, String... replacement) {
        return getRawMessage(messages.remove(path), replacement);
    }

    public void getListMessage(String path, Consumer<String> messageConsumer, String... replacement) {
        final String[] rawMessages = listMessages.get(path);
        if (rawMessages == null)
            return;

        final int middle = (replacement.length - replacement.length % 2) / 2;
        for (String message : rawMessages) {
            for (int index = 0; index < middle; index++) {
                message = message.replace(replacement[index], replacement[index + middle]);
            }
            messageConsumer.accept(message);
        }
    }

    public boolean sendMessage(CommandSender sender, String path, String... replacement) {
        final String message = getMessage(path, replacement);
        if (message != null)
            sender.sendMessage(message);
        return true;
    }

    public boolean sendMessageList(CommandSender sender, String path, String... replacement) {
        getListMessage(path, sender::sendMessage, replacement);
        return true;
    }

}
