package com.nisaapp;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.nisaapp.AdditionalFunctions.Location;
import com.nisaapp.HomeScreen.HomeScreen;
import com.nisaapp.Profile.MainProfile;

import java.io.File;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class MainActivity extends Activity implements OnCompleteListener<DocumentSnapshot> {


    boolean allPermissionsGranted = TRUE;
    boolean needExplanation = FALSE;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        int i;
        boolean b = TRUE;
        for (i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) b = FALSE;
        }

        // If request is cancelled, the result arrays are empty.
        if (b) {

            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS && GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE > 11000)
                launch();

            else
                Toast.makeText(this, "Please install/update Google Play Services", Toast.LENGTH_LONG).show();

        } else {
            //Take Lite
        }
        return;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
        //////////////

        checkPermissions();

    }

    private void checkPermissions() {

        String[] permissions = new String[]{android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_SMS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION};

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this,
                    permissions[i])
                    != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = FALSE;
                break;
            }
        }
        for (int i = 0; i < permissions.length; i++) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permissions[i])) {
                needExplanation = TRUE;
                break;
            }
        }
        //Check if the app has all permissions
        if (allPermissionsGranted == FALSE) {

            // Should we show an explanation?
            if (needExplanation) {
                Toast toast = Toast.makeText(this, "Need permissions for OTP verification", Toast.LENGTH_SHORT);
                toast.show();

                ActivityCompat.requestPermissions(this,
                        permissions,
                        1);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        permissions,
                        0);
            }
        } else {
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS && GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE > 11000) {
                launch();

            } else
                Toast.makeText(this, "Please install/update Google Play Services", Toast.LENGTH_LONG).show();
        }

    }

    private void launch() {
        File file = this.getFileStreamPath("myImages");
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(this.getFileStreamPath("myImages"), "thumbs");
        if (!file.exists()) {
            file.mkdir();
        }

        Location.locationProvider = LocationServices.getFusedLocationProviderClient(this);

        Firebase.auth = FirebaseAuth.getInstance();

        getFragmentManager().beginTransaction().replace(R.id.fragment, new SplashScreen()).commit();
        Firebase.progressDialog = new ProgressDialog(this);
        Firebase.progressDialog.setCanceledOnTouchOutside(false);
        Firebase.progressDialog.setTitle("Status");
        Firebase.progressDialog.setMessage("Fetching your data...");
        Firebase.progressDialog.show();

        Firebase.activity = this;
        Firebase.LaunchAsyncTask task = new Firebase.LaunchAsyncTask();
        task.execute(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.myProfile:
                MainProfile mainProfile = new MainProfile();
                mainProfile.isMyProfile = TRUE;
                getFragmentManager().beginTransaction().replace(R.id.fragment, mainProfile, "MY_PROFILE").addToBackStack("myProfile").commit();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {

        MainProfile mainProfile = (MainProfile) getFragmentManager().findFragmentByTag("MY_PROFILE");
        if (mainProfile != null && mainProfile.isVisible()) {
            Log.w("here", "Yes");
            //HomeScreen homeScreen = new HomeScreen();
            //getFragmentManager().beginTransaction().replace(R.id.fragment,homeScreen).commit();
            this.getFragmentManager().popBackStack("myProfile", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if (task.isSuccessful()) {
            Firebase.aboutMe = task.getResult().getString("About Me");
            Firebase.age = (long) task.getResult().get("Age");
            Firebase.gender = task.getResult().getString("Gender");
            Firebase.progressDialog.dismiss();
            HomeScreen homeScreen = new HomeScreen();
            getFragmentManager().beginTransaction().replace(R.id.fragment, homeScreen).commit();
        } else {
            Toast.makeText(this, task.getException().toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
