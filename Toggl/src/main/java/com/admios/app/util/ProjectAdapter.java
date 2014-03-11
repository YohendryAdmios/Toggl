package com.admios.app.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.admios.app.R;
import com.admios.model.Project;

import java.util.List;

/**
 * Created by yohendryhurtado on 3/7/14.
 */
public class ProjectAdapter extends ArrayAdapter<Project> {
  private final Context context;
  private final int layoutResourceId;
  private final List<Project> projects;
  private final LayoutInflater inflater;

  public ProjectAdapter(Context context, int resource, List<Project> objects) {
    super(context, resource, R.id.projectName, objects);
    this.context = context;
    this.layoutResourceId = resource;
    this.projects = objects;
    inflater = ((Activity) context).getLayoutInflater();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    int type = getItemViewType(position);

    // inflate the layout

    convertView = createItem(position, convertView, parent);
    return convertView;
  }

  private View createItem(int position, View convertView, ViewGroup parent) {
    convertView = inflater.inflate(R.layout.project_item, parent, false);
    Project project = projects.get(position);
    ((TextView) convertView.findViewById(R.id.projectName)).setText(project.getName());
    ((TextView) convertView.findViewById(R.id.clientName)).setText(project.getClientName());
    return convertView;
  }
}
