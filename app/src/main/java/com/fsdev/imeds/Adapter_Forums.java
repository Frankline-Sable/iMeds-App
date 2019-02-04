package com.fsdev.imeds;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Frankline Sable on 04/12/2017...On resource
 */

public class Adapter_Forums extends CursorAdapter{
    private final LayoutInflater cursorInflater;
    private TypeFace_Handler tp;
    private viewHolder holder=null;

    private final class viewHolder{
        private TextView titleView, descView, userView, timeView;
        private CircleImageView userImage;

        public viewHolder(View view) {
            titleView = (TextView) view.findViewById(R.id.forumTitle);
            descView = (TextView) view.findViewById(R.id.forumDesc);
            userView = (TextView) view.findViewById(R.id.forumUser);
            timeView = (TextView) view.findViewById(R.id.forumTime);
            userImage = (CircleImageView) view.findViewById(R.id.forumImage);
        }
    }

    public Adapter_Forums(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tp = new TypeFace_Handler(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.layout_forum, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        holder = new viewHolder(view);
        holder.userView.setText(cursor.getString(cursor.getColumnIndexOrThrow(Db_Forums.struct_forums.KEY_USERNAME)));
        holder.titleView.setText(cursor.getString(cursor.getColumnIndexOrThrow(Db_Forums.struct_forums.KEY_TITLE)));
        holder.descView.setText(cursor.getString(cursor.getColumnIndexOrThrow(Db_Forums.struct_forums.KEY_DESCRIPTION)));
        holder.timeView.setText(cursor.getString(cursor.getColumnIndexOrThrow(Db_Forums.struct_forums.KEY_ViewTime)));

        holder.userView.setTypeface(tp.setFont("Roboto-Medium.ttf"));
        holder.titleView.setTypeface(tp.setFont("Roboto-Regular.ttf"));
        holder.descView.setTypeface(tp.setFont("Roboto-Light.ttf"));

        String profileImageUri=cursor.getString(cursor.getColumnIndexOrThrow(Db_Forums.struct_forums.KEY_IMAGE));
        if (profileImageUri.length() < 10) {
            holder.userImage.setImageResource(R.drawable.ic_account_circle_black_48dp);
        } else {
            holder.userImage.setImageURI(Uri.parse(profileImageUri));
        }
    }
}