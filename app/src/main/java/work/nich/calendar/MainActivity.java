package work.nich.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.Toast;

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
        SparseArray<HighlightStyle> array = new SparseArray<>();
        array.append(calendar.get(Calendar.DAY_OF_MONTH), HighlightStyle.SOLID_CIRCLE);

        mMonthView.setCalendar(calendar);
        mMonthView.setDayStyleArray(array);
        mMonthView.setMode(MonthView.Mode.SELECT);
        mMonthView.setOnDayClickListener(new MonthView.OnDayClickedListener() {
            @Override
            public void onDayClicked(int day) {
                Toast.makeText(MainActivity.this, Integer.toString(day), Toast.LENGTH_SHORT).show();
            }
        });
        mMonthView.setOnDaySelectListener(new MonthView.OnDaySelectedListener() {
            @Override
            public void onDayJustSelected(int day) {
                Toast.makeText(MainActivity.this, Integer.toString(day) + "selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDayJustDeselected(int day) {
                Toast.makeText(MainActivity.this, Integer.toString(day) + "deselected", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
