package applications.kantedal.hejablvitt.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by filles-dator on 2015-07-23.
 */
public class DateHandler {
    public static String dateToString(Date d){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        dateFormatter.setTimeZone(TimeZone.getTimeZone("CEST"));

        Long time = d.getTime();
        time +=(2*60*60*1000);
        Date discussionDate = new Date(time);

        return dateFormatter.format(discussionDate);
    }
}
