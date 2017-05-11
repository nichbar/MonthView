package work.nich.calendarview;

import android.content.Context;
import android.content.res.TypedArray;
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
 * MonthView
 * @author nich
 * Guangzhou, China, Asia, Earth.
 */

public class MonthView extends View {
    private static final String TAG = MonthView.class.getSimpleName();

    private Paint mDayTextPaint;
    private Paint mHintDayTextPaint;
    private Paint mWeekdayIndicatorPaint;
    private Paint mHighlightedDayTextPaint;
    private Paint mHighlightedCirclePaint;
    private Paint mHighlightedRingPaint;

    private static final int DEFAULT_INDICATOR_HEIGHT = 30;
    private static final int DEFAULT_DAY_HEIGHT = 40;
    private static final int DEFAULT_DAY_TEXT_SIZE = 14;
    private static final int DEFAULT_DAY_RADIUS = 18;
    private static final int PADDING_BOTTOM = 8;

    private static final int COLUMN_NUM = 7;
    private static final int ROW_NUM = 6;

    private String[] mDaysIndicator = {"一", "二", "三", "四", "五", "六", "日"}; // TODO Day indicator should not be hardcoded.
    private SparseArray<HighlightStyle> mDayArray; // Array of storing highlight style of day;
    private OnDayClickedListener mOnDayClickedListener;

    private int mIndicatorColor;
    private int mHighlightColor;
    private int mDayTextColor;
    private int mHighlightTextColor;
    private int mHintDayTextColor;

    private int mIndicatorHeight;
    private int mRowHeight;
    private int mPaddingBottom;
    private int mDayRadius; // Radius of highlighted day's circle.
    private int mTextSize;

    private int mFirstDayOfWeek;
    private int mMonthDayNum;
    private int mPreMonthDayNum;
    private int mToday;
    private int mDayOffset;

    private float mWidthOfDay;
    private float mHalfWidthOfDay;

    private boolean mDayClickable;

