package work.nich.calendar;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import work.nich.view.MonthView;

public class MonthAdapter extends PagerAdapter{
    private List<MonthView> mMonthList;
    
    public void setMonthList(List<MonthView> list){
        mMonthList = list;
    }
    
    @Override
    public int getCount() {
        return mMonthList.size();
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mMonthList.get(position));
        return mMonthList.get(position);
    }
    
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mMonthList.get(position));
    }
    
    public MonthView getMonthView(int position){
        return mMonthList.get(position);
    }
}
