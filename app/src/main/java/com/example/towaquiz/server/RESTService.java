package com.example.towaquiz.server;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class RESTService extends AsyncTask<String, String, String> {

    //params order
    private static final int URL = 0;
    private static final int METHOD = 1;
    private static final int QUERY = 2;

    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;

    @Override
    protected String doInBackground(String... strings) {
        String result = "";
        String inputLine;

        HttpURLConnection conn = this.composeRequest(strings);

        //DBService.add(conn);

        try {
            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) { // connection ok
                //Create a new InputStreamReader
                InputStreamReader streamReader = new InputStreamReader(conn.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);

                    Log.i("result line", inputLine);

                }

                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            }else if(responseCode == HttpURLConnection.HTTP_NOT_FOUND ||
                    responseCode == HttpURLConnection.HTTP_BAD_REQUEST){
                result = "404";
            }
            else if(responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR){
                result = "500";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;
    }

    private HttpURLConnection composeRequest(String... strings){
        URL urlObj;
        HttpURLConnection connection = null;

        String url = strings[URL];
        String method = strings.length > 1 ? strings[METHOD] : "GET";
        String query = strings.length > 2 ? strings[QUERY] : "";

        String composedUrl = url + "?" + query;

        try {
            urlObj = new URL(composedUrl);

            connection = (HttpURLConnection) urlObj.openConnection();

            connection.setRequestMethod(method);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    return connection;
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
    }

}
