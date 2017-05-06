package work.nich.calendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.SparseArray;
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
    private Paint mMonthDayTextPaint;
    private Paint mHighlightedMonthDayTextPaint;
    private Paint mHighlightedCirclePaint;
    private Paint mHighlightedRingPaint;
    
    private static final int DEFAULT_DAY_HEIGHT = 40;
    private static final int DEFAULT_DAY_TEXT_SIZE = 14;
    private static final int COMPENSATE_HEIGHT = 8;
    private static final int DEFAULT_DAY_RADIUS = 18;
    
    private static final int ROW_NUM = 6;
    private static final int COLUMN_NUM = 7;
    
    private String[] mDaysIndicator = {"一", "二", "三", "四", "五", "六", "日"};
    private SparseArray<HighlightType> mHighlightDayArray; // Array to store highlight type of day;
    private OnDayClickedListener mOnDayClickedListener;
    
    private int mIndicatorColor;
    private int mHighlightedColor;
    private int mDayTextColor;
    private int mHighlightedTextColor;
    
    private int mRowHeight;
    private int mCompensateHeight;
    private int mDayRadius; // The radius of highlighted day's circle.
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
        mCompensateHeight = dp2px(COMPENSATE_HEIGHT);
        mDayRadius = dp2px(DEFAULT_DAY_RADIUS);
        
        mFirstDayOfWeek = Calendar.MONDAY; // default value of start day of the week
        
        mDayCalendar = Calendar.getInstance();
        mMonthDayNum = mDayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        mHighlightDayArray = new SparseArray<>();
        
        // TODO Provide day theme and night theme for switching.
        mIndicatorColor = getResources().getColor(R.color.nc_default_indicator_text_color);
        mHighlightedColor = getResources().getColor(R.color.nc_default_highlighted_color);
        mDayTextColor = getResources().getColor(R.color.nc_default_day_text_color);
        mHighlightedTextColor = getResources().getColor(R.color.nc_default_highlighted_day_text_color);
    }
    
    private void initPaint() {
        mWeekdayIndicatorPaint = new Paint();
        mWeekdayIndicatorPaint.setAntiAlias(true);
        mWeekdayIndicatorPaint.setFakeBoldText(true);
        mWeekdayIndicatorPaint.setTextSize(mTextSize);
        mWeekdayIndicatorPaint.setStyle(Paint.Style.FILL);
        mWeekdayIndicatorPaint.setColor(mIndicatorColor);
        mWeekdayIndicatorPaint.setTextAlign(Paint.Align.CENTER);
        
        mHighlightedRingPaint = new Paint();
        mHighlightedRingPaint.setAntiAlias(true);
        mHighlightedRingPaint.setStyle(Paint.Style.STROKE);
        mHighlightedRingPaint.setColor(mHighlightedColor);
        
        mMonthDayTextPaint = new Paint();
        mMonthDayTextPaint.setAntiAlias(true);
        mMonthDayTextPaint.setColor(mDayTextColor);
        mMonthDayTextPaint.setStyle(Paint.Style.FILL);
        mMonthDayTextPaint.setTextSize(mTextSize);
        mMonthDayTextPaint.setTextAlign(Paint.Align.CENTER);
        
        mHighlightedMonthDayTextPaint = new Paint();
        mHighlightedMonthDayTextPaint.setAntiAlias(true);
        mHighlightedMonthDayTextPaint.setFakeBoldText(true);
        mHighlightedMonthDayTextPaint.setStyle(Paint.Style.FILL);
        mHighlightedMonthDayTextPaint.setTextSize(mTextSize);
        mHighlightedMonthDayTextPaint.setColor(mHighlightedTextColor);
        mHighlightedMonthDayTextPaint.setTextAlign(Paint.Align.CENTER);
        
        mHighlightedCirclePaint = new Paint();
        mHighlightedCirclePaint.setAntiAlias(true);
        mHighlightedCirclePaint.setStyle(Paint.Style.FILL);
        mHighlightedCirclePaint.setColor(mHighlightedColor);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mRowHeight * ROW_NUM + mCompensateHeight);
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
        
        for (int day = 1; day <= mMonthDayNum; day++) {
            int columnNum = (day + dayOffset) % 7;
            // locate every single "day"
            float x = columnNum * mWidthOfEveryday + mHalfWidthOfEveryday;
            
            drawHighlight(canvas, day, x, y);
            
            canvas.drawText(Integer.toString(day), x, y, mMonthDayTextPaint);
            
            if (columnNum + 1 == COLUMN_NUM) {
                y += mRowHeight;
            }
        }
    }
    
    /**
     * Draw specify highlight.
     * TODO : Let the user to draw their highlight style by overriding this method.
     *
     * @param canvas just the canvas you want
     * @param day    the day you wanna highlight
     * @param x      x-coordinate of this day (center)
     * @param y      y-coordinate of this day (center)
     */
    public void drawHighlight(Canvas canvas, int day, float x, float y) {
        RectF rectF = new RectF(x - mDayRadius, y - mDayRadius - mTextSize / 3, x + mDayRadius, y + mDayRadius - mTextSize / 3);
        
        switch (mHighlightDayArray.get(day, HighlightType.NO_HIGHLIGHT)) {
            case NO_HIGHLIGHT:
                break;
            case SOLID_CIRCLE:
                canvas.drawCircle(x, y - mTextSize / 3, mDayRadius, mHighlightedCirclePaint);
                break;
            case RING_ONLY:
                canvas.drawCircle(x, y - mTextSize / 3, mDayRadius, mHighlightedRingPaint);
                break;
            case TOP_SEMICIRCLE:
                canvas.drawArc(rectF, 0, 180, false, mHighlightedCirclePaint);
                break;
            case BOTTOM_SEMICIRCLE:
                canvas.drawArc(rectF, 180, 180, false, mHighlightedCirclePaint);
                break;
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                int day = locateClickedDay(event);
                if (0 <= day) {
                    highlightDay(day);
                    
                    if (mOnDayClickedListener != null) {
                        mOnDayClickedListener.onDayClicked(day);
                    }
                }
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
        if (mHighlightDayArray.get(i, HighlightType.NO_HIGHLIGHT) == HighlightType.NO_HIGHLIGHT) {
            mHighlightDayArray.append(i, HighlightType.SOLID_CIRCLE);
        } else {
            mHighlightDayArray.append(i, HighlightType.SOLID_CIRCLE);
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
        int offset = startDayOfWeek - mFirstDayOfWeek - Calendar.SUNDAY;
        if (offset < 0) {
            return offset + 7;
        } else {
            return offset;
        }
    }
    
    public void setOnDayClickedListener(OnDayClickedListener listener) {
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
    
    public interface OnDayClickedListener {
        void onDayClicked(int day);
    }
}
