package com.admios.network;

import com.admios.model.User;
import retrofit.http.GET;

/**
 * Created by yohendryhurtado on 3/3/14.
 */
public interface TogglService {
  @GET("/me")
  User login();
}
