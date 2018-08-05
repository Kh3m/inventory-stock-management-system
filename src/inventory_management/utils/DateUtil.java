package inventory_management.utils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Created by Kh3m on 7/5/2018.
 */
public class DateUtil {

    public static String dateFormatter (Timestamp timestamp) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(timestamp.toInstant(), TimeZone.getDefault().toZoneId());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy hh:mm:ss a");
        String format = localDateTime.format(dateTimeFormatter);

        return format;
    }
}
