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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * MonthView
 * @author nich
 * Guangzhou, China, Asia, Earth.
 */

public class MonthView extends View {
    private static final String TAG = MonthView.class.getSimpleName();

    private Paint mDayTextPaint;
    private Paint mHintDayTextPaint;
    private Paint mWeekdayTextPaint;
    private Paint mHighlightedDayTextPaint;
    private Paint mHighlightedCirclePaint;
    private Paint mHighlightedRingPaint;

    private static final int DEFAULT_WEEKDAY_HEIGHT = 30;
    private static final int DEFAULT_DAY_HEIGHT = 40;
    private static final int DEFAULT_DAY_TEXT_SIZE = 14;
    private static final int DEFAULT_DAY_RADIUS = 18;
    private static final int PADDING_BOTTOM = 8;

    private static final int COLUMN_NUM = 7;
    private static final int ROW_NUM = 6;

    private String[] mWeekdayName;
    private SparseArray<HighlightStyle> mDayArray; // Array of storing highlight style of day;
    private OnDayClickedListener mOnDayClickedListener;
    private OnDaySelectedListener mOnDaySelectedListener;

    private int mWeekdayTextColor;
    private int mHighlightColor;
    private int mDayTextColor;
    private int mHighlightTextColor;
    private int mHintDayTextColor;

    private int mWeekdayHeight;
    private int mRowHeight;
    private int mPaddingBottom;
    private int mDayRadius; // Radius of highlighted day's circle.
    private int mTextSize;

    private int mFirstDayOfWeek;
    private int mMonthDayCount;
    private int mPreMonthDayCount;
    private int mDayOffset;

    private float mWidthOfDay;
    private float mHalfWidthOfDay;

    private float mLastDayCoordinateY;
    private int mLastDayColumn;

    private boolean mDayClickable;

    private Mode mMode;
    private Calendar mCalendar; // calendar of this month

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

