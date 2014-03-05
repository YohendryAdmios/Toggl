package com.admios.app;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.admios.model.User;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MainToggl extends ActionBarActivity implements ActionBar.OnNavigationListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
  private User user;
  private MenuItem mi;

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_toggl);

      user = getUser();
      this.setTitle(user.getData().getFullname());



    }

    private User getUser(){
      Gson gson = new Gson();
      return gson.fromJson(getIntent().getStringExtra("user"), User.class);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.main_toggl, menu);

      mi = (MenuItem)menu.findItem(R.id.action_profile);
      LoadImageTask lit = new LoadImageTask();
      lit.execute((Void)null);
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
        Log.e("HEY",e.getMessage());
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(Drawable drawable) {
      if(drawable != null){
        changeProfileData(drawable);
      }
    }
  }


  private void changeProfileData(Drawable drawable) {
    mi.setIcon(drawable);
    mi.setTitle(user.getData().getFullname());
  }
}
