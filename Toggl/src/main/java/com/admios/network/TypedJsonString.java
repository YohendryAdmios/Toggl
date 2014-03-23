package com.admios.network;

import retrofit.mime.TypedString;

/**
 * Created by yohendryhurtado on 3/23/14.
 */
public class TypedJsonString extends TypedString {
  public TypedJsonString(String wrap, String body) {
    super(String.format("{\"%s\":%s}",wrap,body));
  }

  @Override public String mimeType() {
    return "application/json";
  }
}