package com.pgs.openskyingest.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static String extractIcao24(String tailNumberWithIcao24) {
        Pattern pattern = Pattern.compile("(.*)\\((.*)\\)");
        Matcher matcher = pattern.matcher(tailNumberWithIcao24);

        String icao24 = "";

        while (matcher.find()) {
            icao24 = matcher.group(2).toLowerCase();
        }

        return icao24;
    }
}
