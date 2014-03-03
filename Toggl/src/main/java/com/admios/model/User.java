package main.java.com.admios.model;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by yohendryhurtado on 3/3/14.
 */
public class User{

  private String since;
  private Data data;

  public User() {
  }



  public static void main(String arg[]){
    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    User user = gson.fromJson("{\"since\":\"1393874508\",\"data\":{\"id\":503709,\"api_token\":\"2d9f0a94c7ffe0683b2a6fc6f1e50376\",\"default_wid\":33654,\"email\":\"yohendry.hurtado@admios-sa.com\",\"fullname\":\"Yohendry hurtado\",\"jquery_timeofday_format\":\"h:i A\",\"jquery_date_format\":\"m/d/Y\",\"timeofday_format\":\"h:mm A\",\"date_format\":\"MM/DD/YYYY\",\"store_start_and_stop_time\":true,\"beginning_of_week\":1,\"language\":\"en_US\",\"image_url\":\"https://www.toggl.com/images/profile.png\",\"sidebar_piechart\":false,\"at\":\"2014-02-26T15:55:08+00:00\",\"created_at\":\"2013-05-29T17:01:47+00:00\",\"retention\":9,\"record_timeline\":true,\"render_timeline\":true,\"timeline_enabled\":true,\"timeline_experiment\":true,\"manual_mode\":false,\"new_blog_post\":{\"title\":\"Comfort Zone – Friend or Foe?\",\"url\":\"http://blog.toggl.com/2014/02/comfort-zone-friend-foe/?utm_source=rss&utm_medium=rss&utm_campaign=comfort-zone-friend-foe\",\"category\":\"Uncategorized\",\"pub_date\":\"2014-02-28T07:43:21Z\"},\"should_upgrade\":false,\"show_offer\":false,\"share_experiment\":false,\"team_experiment\":false,\"achievements_enabled\":false,\"timezone\":\"Etc/UTC\",\"openid_enabled\":true,\"send_product_emails\":true,\"send_weekly_report\":false,\"send_timer_notifications\":true,\"last_blog_entry\":\"http://blog.toggl.com/2013/12/special-offer-get-20-extra-toggl-credit/?utm_source=rss&utm_medium=rss&utm_campaign=special-offer-get-20-extra-toggl-credit\",\"invitation\":{},\"workspaces\":[{\"id\":33654,\"name\":\"Admios workspace\",\"premium\":true,\"admin\":false,\"default_hourly_rate\":0,\"default_currency\":\"USD\",\"only_admins_may_create_projects\":false,\"only_admins_see_billable_rates\":false,\"only_admins_see_team_dashboard\":false,\"projects_billable_by_default\":true,\"rounding\":1,\"rounding_minutes\":0,\"at\":\"2013-07-30T19:09:13+00:00\",\"logo_url\":\"https://www.toggl.com/images/workspace.jpg\",\"ical_url\":\"/ical/workspace_user/7191afb6d2f12b82dffb136c3877d8b5\"}],\"used_next\":true,\"duration_format\":\"classic\"}}",User.class);
    System.out.print(gson.toJson(user));
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