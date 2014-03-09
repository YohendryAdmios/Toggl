package com.admios.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.admios.app.util.ProjectAdapter;
import com.admios.app.util.TimeEntryAdapter;
import com.admios.model.Client;
import com.admios.model.Project;
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
import com.squareup.timessquare.CalendarPickerView;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
  private User user;
  private MenuItem mi;
  private RestAdapter restAdapter;
  private Gson gson;
  private TogglService service;
  private List<TimeEntry> timeEntries;
  private String timeFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
  private String appDateFormat = "MM-dd-yyyy";

  private SimpleDateFormat appDateSimpleFormater;
  private SimpleDateFormat serverDateSimpleFormater;
  private View mMainContainer;
  private View mLoadingView;
  private EditText mDateEntryEditText;
  private CalendarPickerView dialogView;
  private FrameLayout timeDialogView;
  private AlertDialog theDialog;
  private AlertDialog timeDialog;
  private Calendar startDate;
  private Calendar endDate;
  private TimePicker tp;
  private EditText mEndedEntryEdit;
  private EditText mStartedEntryEdit;
  private List<Client> clients;
  private Spinner mProjectList;
  private View mEditContainer;
  private Button mNewTimeEntryButton;
  private TimeEntry currentTimeEntry;
  private SimpleDateFormat timeFormater;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_toggl);
    mMainContainer = findViewById(R.id.container);
    mLoadingView = findViewById(R.id.loading_status);
    mEditContainer = findViewById(R.id.edit_container);

    hideKeyboard();
    loadUser();
    changeTitle();
    buildApiAdapter();


    appDateSimpleFormater = new SimpleDateFormat(appDateFormat);
    serverDateSimpleFormater = new SimpleDateFormat(timeFormat);
    timeFormater = new SimpleDateFormat("hh:mm a");

    //edit Time Entry

    startDate = Calendar.getInstance();
    startDate.set(Calendar.DAY_OF_MONTH, 1);

    endDate = Calendar.getInstance();
    endDate.set(Calendar.DAY_OF_MONTH, startDate.getActualMaximum(Calendar.DAY_OF_MONTH));

    mDateEntryEditText = (EditText) findViewById(R.id.dateEditText);
    setDateEntryListeners();

    mStartedEntryEdit = (EditText) findViewById(R.id.startedEditText);
    setTimeEditListeners(mStartedEntryEdit);

    mEndedEntryEdit = (EditText) findViewById(R.id.endedEditText);
    setTimeEditListeners(mEndedEntryEdit);

    mProjectList = (Spinner) findViewById(R.id.projects_list);

    mNewTimeEntryButton = (Button) findViewById(R.id.new_task_button);
    mNewTimeEntryButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        newTimeEntry(v);
      }
    });


    refresh();
  }

  private void setDateEntryListeners() {
    mDateEntryEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          showCalendarDialog();
        }
      }
    });

    mDateEntryEditText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showCalendarDialog();
      }
    });
  }


  private void setTimeEditListeners(final EditText textField) {
    textField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          showTimeDialog(textField);
        }
      }
    });

    textField.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showTimeDialog(textField);
      }
    });
  }

  private void showTimeDialog(final EditText textField) {

    timeDialogView = (FrameLayout) getLayoutInflater().inflate(R.layout.time_dialog, null, false);

    timeDialog =
            new AlertDialog.Builder(MainToggl.this).setTitle("Select Hour")
                    .setView(timeDialogView)
                    .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                      }
                    })
                    .setPositiveButton("Select", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {
                        tp = (TimePicker) timeDialogView.findViewById(R.id.timePicker);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR,tp.getCurrentHour());
                        calendar.set(Calendar.MINUTE,tp.getCurrentMinute());
                        textField.setText(timeFormater.format(calendar.getTime()));
                        dialogInterface.dismiss();
                      }
                    })
                    .create();


    if (!timeDialog.isShowing()) {
      timeDialog.show();
    }
  }

  private int getSelectedHour() {
    return tp.getCurrentHour() > 12 ? tp.getCurrentHour() - 12 : tp.getCurrentHour();
  }

  private String getSelectedHourLapse() {
    return tp.getCurrentHour() > 12 ? "PM " : "AM";
  }

  private void showCalendarDialog() {
    if (dialogView == null) {
      dialogView = (CalendarPickerView) getLayoutInflater().inflate(R.layout.calendar_dialog, null, false);
      dialogView.init(startDate.getTime(), endDate.getTime())
              .withSelectedDate(new Date());
    }
    if (theDialog == null) {
      theDialog =
              new AlertDialog.Builder(MainToggl.this).setTitle("Select Date")
                      .setView(dialogView)
                      .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                          dialogInterface.dismiss();
                        }
                      })
                      .setPositiveButton("Select", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                          mDateEntryEditText.setText(appDateSimpleFormater.format(dialogView.getSelectedDate()));
                          dialogInterface.dismiss();
                        }
                      })
                      .create();
      theDialog.setOnShowListener(new DialogInterface.OnShowListener() {
        @Override
        public void onShow(DialogInterface dialogInterface) {
          dialogView.fixDialogDimens();
        }
      });
    }
    if (!theDialog.isShowing()) {
      theDialog.show();
    }
  }

  private void refresh() {
    new LoadInitialDataTask().execute();
  }

  private void showLoading(boolean show) {
    mLoadingView.setVisibility(show ? View.VISIBLE : View.GONE);
    mMainContainer.setVisibility(show ? View.GONE : View.VISIBLE);
    mEditContainer.setVisibility(View.GONE);
  }

  private void showEdit(boolean show) {
    if (show) {
      loadTimeEntry();
    }
    mEditContainer.setVisibility(show ? View.VISIBLE : View.GONE);
    mMainContainer.setVisibility(show ? View.GONE : View.VISIBLE);
    mLoadingView.setVisibility(View.GONE);
  }

  private void loadTimeEntry() {
    mStartedEntryEdit.setText(timeFormater.format(currentTimeEntry.getStart()));
    mEndedEntryEdit.setText(timeFormater.format(currentTimeEntry.getStop()));
    mDateEntryEditText.setText(appDateSimpleFormater.format(currentTimeEntry.getAt()));
  }

  private void buildApiAdapter() {
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

    String start = serverDateSimpleFormater.format(startDate.getTime());
    String end = serverDateSimpleFormater.format(new Date());
    start = format(start);
    end = format(end);

    timeEntries = service.timeEntries(start, end);
    Collections.sort(timeEntries, new TimeEntryComparator());
  }

  private String format(String date) {
    return date.substring(0, date.length() - 2) + ":" + date.substring(date.length() - 2, date.length());
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
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_refresh:
        refresh();
        return true;
      case R.id.action_log_out:
        logOut();
        return true;
      case R.id.action_main:
        showEdit(false);
        return true;
      case R.id.action_edit:
        showEdit(true);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void logOut() {
    getPreferences(MODE_PRIVATE).edit().remove("api_token").commit();
    finish();
  }

  @Override
  public boolean onNavigationItemSelected(int i, long l) {
    return false;
  }

  private void printListView() {
    showEdit(false);
    TimeEntryAdapter adapter = new TimeEntryAdapter(this, R.layout.time_entry_item);
    ListView lv = (ListView) findViewById(R.id.entriesList);
    lv.setAdapter(adapter);

    List<TimeEntry> list = prepareList();
    for (TimeEntry timeEntry : list) {
      if (timeEntry.getType() == TimeEntry.ITEM) {
        adapter.addTimeEntry(timeEntry);
      } else {
        adapter.addSeparatorItem(timeEntry);
      }
    }
    adapter.notifyDataSetChanged();

  }

  private void addProjectsToSpinner() {
    List<Project> projects = new ArrayList<Project>();
    for (Client client : clients) {
      for (Project project : client.getProjects()) {
        project.setClientName(client.getName());
        projects.add(project);
      }
    }
    mProjectList.setAdapter(new ProjectAdapter(this, R.layout.project_item, projects));
  }

  private TimeEntry createSeparator(Date date) {
    TimeEntry te = new TimeEntry();
    te.setType(TimeEntry.SEPARATOR);
    te.setStart(new SimpleDateFormat(timeFormat).format(date));
    return te;
  }

  private List<TimeEntry> prepareList() {
    List<TimeEntry> list = new ArrayList<TimeEntry>();
    TimeEntry lastSeparator = null;
    Date lastDate = null;
    for (TimeEntry te : timeEntries) {

      if ((lastDate != null)) {
        if (te.getStart().before(lastDate)) {
          lastSeparator = createSeparator(te.getStart());
          list.add(lastSeparator);
          lastDate = te.getStart();
        }
      } else {
        lastSeparator = createSeparator(te.getStart());
        list.add(lastSeparator);
        lastDate = te.getStart();
      }
      lastSeparator.setDuration(lastSeparator.getDuration() + te.getDuration());
      list.add(te);
    }
    return list;
  }

  private void changeProfileData(Drawable drawable) {
    mi.setIcon(drawable);
    mi.setTitle(user.getData().getFullname());
  }

  private void loadClients() {
    clients = service.getClients();
    List<Client> toRemove = new ArrayList<Client>();
    for (Client client : clients) {
      client.setProjects(service.getProjectsByClient(client.getId()));
      if (client.getProjects().isEmpty()) {
        toRemove.add(client);
      }
    }

    for (Client client : toRemove) {
      clients.remove(client);
    }
  }

  private Drawable loadProfile() {
    try {
      Bitmap bm = Picasso.with(getApplicationContext())
              .load(user.getData().getImageUrl())
              .placeholder(R.drawable.ic_action_search)
              .error(R.drawable.abc_ic_clear)
              .get();
      Drawable d = new BitmapDrawable(getResources(), bm);
      return d;
    } catch (Exception e) {
      Toast.makeText(getApplicationContext(), "Error loading profile data", Toast.LENGTH_LONG).show();
    }
    return null;
  }

  public void newTimeEntry(View v) {
    createNewTimeEntry();
    showEdit(true);
  }

  public void createNewTimeEntry() {
    currentTimeEntry = new TimeEntry();
    Date now = new Date();

    currentTimeEntry.setStart(serverDateSimpleFormater.format(now));
    currentTimeEntry.setStop(serverDateSimpleFormater.format(now));
    currentTimeEntry.setAt(serverDateSimpleFormater.format(now));
    Log.d("HEY", gson.toJson(currentTimeEntry));
  }

  private class LoadInitialDataTask extends AsyncTask<Void, Void, Drawable> {
    @Override
    protected void onPreExecute() {
      showLoading(true);
      super.onPreExecute();
    }

    @Override
    protected Drawable doInBackground(Void... voids) {
      loadTimeEntries();
      loadClients();
      return loadProfile();
    }

    @Override
    protected void onPostExecute(Drawable drawable) {
      if (drawable != null) {
        changeProfileData(drawable);
      }
      addProjectsToSpinner();
      showLoading(false);
      printListView();
    }
  }

  private class TimeEntryComparator implements Comparator<TimeEntry> {
    @Override
    public int compare(TimeEntry lhs, TimeEntry rhs) {
      return rhs.getStart().compareTo(lhs.getStart());
    }
  }
}
