package work.nich.calendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;

/**
 * This file is created by nich
 * on 2017/3/22 in Calendar
 */

public class MonthView extends View {
    private Paint mWeekdayIndicatorPaint;
    private Paint mMonthDayPaint;
    private Paint mSelectedTextPaint;
    private Paint mSelectedCirclePaint;

    private static final int COLOR_PRIMARY_DARK_BLUE = 0xff303F9F;
    private static final int COLOR_PRIMARY_LIGHT_BLUE = 0xff32a1ff;
    private static final int COLOR_ACCENT = 0xffFF4081;
    private static final int COLOR_ORIGIN_GRAY = 0xff999999;
    private static final int SELECTED_CIRCLE_ALPHA = 50;
    private static final int DEFAULT_DAY_HEIGHT = 35;
    private static final int DEFAULT_DAY_TEXT_SIZE = 16;

    private static final int ROW_NUM = 6;
    private static final int COLUMN_NUM = 7;

    private String[] mDaysIndicator = {"一", "二", "三", "四", "五", "六", "日"};
    private SparseBooleanArray mHighlightDayArray;
    private OnDayClickedListener mOnDayClickedListener;

    private int mPrimaryDarkColor;
    private int mPrimaryLightColor;
    private int mAccentColor;
    private int mDayTextColor;
    private int mSelectedDayColor;

    private int mRowHeight;
    private int mTextSize;
    private int mWidth;
    private int mPadding;

    private int mFirstDayOfWeek;
    private int mMonthDayNum;
    private int mToday;

    private float mWidthOfEveryday;
    private float mHalfWidthOfEveryday;

