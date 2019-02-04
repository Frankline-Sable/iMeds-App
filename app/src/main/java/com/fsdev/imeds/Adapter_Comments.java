package com.fsdev.imeds;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Frankline Sable on 03/12/2017.
 */


public class Adapter_Comments extends CursorAdapter {
    private final LayoutInflater cursorInflater;
    private TypeFace_Handler tp;

    public static class viewHolder {
        private TextView cName, cComm, cDate;
        private CircleImageView cImage;

        public viewHolder(View view) {
            cName = (TextView) view.findViewById(R.id.cName);
            cComm = (TextView) view.findViewById(R.id.cComment);
            cDate = (TextView) view.findViewById(R.id.cDate);
            cImage = (CircleImageView) view.findViewById(R.id.cImage);
        }
    }

    public Adapter_Comments(Context context, Cursor c, int flags) {
        super(context, c, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tp = new TypeFace_Handler(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.layout_commentor, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final viewHolder holder;
        holder = new viewHolder(view);
        Typeface typeface = tp.setFont("Roboto Light (Open Type).ttf");
        holder.cComm.setTypeface(typeface);
        holder.cDate.setTypeface(typeface);

        holder.cName.setTypeface(tp.setFont("HelveticaNeueMedium.ttf"));

        holder.cName.setText(cursor.getString(cursor.getColumnIndexOrThrow(Db_Forums.struct_forums.KEY_USERNAME)));
        holder.cComm.setText(cursor.getString(cursor.getColumnIndexOrThrow(Db_Forums.struct_forums.KEY_DESCRIPTION)));
        holder.cDate.setText(formatCommentsDate(cursor.getString(cursor.getColumnIndexOrThrow(Db_Forums.struct_forums.KEY_SCHEDULED))));

        String image = cursor.getString(cursor.getColumnIndexOrThrow(Db_Forums.struct_forums.KEY_IMAGE));
        if (image.length() > 10) {
            holder.cImage.setImageURI(Uri.parse(image));
        }
    }

    private String formatCommentsDate(String date) {
        SimpleDateFormat dateTimeFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date dateStart = null;

        try {
            dateStart = dateTimeFmt.parse(date);
        } catch (ParseException e) {
            return "???";
        }

        Calendar forumCalendar = Calendar.getInstance();
        forumCalendar.setTime(dateStart);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        return dateFormat.format(forumCalendar.getTime());
    }
}