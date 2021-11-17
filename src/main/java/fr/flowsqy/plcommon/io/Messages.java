package fr.flowsqy.plcommon.io;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Objects;

public class Messages {

    private final YamlConfiguration yamlConfiguration;
    private final String prefix;

    public Messages(YamlConfiguration yamlConfiguration, String defaultPrefix) {
        this.yamlConfiguration = yamlConfiguration;
        final String originalPrefix = yamlConfiguration.getString("prefix", defaultPrefix);
        Objects.requireNonNull(originalPrefix);
        this.prefix = ChatColor.translateAlternateColorCodes('&', originalPrefix);
    }

    public YamlConfiguration getYamlConfiguration() {
        return yamlConfiguration;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getMessage(String path, String... replace) {
        String message = yamlConfiguration.getString(path);
        if (message == null)
            return null;

        message = message.replace("%prefix%", prefix);

        if (replace == null)
            return ChatColor.translateAlternateColorCodes('&', message);

        final int middle = (replace.length - replace.length % 2) / 2;
        for (int index = 0; index < middle; index++) {
            message = message.replace(replace[index], replace[index + middle]);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public boolean sendMessage(CommandSender sender, String path, String... replace) {
        final String message = getMessage(path, replace);
        if (message != null)
            sender.sendMessage(message);
        return true;
    }

}
