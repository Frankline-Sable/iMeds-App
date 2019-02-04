package com.fsdev.imeds;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MedicalEvents extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Adapter_Events mAdapter;
    RecyclerView recyclerView;
    List<Model_Events> eventsList = new ArrayList<>();
    private Spinner spinnerMonths;
    private Button yearPickerButton;
    private String[] monthsArray;
    private Db_Events db_events;
    private String sortOrderMonthly;
    private String sortOrderYearly;
    private String sortOrder = null;
    private Calendar latestCalendar;
    private LinearLayout eventListProgress;
    private TextView emptyView;
    private PreferencesHandler prefsHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Window window=getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        recyclerView = (RecyclerView) findViewById(R.id.eventsRecycleView);
        mAdapter = new Adapter_Events(this, eventsList);
        prefsHandler=new PreferencesHandler(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        latestCalendar = Calendar.getInstance();
        prepareClassVariables(latestCalendar);

        spinnerMonths = (Spinner) findViewById(R.id.spinnerMonths);
        spinnerMonths.setSelection(latestCalendar.get(Calendar.MONTH));
        spinnerMonths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                sortOrderMonthly = spinnerMonths.getSelectedItem().toString();
                if (sortOrderMonthly.equalsIgnoreCase("all")) {
                    sortOrderMonthly = null;
                    yearPickerButton.setText("........");
                }
                else {
                    yearPickerButton.setText(sortOrderYearly);
                }
                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });
        db_events = new Db_Events(this);
        eventListProgress=(LinearLayout)findViewById(R.id.eventLoader);
        emptyView=(TextView) findViewById(R.id.emptyView);

        refresh();
    }

    private String formatEventsDate(int dateChosen, String dateString) {
        SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date dateFromSql = null;
        try {
            dateFromSql = sqlDateFormat.parse(dateString);
        } catch (ParseException e) {
            return "???";
        }

        String datePatten, returnDate;
        switch (dateChosen) {
            case 1:
                datePatten = "EEE, LLL dd yyyy";
                break;
            case 2:
                datePatten = "EEE";
                break;
            case 3:
                datePatten = "dd";
                break;
            case 4:
                datePatten = "hh:mm a";
                break;
            default:
                datePatten = "yyyy-MM-dd";
        }

        SimpleDateFormat dateFmtEvents = new SimpleDateFormat(datePatten, Locale.US);
        returnDate = dateFmtEvents.format(dateFromSql);

        return returnDate;
    }

    public void launchYearPicker(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar yearCalendar = Calendar.getInstance();
        yearCalendar.set(Calendar.YEAR, year);
        yearCalendar.set(Calendar.MONTH, month);
        yearCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        prepareClassVariables(yearCalendar);

        for (int i = 0; i < monthsArray.length; i++) {
            if (sortOrderMonthly.equalsIgnoreCase(monthsArray[i])) {
                spinnerMonths.setSelection(i);
                break;
            }
        }
    }

    public void refresh() {
        AsyncRefresh asyncRefresh = new AsyncRefresh();
        asyncRefresh.execute();
    }

    public class AsyncRefresh extends AsyncTask<Void, Cursor, Cursor> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            db_events.open();
            db_events.updateWhetherViewed(0,true);
            Cursor cursor;
            if (sortOrderMonthly == null) {
                cursor = db_events.readDb(Db_Events.struct_events.KEY_YEAR + " = " + sortOrderYearly, null, sortOrder);
            } else {
                cursor = db_events.readDb(Db_Events.struct_events.KEY_MONTH + " =\"" + sortOrderMonthly + "\" AND " + Db_Events.struct_events.KEY_YEAR + " = " + sortOrderYearly, null, sortOrder);
            }
            cursor.moveToFirst();
            eventsList.clear();
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null) {
                int tit = cursor.getColumnIndexOrThrow(Db_Events.struct_events.KEY_TITLE);
                int desc = cursor.getColumnIndexOrThrow(Db_Events.struct_events.KEY_DESCRIPTION);
                int sch = cursor.getColumnIndexOrThrow(Db_Events.struct_events.KEY_SCHEDULED);
                int img = cursor.getColumnIndexOrThrow(Db_Events.struct_events.KEY_IMAGE);
                int loc = cursor.getColumnIndexOrThrow(Db_Events.struct_events.KEY_VENUE);
                int alarm = cursor.getColumnIndexOrThrow(Db_Events.struct_events.KEY_ALARM);
                String month = null;
                while (!cursor.isAfterLast()) {
                    Model_Events events = new Model_Events(formatEventsDate(3, cursor.getString(sch)), formatEventsDate(2, cursor.getString(sch)), cursor.getString(tit), cursor.getString(desc), formatEventsDate(4, cursor.getString(sch)), cursor.getString(loc), formatEventsDate(1, cursor.getString(sch)), cursor.getString(img), cursor.getInt(alarm), cursor.getString(sch));
                    eventsList.add(events);
                    cursor.moveToNext();
                }
                cursor.close();
                db_events.close();
                mAdapter.notifyDataSetChanged();
                eventListProgress.setVisibility(View.GONE);

                if (mAdapter.getItemCount() < 1) {
                    emptyView.setText(getResources().getString(R.string.emptyView, sortOrderMonthly));
                    emptyView.setVisibility(View.VISIBLE);
                }else{
                    emptyView.setVisibility(View.GONE);
                }
            }
        }
    }

    void prepareClassVariables(Calendar mCalendar) {
        sortOrderYearly = String.valueOf(mCalendar.get(Calendar.YEAR));
        yearPickerButton = (Button) findViewById(R.id.yearPicker);
        yearPickerButton.setText(sortOrderYearly);

        monthsArray = getResources().getStringArray(R.array.months);
        sortOrderMonthly = monthsArray[mCalendar.get(Calendar.MONTH)];
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_events, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        item.setChecked(true);
        if (id == R.id.sortTitle) {
            sortOrder = Db_Events.struct_events.KEY_TITLE + " ASC";

        } else if (id == R.id.sortDate) {
            sortOrder = Db_Events.struct_events.KEY_SCHEDULED + " ASC";

        } else if (id == R.id.sortStatus) {
            sortOrder = Db_Events.struct_events.KEY_ALARM + " ASC";
        }
        refresh();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(prefsHandler.getUpdatesCount(PreferencesHandler.KEY_NEW_EVENT)>0){
            prefsHandler.updateNewCount(PreferencesHandler.KEY_NEW_EVENT,0);
        }
    }
}
