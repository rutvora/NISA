package com.nisaapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nisaapp.Authentication.PhoneVerification;

import java.io.File;
import java.util.Map;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class Firebase {

    public static FirebaseAuth auth;
    public static String UID;
    public static FirebaseUser currentUser;
    public static String idToken;
    public static FirebaseFirestore db;
    public static StorageReference storageReference;
    public static String displayName;
    public static String aboutMe;
    public static long age;
    public static String gender;


    public static ProgressDialog progressDialog;
    static Activity activity;

    public static UploadTask uploadFile(final Context context, Uri file, String folder, boolean isProPic, boolean isThumb) {
        UploadTask task = null;
        if (file != null) {

            Log.w("Uploading file", "Yes");
            progressDialog = new ProgressDialog(context);
            progressDialog.setCanceledOnTouchOutside(false);
            //displaying a progress dialog while upload is going on
            progressDialog.setTitle("Status");
            progressDialog.show();

            File f = new File(file.toString());
            String child;
            if (isProPic && isThumb) child = folder + "/" + Firebase.UID + "/profile.thumb";
            else if (isProPic && !isThumb) child = folder + "/" + Firebase.UID + "/profile.jpg";
            else child = folder + "/" + Firebase.UID + "/" + file.getLastPathSegment();
            StorageReference storageReference = Firebase.storageReference.child(child);
            task = storageReference.putFile(file);
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //if the upload is not successful
                    //hiding the progress dialog
                    progressDialog.dismiss();

                    //and displaying error message
                    Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    //displaying percentage in progress dialog
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                }
            });

        }
        return task;
    }

    public static void updateDB(Map<String, Object> map, OnCompleteListener<Void> listener) {
        Firebase.db.collection("user_profile")
                .document(Firebase.UID)
                .set(map, SetOptions.merge())
                .addOnCompleteListener(listener);

    }

    public static class LaunchAsyncTask extends AsyncTask<OnCompleteListener<DocumentSnapshot>, Void, Integer> {

        @Override
        protected Integer doInBackground(OnCompleteListener<DocumentSnapshot>... listeners) {

            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            final OnCompleteListener<DocumentSnapshot> listener = listeners[0];


            if (currentUser == null) {
                return -1;

            } else {
                currentUser.getIdToken(true)
                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                Log.w("Test", "Till here");
                                if (task.isSuccessful()) {
                                    Firebase.idToken = task.getResult().getToken();
                                    Log.w("Token", "idToken = " + Firebase.idToken);
                                    Firebase.storageReference = FirebaseStorage.getInstance().getReference();
                                    Firebase.db = FirebaseFirestore.getInstance();
                                    Firebase.displayName = Firebase.currentUser.getDisplayName();
                                    Firebase.UID = Firebase.currentUser.getUid();
                                    db.setFirestoreSettings(new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build());
                                    db.collection("user_profile").document(Firebase.UID).get().addOnCompleteListener(listener);

                                    // Send token to your backend via HTTPS
                                    // ...
                                } else {
                                    Toast.makeText(activity, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }


            return null;
        }


        @Override
        protected void onPostExecute(Integer result) {
            if (result != null && result == -1) {
                Firebase.progressDialog.dismiss();
                PhoneVerification verify = new PhoneVerification();
                activity.getFragmentManager().beginTransaction().replace(R.id.fragment, verify).commit();
            }
        }

    }
}

/*
Firebase.age = Long.parseLong(age.getText().toString());
                        Firebase.gender = gender.getSelectedItem().toString();
                        Firebase.aboutMe = aboutMe.getText().toString();
                        Firebase.displayName = name.getText().toString();
                        Firebase.progressDialog.dismiss();
                        HomeScreen homeScreen = new HomeScreen();
                        getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment, homeScreen).commit();
 */