package it.blacked.lifestealcore.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static long parseDuration(String time) {
        long duration = 0;

        String value = "";
        for (char c : time.toCharArray()) {
            if (Character.isDigit(c)) {
                value += c;
            } else {
                if (!value.isEmpty()) {
                    int amount = Integer.parseInt(value);

                    switch (c) {
                        case 's':
                            duration += TimeUnit.SECONDS.toMillis(amount);
                            break;
                        case 'm':
                            duration += TimeUnit.MINUTES.toMillis(amount);
                            break;
                        case 'h':
                            duration += TimeUnit.HOURS.toMillis(amount);
                            break;
                        case 'd':
                            duration += TimeUnit.DAYS.toMillis(amount);
                            break;
                    }

                    value = "";
                }
            }
        }

        if (!value.isEmpty()) {
            duration += TimeUnit.SECONDS.toMillis(Integer.parseInt(value));
        }

        return duration;
    }

    public static String formatDuration(long duration) {
        if (duration <= 0) {
            return "0s";
        }

        long days = TimeUnit.MILLISECONDS.toDays(duration);
        duration -= TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        duration -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        duration -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);

        StringBuilder sb = new StringBuilder();

        if (days > 0) {
            sb.append(days).append("d ");
        }

        if (hours > 0 || days > 0) {
            sb.append(hours).append("h ");
        }

        if (minutes > 0 || hours > 0 || days > 0) {
            sb.append(minutes).append("m ");
        }

        sb.append(seconds).append("s");

        return sb.toString().trim();
    }
}