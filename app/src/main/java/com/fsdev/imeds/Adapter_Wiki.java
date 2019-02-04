package com.fsdev.imeds;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Frankline Sable on 28/11/2017.
 */

public class Adapter_Wiki extends CursorAdapter {
    private final LayoutInflater cursorInflater;
    private TypeFace_Handler tp;

    public static class viewHolder {
        private TextView descView;
        private ImageView viewedImage;
        public viewHolder(View view) {
            descView=(TextView) view.findViewById(R.id.wikiDesc);
            viewedImage=(ImageView)view.findViewById(R.id.viewedImage);
        }
    }

    public Adapter_Wiki(Context context, Cursor c, int flags) {
        super(context, c, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tp = new TypeFace_Handler(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.layout_wikis, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final viewHolder holder;
        holder = new viewHolder(view);
        holder.descView = (TextView) view.findViewById(R.id.wikiDesc);
        holder.descView.setText(cursor.getString(cursor.getColumnIndexOrThrow(Db_Wiki.struct_wiki.KEY_TITLE)));
        Typeface typeface=tp.setFont("Roboto Light (Open Type).ttf");
        holder.descView.setTypeface(typeface);

        if(cursor.getInt(cursor.getColumnIndexOrThrow(Db_Events.struct_events.KEY_VIEWED ))<1){
            holder.viewedImage.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
            holder.descView.setAlpha(1f);
            holder.viewedImage.setAlpha(1f);
        }else {
            holder.viewedImage.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
            holder.descView.setAlpha(.5f);
            holder.viewedImage.setAlpha(.5f);
        }
    }
}





/*
public class Adapter_Wiki extends ArrayAdapter<Model_Wiki> {
    private final Activity mContext;
    private final List<Model_Wiki> WikiList;

    public static class viewHolder {
        private TextView descView;
    }

    public Adapter_Wiki(Activity mContext, List<Model_Wiki> WikiList) {
        super(mContext, R.layout.layout_wikis, WikiList);
        this.mContext = mContext;
        this.WikiList = WikiList;
    }

    @NonNull
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final viewHolder holder;

        if (view == null) {
            holder = new viewHolder();
            LayoutInflater inflater = mContext.getLayoutInflater();
            view = inflater.inflate(R.layout.layout_wikis, null);

            holder.descView = (TextView) view.findViewById(R.id.wikiDesc);
            view.setTag(holder);
        } else {
            holder = (viewHolder) view.getTag();
        }

        Model_Wiki Wiki = WikiList.get(position);
        holder.descView.setText(Wiki.getDescription());

        return view;
    }

    @Override
    public int getCount() {
        return WikiList.size();
    }
}
*/
