package com.admios.network;

import com.admios.model.Client;
import com.admios.model.Project;
import com.admios.model.TimeEntry;
import com.admios.model.TimeEntryWraper;
import com.admios.model.User;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by yohendryhurtado on 3/3/14.
 */
public interface TogglService {
  @GET("/me")
  User login();

  @GET("/time_entries")
  List<TimeEntry> timeEntries(@Query("start_date") String start, @Query("end_date") String end);

  @GET("/clients")
  List<Client> getClients();

  @GET("/clients/{client_id}/projects")
  List<Project> getProjectsByClient(@Path("client_id") int clientId);

  @PUT("/time_entries/{time_entry_id}")
  TimeEntryWraper updateTimeEntry(@Path("time_entry_id") int timeEntryId,@Body TypedJsonString body);

  @DELETE("/time_entries/{time_entry_id}")
  TimeEntryWraper deleteTimeEntry(@Path("time_entry_id") int timeEntryId);

  @POST("/time_entries")
  TimeEntryWraper createTimeEntry(@Body TypedJsonString body);

}
