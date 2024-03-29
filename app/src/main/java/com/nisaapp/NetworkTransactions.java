package com.nisaapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.Charset;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class NetworkTransactions {

    private static String makeHttpRequest(URL url, String method, String data) throws IOException {
        Log.w("Internet Status", String.valueOf(isInternetAvailable()));
        String jsonResponse = "";


        HttpsURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setReadTimeout(60000 /* milliseconds */);
            urlConnection.setConnectTimeout(60000 /* milliseconds */);
            urlConnection.setRequestProperty("authorization", "NISA " + Firebase.idToken);
            //urlConnection.connect();
            if (method.equals("POST")) {
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod(method);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                writeToStream(urlConnection, data);
            } else {
                urlConnection.setRequestMethod(method);
            }
            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
        } catch (IOException e) {
            Log.e("Error", "Problem reaching the server.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static void writeToStream(HttpsURLConnection urlConnection, String data) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
        outputStreamWriter.write(data);
        outputStreamWriter.flush();
        Log.w("JSON", data);
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.toString().equals("");

        } catch (Exception e) {
            return false;
        }
    }

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public static class NetworkRequest extends AsyncTask<String, Void, String> {

        public AsyncResponse response = null;

        @Override
        protected String doInBackground(String... strings) {
            String response = null;
            try {
                response = makeHttpRequest(new URL(strings[0]), strings[1], strings[2]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            response.processFinish(s);
        }
    }

}
