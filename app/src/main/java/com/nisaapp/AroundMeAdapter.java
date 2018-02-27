package com.nisaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class AroundMeAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    ArrayList<String> name;
    Context context;
    ArrayList<String> email;
    ArrayList<String> picture;
    ArrayList<Integer> id;

    public AroundMeAdapter(Context receivedContext, ArrayList<String> name, ArrayList<String> imageResource) {

        this.name = name;
        context = receivedContext;
        picture = imageResource;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

        return name.size();
    }

    @Override
    public Object getItem(int position) {

        return position;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView;
        rowView = inflater.inflate(R.layout.contact_row_item, null);
        TextView name = rowView.findViewById(R.id.name);
        //TextView email = rowView.findViewById(R.id.email);
        CircleImageView pic = rowView.findViewById(R.id.picture);
        name.setText(this.name.get(position));
        //email.setText(this.email.get(position));
        pic.setImageResource(android.R.drawable.ic_menu_camera);
        if (picture.get(position) != null) {
            Picasso.with(context).load(this.picture.get(position)).into(pic);
        }
        //holder.img.setImageResource(imageId[position]);
        return rowView;
    }

}