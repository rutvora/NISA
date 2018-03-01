package com.nisaapp.HomeScreen;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.nisaapp.AdditionalFunctions.Location;
import com.nisaapp.AroundMeAdapter;
import com.nisaapp.NetworkTransactions;
import com.nisaapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class AroundMe extends android.app.Fragment implements NetworkTransactions.AsyncResponse {

    ProgressBar loading;
    ListView listview;
    TextView noResults;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.around_me, container, false);

        if (Location.isLocationAvailable(getActivity())) {

            Location.locationProvider.getLastLocation().addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                @Override
                public void onSuccess(android.location.Location location) {
                    getAroundMe(location.getLatitude(), location.getLongitude());
                }
            });
        } else {
            Log.w("Location: ", "Unavailable");
        }

        loading = rootView.findViewById(R.id.loading);

        listview = rootView.findViewById(R.id.list);
        listview.setVisibility(View.GONE);


        noResults = rootView.findViewById(R.id.noResults);
        noResults.setVisibility(View.GONE);

        return rootView;
    }

    @Override
    public void processFinish(String output) {
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> photos = new ArrayList<>();
        try {
            JSONArray response = new JSONArray(output);
            if (response.length() > 0) {
                for (int i = 0; i < response.length(); i++) {
                    name.add(response.getJSONObject(i).getString("displayName"));
                    photos.add(response.getJSONObject(i).getString("photoURL"));
                }
                loading.setVisibility(View.GONE);
                AroundMeAdapter adapter = new AroundMeAdapter(getActivity(), name, photos);
                listview.setAdapter(adapter);
                listview.setVisibility(View.VISIBLE);
            } else {
                loading.setVisibility(View.GONE);
                noResults.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void getAroundMe(double latitude, double longitude) {
        Uri uri = new Uri.Builder().scheme("https").authority("us-central1-nisa-anspd.cloudfunctions.net").path("/app/aroundme").build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JSONObject updateLocation = new JSONObject();
        try {
            JSONObject location = new JSONObject();
            location.put("latitude", latitude);
            location.put("longitude", longitude);
            updateLocation.put("location", location);
            updateLocation.put("radius", 5);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (url != null) {
            NetworkTransactions.NetworkRequest request = new NetworkTransactions.NetworkRequest();
            request.response = this;
            request.execute(url.toString(), "POST", updateLocation.toString());
        }
    }
}
