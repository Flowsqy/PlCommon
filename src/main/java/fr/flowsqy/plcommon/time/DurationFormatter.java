package fr.flowsqy.plcommon.time;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class DurationFormatter {

    public static final DurationFormatter DEFAULT_FORMATTER = new DurationFormatter(
            "%day%%hour%%minute%%second%",
            " ",
            new TimeFormatter(
                    "",
                    "%time%d ",
                    "%time%d "
            ),
            new TimeFormatter(
                    "",
                    "%time%h ",
                    "%time%h "
            ),
            new TimeFormatter(
                    "",
                    "%time%m ",
                    "%time%m "
            ),
            new TimeFormatter(
                    "",
                    "%time%s ",
                    "%time%s "
            )
    );

    private static final String SECOND_PLACEHOLDER = "%second%";
    private static final String MINUTE_PLACEHOLDER = "%minute%";
    private static final String HOUR_PLACEHOLDER = "%hour%";
    private static final String DAY_PLACEHOLDER = "%day%";

    private static final long SECOND = 1000;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;

    private final String timeFormat;
    private final String empty;
    private final TimeFormatter dayFormat;
    private final TimeFormatter hourFormat;
    private final TimeFormatter minuteFormat;
    private final TimeFormatter secondFormat;

    public DurationFormatter(String timeFormat, String empty, TimeFormatter dayFormat, TimeFormatter hourFormat, TimeFormatter minuteFormat, TimeFormatter secondFormat) {
        this.timeFormat = timeFormat;
        this.empty = empty;
        this.dayFormat = dayFormat;
        this.hourFormat = hourFormat;
        this.minuteFormat = minuteFormat;
        this.secondFormat = secondFormat;
    }

    public static DurationFormatter deserializeDurationFormatter(ConfigurationSection section, DurationFormatter defaultFormatter) {
        if (section == null)
            return defaultFormatter;
        final String format = section.getString("format");
        if (format == null)
            return defaultFormatter;
        final String empty = section.getString("empty");
        if (empty == null)
            return defaultFormatter;
        final TimeFormatter dayFormatter = TimeFormatter.deserializeTimeFormatter(section.getConfigurationSection("day"));
        if (dayFormatter == null)
            return defaultFormatter;
        final TimeFormatter hourFormatter = TimeFormatter.deserializeTimeFormatter(section.getConfigurationSection("hour"));
        if (hourFormatter == null)
            return defaultFormatter;
        final TimeFormatter minuteFormatter = TimeFormatter.deserializeTimeFormatter(section.getConfigurationSection("minute"));
        if (minuteFormatter == null)
            return defaultFormatter;
        final TimeFormatter secondFormatter = TimeFormatter.deserializeTimeFormatter(section.getConfigurationSection("second"));
        if (secondFormatter == null)
            return defaultFormatter;
        return new DurationFormatter(
                ChatColor.translateAlternateColorCodes('&', format),
                ChatColor.translateAlternateColorCodes('&', empty),
                dayFormatter,
                hourFormatter,
                minuteFormatter,
                secondFormatter
        );
    }

    public String format(long duration) {
        String output = timeFormat;

        final long woDays = duration % DAY;
        final long days = (duration - woDays) / DAY;
        output = output.replace(DAY_PLACEHOLDER, dayFormat.format(days));

        final long woHours = woDays % HOUR;
        final long hours = (woDays - woHours) / HOUR;
        output = output.replace(HOUR_PLACEHOLDER, hourFormat.format(hours));

        final long woMinutes = woHours % MINUTE;
        final long minutes = (woHours - woMinutes) / MINUTE;
        output = output.replace(MINUTE_PLACEHOLDER, minuteFormat.format(minutes));

        final long woSeconds = woMinutes % SECOND;
        long seconds = (woMinutes - woSeconds) / SECOND;
        if (seconds == 0 && woSeconds > 0) {
            seconds = 1;
        }
        output = output.replace(SECOND_PLACEHOLDER, secondFormat.format(seconds));

        if (output.isEmpty()) {
            output = empty;
        }

        return output;
    }

}
