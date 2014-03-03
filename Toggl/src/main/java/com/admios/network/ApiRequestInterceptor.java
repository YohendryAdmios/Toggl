package main.java.com.admios.network;

import android.util.Base64;

import retrofit.RequestInterceptor;

/**
 * Created by yohendryhurtado on 3/3/14.
 */
public class ApiRequestInterceptor implements RequestInterceptor{

  @Override
  public void intercept(RequestFacade requestFacade) {

   final String authorizationValue = encodeCredentialsForBasicAuthorization();
   requestFacade.addHeader("Authorization", authorizationValue);
  }

  private String encodeCredentialsForBasicAuthorization() {
    final String userAndPassword = "yohendry.hurtado@admios-sa.com:jyy212629";
    final int flags = 0;
    return "Basic " + Base64.encodeToString(userAndPassword.getBytes(), flags);
  }

}
