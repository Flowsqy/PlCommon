package fr.flowsqy.plcommon.time;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class TimeFormatter {

    private static final String TIME_PLACEHOLDER = "%time%";

    private final String none;
    private final String one;
    private final String multiple;

    public TimeFormatter(String none, String one, String multiple) {
        this.none = none;
        this.one = one;
        this.multiple = multiple;
    }

    public static TimeFormatter deserializeTimeFormatter(ConfigurationSection section) {
        if (section == null)
            return null;
        final String none = section.getString("none");
        if (none == null)
            return null;
        final String one = section.getString("one");
        if (one == null)
            return null;
        final String multiple = section.getString("multiple");
        if (multiple == null)
            return null;
        return new TimeFormatter(
                ChatColor.translateAlternateColorCodes('&', none),
                ChatColor.translateAlternateColorCodes('&', one),
                ChatColor.translateAlternateColorCodes('&', multiple)
        );
    }

    public String format(long time) {
        String output;
        if (time > 1L) {
            output = multiple;
        } else if (time == 1) {
            output = one;
        } else {
            output = none;
        }
        return output.replace(TIME_PLACEHOLDER, String.valueOf(time));
    }

}