    private Calendar mCalendar; // calendar of this month
    private Calendar mPreCalendar; // calendar of previous month

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
        // TODO Provide day theme and night theme for switching.
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MonthView);
            mRowHeight = (int) a.getDimension(R.styleable.MonthView_day_height, dp2px(DEFAULT_DAY_HEIGHT));
            mTextSize = (int) a.getDimension(R.styleable.MonthView_day_textSize, sp2px(DEFAULT_DAY_TEXT_SIZE));
            mDayRadius = (int) a.getDimension(R.styleable.MonthView_day_radius, dp2px(DEFAULT_DAY_RADIUS));
            mDayClickable = a.getBoolean(R.styleable.MonthView_day_clickable, false);

            mIndicatorColor = a.getColor(R.styleable.MonthView_indicator_textColor, getResources().getColor(R.color.nc_default_indicator_text_color));
            mHighlightColor = a.getColor(R.styleable.MonthView_highlight_color, getResources().getColor(R.color.nc_default_highlight_color));
            mDayTextColor = a.getColor(R.styleable.MonthView_day_textColor, getResources().getColor(R.color.nc_default_day_text_color));
            mHintDayTextColor = a.getColor(R.styleable.MonthView_hint_day_textColor, getResources().getColor(R.color.nc_default_hint_day_text_color));
            mHighlightTextColor = a.getColor(R.styleable.MonthView_highlight_day_textColor, getResources().getColor(R.color.nc_default_highlight_day_text_color));
            a.recycle();
        }

        mPaddingBottom = dp2px(PADDING_BOTTOM);
        mIndicatorHeight = dp2px(DEFAULT_INDICATOR_HEIGHT);
        mFirstDayOfWeek = Calendar.MONDAY; // default value of start day of the week
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
        mHighlightedRingPaint.setColor(mHighlightColor);

        mDayTextPaint = new Paint();
        mDayTextPaint.setAntiAlias(true);
        mDayTextPaint.setColor(mDayTextColor);
        mDayTextPaint.setStyle(Paint.Style.FILL);
        mDayTextPaint.setTextSize(mTextSize);
        mDayTextPaint.setTextAlign(Paint.Align.CENTER);

        mHintDayTextPaint = new Paint();
        mHintDayTextPaint.setAntiAlias(true);
        mHintDayTextPaint.setColor(mHintDayTextColor);
        mHintDayTextPaint.setStyle(Paint.Style.FILL);
        mHintDayTextPaint.setTextSize(mTextSize);
        mHintDayTextPaint.setTextAlign(Paint.Align.CENTER);

        mHighlightedDayTextPaint = new Paint();
        mHighlightedDayTextPaint.setAntiAlias(true);
        mHighlightedDayTextPaint.setStyle(Paint.Style.FILL);
        mHighlightedDayTextPaint.setTextSize(mTextSize);
        mHighlightedDayTextPaint.setColor(mHighlightTextColor);
        mHighlightedDayTextPaint.setTextAlign(Paint.Align.CENTER);

        mHighlightedCirclePaint = new Paint();
        mHighlightedCirclePaint.setAntiAlias(true);
        mHighlightedCirclePaint.setStyle(Paint.Style.FILL);
        mHighlightedCirclePaint.setColor(mHighlightColor);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }
        if (mDayArray == null) {
            mDayArray = new SparseArray<>();
        }

        mPreCalendar = (Calendar) mCalendar.clone();
        mPreCalendar.add(Calendar.MONTH, -1);
        mDayOffset = getDayOffset();
        mMonthDayNum = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        mPreMonthDayNum = mPreCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mIndicatorHeight + mRowHeight * ROW_NUM + mPaddingBottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // initial the width of every "day"
        mWidthOfDay = w / COLUMN_NUM;
        mHalfWidthOfDay = mWidthOfDay / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawWeekdayIndicator(canvas);
        drawDays(canvas);
        drawHintDays(canvas);
    }

    private void drawWeekdayIndicator(Canvas canvas) {
        float y = mIndicatorHeight - mTextSize / 2;

        for (int i = 0; i < COLUMN_NUM; i++) {
            float x = i * mWidthOfDay + mHalfWidthOfDay;
            int dayOfWeek = i % COLUMN_NUM;  // TODO provide method to change the first day of the week.
            String indicator = getWeekdayIndicator(dayOfWeek);
            canvas.drawText(indicator, x, y, mWeekdayIndicatorPaint);
        }
    }

    private void drawDays(Canvas canvas) {
        float y = mIndicatorHeight + mRowHeight - mTextSize / 2; // start at the bottom of indicator and take the center vertical coordinate as y

        for (int day = 1; day <= mMonthDayNum; day++) {
            int columnNum = (day + mDayOffset) % 7;
            // locate every single "day"
            float x = columnNum * mWidthOfDay + mHalfWidthOfDay;

            HighlightStyle style = mDayArray.get(day, HighlightStyle.NO_HIGHLIGHT);
            drawSpecificDay(canvas, style, day, x, y);

            if (columnNum + 1 == COLUMN_NUM) {
                y += mRowHeight;
            }
        }
    }

    /**
     * Draw specify day.
     * You can draw your own day in different highlighted style by overriding this method.
     *
     * @param canvas just the canvas you want
     * @param style  the style of highlight style
     * @param day    the day you wanna draw
     * @param x      x-coordinate of this day (center)
     * @param y      y-coordinate of this day (center)
     */
    public void drawSpecificDay(Canvas canvas, HighlightStyle style, int day, float x, float y) {
        RectF rectF = new RectF(x - mDayRadius, y - mDayRadius - mTextSize / 3, x + mDayRadius, y + mDayRadius - mTextSize / 3);
        switch (style) {
            case NO_HIGHLIGHT:
                drawDayText(canvas, day, x, y, mDayTextPaint);
                break;
            case SOLID_CIRCLE:
                drawCircle(canvas, x, y - mTextSize / 3, mDayRadius, mHighlightedCirclePaint);
                drawDayText(canvas, day, x, y, mHighlightedDayTextPaint);
                break;
            case RING_ONLY:
                drawCircle(canvas, x, y - mTextSize / 3, mDayRadius, mHighlightedRingPaint);
                drawDayText(canvas, day, x, y, mHighlightedDayTextPaint);
                break;
            case TOP_SEMICIRCLE:
                drawCircle(canvas, x, y - mTextSize / 3, mDayRadius, mHighlightedRingPaint);
                drawSemiCircle(canvas, rectF, 0, 180, mHighlightedCirclePaint);
                drawDayText(canvas, day, x, y, mHighlightedDayTextPaint);
                break;
            case BOTTOM_SEMICIRCLE:
                drawCircle(canvas, x, y - mTextSize / 3, mDayRadius, mHighlightedRingPaint);
                drawSemiCircle(canvas, rectF, 180, 180, mHighlightedCirclePaint);
                drawDayText(canvas, day, x, y, mHighlightedDayTextPaint);
                break;
        }
    }

    private void drawDayText(Canvas canvas, int day, float x, float y, Paint paint) {
        canvas.drawText(Integer.toString(day), x, y, paint);
    }

    private void drawSemiCircle(Canvas canvas, RectF rectF, float startAngle, float sweepAngle, Paint paint) {
        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);
    }

    private void drawCircle(Canvas canvas, float cx, float cy, float radius, Paint paint) {
        canvas.drawCircle(cx, cy, radius, paint);
    }

    /**
     * Draw days that belonging to previous month and next month.
     */
    private void drawHintDays(Canvas canvas) {
        if (mDayOffset != 6){ // When mDayOffset equals 6 means the 1st day of this month is the 1st day of the 1st week.
            drawHintDaysInPreviousMonth(canvas);
        }
        drawHintDaysInNextMonth(canvas);
    }

    private void drawHintDaysInPreviousMonth(Canvas canvas) {
        // TODO draw in details
    }

    private void drawHintDaysInNextMonth(Canvas canvas) {
        // TODO draw in details
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mDayClickable) {
                    int day = locateClickedDay(event);
                    if (0 <= day) {
                        highlightDay(day);

                        if (mOnDayClickedListener != null) {
                            mOnDayClickedListener.onDayClicked(day);
                        }
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

        float y = mRowHeight * 2 - mRowHeight / 2; // start at the bottom of indicator and take the center vertical coordinate as y

        for (int i = 1; i <= mMonthDayNum; i++) {
            int columnNum = (i + mDayOffset) % 7;
            float x = columnNum * mWidthOfDay + mHalfWidthOfDay;
            if (x - mHalfWidthOfDay <= touchX && touchX <= x + mHalfWidthOfDay && y - mRowHeight / 2 <= touchY && touchY <= y + mRowHeight / 2) {
                day = i;
            }
            if (columnNum + 1 == COLUMN_NUM) {
                y += mRowHeight;
            }
        }
        return day;
    }

    private void toggleHighlight(int i) {
        if (mDayArray.get(i, HighlightStyle.NO_HIGHLIGHT) == HighlightStyle.NO_HIGHLIGHT) {
            mDayArray.append(i, HighlightStyle.SOLID_CIRCLE);
        } else {
            mDayArray.append(i, HighlightStyle.NO_HIGHLIGHT);
        }
    }

    private int getDayOfMonth() {
        if (mToday == 0) {
            Calendar cal = Calendar.getInstance();
            mToday = cal.get(Calendar.DAY_OF_MONTH);
        }
        return mToday;
    }

    private int getDayOffset() {
        // TODO provide method to change the default selected day
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int startDayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
        int offset = startDayOfWeek - mFirstDayOfWeek - Calendar.SUNDAY;
        if (offset < 0) {
            return offset + 7;
        } else {
            return offset;
        }
    }

    /**
     * Set the calendar.
     * This calendar instance is the fundamental of this MonthView class.
     *
     * @param calendar the calendar you want this MonthView to show.
     */
    public void setCalendar(Calendar calendar) {
        mCalendar = calendar;
    }

    /**
     * Set the day sparseArray.
     * onDraw method is base on this array to decide whether this day should be highlighted and which style it should be highlighted.
     *
     * @param array a sparseArray , the key is the day, the value is the style you wanna highlight in {@link HighlightStyle}
     */
    public void setDayStyleArray(SparseArray<HighlightStyle> array) {
        mDayArray = array;
    }

    public void setOnDayClickListener(OnDayClickedListener listener) {
        this.mOnDayClickedListener = listener;
    }

    public void setDayClickable(boolean clickable) {
        mDayClickable = clickable;
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

    /**
     * Listener of every single "day".
     * You may use setOnDayClickListener and listen to click(touch exactly) event over every single "day".
     */
    public interface OnDayClickedListener {
        void onDayClicked(int day);
    }
}
