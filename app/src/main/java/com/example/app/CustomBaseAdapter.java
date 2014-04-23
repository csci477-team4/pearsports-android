package com.example.app;

/**
 * Created by tcparker on 3/25/14.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app.trainee.TraineeContent;

import java.util.List;

public class CustomBaseAdapter extends BaseAdapter implements View.OnClickListener {

    Context context;
    List<RowItem> rowItems;
    SharedPreferences pref;

    public CustomBaseAdapter(Context context, List<RowItem> items) {
        this.context = context;
        this.rowItems = items;
        this.pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageTrainee;
        ImageView imageArrow;
        TextView txtName;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.trainee_row, null);
            holder = new ViewHolder();
            holder.imageTrainee = (ImageView) convertView.findViewById(R.id.trainee_pic);
            holder.imageArrow = (ImageView) convertView.findViewById(R.id.right_arrow);
            holder.txtName = (TextView) convertView.findViewById(R.id.trainee_name);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        RowItem rowItem = (RowItem) getItem(position);

        holder.imageTrainee.setImageResource(rowItem.getTraineeId());
        holder.imageArrow.setImageResource(rowItem.getRightArrow());
        holder.txtName.setText(rowItem.getTraineeName());

        View v = convertView;

        ImageView tp = (ImageView) v.findViewById(R.id.trainee_pic);
        tp.setTag(new Integer(position));
        tp.setOnClickListener(this);

        TextView tn = (TextView) v.findViewById(R.id.trainee_name);
        tn.setTag(new Integer(position));
        tn.setOnClickListener(this);

        ImageView ra = (ImageView) v.findViewById(R.id.right_arrow);
        ra.setTag(new Integer(position));
        ra.setOnClickListener(this);

        return convertView;
    }

    public void onClick(View v) {

        Integer pos = (Integer) v.getTag();

        SharedPreferences.Editor edit = pref.edit();
        edit.putString("trainee_id", TraineeContent.TRAINEES.get(pos).id);
        edit.apply();

        Intent i = new Intent(context, WorkoutHistoryActivity.class);
        i.putExtra(TraineeDetailFragment.ARG_ITEM_ID, TraineeContent.TRAINEES.get(pos).id);
        i.putExtra("trainee_id", TraineeContent.TRAINEES.get(pos).id);
        i.putExtra("name", TraineeContent.TRAINEES.get(pos).name);
        v.getContext().startActivity(i);
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }
}
