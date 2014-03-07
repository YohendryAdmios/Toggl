package com.admios.network;

import com.admios.model.TimeEntry;
import com.admios.model.User;

import java.util.Date;
import java.util.List;

import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by yohendryhurtado on 3/3/14.
 */
public interface TogglService {
  @GET("/me")
  User login();

  @GET("/time_entries")
  List<TimeEntry> timeEntries(@Query("start_date") String start,@Query("end_date") String end);

}
