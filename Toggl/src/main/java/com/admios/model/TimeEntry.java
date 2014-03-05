package com.admios.model;

/**
 * Created by yohendryhurtado on 3/5/14.
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeEntry {
  private int id;
  private String guid;
  private int pid;
  private boolean billiable;
  private String start;
  private String stop;
  private int duration;
  private String description;
  private String at;

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

  public Date getStart() {
    return getDate(this.start);
  }

  public void setStart(String start) {
    this.start = start;
  }

  public Date getStop() {
    return getDate(this.stop);
  }

  public void setStop(String stop) {
    this.stop = stop;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getAt() {
    return getDate(this.at);
  }

  public void setAt(String at) {

    this.at = at;
  }

  private Date getDate(String date){
    String timeFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
    try {
      return new SimpleDateFormat(timeFormat).parse(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }
}
