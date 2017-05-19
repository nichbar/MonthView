package work.nich.calendarview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.Calendar;

/**
 * CalendarView
 * @author nich
 * Guangzhou, China, Asia, Earth.
 */

public class CalendarView extends LinearLayout {
    private static final String TAG = CalendarView.class.getSimpleName();

    private static final int DEFAULT_OFFSCREEN_PAGES = 1;
    private static final int MAX_SETTLE_DURATION = 600; // ms
    private static final int MIN_DISTANCE_FOR_FLING = 25; // dips

    private static final int DEFAULT_GUTTER_SIZE = 16; // dips

    private static final int MIN_FLING_VELOCITY = 400; // dips

    private Calendar mCurrentMonth;
    private Calendar mPreviousMonth;
    private Calendar mNextMonth;

    private MonthView mCurrentMonthView;
    private MonthView mPreviousMonthView;
    private MonthView mNextMonthView;

    public CalendarView(Context context) {
        super(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setFocusable(true);

        setOrientation(HORIZONTAL);

        updateCalendar(Calendar.getInstance());
    }

    private void updateCalendar(Calendar calendar) {
        mCurrentMonth = calendar;
        mPreviousMonth = (Calendar) calendar.clone();
        mPreviousMonth.set(Calendar.MONTH, -1);
        mNextMonth = (Calendar) calendar.clone();
        mNextMonth.set(Calendar.MONTH, 1);
    }
}
