package au.com.psilisoft.www.rosterbuilder;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import au.com.psilisoft.www.rosterbuilder.custom_objects.Availability;
import au.com.psilisoft.www.rosterbuilder.custom_objects.TimedSegment;
import au.com.psilisoft.www.rosterbuilder.provider.Contract;

/**
 * Created by Fletcher on 2/08/2015.
 */
class AvailabilityAdapter extends BaseAdapter {

    private Context mContext;
    private List<Availability> mList;
    private int mEarliestTime = 24*60;
    private int mLatestTime = 0;

    public AvailabilityAdapter(Context context, List<Availability> list) {
        mContext = context;
        setList(list);
    }

    public void setList(List<Availability> list) {
        mList = list;
        sortList();
        notifyDataSetChanged();
    }

    private void sortList() {

        int size = mList.size();
        Availability first;
        Availability second;
        for (int i = 0; i < size; i++) {
            first = mList.get(i);
            for (int j = i + 1; j < size; j++) {
                second = mList.get(j);
                if ((first.getDay() + 5) % 7 > (second.getDay() + 5) % 7) {
                    first = second;
                }
            }
            Collections.swap(mList, i, mList.indexOf(first));
            checkEarliestAndLatest(first);
        }
    }

    private void checkEarliestAndLatest(Availability segment) {

        int startTime = segment.getStartTime().get(Calendar.HOUR_OF_DAY) * 60
                + segment.getStartTime().get(Calendar.MINUTE);
        int finishTime = segment.getFinishTime().get(Calendar.HOUR_OF_DAY) * 60
                + segment.getFinishTime().get(Calendar.MINUTE);
        if (startTime < mEarliestTime) mEarliestTime = startTime;
        if (finishTime > mLatestTime) mLatestTime = finishTime;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Availability availability = mList.get(position);

        ViewHolder holder;
        if (view == null) {
            view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.row_availability, parent, false);
            holder = new ViewHolder();
            holder.textDay = (TextView) view.findViewById(R.id.text_view_day);
            holder.textStartTime = (TextView) view.findViewById(R.id.text_view_start_time);
            holder.textFinishTime = (TextView) view.findViewById(R.id.text_view_finish_time);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.textDay.setText(availability.getDateString());
        holder.textStartTime.setText(availability.getStartTimeString());
        holder.textFinishTime.setText(availability.getFinishTimeString());

        int startTime = availability.getStartTime().get(Calendar.HOUR_OF_DAY) * 60
                + availability.getStartTime().get(Calendar.MINUTE);
        int finishTime = availability.getFinishTime().get(Calendar.HOUR_OF_DAY) * 60
                + availability.getFinishTime().get(Calendar.MINUTE);

        AbsListView.LayoutParams params = (AbsListView.LayoutParams) view.getLayoutParams();
        params.width = (int) (parent.getWidth()
                * (finishTime - startTime) / (float) (mLatestTime - mEarliestTime));

        view.setTranslationX(parent.getWidth()
                * (startTime - mEarliestTime) / (float) (mLatestTime - mEarliestTime));

        return view;
    }

    private class ViewHolder {
        public TextView textDay;
        public TextView textStartTime;
        public TextView textFinishTime;
    }

}
