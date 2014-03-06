package com.admios.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.admios.model.TimeEntry;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by yohendryhurtado on 3/5/14.
 */
public class TimeEntryAdapter extends ArrayAdapter<TimeEntry> {
  private Context context;
  private int layoutResourceId;
  List<TimeEntry> timeEntries;
  private int drawableId;
  private String billiable;
  private TimeEntry timeEntry;
  private ImageView billiableImageView;
  private TextView descripctionTextView;
  private TextView durationTextView;

  public TimeEntryAdapter(Context context, int layoutResourceId, List<TimeEntry> timeEntries) {
    super(context, layoutResourceId, timeEntries);
    this.context = context;
    this.layoutResourceId = layoutResourceId;
    this.timeEntries = timeEntries;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    if(convertView==null){
      // inflate the layout
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      convertView = inflater.inflate(layoutResourceId, parent, false);
    }

    // object item based on the position
    timeEntry = timeEntries.get(position);
    billiableImageView = (ImageView) convertView.findViewById(R.id.billiableImageView);
    descripctionTextView = (TextView) convertView.findViewById(R.id.descriptionTextView);
    durationTextView = (TextView) convertView.findViewById(R.id.durationTextView);

    drawableId = R.drawable.bill_off;
    billiable = context.getString(R.string.no_billiable);
    if(timeEntry.isBilliable()){
      billiable = context.getString(R.string.billiable);
      drawableId = R.drawable.bill_on;
    }
    Picasso.with(context).load(R.drawable.bill_on).into(billiableImageView);
    descripctionTextView.setText(timeEntry.getDescription());
    durationTextView.setText("Duration : "+(timeEntry.getDuration()/60/60)+ "Hrs");
    return convertView;

  }
}