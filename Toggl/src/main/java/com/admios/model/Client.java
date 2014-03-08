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

  public boolean add(Project object) {
    return projects.add(object);
  }

  public boolean addAll(Collection<? extends Project> collection) {
    return projects.addAll(collection);
  }

  public void clear() {
    projects.clear();
  }

  public boolean contains(Object object) {
    return projects.contains(object);
  }

  @Override
  public boolean equals(Object object) {
    return projects.equals(object);
  }

  public Project get(int location) {
    return projects.get(location);
  }

  public boolean isEmpty() {
    return projects.isEmpty();
  }

  public Iterator<Project> iterator() {
    return projects.iterator();
  }

  public Project remove(int location) {
    return projects.remove(location);
  }
}
