package com.admios.exception;

import retrofit.RetrofitError;

/**
 * Created by yohendryhurtado on 3/4/14.
 */
public class ForbiddenException extends Exception {
  public ForbiddenException(RetrofitError retrofitError) {
    super(retrofitError.getMessage());
  }
}
