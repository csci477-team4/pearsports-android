package com.example.app;

/**
 * Created by tcparker on 4/28/14.
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

import java.util.List;

public class WorkoutAdapter extends BaseAdapter implements View.OnClickListener {

    Context context;
    List<WorkoutItem> workouts;
    SharedPreferences pref;

    public WorkoutAdapter(Context context, List<WorkoutItem> workouts) {
        this.context = context;
        this.workouts = workouts;
        this.pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView image;
        TextView name;
        TextView desc;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        WorkoutItem item = (WorkoutItem) getItem(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.image_text_row, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.pic);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.desc = (TextView) convertView.findViewById(R.id.desc);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.image.setImageDrawable(item.getPic());
        holder.name.setText(item.getName());
        holder.desc.setText(item.getDesc());

        View v = convertView;

        ImageView p = (ImageView) v.findViewById(R.id.pic);
        p.setTag(new Integer(position));
        p.setOnClickListener(this);

        TextView n = (TextView) v.findViewById(R.id.name);
        n.setTag(new Integer(position));
        n.setOnClickListener(this);

        TextView d = (TextView) v.findViewById(R.id.desc);
        d.setTag(new Integer(position));
        d.setOnClickListener(this);

        return convertView;
    }

    public void onClick(View v) {

        Integer pos = (Integer) v.getTag();

        Intent i = new Intent(context, ScheduleWorkoutActivity.class);
        i.putExtra("trainee_id", pref.getString("trainee_id", ""));
        i.putExtra("sku", workouts.get(pos).getSku());
        i.putExtra("name", workouts.get(pos).getName());
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        v.getContext().startActivity(i);
    }

    @Override
    public int getCount() {
        return workouts.size();
    }

    @Override
    public Object getItem(int position) {
        return workouts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return workouts.indexOf(getItem(position));
    }

}
