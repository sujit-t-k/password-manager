package org.ajikhoji.passwordmanager.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utility {

    private Utility() {}

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public static String getFormatedDateTimeString(final LocalDateTime ldt) {
        return dtf.format(ldt);
    }

    public static Timestamp getSqlTimeStampForLocalDateTime(final LocalDateTime ldt) {
        if(ldt == null) {
            return null;
        }
        return Timestamp.valueOf(ldt);
    }

    public static boolean isSameValuedObject(final Object obj1, final Object obj2) {
        if(obj1 == null) {
            return obj2 == null;
        } else if(obj2 == null) {
            return false;
        }
        return obj1.equals(obj2);
    }

    public static boolean isSameDateTimeValue(final LocalDateTime ldt1, final LocalDateTime ldt2) {
        if(ldt1 == null) {
            return ldt2 == null;
        } else if(ldt2 == null) {
            return false;
        }
        return ldt1.isEqual(ldt2);
    }

}
