package work.nich.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import work.nich.view.HighlightStyle;
import work.nich.view.MonthDay;
import work.nich.view.MonthView;

/**
 * Created by nich on 2017/2/23.
 * A sample activity.
 */

public class MainActivity extends Activity {
    private List<MonthView> mMonthViews;
    
    private TextView mMonthTv;
    private ViewPager mViewPager;
    
    private MonthView.Mode mMode = MonthView.Mode.DISPLAY_ONLY;
    private HighlightStyle mHighlightStyle = HighlightStyle.SOLID_CIRCLE;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        bindViewPager();
    }
    
    private void bindViewPager() {
        Calendar calendar;
        mMonthTv = (TextView) findViewById(R.id.tv_month);
        
        mMonthViews = new ArrayList<>();
        
        for (int i = 0; i < 12; i++) {
            MonthView monthView = new MonthView(this);
            monthView.setMode(MonthView.Mode.SELECT);
            calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, i);
            monthView.setCalendar(calendar);
            mMonthViews.add(monthView);
        }
        
        final MonthAdapter adapter = new MonthAdapter();
        adapter.setMonthList(mMonthViews);
    
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(adapter);
        
        calendar = Calendar.getInstance();
        mViewPager.setCurrentItem(calendar.get(Calendar.MONTH));
        mMonthTv.setText(calendar.get(Calendar.YEAR) + "." + (calendar.get(Calendar.MONTH) + 1));
    
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                
            }
            
            @Override
            public void onPageSelected(int position) {
                Calendar cal = adapter.getMonthView(position).getCalendar();
                mMonthTv.setText(cal.get(Calendar.YEAR) + "." + (cal.get(Calendar.MONTH) + 1));
            }
            
            @Override
            public void onPageScrollStateChanged(int state) {
                
            }
        });

//        mMonthView = (MonthView) findViewById(R.id.view_month);
//        Calendar calendar = Calendar.getInstance();
//        SparseArray<HighlightStyle> array = new SparseArray<>();
//        array.append(calendar.get(Calendar.DAY_OF_MONTH), HighlightStyle.SOLID_CIRCLE);
//
//        mMonthView.setCalendar(calendar);
//        mMonthView.setDayStyleArray(array);
//        mMonthView.setMode(MonthView.Mode.SELECT);
//        mMonthView.setFirstDayOfWeek(Calendar.SUNDAY);
//        mMonthView.setDisplayHintDays(false);
//        mMonthView.setOnDayClickListener(new MonthView.OnDayClickedListener() {
//            @Override
//            public void onDayClicked(int day) {
//                Toast.makeText(MainActivity.this, Integer.toString(day), Toast.LENGTH_SHORT).show();
//            }
//        });
//        mMonthView.setOnDaySelectListener(new MonthView.OnDaySelectedListener() {
//            @Override
//            public void onDayJustSelected(int day) {
//                Toast.makeText(MainActivity.this, Integer.toString(day) + "selected", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onDayJustDeselected(int day) {
//                Toast.makeText(MainActivity.this, Integer.toString(day) + "deselected", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
    
    public void changeMode(View view) {
        if (mMode == MonthView.Mode.SELECT) {
            mMode = MonthView.Mode.DISPLAY_ONLY;
            Toast.makeText(this, "now you are in display only mode", Toast.LENGTH_SHORT).show();
        } else {
            mMode = MonthView.Mode.SELECT;
            Toast.makeText(this, "now you are in select mode", Toast.LENGTH_SHORT).show();
        }
        for (MonthView monthView : mMonthViews) {
            monthView.setMode(mMode);
        }
        
        mViewPager.getAdapter().notifyDataSetChanged();
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
    
        for (MonthView monthView : mMonthViews) {
            monthView.setSelectedStyle(mHighlightStyle);
        }
    
        mViewPager.getAdapter().notifyDataSetChanged();
    }
    
    public void getSelectedDays(View view) {
        for (MonthView monthView : mMonthViews) {
            MonthDay monthDayArray[] = monthView.getSelectedDays();
            if (monthDayArray.length != 0)
                Toast.makeText(this, Arrays.toString(monthDayArray), Toast.LENGTH_LONG).show();
        }
    }
}
