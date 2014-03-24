package com.admios.exception;

import retrofit.RetrofitError;

/**
 * Created by yohendryhurtado on 3/23/14.
 */
public class BadRequestException extends Exception {
  public BadRequestException(RetrofitError cause) {
    super(cause.getMessage(),cause);
  }
}
