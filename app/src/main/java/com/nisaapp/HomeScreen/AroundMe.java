package com.nisaapp.HomeScreen;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.around_me, container, false);

        loading = rootView.findViewById(R.id.loading);


        listview = rootView.findViewById(R.id.list);
        listview.setVisibility(View.GONE);


        TextView noResults = rootView.findViewById(R.id.noResults);
        noResults.setVisibility(View.GONE);

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
            location.put("latitude", 17.5464411);
            location.put("longitude", 78.5718079);
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


        return rootView;
    }

    @Override
    public void processFinish(String output) {
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> photos = new ArrayList<>();
        try {
            JSONArray response = new JSONArray(output);
            for (int i = 0; i < response.length(); i++) {
                name.add(response.getJSONObject(i).getString("displayName"));
                photos.add(response.getJSONObject(i).getString("photoURL"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loading.setVisibility(View.GONE);
        AroundMeAdapter adapter = new AroundMeAdapter(getActivity(), name, photos);
        listview.setAdapter(adapter);
        listview.setVisibility(View.VISIBLE);

    }
}
