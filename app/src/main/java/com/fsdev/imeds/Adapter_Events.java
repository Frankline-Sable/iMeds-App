package com.fsdev.imeds;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Frankline Sable on 04/11/2017.
 */

public class Adapter_Events extends RecyclerView.Adapter<Adapter_Events.viewHolder> {
    private Context mContext;
    private List<Model_Events> eventsList;

    public class viewHolder extends RecyclerView.ViewHolder {
        public TextView title, eventSubDesc, time, dayOfMonth, dayOfWeek, daysView;
        private ImageView notification_Icon;
        private ImageView eventImage;
        private TextView eventsDesc, eventsVenue, dateScheduled;
        private LinearLayout topView;
        private RelativeLayout bottomView;
        private FrameLayout captionView;

        public viewHolder(View view) {
            super(view);
            notification_Icon = (ImageView) view.findViewById(R.id.notification_Icon);
            title = (TextView) view.findViewById(R.id.event_Title);
            eventSubDesc = (TextView) view.findViewById(R.id.events_Subtitle);
            time = (TextView) view.findViewById(R.id.eventTime);
            dayOfMonth = (TextView) view.findViewById(R.id.dayOfMonth);
            dayOfWeek = (TextView) view.findViewById(R.id.dayOfWeek);
            eventsDesc = (TextView) view.findViewById(R.id.description);
            eventsVenue = (TextView) view.findViewById(R.id.venue);
            dateScheduled = (TextView) view.findViewById(R.id.dateScheduled);
            eventImage = (ImageView) view.findViewById(R.id.eventImage);
            daysView = (TextView) view.findViewById(R.id.notifyButton);
            topView = (LinearLayout) view.findViewById(R.id.topView);
            bottomView = (RelativeLayout) view.findViewById(R.id.bottomView);
            captionView = (FrameLayout) view.findViewById(R.id.caption);
        }
    }

    public Adapter_Events(Context mContext, List<Model_Events> eventsList) {
        this.mContext = mContext;
        this.eventsList = eventsList;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_events, parent, false);
        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, int position) {
        try {
            Model_Events events = eventsList.get(position);
            holder.title.setText(events.getTitle());
            holder.eventSubDesc.setText(events.getDescription());
            holder.time.setText(events.getTimeView());
            holder.dayOfMonth.setText(events.getDayOfMonth());
            holder.dayOfWeek.setText(events.getDayOfWeek());
            holder.eventsDesc.setText(events.getDescription());
            holder.eventsVenue.setText(events.getEventVenue());
            holder.dateScheduled.setText(events.getEventDate());
            holder.daysView.setText(timeObserver(events.getFullDateTime()));

            if (events.getAlarm() == 1) {
                holder.notification_Icon.setImageResource(R.drawable.ic_notifications_active_black_24dp);
                holder.notification_Icon.setTag(eventsList);
            }

            // Picasso.with(mContext).load(events.getEventImage()).error(R.drawable.testingthis).into( holder.eventImage);

            holder.topView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.bottomView.getVisibility() == View.VISIBLE) {
                        holder.bottomView.setVisibility(View.GONE);
                    } else {
                        holder.bottomView.setVisibility(View.VISIBLE);
                    }
                }
            });
            if (events.getEventImage().length() < 5) {
                holder.captionView.setVisibility(View.GONE);
            } else {
                holder.eventImage.setImageURI(Uri.parse(events.getEventImage()));
            }
            holder.notification_Icon.setOnClickListener(new onClickHandler(events.getFullDateTime()));
        }catch (IndexOutOfBoundsException e){
            return;
        }
    }

    private class onClickHandler implements View.OnClickListener {
        private String timeToSet;

        public onClickHandler(String timeToSet) {
            this.timeToSet = timeToSet;
        }

        @Override
        public void onClick(View v) {
            changeNotifyState(v, timeToSet);
        }
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public void changeNotifyState(View v, String time) {
        Log.i("i-meds", "ev date " + time);
        ImageView notImg = (ImageView) v;
        if (notImg.getTag() != null) {
            notImg.setImageResource(R.drawable.ic_notifications_off_black_24dp);
            notImg.setTag(null);
        } else {
            notImg.setImageResource(R.drawable.ic_notifications_active_black_24dp);
            notImg.setTag(eventsList);
            activateAlarm();

            Toast.makeText(mContext, "Alarm notification has been activated.", Toast.LENGTH_SHORT).show();
        }
    }

    private void activateAlarm() {

    }

    private String timeObserver(String dt) {
        Date startDate=new Date();
        SimpleDateFormat dateTimeFmt=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        Date dateEnd=null;
        String timeRemaining="Coming Soon";

        try {
            dateEnd=dateTimeFmt.parse(dt);
        }
        catch (ParseException e) {
            return timeRemaining;
        }

        long difference = dateEnd.getTime() - startDate.getTime();
        if(difference<0){
            return "Event had already took place";
        }
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        final long elapsedDays = difference / daysInMilli;
        difference = difference % daysInMilli;

        final long elapsedHours = difference / hoursInMilli;
        difference = difference % hoursInMilli;

        final long elapsedMinutes = difference / minutesInMilli;
        difference = difference % minutesInMilli;

        final long elapsedSeconds = difference / secondsInMilli;

        final long elapsedMonths =elapsedDays/31;

        final long elapsedYears = elapsedDays/(long)365.25;

        if(elapsedYears>0){
            timeRemaining=elapsedYears+" years remaining";
        }
        else if(elapsedMonths>0){
            timeRemaining=elapsedMonths+" month remaining";
        }
        else if(elapsedDays>0){
            if(elapsedDays<2){
                timeRemaining="Happening tomorrow";
            }
            else  if(elapsedDays==7){
                timeRemaining="a week left";
            }
            else {
                timeRemaining=elapsedDays+" days remaining";
            }
        }
        else if(elapsedHours>0){
            timeRemaining=elapsedHours+" hour to go";
        }
        else if(elapsedMinutes>0){
            timeRemaining=elapsedMinutes+" min to go";
        }
        else if(elapsedSeconds>0) {
            timeRemaining = elapsedSeconds + " sec remaining";
        }
        return timeRemaining;
    }
}