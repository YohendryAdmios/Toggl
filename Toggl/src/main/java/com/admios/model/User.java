package com.admios.model;

/**
 * Created by yohendryhurtado on 3/3/14.
 */
public class User {

  private String since;
  private Data data;

  public User() {
  }

  public Data getData() {
    return data;
  }

  public static class Data {

    public int id;
    private String apiToken;
    private int defaultWid;
    private String email;
    private String fullname;
    private String timeofdayFormat;
    private String dateFormat;
    private boolean storeStartAndStopTime;
    private int beginningOfWeek;
    private String language;
    private String imageUrl;

    public Data() {
    }


    public String getImageUrl() {
      return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
    }

    public int getDefaultWid() {
      return defaultWid;
    }

    public void setDefaultWid(int defaultWid) {
      this.defaultWid = defaultWid;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getFullname() {
      return fullname;
    }

    public void setFullname(String fullname) {
      this.fullname = fullname;
    }

    public String getTimeofdayFormat() {
      return timeofdayFormat;
    }

    public void setTimeofdayFormat(String timeofdayFormat) {
      this.timeofdayFormat = timeofdayFormat;
    }

    public String getDateFormat() {
      return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
      this.dateFormat = dateFormat;
    }

    public boolean isStoreStartAndStopTime() {
      return storeStartAndStopTime;
    }

    public void setStoreStartAndStopTime(boolean storeStartAndStopTime) {
      this.storeStartAndStopTime = storeStartAndStopTime;
    }

    public int getBeginningOfWeek() {
      return beginningOfWeek;
    }

    public void setBeginningOfWeek(int beginningOfWeek) {
      this.beginningOfWeek = beginningOfWeek;
    }

    public String getLanguage() {
      return language;
    }

    public void setLanguage(String language) {
      this.language = language;
    }

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getApiToken() {
      return apiToken;
    }

    public void setApiToken(String apiToken) {
      this.apiToken = apiToken;
    }
  }
}