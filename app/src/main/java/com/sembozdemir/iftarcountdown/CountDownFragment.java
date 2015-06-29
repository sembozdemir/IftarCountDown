package com.sembozdemir.iftarcountdown;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;


/**
 * A placeholder fragment containing a simple view.
 */
public class CountDownFragment extends Fragment {
    private static final String LOG_TAG = CountDownFragment.class.getSimpleName();

    TextView timerTextView;
    CountDown countDown;
    FetchIftarTimeTask fetchIftarTimeTask;

    public CountDownFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        timerTextView = (TextView) rootView.findViewById(R.id.timerTextView);
        fetchIftarTimeTask = new FetchIftarTimeTask();
        String[] params = { String.valueOf(false), "izmir" };
        fetchIftarTimeTask.execute(params);

        return rootView;
    }

    private class FetchIftarTimeTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            Boolean autoLocation = Boolean.valueOf(params[0]);
            String city = params[1];

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String jsonStr = null;

            try {
                // Construct the URL for the MuslimSalat.com query
                URL url;
                if (autoLocation) {
                    url = new URL("http://muslimsalat.com/daily.json?41ea0b7ca953568a6a5ab7836ac8cf47");
                } else {
                    final String MUSLIM_SALAT_BASE_URL = "http://muslimsalat.com/";
                    final String PARAM_CITY = city;
                    final String SUFFIX = ".json?41ea0b7ca953568a6a5ab7836ac8cf47";
                    url = new URL(MUSLIM_SALAT_BASE_URL + PARAM_CITY + SUFFIX);
                }


                // Create the request to MuslimSalat.com, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getIftarTimeDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        private String getIftarTimeDataFromJson(String jsonStr) throws JSONException{

            // Parse iftar time from JSON
            if (jsonStr == null) {
                return "";
            }

            final String MS_FIVE_TIMES = "items";
            final String MS_IFTAR = "maghrib";
            final String MS_DATE = "date_for";

            JSONObject muslimSalatDataJson = new JSONObject(jsonStr);
            JSONArray fivetimesArray = muslimSalatDataJson.getJSONArray(MS_FIVE_TIMES);
            JSONObject oneDayJson = fivetimesArray.getJSONObject(0);
            String iftarClock = oneDayJson.getString(MS_IFTAR);
            String today = oneDayJson.getString(MS_DATE);

            String iftarTime = today + " " + iftarClock;
            Log.d(LOG_TAG, iftarTime);

            return iftarTime;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                MyTime iftarTime = new MyTime(s);
                MyTime now = new MyTime(System.currentTimeMillis());
                countDown = new CountDown(getActivity().getApplicationContext(), timerTextView, iftarTime);
                countDown.setTimer(now);
                countDown.start();
            } catch (ParseException e) {
                Log.e(LOG_TAG, e.toString());
            }
        }
    }
}
