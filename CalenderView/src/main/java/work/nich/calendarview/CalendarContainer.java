package work.nich.calendarview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * This file is created by nich
 * on 2017/3/21 in Calendar
 */

public class CalendarContainer extends LinearLayout {
    public CalendarContainer(Context context) {
        super(context);
    }

    public CalendarContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CalendarContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
