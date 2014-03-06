package com.admios.model;

/**
 * Created by yohendryhurtado on 3/5/14.
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeEntry {
  public static final int ITEM = 1;
  public static final int SEPARATOR = 2;
  private int id;
  private String guid;
  private int pid;
  private boolean billiable;
  private String start;
  private String stop;
  private int duration;
  private String description;
  private String at;
  private int type = ITEM;

  public void setType(int type) {
    this.type = type;
  }

  public int getType(){
    return this.type;
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

  public Date getStart() {
    return getShortDate(this.start);
  }

  public void setStart(String start) {
    this.start = start;
  }

  public Date getStop() {
    return getShortDate(this.stop);
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
    return getShortDate(this.at);
  }

  private Date getShortDate(String at) {
    return formatDate("yyyy-MM-dd",at);
  }

  private Date formatDate(String format,String date){
    try {
      return new SimpleDateFormat(format).parse(date.substring(0,date.length()-6));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void setAt(String at) {

    this.at = at;
  }

  private Date getFullDate(String date){
    return formatDate("yyyy-MM-dd'T'HH:mm:ss",date);
  }
}
