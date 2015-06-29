package com.sembozdemir.iftarcountdown;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by semih on 28.06.2015.
 */
public class MyTime {
    private static final String LOG_TAG = MyTime.class.getSimpleName();
    private long milliseconds;

    public MyTime(String dateInString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd h:mm a");
        Log.d(LOG_TAG, "dateInString: " + dateInString);
        Date date = sdf.parse(dateInString);
        Log.d(LOG_TAG, date.toString());

        milliseconds = date.getTime();
    }

    public MyTime(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public MyTime minus(MyTime myTime) {
        // TODO: can throw TimeMinusException
        return new MyTime(this.milliseconds - myTime.milliseconds);
    }

    public void count() {
        // TODO: check if it is zero already.
        milliseconds = milliseconds - 1000;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public String getTimeStamp() {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliseconds),
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) % TimeUnit.MINUTES.toSeconds(1));
    }
}
