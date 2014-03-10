package com.admios.app.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.admios.app.R;
import com.admios.model.TimeEntry;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by yohendryhurtado on 3/5/14.
 */
public class TimeEntryAdapter extends ArrayAdapter<TimeEntry> {
  private Context context;
  List<TimeEntry> timeEntries;
  private TimeEntry timeEntry;
  private ImageView billiableImageView;
  private TextView descripctionTextView;
  private TextView durationTextView;

  private static final int TYPE_ITEM = 0;
  private static final int TYPE_SEPARATOR = 1;
  private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

  private TreeSet mSeparatorsSet = new TreeSet();
  private LayoutInflater inflater;
  private TextView dateTextView;
  private TextView totalTextView;

  public TimeEntryAdapter(Context context, int layoutResourceId) {
    super(context, layoutResourceId);
    this.context = context;
    int layoutResourceId1 = layoutResourceId;
    this.timeEntries = new ArrayList<TimeEntry>();
    inflater = ((Activity) context).getLayoutInflater();
  }

  public void addTimeEntry(final TimeEntry item) {
    timeEntries.add(item);
    notifyDataSetChanged();
  }

  public void addSeparatorItem(final TimeEntry item) {
    timeEntries.add(item);
    // save separator position
    mSeparatorsSet.add(timeEntries.size() - 1);
    notifyDataSetChanged();
  }

  @Override
  public int getItemViewType(int position) {
    return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
  }

  @Override
  public int getViewTypeCount() {
    return TYPE_MAX_COUNT;
  }

  @Override
  public int getCount() {
    return timeEntries.size();
  }

  @Override
  public TimeEntry getItem(int position) {
    return timeEntries.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    int type = getItemViewType(position);

    // inflate the layout
    switch (type) {
      case TYPE_ITEM:
        convertView = createItem(position, convertView, parent);
        break;
      case TYPE_SEPARATOR:
        convertView = createSeparator(position, convertView, parent);
        break;
    }

    return convertView;

  }

  private View createItem(int position, View convertView, ViewGroup parent) {
    convertView = inflater.inflate(R.layout.time_entry_item, parent, false);
    // object item based on the position
    timeEntry = timeEntries.get(position);
    billiableImageView = (ImageView) convertView.findViewById(R.id.billiableImageView);
    descripctionTextView = (TextView) convertView.findViewById(R.id.descriptionTextView);
    durationTextView = (TextView) convertView.findViewById(R.id.durationTextView);

    int drawableId;
    drawableId = R.drawable.bill_off;
    if (timeEntry.isBilliable()) {
      drawableId = R.drawable.bill_on;
    }
    Picasso.with(context).load(R.drawable.bill_on).into(billiableImageView);
    descripctionTextView.setText(timeEntry.getDescription());

    durationTextView.setText("Fecha : " +
              DateUtil.toShortDate(DateUtil.parseLongDate(timeEntry.getStart())) + " Duration : " + (timeEntry.getDuration() / 60 / 60) + "Hrs");
    return convertView;
  }

  private View createSeparator(int position, View convertView, ViewGroup parent) {
    convertView = inflater.inflate(R.layout.time_entry_separator, parent, false);
    // object item based on the position
    timeEntry = timeEntries.get(position);
    dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);
    dateTextView.setText(DateUtil.toShortDate(DateUtil.parseLongDate(timeEntry.getStart())));
    totalTextView = (TextView) convertView.findViewById(R.id.durationTextView);
    totalTextView.setText("Total : " + (timeEntry.getDuration() / 60 / 60) + "Hrs");


    return convertView;
  }

  private String formatDate(Date date) {
    return new SimpleDateFormat("MM-dd-yyyy").format(date);
  }
}
