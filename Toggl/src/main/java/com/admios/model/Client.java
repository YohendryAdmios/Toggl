package com.admios.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yohendryhurtado on 3/7/14.
 */
public class Client {
  private int id;
  private String name;
  private List<Project> projects;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setProjects(List<Project> projects) {
    this.projects = projects;
  }

  public List<Project> getProjects() {
    return projects;
  }

  @Override
  public String toString() {
    return this.getName();
  }
}
