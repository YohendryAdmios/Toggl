package com.admios.network;

import android.util.Base64;

import retrofit.RequestInterceptor;

/**
 * Created by yohendryhurtado on 3/3/14.
 */
public class ApiRequestInterceptor implements RequestInterceptor {
  private String username;
  private String password;

  public ApiRequestInterceptor(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }


  public String getUsername() {
    return username;
  }

  @Override
  public void intercept(RequestFacade requestFacade) {

   final String authorizationValue = encodeCredentialsForBasicAuthorization();
   requestFacade.addHeader("Authorization", authorizationValue);
  }

  private String encodeCredentialsForBasicAuthorization() {
    final int flags = 0;
    return "Basic " + Base64.encodeToString((getUsername() + ":" + getPassword()).getBytes(), flags);
  }

}
