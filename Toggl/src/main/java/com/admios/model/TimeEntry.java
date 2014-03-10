package com.admios.model;

/**
 * Created by yohendryhurtado on 3/5/14.
 */

import com.admios.app.util.DateUtil;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeEntry {
  public static final int ITEM = 1;
  private int type = ITEM;
  public static final int SEPARATOR = 2;
  private int id;
  private String guid;
  private int pid;
  private boolean billiable;
  private String start;
  private String stop;
  private long duration = 0;
  private String description;
  private String at;

  public int getType() {
    return this.type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getGuid() {
    return guid;
  }

  public void setGuid(String guid) {
    this.guid = guid;
  }

  public int getPid() {
    return pid;
  }

  public void setPid(int pid) {
    this.pid = pid;
  }

  public boolean isBilliable() {
    return billiable;
  }

  public void setBilliable(boolean billiable) {
    this.billiable = billiable;
  }

  public String getStart() {
    return this.start;

  }

  public void setStart(String start) {
    this.start = start;
  }

  public String getStop() {
    return this.stop;
  }

  public void setStop(String stop) {
    this.stop = stop;
  }

  public long getDuration() {
    if(this.isNew()){
      return duration;
    } else {
      return calculateDuration();
    }

  }

  private long calculateDuration() {
      return DateUtil.getDateDiff(DateUtil.parseLongDate(getStart()), DateUtil.parseLongDate(getStop()), TimeUnit.SECONDS);
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAt() {
    return this.at;
  }

  public void setAt(String at) {

    this.at = at;
  }

  private Date formatDate(String format, String date) {
    try {
      return new SimpleDateFormat(format).parse(date.substring(0, date.length() - 6));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public boolean isNew(){
    if(getId() > 0){
      return false;
    }
    return true;
  }
}
