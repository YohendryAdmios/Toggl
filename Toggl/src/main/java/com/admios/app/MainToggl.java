package com.admios.app;

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
import android.widget.ListView;

import com.admios.app.util.TimeEntryAdapter;
import com.admios.model.TimeEntry;
import com.admios.model.User;
import com.admios.network.ApiTokenInterceptor;
import com.admios.network.NetworkErrorHandler;
import com.admios.network.TogglService;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
  private List<TimeEntry> timeEntries;
  private String timeFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
  private String appDateFormat = "yyyy-MM-dd";

  private SimpleDateFormat appDateSimpleFormater;
  private SimpleDateFormat serverDateSimpleFormater;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_toggl);
    hideKeyboard();
    loadUser();
    changeTitle();
    buildApiAdapter();
    LoadImageTask lit = new LoadImageTask();
    lit.execute((Void) null);
    appDateSimpleFormater = new SimpleDateFormat(appDateFormat);
    serverDateSimpleFormater = new SimpleDateFormat(timeFormat);

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

    String start = serverDateSimpleFormater.format(new Date(1375333200000L));
    String end = serverDateSimpleFormater.format(new Date(1377838800000L));
    start = format(start);
    end = format(end);

    timeEntries = service.timeEntries(start,end);
    Collections.sort(timeEntries,new TimeEntryComparator());

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
        loadTimeEntries();
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
      printListView();
    }
  }

  private void printListView() {
    TimeEntryAdapter adapter = new TimeEntryAdapter(this, R.layout.time_entry_item);
    ListView lv = (ListView)findViewById(R.id.entriesList);
    lv.setAdapter(adapter);

    List<TimeEntry> list = prepareList();
    for(TimeEntry timeEntry : list) {
      if(timeEntry.getType() == TimeEntry.ITEM){
        adapter.addTimeEntry(timeEntry);
      } else {
        adapter.addSeparatorItem(timeEntry);
      }
      Log.d("HEY",gson.toJson(timeEntry));
    }


  }

  private TimeEntry createSeparator(Date date){
    TimeEntry te = new TimeEntry();
    te.setType(TimeEntry.SEPARATOR);
    te.setStart(new SimpleDateFormat(timeFormat).format(date));
    return te;
  }

  private List<TimeEntry> prepareList(){
    List<TimeEntry> list = new ArrayList<TimeEntry>();
    Date lastDate = null;
    for (TimeEntry te : timeEntries){

      if((lastDate != null)){
        if(te.getStart().before(lastDate)){
          list.add(createSeparator(te.getStart()));
          lastDate = te.getStart();
        }
      } else {
        list.add(createSeparator(te.getStart()));
        lastDate = te.getStart();
      }

      list.add(te);
    }
    return list;
  }

  private void changeProfileData(Drawable drawable) {
    mi.setIcon(drawable);
    mi.setTitle(user.getData().getFullname());
  }

  private class TimeEntryComparator implements Comparator<TimeEntry> {

    @Override
    public int compare(TimeEntry lhs, TimeEntry rhs) {
      return rhs.getStart().compareTo(lhs.getStart());
    }
  }
}
