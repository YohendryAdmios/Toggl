package main.java.com.admios.network;

import java.util.List;

import main.java.com.admios.model.User;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by yohendryhurtado on 3/3/14.
 */
public interface TogglService {
  @GET("/me")
  User login(@Path("user") String user,String pass);
}
