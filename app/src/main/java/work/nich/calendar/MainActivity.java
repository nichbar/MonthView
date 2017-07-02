package work.nich.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;

import work.nich.view.HighlightStyle;
import work.nich.view.MonthDay;
import work.nich.view.MonthView;

/**
 * Created by nich.
 * A sample activity.
 */

public class MainActivity extends Activity {
    
    private MonthView mMonthView;
    
    private MonthView.Mode mMode = MonthView.Mode.DISPLAY_ONLY;
    private HighlightStyle mHighlightStyle = HighlightStyle.SOLID_CIRCLE;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        bindViewPager();
    }
    
    private void bindViewPager() {
        Calendar calendar = Calendar.getInstance();
    
        MonthView monthView = new MonthView(this);
        monthView.setMode(MonthView.Mode.SELECT);
        monthView.setCalendar(calendar);
    
        mMonthView = (MonthView) findViewById(R.id.view_month);
        SparseArray<HighlightStyle> array = new SparseArray<>();
        array.append(calendar.get(Calendar.DAY_OF_MONTH), HighlightStyle.SOLID_CIRCLE);

        mMonthView.setCalendar(calendar);
        mMonthView.setDayStyleArray(array);
        mMonthView.setMode(MonthView.Mode.SELECT);
        mMonthView.setFirstDayOfWeek(Calendar.SUNDAY);
        mMonthView.setDisplayHintDays(false);
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
    
    public void changeMode(View view) {
        if (mMode == MonthView.Mode.SELECT) {
            mMode = MonthView.Mode.DISPLAY_ONLY;
            Toast.makeText(this, "now you are in display only mode", Toast.LENGTH_SHORT).show();
        } else {
            mMode = MonthView.Mode.SELECT;
            Toast.makeText(this, "now you are in select mode", Toast.LENGTH_SHORT).show();
        }
            mMonthView.setMode(mMode);
    }
    
    public void changeStyle(View view) {
        switch (mHighlightStyle) {
            case SOLID_CIRCLE:
                mHighlightStyle = HighlightStyle.RING_ONLY;
                Toast.makeText(this, "'ring only' style", Toast.LENGTH_SHORT).show();
                break;
            case RING_ONLY:
                mHighlightStyle = HighlightStyle.TOP_SEMICIRCLE;
                Toast.makeText(this, "'top semicircle' style", Toast.LENGTH_SHORT).show();
                break;
            case TOP_SEMICIRCLE:
                mHighlightStyle = HighlightStyle.BOTTOM_SEMICIRCLE;
                Toast.makeText(this, "'bottom semicircle' style", Toast.LENGTH_SHORT).show();
                break;
            case BOTTOM_SEMICIRCLE:
                mHighlightStyle = HighlightStyle.SOLID_CIRCLE;
                Toast.makeText(this, "'solid circle' style", Toast.LENGTH_SHORT).show();
                break;
        }
        mMonthView.setSelectedStyle(mHighlightStyle);
    }
    
    public void getSelectedDays(View view) {
        MonthDay monthDayArray[] = mMonthView.getSelectedDays();
        if (monthDayArray.length != 0)
            Toast.makeText(this, Arrays.toString(monthDayArray), Toast.LENGTH_LONG).show();
    }
}
