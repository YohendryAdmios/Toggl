package com.admios.app.util;

import android.content.Context;
import android.util.Log;

import com.admios.model.Client;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by yohendryhurtado on 3/12/14.
 */
public class Writer {
  private Gson gson;
  private Context context;
  public Writer(Context context) {
    this.context = context;
    gson = new Gson();
  }

  public void writeClients(List<Client> clients){
    String clintString = gson.toJson(clients);
    writeToFile("clients.json ",clintString);
    Log.d("HEY",String.format("writting clients %s on %s",clintString,context.getFilesDir()));
  }

  public List<Client> readClients() throws IOException {
    String clients_s = readFromFile("clients.json ");
    Type collectionType = new TypeToken<List<Client>>() {
    }.getType();


    return gson.fromJson(clients_s,collectionType);

  }

  private void writeToFile(String name, String data) {
    try {

      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(name,context.MODE_PRIVATE));
      outputStreamWriter.write(data);
      outputStreamWriter.close();

    }
    catch (IOException e) {
      Log.e("HEY", "File write failed: " + e.toString());
    }
  }

  private String readFromFile(String name) throws IOException {

    String ret = "";

      InputStream inputStream =context.openFileInput(name);

      if ( inputStream != null ) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String receiveString = "";
        StringBuilder stringBuilder = new StringBuilder();

        while ( (receiveString = bufferedReader.readLine()) != null ) {
          stringBuilder.append(receiveString);
        }

        inputStream.close();
        ret = stringBuilder.toString();
      }

    return ret;
  }
}
