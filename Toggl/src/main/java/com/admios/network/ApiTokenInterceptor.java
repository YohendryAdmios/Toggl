package com.admios.network;

import android.util.Base64;

import retrofit.RequestInterceptor;

/**
 * Created by yohendryhurtado on 3/4/14.
 */
public class ApiTokenInterceptor implements RequestInterceptor  {
  private String apiToken;

  public String getApiToken() {
    return apiToken;
  }

  public void setApiToken(String apiToken) {
    this.apiToken = apiToken;
  }

  @Override
  public void intercept(RequestInterceptor.RequestFacade requestFacade) {

    final String authorizationValue = encodeCredentialsForBasicAuthorization();
    requestFacade.addHeader("Authorization", authorizationValue);
  }

  private String encodeCredentialsForBasicAuthorization() {
    final String userAndPassword = getApiToken()+":api_token";
    final int flags = 0;
    return "Basic " + Base64.encodeToString(userAndPassword.getBytes(), flags);
  }
}