    // TODO Let the value listed below can be set by users.
    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MonthView);
            mRowHeight = (int) a.getDimension(R.styleable.MonthView_day_height, dp2px(DEFAULT_DAY_HEIGHT));
            mTextSize = (int) a.getDimension(R.styleable.MonthView_day_textSize, sp2px(DEFAULT_DAY_TEXT_SIZE));
            mDayRadius = (int) a.getDimension(R.styleable.MonthView_day_radius, dp2px(DEFAULT_DAY_RADIUS));
            mDayClickable = a.getBoolean(R.styleable.MonthView_day_clickable, true);

            mWeekdayTextColor = a.getColor(R.styleable.MonthView_weekday_textColor, getResources().getColor(R.color.nc_default_weekday_text_color));
            mHighlightColor = a.getColor(R.styleable.MonthView_highlight_color, getResources().getColor(R.color.nc_default_highlight_color));
            mDayTextColor = a.getColor(R.styleable.MonthView_day_textColor, getResources().getColor(R.color.nc_default_day_text_color));
            mHintDayTextColor = a.getColor(R.styleable.MonthView_hint_day_textColor, getResources().getColor(R.color.nc_default_hint_day_text_color));
            mHighlightTextColor = a.getColor(R.styleable.MonthView_highlight_day_textColor, getResources().getColor(R.color.nc_default_highlight_day_text_color));
            a.recycle();
        }
    
        Locale chineseLocale = new Locale("zh");
        Locale currentLocale = Locale.getDefault();
        boolean isChineseUser = currentLocale.getLanguage().equals(chineseLocale.getLanguage());
        if (isChineseUser) {
            mWeekdayName = getContext().getResources().getStringArray(R.array.monday_first_chinese_weekday_name);
        } else {
            mWeekdayName = getContext().getResources().getStringArray(R.array.monday_first_english_weekday_name);
        }
        
        mPaddingBottom = dp2px(PADDING_BOTTOM);
        mWeekdayHeight = dp2px(DEFAULT_WEEKDAY_HEIGHT);
        mFirstDayOfWeek = Calendar.MONDAY; // Default value of start day of the week.

        mMode = Mode.DISPLAY_ONLY;
        mDayArray = new SparseArray<>();
        setCalendar(Calendar.getInstance()); // Use current day to get a calendar instance by default.
    }

    private void initPaint() {
        mWeekdayTextPaint = new Paint();
        mWeekdayTextPaint.setAntiAlias(true);
        mWeekdayTextPaint.setFakeBoldText(true);
        mWeekdayTextPaint.setTextSize(mTextSize);
        mWeekdayTextPaint.setStyle(Paint.Style.FILL);
        mWeekdayTextPaint.setColor(mWeekdayTextColor);
        mWeekdayTextPaint.setTextAlign(Paint.Align.CENTER);

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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mWeekdayHeight + mRowHeight * ROW_NUM + mPaddingBottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // initial the width of every "day"
        mWidthOfDay = w / COLUMN_NUM;
        mHalfWidthOfDay = mWidthOfDay / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawWeekdayText(canvas);
        drawDays(canvas);
        drawHintDays(canvas);
    }

    private void drawWeekdayText(Canvas canvas) {
        float y = mWeekdayHeight - mTextSize / 2;

        for (int i = 0; i < COLUMN_NUM; i++) {
            float x = i * mWidthOfDay + mHalfWidthOfDay;
            int dayOfWeek = i % COLUMN_NUM;  // TODO provide method to change the first day of the week.
            String weekday = getWeekdayName(dayOfWeek);
            canvas.drawText(weekday, x, y, mWeekdayTextPaint);
        }
    }

    private void drawDays(Canvas canvas) {
        float x = 0;
        float y = mWeekdayHeight + mRowHeight - mTextSize / 2; // start at the bottom of weekday and take the center vertical coordinate as y
        int columnNum = 0;

        for (int day = 1; day <= mMonthDayCount; day++) {
            columnNum = (day + mDayOffset) % 7;
            x = columnNum * mWidthOfDay + mHalfWidthOfDay;

            HighlightStyle style = mDayArray.get(day, HighlightStyle.NO_HIGHLIGHT);
            drawSpecificDay(canvas, style, day, x, y);

            if (columnNum + 1 == COLUMN_NUM) {
                y += mRowHeight;
            }
        }

        mLastDayCoordinateY = y;
        mLastDayColumn = columnNum;
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
        float y = mWeekdayHeight + mRowHeight - mTextSize / 2;

        for (int day = mPreMonthDayCount, offset = mDayOffset; day > 0; day--, offset--) {
            float x = offset * mWidthOfDay + mHalfWidthOfDay;
            drawDayText(canvas, day, x, y, mHintDayTextPaint);
        }
    }

    private void drawHintDaysInNextMonth(Canvas canvas) {
        float x;
        float y = mLastDayCoordinateY;
        int bottom = getBottom();
        float columnNum = mLastDayColumn + 1 >= COLUMN_NUM ? 0 : mLastDayColumn + 1;

        for (int day = 1; day <= 14; day++) {
            x = columnNum * mWidthOfDay + mHalfWidthOfDay;
            drawDayText(canvas, day, x, y, mHintDayTextPaint);
            columnNum++;
            if (columnNum >= COLUMN_NUM) {
                y += mRowHeight;
                columnNum = 0;
            }
            if (y >= bottom) break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mDayClickable) {
                    int day = locateClickedDay(event);
                    if (0 <= day) {
                        if (mMode == Mode.SELECT) {
                            highlightDay(day);
                        } else if (mMode == Mode.DISPLAY_ONLY) {
                            makeClickCallback(day);
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

        float y = mRowHeight * 2 - mRowHeight / 2; // start at the bottom of weekday and take the center vertical coordinate as y

        for (int i = 1; i <= mMonthDayCount; i++) {
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
        boolean isActionOn = mDayArray.get(i, HighlightStyle.NO_HIGHLIGHT) == HighlightStyle.NO_HIGHLIGHT;
        if (isActionOn) {
            mDayArray.append(i, HighlightStyle.SOLID_CIRCLE);
        } else {
            mDayArray.append(i, HighlightStyle.NO_HIGHLIGHT);
        }

        if (mMode == Mode.SELECT) {
            makeSelectCallback(i, isActionOn);
        }
    }

    private void makeSelectCallback(int i, boolean isActionOn) {
        if (mOnDaySelectedListener == null)
            return;

        if (isActionOn) {
            mOnDaySelectedListener.onDayJustSelected(i);
        } else {
            mOnDaySelectedListener.onDayJustDeselected(i);
        }
    }

    private void makeClickCallback(int day) {
        if (mOnDayClickedListener != null) {
            mOnDayClickedListener.onDayClicked(day);
        }
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
        if (mCalendar != calendar) {
            mCalendar = calendar;

            Calendar preCalendar = (Calendar) mCalendar.clone();
            preCalendar.add(Calendar.MONTH, -1);
            mDayOffset = getDayOffset();
            mMonthDayCount = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            mPreMonthDayCount = preCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            postInvalidate();
        }
    }

    /**
     * Get days that the user selected as a int array.
     *
     * @return selected days.
     */
    public int[] getSelectedDays() {
        ArrayList<Integer> mSelectedDays = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            if (mDayArray.get(i, HighlightStyle.NO_HIGHLIGHT) != HighlightStyle.NO_HIGHLIGHT) {
                mSelectedDays.add(i);
            }
        }
        int[] selectedDays = new int[mSelectedDays.size()];
        for (int i = 0; i < mSelectedDays.size(); i++) {
            selectedDays[i] = mSelectedDays.get(i);
        }
        return selectedDays;
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

    public void setOnDaySelectListener(OnDaySelectedListener listener){
        this.mOnDaySelectedListener = listener;
    }

    public void setMode(Mode mode){
        mMode = mode;
    }
    
    /**
     * Decide whether the day can be clicked or not.
     * Default value is true.
     */
    public void setDayClickable(boolean clickable) {
        mDayClickable = clickable;
    }

    private String getWeekdayName(int dayOfWeek) {
        return mWeekdayName[dayOfWeek];
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    /**
     * Listener of every single "day". It works only in {@link Mode.DISPLAY_ONLY} mode.
     * You may use {@link #setOnDayClickListener(OnDayClickedListener)} and listen to click(touch exactly) event over every single "day".
     */
    @SuppressWarnings("JavadocReference")
    public interface OnDayClickedListener {
        void onDayClicked(int day);
    }

    /**
     * Listener of selected days.It works only in {@link Mode.SELECT} mode.
     * You may use {@link #setOnDaySelectListener(OnDaySelectedListener)} and listen to select and deselect action over every single "day".
     */
    @SuppressWarnings("JavadocReference")
    public interface OnDaySelectedListener {
        void onDayJustSelected(int day);

        void onDayJustDeselected(int day);
    }

    /**
     * In DISPLAY_ONLY mode you can only set a {@link OnDayClickedListener} by {@link #setOnDayClickListener(OnDayClickedListener)}
     * and do whatever you what in onDayClicked(day) callback.
     *
     * In SELECT mode you can call {@link #getSelectedDays()} to get user's selection.
     */
    public enum Mode {
        DISPLAY_ONLY, SELECT
    }
}
