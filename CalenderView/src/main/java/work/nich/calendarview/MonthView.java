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
 * MonthView
 * @author nich
 * Guangzhou, China, Asia, Earth.
 */

public class MonthView extends View {
    private Paint mDayTextPaint;
    private Paint mWeekdayIndicatorPaint;
    private Paint mHighlightedDayTextPaint;
    private Paint mHighlightedCirclePaint;
    private Paint mHighlightedRingPaint;

    // TODO Constants listed below should be able to set in layout XML.
    private static final int DEFAULT_DAY_HEIGHT = 40;
    private static final int DEFAULT_DAY_TEXT_SIZE = 14;
    private static final int COMPENSATE_HEIGHT = 8;
    private static final int DEFAULT_DAY_RADIUS = 18;

    private static final int ROW_NUM = 6;
    private static final int COLUMN_NUM = 7;

    private String[] mDaysIndicator = {"一", "二", "三", "四", "五", "六", "日"};
    private SparseArray<HighlightType> mDayArray; // Array of storing highlight type of day;
    private OnDayClickedListener mOnDayClickedListener;

    private int mIndicatorColor;
    private int mHighlightedColor;
    private int mDayTextColor;
    private int mHighlightedTextColor;

    private int mRowHeight;
    private int mCompensateHeight;
    private int mDayRadius; // Radius of highlighted day's circle.
    private int mTextSize;
    private int mWidth;
    private int mPadding;

    private int mFirstDayOfWeek;
    private int mMonthDayNum;
    private int mRowNum; // the row number of
    private int mToday;

    private float mWidthOfDay;
    private float mHalfWidthOfDay;

    private Calendar mCalendar;

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

        mDayTextPaint = new Paint();
        mDayTextPaint.setAntiAlias(true);
        mDayTextPaint.setColor(mDayTextColor);
        mDayTextPaint.setStyle(Paint.Style.FILL);
        mDayTextPaint.setTextSize(mTextSize);
        mDayTextPaint.setTextAlign(Paint.Align.CENTER);

        mHighlightedDayTextPaint = new Paint();
        mHighlightedDayTextPaint.setAntiAlias(true);
        mHighlightedDayTextPaint.setStyle(Paint.Style.FILL);
        mHighlightedDayTextPaint.setTextSize(mTextSize);
        mHighlightedDayTextPaint.setColor(mHighlightedTextColor);
        mHighlightedDayTextPaint.setTextAlign(Paint.Align.CENTER);

        mHighlightedCirclePaint = new Paint();
        mHighlightedCirclePaint.setAntiAlias(true);
        mHighlightedCirclePaint.setStyle(Paint.Style.FILL);
        mHighlightedCirclePaint.setColor(mHighlightedColor);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
            mMonthDayNum = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        if (mDayArray == null){
            mDayArray = new SparseArray<>();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mRowHeight * ROW_NUM + mCompensateHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        // initial the width of every "day"
        mWidthOfDay = mWidth / COLUMN_NUM;
        mHalfWidthOfDay = mWidthOfDay / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawWeekdayIndicator(canvas);
        drawDay(canvas);
    }

    private void drawWeekdayIndicator(Canvas canvas) {
        float y = mRowHeight - mTextSize / 2;

        for (int i = 0; i < COLUMN_NUM; i++) {
            float x = i * mWidthOfDay + mHalfWidthOfDay;
            int dayOfWeek = i % COLUMN_NUM;  // TODO provide method to change the first day of the week.
            String indicator = getWeekdayIndicator(dayOfWeek);
            canvas.drawText(indicator, x, y, mWeekdayIndicatorPaint);
        }
    }

    private void drawDay(Canvas canvas) {
        float y = mRowHeight * 2 - mTextSize / 2; // start at the bottom of indicator and take the center vertical coordinate as y
        int dayOffset = getDayOffset();

        for (int day = 1; day <= mMonthDayNum; day++) {
            int columnNum = (day + dayOffset) % 7;
            // locate every single "day"
            float x = columnNum * mWidthOfDay + mHalfWidthOfDay;

            HighlightType type = mDayArray.get(day, HighlightType.NO_HIGHLIGHT);
            drawSpecificDay(canvas, type, day, x, y);

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
     * @param type   the type of highlight style
     * @param day    the day you wanna draw
     * @param x      x-coordinate of this day (center)
     * @param y      y-coordinate of this day (center)
     */
    public void drawSpecificDay(Canvas canvas, HighlightType type, int day, float x, float y) {
        RectF rectF = new RectF(x - mDayRadius, y - mDayRadius - mTextSize / 3, x + mDayRadius, y + mDayRadius - mTextSize / 3);
        switch (type) {
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

        float y = mRowHeight * 2 - mRowHeight / 2; // start at the bottom of indicator and take the center vertical coordinate as y
        int dayOffset = getDayOffset();

        for (int i = 1; i <= mMonthDayNum; i++) {
            int columnNum = (i + dayOffset) % 7;
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
        if (mDayArray.get(i, HighlightType.NO_HIGHLIGHT) == HighlightType.NO_HIGHLIGHT) {
            mDayArray.append(i, HighlightType.SOLID_CIRCLE);
        } else {
            mDayArray.append(i, HighlightType.TOP_SEMICIRCLE);
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
     * @param array a sparseArray , the key is the day, the value is the style you wanna highlight in {@link HighlightType}
     */
    public void setDayStyleArray(SparseArray<HighlightType> array){
        mDayArray = array;
    }

    public void setOnDayClickListener(OnDayClickedListener listener) {
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

    /**
     * Listener of every single "day".
     * You may use setOnDayClickListener and listen to click(touch exactly) event over every single "day".
     */
    public interface OnDayClickedListener {
        void onDayClicked(int day);
    }
}