    private Calendar mDayCalendar;

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
        initPaint();
    }

    public MonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        initPaint();
    }

    private void init(AttributeSet attrs) {
        // TODO Try to get color, text size and text fonts from attrs, use default value if attrs haven't not been declared.

        mRowHeight = dp2px(DEFAULT_DAY_HEIGHT);
        mTextSize = sp2px(DEFAULT_DAY_TEXT_SIZE);

        mFirstDayOfWeek = Calendar.MONDAY; // default value of start day of the week

        mDayCalendar = Calendar.getInstance();
        mMonthDayNum = mDayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        mHighlightDayArray = new SparseBooleanArray();

        mPrimaryDarkColor = COLOR_PRIMARY_DARK_BLUE;
        mPrimaryLightColor = COLOR_PRIMARY_LIGHT_BLUE;
        mAccentColor = COLOR_ACCENT;
        mDayTextColor = COLOR_ORIGIN_GRAY;
        mSelectedDayColor = COLOR_PRIMARY_LIGHT_BLUE;
    }

    private void initPaint() {
        mWeekdayIndicatorPaint = new Paint();
        mWeekdayIndicatorPaint.setAntiAlias(true);
        mWeekdayIndicatorPaint.setFakeBoldText(true);
        mWeekdayIndicatorPaint.setTextSize(mTextSize);
        mWeekdayIndicatorPaint.setStyle(Paint.Style.FILL);
        mWeekdayIndicatorPaint.setColor(mPrimaryDarkColor);
        mWeekdayIndicatorPaint.setTextAlign(Paint.Align.CENTER);

        mMonthDayPaint = new Paint();
        mMonthDayPaint.setAntiAlias(true);
        mMonthDayPaint.setColor(mDayTextColor);
        mMonthDayPaint.setStyle(Paint.Style.FILL);
        mMonthDayPaint.setTextSize(mTextSize);
        mMonthDayPaint.setTextAlign(Paint.Align.CENTER);

        mSelectedTextPaint = new Paint();
        mSelectedTextPaint.setAntiAlias(true);
        mSelectedTextPaint.setFakeBoldText(true);
        mSelectedTextPaint.setStyle(Paint.Style.FILL);
        mSelectedTextPaint.setTextSize(mTextSize);
        mSelectedTextPaint.setColor(mSelectedDayColor);
        mSelectedTextPaint.setTextAlign(Paint.Align.CENTER);

        mSelectedCirclePaint = new Paint();
        mSelectedCirclePaint.setAntiAlias(true);
        mSelectedCirclePaint.setStyle(Paint.Style.FILL);
        mSelectedCirclePaint.setColor(mPrimaryLightColor);
        mSelectedCirclePaint.setAlpha(SELECTED_CIRCLE_ALPHA);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mRowHeight * ROW_NUM);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawWeekdayIndicator(canvas);
        drawMonthDay(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        // initial the width of every "day"
        mWidthOfEveryday = mWidth / COLUMN_NUM;
        mHalfWidthOfEveryday = mWidthOfEveryday / 2;
    }

    private void drawWeekdayIndicator(Canvas canvas) {
        float y = mRowHeight - mTextSize / 2;

        for (int i = 0; i < COLUMN_NUM; i++) {
            float x = i * mWidthOfEveryday + mHalfWidthOfEveryday;
            int dayOfWeek = i % COLUMN_NUM;  // TODO provide method to change the first day of the week.
            String indicator = getWeekdayIndicator(dayOfWeek);
            canvas.drawText(indicator, x, y, mWeekdayIndicatorPaint);
        }
    }

    private void drawMonthDay(Canvas canvas) {
        float y = mRowHeight * 2 - mTextSize / 2; // start at the bottom of indicator and take the center vertical coordinate as y
        int dayOffset = getDayOffset();

        for (int i = 1; i <= mMonthDayNum; i++) {
            int columnNum = (i + dayOffset) % 7;
            // locate every single "day"
            float x = columnNum * mWidthOfEveryday + mHalfWidthOfEveryday;

            if (i == getTodayOfMonth() || mHighlightDayArray.get(i, false)) { // day to highlight
                canvas.drawCircle(x, y - mTextSize / 3, mTextSize, mSelectedCirclePaint);
            }
            canvas.drawText(Integer.toString(i), x, y, mMonthDayPaint);
            if (columnNum + 1 == COLUMN_NUM) {
                y += mRowHeight;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                int day = locateClickedDay(event);
                highlightDay(day);
                break;
        }
        return true;
    }

    private void highlightDay(int day) {
        toggleHighlight(day);
        invalidate();
    }

    private int locateClickedDay(MotionEvent event) {
        int day = 0;
        float touchX = event.getX();
        float touchY = event.getY();

        float y = mRowHeight * 2 - mTextSize / 2; // start at the bottom of indicator and take the center vertical coordinate as y
        int dayOffset = getDayOffset();

        for (int i = 1; i <= mMonthDayNum; i++) {
            int columnNum = (i + dayOffset) % 7;
            float x = columnNum * mWidthOfEveryday + mHalfWidthOfEveryday;
            if (columnNum + 1 == COLUMN_NUM) {
                y += mRowHeight;
            }
            if (x - mWidthOfEveryday / 2 <= touchX && touchX <= x + mWidthOfEveryday / 2 && y - mRowHeight <= touchY && touchY <= y) {
                day = i;
            }
        }
        return day;
    }

    private void toggleHighlight(int i) {
        if (mHighlightDayArray.get(i, false)) {
            mHighlightDayArray.append(i, false);
        } else {
            mHighlightDayArray.append(i, true);
        }
    }

    private int getTodayOfMonth() {
        if (mToday == 0) {
            Calendar cal = Calendar.getInstance();
            mToday = cal.get(Calendar.DAY_OF_MONTH);
        }
        return mToday;
    }

    private int getDayOffset() {
        // TODO provide method to change the default selected day
        mDayCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int startDayOfWeek = mDayCalendar.get(Calendar.DAY_OF_WEEK);
        return Math.abs(startDayOfWeek - mFirstDayOfWeek - 1);
    }

    public void setOnDayClickedListener(OnDayClickedListener listener){
        this.mOnDayClickedListener = listener;
    }

    private String getWeekdayIndicator(int dayOfWeek) {
        return mDaysIndicator[dayOfWeek];
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    public interface OnDayClickedListener{
        void onDayClicked(int day);
    }
}
