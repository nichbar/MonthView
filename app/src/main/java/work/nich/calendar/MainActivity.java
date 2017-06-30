package work.nich.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import work.nich.view.MonthView;

/**
 * Created by nich on 2017/2/23.
 * A sample activity.
 */

public class MainActivity extends Activity {
    private List<MonthView> mMonthViews;
    private MonthView mMonthView;
    private TextView mMonthTv;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        init();
    }
    
    private void init() {
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
        
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        
        calendar = Calendar.getInstance();
        pager.setCurrentItem(calendar.get(Calendar.MONTH));
        mMonthTv.setText(calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月");
        
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                
            }
            
            @Override
            public void onPageSelected(int position) {
                Calendar cal = adapter.getMonthView(position).getCalendar();
                mMonthTv.setText(cal.get(Calendar.YEAR) + "年" + (cal.get(Calendar.MONTH) + 1) + "月");
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
}
