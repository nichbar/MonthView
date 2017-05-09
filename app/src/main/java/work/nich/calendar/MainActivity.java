package work.nich.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;

import java.util.Calendar;

import work.nich.calendarview.HighlightType;
import work.nich.calendarview.MonthView;

/**
 * Created by nichbar on 2017/2/23.
 * Just a temporary test activity.
 */

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        init();
    }

    private void init() {
        MonthView monthView = (MonthView) findViewById(R.id.view_month);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        SparseArray<HighlightType> array = new SparseArray<>();
        array.append(13, HighlightType.BOTTOM_SEMICIRCLE);

        monthView.setCalendar(calendar);
        monthView.setDayStyleArray(array);
    }
}
