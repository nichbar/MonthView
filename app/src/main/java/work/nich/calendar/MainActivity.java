package work.nich.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;

import work.nich.calendarview.HighlightStyle;
import work.nich.calendarview.MonthView;

/**
 * Created by nichbar on 2017/2/23.
 * A sample activity.
 */

public class MainActivity extends Activity {
    MonthView mMonthView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        init();
    }

    private void init() {
        mMonthView = (MonthView) findViewById(R.id.view_month);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Calendar.MAY);
        SparseArray<HighlightStyle> array = new SparseArray<>();
        array.append(12, HighlightStyle.SOLID_CIRCLE);

        mMonthView.setCalendar(calendar);
        mMonthView.setDayStyleArray(array);
        mMonthView.setMode(MonthView.Mode.DISPLAY_ONLY);
        mMonthView.setOnDayClickListener(new MonthView.OnDayClickedListener() {
            @Override
            public void onDayClicked(int day) {
                Toast.makeText(MainActivity.this, Integer.toString(day), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
