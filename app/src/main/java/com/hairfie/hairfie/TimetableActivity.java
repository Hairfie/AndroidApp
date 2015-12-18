package com.hairfie.hairfie;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hairfie.hairfie.models.TimeWindow;
import com.hairfie.hairfie.models.Timetable;

import java.util.zip.Inflater;

public class TimetableActivity extends AppCompatActivity {

    public static final String ARG_TIMETABLE = "timetable";
    private Timetable mTimetable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTimetable = (Timetable)getIntent().getParcelableExtra(ARG_TIMETABLE);
        if (null == mTimetable) {
            Log.e(Application.TAG, "No timetable provided");
            finish();
        }

        TextView titleTextView = (TextView)findViewById(R.id.appbar_title);
        if (null != titleTextView)
            titleTextView.setText(R.string.timetable);

        LayoutInflater inflater = getLayoutInflater();

        int[] dayNames = {
                R.string.monday,
                R.string.tuesday,
                R.string.wednesday,
                R.string.thursday,
                R.string.friday,
                R.string.saturday,
                R.string.sunday,
        };

        TimeWindow[][] timeWindows = {
                mTimetable.monday,
                mTimetable.tuesday,
                mTimetable.wednesday,
                mTimetable.thursday,
                mTimetable.friday,
                mTimetable.saturday,
                mTimetable.sunday,
        };
        LinearLayout container = (LinearLayout)findViewById(R.id.container);

        for (int i = 0; container != null && i < dayNames.length && i < timeWindows.length; i++) {
            View view = inflater.inflate(R.layout.fragment_timetable, null, false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics()));
            view.setLayoutParams(layoutParams);
            container.addView(view);
            TextView dayNameTextView = (TextView)view.findViewById(R.id.day);
            TextView windowsTextView = (TextView)view.findViewById(R.id.hours);
            dayNameTextView.setText(dayNames[i]);

            TimeWindow[] windows = timeWindows[i];
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < windows.length; j++) {
                TimeWindow window = windows[j];
                if (window.startTime == null || window.endTime == null)
                    continue;

                if (j > 0)
                    builder.append(" / ");
                builder.append(window.startTime);
                builder.append("-");
                builder.append(window.endTime);
            }
            windowsTextView.setText(builder.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Application.getInstance().trackScreenName("TimetableActivity");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
