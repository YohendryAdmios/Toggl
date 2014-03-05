package com.admios.app;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.admios.model.TimeEntry;
import com.admios.model.User;
import com.admios.network.ApiRequestInterceptor;
import com.admios.network.ApiTokenInterceptor;
import com.admios.network.NetworkErrorHandler;
import com.admios.network.TogglService;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class MainToggl extends ActionBarActivity implements ActionBar.OnNavigationListener {

  /**
   * The serialization (saved instance state) Bundle key representing the
   * current dropdown position.
   */
  private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
  private User user;
  private MenuItem mi;
  private RestAdapter restAdapter;
  private Gson gson;
  private TogglService service;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_toggl);
    hideKeyboard();
    loadUser();
    changeTitle();
    buildApiAdapter();
    new TimeEntriesCaller().execute();

  }
  private void buildApiAdapter(){
    gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Date.class, new DateTypeAdapter())
            .create();
    restAdapter = new RestAdapter.Builder()
            .setServer("https://www.toggl.com/api/v8")
            .setRequestInterceptor(new ApiTokenInterceptor(user.getData().getApiToken()))
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setErrorHandler(new NetworkErrorHandler())

            .setConverter(new GsonConverter(gson))
            .build();
    service = restAdapter.create(TogglService.class);
  }
  private void loadTimeEntries() {
    //call the service
    //mock dates 08/01/2013 and 08/30/2013
    String timeFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
    String start = new SimpleDateFormat(timeFormat).format(new Date(1375333200000L));
    String end = new SimpleDateFormat(timeFormat).format(new Date(1377838800000L));
    start = format(start);
    end = format(end);

    List<TimeEntry> timeEntries = service.timeEntries(start,end);
    for(TimeEntry timeEntry : timeEntries){
      Log.d("HEY",gson.toJson(timeEntry));
    }

  }

  private String format(String date) {
    return date.substring(0,date.length()-2) + ":" + date.substring(date.length()-2,date.length());
  }

  private void changeTitle() {
    this.setTitle(user.getData().getFullname());
  }

  private void loadUser() {
    Gson gson = new Gson();
    user = gson.fromJson(getIntent().getStringExtra("user"), User.class);
  }


  private void hideKeyboard() {
    getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_toggl, menu);

    mi = (MenuItem) menu.findItem(R.id.action_profile);
    LoadImageTask lit = new LoadImageTask();
    lit.execute((Void) null);
    return super.onCreateOptionsMenu(menu);
  }


  @Override
  public boolean onNavigationItemSelected(int position, long id) {
    return true;
  }

  private class LoadImageTask extends AsyncTask<Void, Void, Drawable> {

    @Override
    protected Drawable doInBackground(Void... voids) {
      try {
        Bitmap bm = Picasso.with(getApplicationContext())
                .load(user.getData().getImageUrl())
                .placeholder(R.drawable.ic_action_search)
                .error(R.drawable.abc_ic_clear)
                .get();
        Drawable d = new BitmapDrawable(getResources(), bm);
        return d;
      } catch (IOException e) {
        Log.e("HEY", e.getMessage());
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(Drawable drawable) {
      if (drawable != null) {
        changeProfileData(drawable);
      }
    }
  }

  private class TimeEntriesCaller extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... voids) {
      loadTimeEntries();
      return null;
    }
  }


  private void changeProfileData(Drawable drawable) {
    mi.setIcon(drawable);
    mi.setTitle(user.getData().getFullname());
  }
}
