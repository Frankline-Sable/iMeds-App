package com.fsdev.imeds;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Frankline Sable on 28/11/2017.
 */

public class Adapter_Topics extends RecyclerView.Adapter<Adapter_Topics.ViewHolder> {

    private List<Model_Topics> topicsLists;
    private Context mContext;
    private TypeFace_Handler tp;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, subInfo, topicsCozunt;
        private RelativeLayout topicsContainer;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.topicTitle);
            subInfo = (TextView) v.findViewById(R.id.subInfo);
            topicsCount = (TextView) v.findViewById(R.id.topicCount);
            topicsContainer = (RelativeLayout) v.findViewById(R.id.topicsContainer);
        }
    }

    public Adapter_Topics(List<Model_Topics> topicsLists, Context mContext) {
        this.topicsLists = topicsLists;
        this.mContext = mContext;
        tp = new TypeFace_Handler(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View topicsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_wiki_topics, parent, false);
        return new ViewHolder(topicsView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Typeface typeface=tp.setFont("Roboto Light (Open Type).ttf");
        Model_Topics topics = topicsLists.get(position);
        //holder.title.setTypeface(tp.setFont("Helvetica Neue Light (Open Type).ttf"));

        holder.title.setText(topics.getTitle());
        holder.title.setCompoundDrawablesWithIntrinsicBounds(topics.getTopicDrawable(),0,0,0);
        holder.subInfo.setText(topics.getSubInfo());

        holder.title.setTypeface(typeface);
        holder.topicsCount.setTypeface(typeface);
        holder.subInfo.setTypeface(typeface);

        if(topics.getCounts()>0){
            holder.topicsCount.setVisibility(View.VISIBLE);
            holder.topicsCount.setText(String.valueOf(topics.getCounts()));
        }



    }

    @Override
    public int getItemCount() {
        return topicsLists.size();
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
}
