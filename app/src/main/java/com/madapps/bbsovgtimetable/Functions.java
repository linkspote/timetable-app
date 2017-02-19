package com.madapps.bbsovgtimetable;

import org.json.JSONArray;

import java.io.Reader;
import java.util.Calendar;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Martin Scherner on 07.03.14.
 */
public class Functions {
    // get date information from string as integer array
    public static Integer[] getDateFromString(String sDate){
        Integer[] iArrDate = new Integer[3];

        Pattern patternDate = Pattern.compile("(.+?)\\.(.+?)\\.(.+)");
        Matcher matcherDate = patternDate.matcher(sDate);

        if (matcherDate.find()){
            iArrDate[0] = Integer.valueOf(matcherDate.group(1));
            iArrDate[1] = Integer.valueOf(matcherDate.group(2));
            iArrDate[2] = Integer.valueOf(matcherDate.group(3));
            return iArrDate;
        } else {
            return null;
        }
    }

    // get string from file reader
    public static String readFile(Reader reader){
        Scanner s = new Scanner(reader).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    // convert given ID of a class to string needed for URL to load time table
    public static String convertClassForURL(Integer iClass){
        String sClass = String.valueOf(iClass);
        while (sClass.length() < 5){
            sClass = "0" + sClass;
        }
        return sClass;
    }

    public static int getCalendarWeek(){
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    public static String getStandardActiveWeek(){
        // get today
        Calendar calendar = Calendar.getInstance();
        // add two days to today; show timetable for next week on Saturday and Sunday
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        Integer iWeekToday =  calendar.get(Calendar.WEEK_OF_YEAR);
        return (iWeekToday < 10) ? "0" : "" + String.valueOf(iWeekToday);
    }
}
