package com.example.ramakrk.shuttletrackerms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ramakrk on 7/24/2017.
 */
public class Utils
{
    JSONParser jsonParser = new JSONParser();
    final String TAG_success = "success";


    public static Date getCurrentLocalTime()
    {
        Calendar calendarObject = Calendar.getInstance();
        Date currentTime = calendarObject.getTime();
        return currentTime;
    }

    public static Date getCurrentLocalTime(int goBackSeconds)
    {
        Calendar calendarObject = Calendar.getInstance();
        calendarObject.add(Calendar.SECOND, -goBackSeconds);
        Date currentTime = calendarObject.getTime();
        return currentTime;
    }

    public static Date parseDate(String date)
    {
        try
        {
            return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").parse(date);
        }
        catch (ParseException e)
        {
            return null;
        }

    }

}
