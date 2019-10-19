package com.example.oandshooter.view;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.example.oandshooter.utils.MyData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MyHttpClient extends AsyncTask<Void,Void,String> {
    public MyCallback callback;
    Context context;
    public String result;
    String link;
    String[] data;
    public static String TAG = MyHttpClient.class.getSimpleName();



    public MyHttpClient(String link, String[] data)
    {
        this.context=MyData.context;
        this.link=link;
        this.data= data;

    }
    @Override
    protected String doInBackground(Void... params) {

        Log.e(TAG , "doInBackground");
        if(connectionCheck()) {


            HttpURLConnection httpurlconnection = null;
            OutputStream outputstream = null;
            BufferedWriter bufferedwriter = null;
            InputStream inputstream = null;
            try {
                URL url = new URL(link);
                httpurlconnection = (HttpURLConnection) url.openConnection();
                httpurlconnection.setRequestMethod("POST");
                httpurlconnection.setDoOutput(true);
                httpurlconnection.setDoInput(true);


                outputstream = httpurlconnection.getOutputStream();
                bufferedwriter = new BufferedWriter(new OutputStreamWriter(outputstream, "UTF-8"));
                String post_data = "";
                if (data != null) {

                    for (int i = 0; i < data.length; i += 2) {

                        if (i == 0) {
                            post_data = URLEncoder.encode(data[0], "UTF-8") + "=" + URLEncoder.encode(data[1], "UTF-8");
                            //+ "&"
                            //+ URLEncoder.encode("otp", "UTF-8") + "=" + URLEncoder.encode(str.toString(), "UTF-8");
                        } else
                            post_data += "&" + URLEncoder.encode(data[i], "UTF-8") + "=" + URLEncoder.encode(data[i + 1], "UTF-8");
                    }
                }
                //  URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(getIntent().getStringExtra("Username"), "UTF-8") + "&"
                // + URLEncoder.encode("otp", "UTF-8") + "=" + URLEncoder.encode(str.toString(), "UTF-8");

                bufferedwriter.write(post_data);
                bufferedwriter.flush();
                bufferedwriter.close();

                int status = httpurlconnection.getResponseCode();

                Log.e(TAG, "doInBackground Status : " + status);
                if (status != HttpURLConnection.HTTP_OK)
                    inputstream = httpurlconnection.getErrorStream();
                else
                    inputstream = httpurlconnection.getInputStream();

                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedreader.readLine()) != null) {
                    result += line;
                }
                bufferedreader.close();
                inputstream.close();
                httpurlconnection.disconnect();

                Log.e(TAG, "doInBackground Result : " + result);
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (httpurlconnection != null) httpurlconnection.disconnect();
                    if (outputstream != null) outputstream.close();
                    if (bufferedwriter != null) bufferedwriter.close();
                    if (inputstream != null) inputstream.close();
                } catch (Exception e) {
                    Log.e(TAG, "Resource Closeing issue");
                    e.printStackTrace();
                }
            }
        }
        else{
            Log.e(TAG,"Please Check Your Internet Connection");
        }
            return "";

    }


    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String result_http ) {
        Log.e(TAG , "onPostExecute : " );
        result=result_http;
        Log.e(TAG , "onPostExecute Result : " + result_http);
        onEvent();

    }

    void onEvent() {
        Log.e(TAG , "onEvent");
        callback.callbackCall();
    }
    private boolean connectionCheck(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;
        return connected;
    }

    // Toast.makeText(Splash.this, ""+username1, Toast.LENGTH_SHORT).show();
}