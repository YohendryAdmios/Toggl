package com.admios.model;

/**
 * Created by yohendryhurtado on 3/7/14.
 */
public class Project {
  private int id;
  private String name;
  private boolean billable;
  private String clientName;

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

  public boolean isBillable() {
    return billable;
  }

  public void setBillable(boolean billable) {
    this.billable = billable;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientName() {
    return clientName;
  }

  @Override
  public String toString() {
    return String.format("%s - %s",this.getClientName(),this.getName());
  }
}
