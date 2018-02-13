package com.nisaapp.Profile;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nisaapp.R;

import java.io.File;

public class GalleryAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Context context;
    File[] file;

    public GalleryAdapter(Context receivedContext, File[] file) {
        // TODO Auto-generated constructor stub
        this.file = file;
        context = receivedContext;

        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated methodAbout stub
        return file.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated methodAbout stub
        return file[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated methodAbout stub
        return 1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated methodAbout stub
        View rowView;
        rowView = inflater.inflate(R.layout.my_gallery_listitem, null);
        ImageView i = rowView.findViewById(R.id.Image);
        i.setLayoutParams(new GridView.LayoutParams(450, 450));
        i.setScaleType(ImageButton.ScaleType.FIT_XY);
        i.setImageDrawable(Drawable.createFromPath(file[position].toString()));
        return rowView;
    }

}