package com.fsdev.imeds;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Frankline Sable on 03/11/2017.
 */

public class Adapter_Feeds extends RecyclerView.Adapter<Adapter_Feeds.viewHolder> {

    private Context mContext;
    private List<Model_Feeds> feedsList;
    public static final String EXTRA_URL = "FEED_URL";

    public class viewHolder extends RecyclerView.ViewHolder {
        private TextView title, description, time, viewMore, shareButton;
        private ImageView image, overflowImage;

        public viewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.titleView);
            description = (TextView) itemView.findViewById(R.id.descView);
            time = (TextView) itemView.findViewById(R.id.timeView);
            shareButton = (TextView) itemView.findViewById(R.id.share);
            viewMore = (TextView) itemView.findViewById(R.id.more);
            overflowImage = (ImageView) itemView.findViewById(R.id.overflowFeedMenu);
            image = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    public Adapter_Feeds(Context mContext, List<Model_Feeds> feedsList) {
        this.mContext = mContext;
        this.feedsList = feedsList;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_feeds, parent, false);

        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, int position) {
        final Model_Feeds containerFeeds = feedsList.get(position);
        holder.title.setText(containerFeeds.getTitle());
        holder.title.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.scaler));

        holder.description.setText(containerFeeds.getDescription());
        holder.time.setText(containerFeeds.getDateTime());
        //holder.image.setImageResource(R.drawable.testingthis);
        String shareText="I just read that "+containerFeeds.getTitle()+".\nVisit: "+containerFeeds.getUrl()+" to learn more. \n(Sent From The Imeds App)";

        holder.viewMore.setOnClickListener(new viewMoreClick(containerFeeds.getUrl()));
        holder.shareButton.setOnClickListener(new shareWithClick(shareText));
        holder.overflowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpMenu(holder.overflowImage);
            }
        });
        if (containerFeeds.getImageUrl().length() < 5) {
            holder.image.setVisibility(View.GONE);
        } else {
            holder.image.setImageURI(Uri.parse(containerFeeds.getImageUrl()));
        }
    }

    private class viewMoreClick implements View.OnClickListener {
        private String url;

        viewMoreClick(String url) {
            this.url = url;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext, FeedsView.class);
            intent.putExtra(Adapter_Feeds.EXTRA_URL, url);
            mContext.startActivity(intent);
        }
    }

    private class shareWithClick implements View.OnClickListener {
        private String text;

        shareWithClick(String text) {
            this.text = text;
        }

        @Override
        public void onClick(View view) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            shareIntent.setType("text/plain");
            mContext.startActivity(Intent.createChooser(shareIntent, "Share with: "));
        }
    }

    private void showPopUpMenu(final View v) {
        final PopupMenu popupMenu = new PopupMenu(mContext, v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.feed_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int itemId = item.getItemId();
                View lowerView = (View) v.getParent();

                if (itemId == R.id.action_delete) {
                    return true;
                } else if (itemId == R.id.action_hide) {
                    return true;
                } else {
                    //TODO add actions and return true in each case
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return feedsList.size();
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

