package com.admios.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.admios.exception.ForbiddenException;
import com.admios.model.User;
import com.admios.network.ApiRequestInterceptor;
import com.admios.network.NetworkErrorHandler;
import com.admios.network.TogglService;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.regex.Pattern;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

  /**
   * Keep track of the login task to ensure we can cancel it if requested.
   */
  private UserLoginTask mAuthTask = null;

  // Values for email and password at the time of the login attempt.
  private String mEmail;
  private String mPassword;

  // UI references.
  private EditText mEmailView;
  private EditText mPasswordView;
  private View mLoginFormView;
  private View mLoginStatusView;
  private CheckBox mCBShowPass;
  private TextView mLoginStatusMessageView;
  private SharedPreferences credentials;
  private CheckBox mCBRemenberMe;
  private User user;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_login);

    //preferences
    credentials = getSharedPreferences("credentials", MODE_PRIVATE);

    // Set up the login form.
    mCBRemenberMe = (CheckBox) findViewById(R.id.check_remember_me);
    mCBRemenberMe.setChecked(credentials.getBoolean("remember", false));

    mEmailView = (EditText) findViewById(R.id.email);


    mPasswordView = (EditText) findViewById(R.id.password);

    mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == R.id.login || id == EditorInfo.IME_NULL) {
          attemptLogin();
          return true;
        }
        return false;
      }
    });
    if (mCBRemenberMe.isChecked()) {
      mPasswordView.setText(credentials.getString("pass", ""));
      mEmailView.setText(credentials.getString("mail", getPrimaryEmail(this.getApplicationContext())));
    } else {
      mPasswordView.setText("");
      mEmailView.setText(getPrimaryEmail(this.getApplicationContext()));
    }


    mLoginFormView = findViewById(R.id.login_form);
    mLoginStatusView = findViewById(R.id.login_status);
    mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

    findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        attemptLogin();
      }
    });

    // get the show/hide password Checkbox
    mCBShowPass = (CheckBox) findViewById(R.id.check_show_pass);

    // add onCheckedListener on checkbox
    // when user clicks on this checkbox, this is the handler.
    mCBShowPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        // checkbox status is changed from uncheck to checked.
        if (!isChecked) {
          // show password
          mCBShowPass.setText(R.string.show_password);
          mPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
          // hide password
          mCBShowPass.setText(R.string.hide_password);
          mPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
      }
    });


  }

  public String getPrimaryEmail(Context context) {
    Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
    Account[] accounts = AccountManager.get(context).getAccounts();
    for (Account account : accounts) {
      if (emailPattern.matcher(account.name).matches()) {
        return account.name;
      }
    }
    return "";
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.login, menu);
    return true;
  }

  /**
   * Attempts to sign in or register the account specified by the login form.
   * If there are form errors (invalid email, missing fields, etc.), the
   * errors are presented and no actual login attempt is made.
   */
  public void attemptLogin() {
    if (mAuthTask != null) {
      return;
    }

    // Reset errors.
    mEmailView.setError(null);
    mPasswordView.setError(null);

    // Store values at the time of the login attempt.
    mEmail = mEmailView.getText().toString();
    mPassword = mPasswordView.getText().toString();

    boolean cancel = false;
    View focusView = null;

    // Check for a valid password.
    if (TextUtils.isEmpty(mPassword)) {
      mPasswordView.setError(getString(R.string.error_field_required));
      focusView = mPasswordView;
      cancel = true;
    } else if (mPassword.length() < 4) {
      mPasswordView.setError(getString(R.string.error_invalid_password));
      focusView = mPasswordView;
      cancel = true;
    }

    // Check for a valid email address.
    if (TextUtils.isEmpty(mEmail)) {
      mEmailView.setError(getString(R.string.error_field_required));
      focusView = mEmailView;
      cancel = true;
    } else if (!mEmail.contains("@")) {
      mEmailView.setError(getString(R.string.error_invalid_email));
      focusView = mEmailView;
      cancel = true;
    }

    if (cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    } else {
      // Show a progress spinner, and kick off a background task to
      // perform the user login attempt.
      mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
      showProgress(true);
      mAuthTask = new UserLoginTask();
      mAuthTask.execute((Void) null);
      hideKeyboard();
    }
  }

  private void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager) getSystemService(
            Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
  }

  /**
   * Shows the progress UI and hides the login form.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  private void showProgress(final boolean show) {
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    // comment
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

      mLoginStatusView.setVisibility(View.VISIBLE);
      mLoginStatusView.animate()
              .setDuration(shortAnimTime)
              .alpha(show ? 1 : 0)
              .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                  mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
              });

      mLoginFormView.setVisibility(View.VISIBLE);
      mLoginFormView.animate()
              .setDuration(shortAnimTime)
              .alpha(show ? 0 : 1)
              .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                  mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
              });
    } else {
      // The ViewPropertyAnimator APIs are not available, so simply show
      // and hide the relevant UI components.
      mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
  }

  private void startMainToggl() {
    Intent intent = new Intent(this, MainToggl.class);
    Gson gson = new Gson();
    intent.putExtra("user", gson.toJson(user));
    startActivity(intent);
  }

  /**
   * Represents an asynchronous login/registration task used to authenticate
   * the user.
   */
  public class UserLoginTask extends AsyncTask<Void, Void, Integer> {
    private static final int OK = 1;
    private static final int ERROR = -1;
    private static final int BAD_LOGIN = -2;

    @Override
    protected Integer doInBackground(Void... params) {
      // TODO: attempt authentication against a network service.
      try {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        RestAdapter restAdapter;
        restAdapter = new RestAdapter.Builder()
                .setServer("https://www.toggl.com/api/v8")
                .setRequestInterceptor(new ApiRequestInterceptor(mEmailView.getText().toString(), mPasswordView.getText().toString()))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(new NetworkErrorHandler())
                .setConverter(new GsonConverter(gson))
                .build();

        TogglService service = restAdapter.create(TogglService.class);
        user = service.login();
        Log.d("HEY", gson.toJson(user));
        SharedPreferences.Editor editor = credentials.edit();
        if (mCBRemenberMe.isChecked()) {
          editor.putString("mail", mEmailView.getText().toString());
          editor.putString("pass", mPasswordView.getText().toString());
          editor.putBoolean("remember", true);

        } else {
          editor.putBoolean("remember", false);
        }
        editor.putString("api_token", user.getData().getApiToken());
        editor.commit();
      } catch (Exception e) {
        Log.e("ERROR",e.getCause().getClass().getName());
        if (e.getCause().getClass().getName().equalsIgnoreCase("com.admios.exception.ForbiddenException")) {
          return UserLoginTask.BAD_LOGIN;
        }
        return UserLoginTask.ERROR;
      }


      // TODO: register the new account here.
      return UserLoginTask.OK;
    }

    @Override
    protected void onPostExecute(final Integer status) {
      mAuthTask = null;
      showProgress(false);

      if (status == UserLoginTask.OK) {
        startMainToggl();
      } else if (status == UserLoginTask.BAD_LOGIN) {
        mPasswordView.setText("");
        mPasswordView.setError(getString(R.string.error_incorrect_password));
        mPasswordView.requestFocus();
      } else if (status == UserLoginTask.ERROR) {
        mPasswordView.setText("");
        mPasswordView.setError("ERROR");
        mPasswordView.requestFocus();
      }
    }

    @Override
    protected void onCancelled() {
      mAuthTask = null;
      showProgress(false);
    }
  }
}
