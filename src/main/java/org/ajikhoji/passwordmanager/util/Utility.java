package org.ajikhoji.passwordmanager.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utility {

    private Utility() {}

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public static String getFormatedDateTimeString(final LocalDateTime ldt) {
        return dtf.format(ldt);
    }

}
