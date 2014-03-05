package com.admios.app;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.admios.model.TimeEntry;

import java.util.List;

/**
 * Created by yohendryhurtado on 3/5/14.
 */
public class TimeEntryAdapter extends ArrayAdapter<TimeEntry> {
  private Context context;
  private int layoutResourceId;
  List<TimeEntry> timeEntries;
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
    TimeEntry timeEntry = timeEntries.get(position);
    ImageView billiableImageView = (ImageView) convertView.findViewById(R.id.billiableImageView);
    TextView descripctionTextView = (TextView) convertView.findViewById(R.id.descriptionTextView);
    TextView durationTextView = (TextView) convertView.findViewById(R.id.durationTextView);

    if(timeEntry.isBilliable()) billiableImageView.setBackground(convertView.getResources().getDrawable(R.drawable.bill_on));
    else {
      billiableImageView.setBackground(convertView.getResources().getDrawable(R.drawable.bill_off));
    }
    descripctionTextView.setText(timeEntry.getDescription());
    durationTextView.setTag("Duration : "+timeEntry.getDuration());
    return convertView;

  }
}
