package com.admios.network;

import com.admios.exception.ForbiddenException;
import com.admios.exception.UnauthorizedException;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by yohendryhurtado on 3/4/14.
 */
public class NetworkErrorHandler implements ErrorHandler {
  @Override
  public Throwable handleError(RetrofitError cause){
    Response r = cause.getResponse();
    if (r != null && r.getStatus() == 403) {
      return new ForbiddenException(cause);
    }
    return cause;
  }
}
