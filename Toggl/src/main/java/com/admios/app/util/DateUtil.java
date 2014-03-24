package com.admios.app.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by yohendryhurtado on 3/10/14.
 */
public class DateUtil {

  private static String longFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
  private static String shortFormat = "MM-dd-yyyy";
  private static String timeFormat = "hh:mm a";
  private static String durationFormat = "HH:mm";
  private static SimpleDateFormat shortFormatter = new SimpleDateFormat(shortFormat);
  private static SimpleDateFormat longFormatter = new SimpleDateFormat(longFormat);
  private static SimpleDateFormat timeFormatter = new SimpleDateFormat(timeFormat);
  private static SimpleDateFormat durationFormatter = new SimpleDateFormat(durationFormat);

  /**
   * Get a diff between two dates
   *
   * @param date1    the oldest date
   * @param date2    the newest date
   * @param timeUnit the unit in which you want the diff
   * @return the diff value, in the provided unit
   */
  public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
    long diffInMillies = date2.getTime() - date1.getTime();
    Log.d("DateUtil", String.format("diff betwin %s and %s is %d", DateUtil.toLongDate(date1), DateUtil.toLongDate(date2), diffInMillies));
    return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
  }

  public static String toShortDate(Date date) {
    return shortFormatter.format(date);
  }

  public static String toLongDate(Date date) {
    return longFormatter.format(date);
  }

  public static String toDuration(Date date) {
    return durationFormatter.format(date);
  }

  public static String toTimeFormat(Date date) {
    return timeFormatter.format(date);
  }

  public static Date parseLongDate(String date) {
    try {
      return longFormatter.parse(date);
    } catch (ParseException e) {
      Log.e("DateUtil", String.format("error trayng to parse %s", date));
      e.printStackTrace();
    }
    return new Date(0);
  }

  public static Date parseShortDate(String date) {
    try {
      return shortFormatter.parse(date);
    } catch (ParseException e) {
      Log.e("DateUtil", String.format("error traying to parse %s", date));
      e.printStackTrace();

    }
    return new Date(0);
  }

  public static String durationToTime(long duration) {
    duration = Math.abs(duration);
    long hours = duration / 3600;
    long minutes = (duration % 3600) / 60;
    return String.format("%02d:%02d", hours, minutes);
  }

  public static String prepareToServer(String time){
    String timeZone = time.substring(time.length()-5,time.length());
    String newDate = "";
    if(timeZone.lastIndexOf(":")>0){
      newDate = time;

    } else {
      newDate = String.format("%s:%s",time.substring(0,time.length()-2),time.substring(time.length()-2));
    }

    return newDate;
  }


}
