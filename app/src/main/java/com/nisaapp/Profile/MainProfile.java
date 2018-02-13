package com.nisaapp.Profile;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;
import com.nisaapp.AdditionalFunctions.ImageRelated;
import com.nisaapp.Firebase;
import com.nisaapp.R;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.blurry.Blurry;

import static android.app.Activity.RESULT_OK;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by charu on 13-01-2018.
 */

public class MainProfile extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, OnSuccessListener<UploadTask.TaskSnapshot> {

    public boolean isMyProfile = FALSE;
    String profilePicPath = null;
    RelativeLayout relativeLayout;
    CircleImageView profilePic;
    TextView displayName;
    TextView aboutMe;
    Button myGallery;
    TextView add;
    ImageButton editProfile;
    GridView gridView;
    Bitmap originalPic;
    File f;
    int count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_new, container, false);

        CircleImageView addPictures = rootView.findViewById(R.id.addPictures);
        addPictures.setVisibility(View.GONE);
        addPictures.setOnClickListener(this);

        relativeLayout = rootView.findViewById(R.id.relativeLayout);
        profilePic = rootView.findViewById(R.id.profile_photo);
        profilePic.setOnClickListener(this);
        displayName = rootView.findViewById(R.id.displayName);
        displayName.setText(Firebase.displayName);
        aboutMe = rootView.findViewById(R.id.aboutMe);
        aboutMe.setText(Firebase.gender + "|" + Firebase.age + "\n" + Firebase.aboutMe);
        myGallery = rootView.findViewById(R.id.gallery);
        try {
            File file = new File(getActivity().getFileStreamPath("myImages"), "thumbs");
            File[] fileArray = file.listFiles();
            File profileThumb = null;
            for (int i = 0; i < fileArray.length; i++) {
                Log.w("File" + i, fileArray[i].toString());
                if (fileArray[i].toString().contains("profile.thumb")) {
                    profileThumb = fileArray[i];
                    Log.w("profileThumb", profileThumb.toString());
                }

                file = getActivity().getFileStreamPath("myImages");
                fileArray = file.listFiles();
                File profile;
                for (i = 0; i < fileArray.length; i++) {

                    if (fileArray[i].toString().contains("profile.png")) {
                        profile = fileArray[i];
                        profilePicPath = profile.toString();
                    }
                }
            }
            profilePic.setImageDrawable(Drawable.createFromPath(profileThumb.toString()));
            Log.w("SetImage", "This Ran");
        } catch (Exception e) {
            e.printStackTrace();
        }
        editProfile = rootView.findViewById(R.id.editProfile);
        editProfile.setVisibility(View.GONE);
        editProfile.setOnClickListener(this);
        add = rootView.findViewById(R.id.add);
        gridView = rootView.findViewById(R.id.gridView);
        File myImages = new File(getActivity().getFileStreamPath("myImages"), "thumbs");
        File[] photoList = myImages.listFiles();
        gridView.setAdapter(new GalleryAdapter(getActivity(), photoList));
        gridView.setOnItemClickListener(this);
        if (isMyProfile) {
            addPictures.setVisibility(View.VISIBLE);
            editProfile.setVisibility(View.VISIBLE);
            add.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.editProfile:
                EditProfile editProfile = new EditProfile();
                editProfile.editMode = TRUE;
                android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, editProfile).addToBackStack(null).commit();
                break;
            case R.id.profile_photo:
                popupPicture(profilePicPath);
                break;
            case R.id.addPictures:
                //TODO
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        File myImages = getActivity().getFileStreamPath("myImages");
        File[] photoList = myImages.listFiles();
        String path = photoList[i + 1].toString();
        popupPicture(path);

    }

    private void popupPicture(String path) {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.expanded_image, null);
        ImageView image = dialogView.findViewById(R.id.expandedImage);
        image.setImageDrawable(Drawable.createFromPath(path));
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        PopupWindow popup = new PopupWindow(dialogView, 900, 900);
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                MainProfile mainProfile = (MainProfile) getFragmentManager().findFragmentByTag("MY_PROFILE");
                getFragmentManager().beginTransaction().detach(mainProfile).attach(mainProfile).commit();
            }
        });
        Blurry.with(getActivity()).radius(10).sampling(1).color(Color.argb(80, 0, 0, 0)).onto(relativeLayout);
        popup.setBackgroundDrawable(new ColorDrawable(0x80000000));
        popup.setFocusable(TRUE);
        popup.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                f = new File(data.getData().toString());
                Log.w("Picked Image", data.getData().toString());

                originalPic = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                File file = new File(getActivity().getFileStreamPath("myImages"), f.getName());
                Uri uri = ImageRelated.writeBitmapToFile(originalPic, file);
                UploadTask task = Firebase.uploadFile(getActivity(), uri, "otherPics", false, false);
                task.addOnSuccessListener(this);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        if (count % 2 == 0) {
            count++;
            Firebase.progressDialog.dismiss();
            Bitmap thumb = ImageRelated.createThumb(originalPic, 450, true);
            File file = new File(getActivity().getFileStreamPath("myImages"), "thumbs/" + f.getName() + ".thumb");
            Uri uri = ImageRelated.writeBitmapToFile(thumb, file);
            UploadTask task = Firebase.uploadFile(getActivity(), uri, "otherPics", false, true);
            task.addOnSuccessListener(this);
        } else {
            count = 0;
            Firebase.progressDialog.dismiss();
            MainProfile mainProfile = (MainProfile) getFragmentManager().findFragmentByTag("MY_PROFILE");
            getFragmentManager().beginTransaction().detach(mainProfile).attach(mainProfile).commit();
        }
    }
}
