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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.admios.app.util.DateUtil;
import com.admios.app.util.ProjectAdapter;
import com.admios.app.util.TimeEntryAdapter;
import com.admios.app.util.Writer;
import com.admios.model.Client;
import com.admios.model.Project;
import com.admios.model.TimeEntry;
import com.admios.model.User;
import com.admios.network.ApiTokenInterceptor;
import com.admios.network.NetworkErrorHandler;
import com.admios.network.TogglService;
import com.admios.network.TypedJsonString;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.timessquare.CalendarPickerView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
  private EditText mDurationEdit;
  private Button mEditCancelButton;
  private Button mEditDeleteButton;
  private ListView lv;
  private EditText mEditDescriptionEditText;
  private ToggleButton mEditBillableButton;
  private boolean forceLoadClients = false;
  private Writer w;
  private Button mSaveButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_toggl);
    mMainContainer = findViewById(R.id.container);
    mLoadingView = findViewById(R.id.loading_status);
    mEditContainer = findViewById(R.id.edit_container);

    w = new Writer(this);

    hideKeyboard();
    loadUser();
    changeTitle();
    buildApiAdapter();

    lv = (ListView) findViewById(R.id.entriesList);
    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TimeEntry tmp = (TimeEntry) parent.getItemAtPosition(position);
        if(tmp.getType() == TimeEntry.ITEM){
          currentTimeEntry = tmp;
          loadTimeEntry();
          showEdit(true);
          Log.d("CURRENT",gson.toJson(currentTimeEntry));
        }

      }
    });



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

    mDurationEdit = (EditText) findViewById(R.id.timeEditText);

    mEditCancelButton = (Button) findViewById(R.id.cancelButton);
    mEditCancelButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showEdit(false);
      }
    });

    mEditDeleteButton = (Button) findViewById(R.id.deleteButton);

    mEditDescriptionEditText = (EditText) findViewById(R.id.descriptionEditText);

    mEditBillableButton = (ToggleButton) findViewById(R.id.toggleButton);

    mSaveButton = (Button) findViewById(R.id.saveButton);
    mSaveButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        updateCurrentTimeEntry();
        if(!currentTimeEntry.isNew()) {
          new updateTimeEntryTask().execute();
        } else {
          Toast.makeText(v.getContext(),"Not implemented",Toast.LENGTH_LONG).show();
        }

      }
    });

    refresh();
  }

  private void updateCurrentTimeEntry() {
    currentTimeEntry.setDescription(mEditDescriptionEditText.getText().toString());
  }

  private class updateTimeEntryTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
      TypedJsonString body = new TypedJsonString("time_entry",gson.toJson(currentTimeEntry));
      service.updateTimeEntry(currentTimeEntry.getId(),body);
      return null;
    }
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
                        calendar.setTime(DateUtil.parseLongDate(currentTimeEntry.getStart()));
                        calendar.set(Calendar.HOUR_OF_DAY, tp.getCurrentHour());
                        calendar.set(Calendar.MINUTE, tp.getCurrentMinute());
                        Log.d("HEY","before > "+gson.toJson(currentTimeEntry));
                        if (textField.getId() == R.id.startedEditText) {
                          currentTimeEntry.setStart(DateUtil.toLongDate(calendar.getTime()));
                        } else {
                          currentTimeEntry.setStop(DateUtil.toLongDate(calendar.getTime()));
                        }
                        Log.d("HEY","after > "+gson.toJson(currentTimeEntry));
                        loadTimeEntry();
                        dialogInterface.dismiss();

                      }
                    })
                    .create();


    if (!timeDialog.isShowing()) {
      timeDialog.show();
    }
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
                          mDateEntryEditText.setText(DateUtil.toShortDate(dialogView.getSelectedDate()));
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
    mEditDescriptionEditText.setText(currentTimeEntry.getDescription());
    mEditDeleteButton.setVisibility((currentTimeEntry.getId() > 1) ? View.VISIBLE:View.GONE);
    mStartedEntryEdit.setText(DateUtil.toTimeFormat(DateUtil.parseLongDate(currentTimeEntry.getStart())));
    mEndedEntryEdit.setText(DateUtil.toTimeFormat(DateUtil.parseLongDate(currentTimeEntry.getStop())));
    mDateEntryEditText.setText(DateUtil.toShortDate(DateUtil.parseLongDate(currentTimeEntry.getAt())));
    mDurationEdit.setText(DateUtil.durationToTime(currentTimeEntry.getDuration()));
    mEditBillableButton.setChecked(currentTimeEntry.isBillable());

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

    String start = DateUtil.toLongDate(startDate.getTime());
    String end = DateUtil.toLongDate(new Date());
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
      case R.id.action_force_load_clients:
        forceLoadClients = true;
        refresh();
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
    te.setStart(DateUtil.toLongDate(date));
    return te;
  }

  private List<TimeEntry> prepareList() {
    List<TimeEntry> list = new ArrayList<TimeEntry>();
    TimeEntry lastSeparator = null;
    Date lastDate = null;
    Date actualShortDate = null;
    Date actualLongDate = null;
    for (TimeEntry te : timeEntries) {
      actualShortDate = DateUtil.parseShortDate(te.getStart());
      actualLongDate = DateUtil.parseLongDate(te.getStart());
      if ((lastDate != null)) {
        if (actualShortDate.before(lastDate)) {
          lastSeparator = createSeparator(actualLongDate);
          list.add(lastSeparator);
          lastDate = actualShortDate;
        }
      } else {
        lastSeparator = createSeparator( actualLongDate);
        list.add(lastSeparator);
        lastDate = actualShortDate;
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
    try{
      clients = w.readClients();
        if(forceLoadClients){
          loadClientsFromServer();
        }
    } catch (IOException e) {
      loadClientsFromServer();
    }

  }

  public void loadClientsFromServer(){
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
    forceLoadClients = false;
    saveClients();
  }

  private boolean saveClients(){

    w.writeClients(clients);
    return  false;
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

    currentTimeEntry.setStart(DateUtil.toLongDate(now));
    currentTimeEntry.setStop(DateUtil.toLongDate(now));
    currentTimeEntry.setAt(DateUtil.toLongDate(now));
    Log.d("HEY", String.format("new TimeEntry %s", gson.toJson(currentTimeEntry)));
  }

  @Override
  public void onBackPressed() {
    if(mEditContainer.getVisibility() == View.VISIBLE){
      showEdit(false);
    } else if(mLoadingView.getVisibility() == View.VISIBLE){
      showLoading(false);
    } else if(mMainContainer.getVisibility() == View.VISIBLE){
      super.onBackPressed();
    }

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
