package com.example.app;

/**
 * Created by tcparker on 3/25/14.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.app.trainee.TraineeContent;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomBaseAdapter extends BaseAdapter implements View.OnClickListener {

    Context context;
    List<RowItem> rowItems;
    SharedPreferences pref;
    TraineeContent traineeContent = TraineeContent.getInstance();

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
        GraphicalView graph;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        RowItem rowItem = (RowItem) getItem(position);

        int[] incomplete = rowItem.getIncomplete();
        int[] complete = rowItem.getComplete();
        int[] marked = rowItem.getMarked();
        int[] scheduled = rowItem.getScheduled();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.trainee_row, null);
            holder = new ViewHolder();
            holder.imageTrainee = (ImageView) convertView.findViewById(R.id.trainee_pic);
            holder.imageArrow = (ImageView) convertView.findViewById(R.id.right_arrow);
            holder.txtName = (TextView) convertView.findViewById(R.id.trainee_name);

            GraphicalView gv = createGraph(incomplete, complete, marked, scheduled);
            holder.graph = gv;
            RelativeLayout rl = (RelativeLayout) convertView.findViewById(R.id.graph_layout);
            rl.addView(gv);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

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
        Log.d("CustomBaseAdapter: ", "onClick " + v.getId());
        loadTraineeContent();
        edit.putString("trainee_id", traineeContent.TRAINEES.get(pos).id);
        edit.apply();

        Intent i = new Intent(context, WorkoutHistoryActivity.class);
        i.putExtra(TraineeDetailFragment.ARG_ITEM_ID, traineeContent.TRAINEES.get(pos).id);
        i.putExtra("trainee_id", traineeContent.TRAINEES.get(pos).id);
        i.putExtra("name", traineeContent.TRAINEES.get(pos).name);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        v.getContext().startActivity(i);
    }

    public GraphicalView createGraph(int[] incomplete, int[] complete, int[] marked, int[] scheduled) {
        String[] titles = new String[] { "Completed", "Missed","Scheduled" };
        List<int[]> values = new ArrayList<int[]>();
        String graph_title = null;
        int week = 0;

        switch(week) {
            case 0:
                graph_title = "This Week";
                break;
            case 1:
                graph_title = "Last Week";
                break;
            case 2:
                graph_title = "Two Weeks Ago";
                break;
        }

        //values.add(new double[] { S, M, T, W, T, F, S});
        values.add(complete); // completed
        values.add(incomplete); // missed
        values.add(scheduled); // scheduled
        int[] colors = new int[] { Color.parseColor("#00EB23"), Color.parseColor("#D11F00"), Color.parseColor("#00C8EC")};
        XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
        renderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
        setChartSettings(renderer, graph_title, "", "# Workouts", 0.5,
                7.5, 0.5, 4, Color.WHITE, Color.WHITE);
        renderer.setXLabels(0);
        renderer.setYLabels(0);
        renderer.addXTextLabel(1, "Sun");
        renderer.addXTextLabel(2, "Mon");
        renderer.addXTextLabel(3, "Tue");
        renderer.addXTextLabel(4, "Wed");
        renderer.addXTextLabel(5, "Thu");
        renderer.addXTextLabel(6, "Fri");
        renderer.addXTextLabel(7, "Sat");
        renderer.addYTextLabel(1, "1");
        renderer.addYTextLabel(2, "2");
        renderer.addYTextLabel(3, "3");
        int length = renderer.getSeriesRendererCount();
        for (int j = 0; j < length; j++) {
            SimpleSeriesRenderer seriesRenderer = renderer.getSeriesRendererAt(j);
            seriesRenderer.setDisplayChartValues(false);
        }


        final GraphicalView grfv = ChartFactory.getBarChartView(context, buildBarDataset(titles, values), renderer, BarChart.Type.STACKED);
        return grfv;
    }
    protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(20);
        renderer.setChartTitleTextSize(30);
        renderer.setLabelsTextSize(20);
        renderer.setLegendTextSize(20);
        renderer.setBarSpacing(1);
        renderer.setPanEnabled(false, false);

        renderer.setMarginsColor(Color.parseColor("#565656"));
        renderer.setXLabelsColor(Color.WHITE);
        renderer.setYLabelsColor(0,Color.WHITE);

        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.parseColor("#565656"));

        int length = colors.length;
        for (int i = 0; i < length; i++) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(colors[i]);
            r.setChartValuesSpacing(0);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }
    protected XYMultipleSeriesDataset buildBarDataset(String[] titles, List<int[]> values) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        int length = titles.length;
        for (int i = 0; i < length; i++) {
            CategorySeries series = new CategorySeries(titles[i]);
            int[] v = values.get(i);
            int seriesLength = v.length;
            for (int k = 0; k < seriesLength; k++) {
                series.add(v[k]);
            }
            dataset.addSeries(series.toXYSeries());
        }
        return dataset;
    }
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
                                    String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
                                    int labelsColor) {
        renderer.setChartTitle(title);
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setMargins(new int[] { 35, 65, 10, 15 });
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
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

    private boolean loadTraineeContent() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(context.getFilesDir() + "trainee_content.txt")));
            traineeContent = (TraineeContent) ois.readObject();
            Log.d("CustomBaseAdapter >> ", "OIS readObject.");
            traineeContent.printTraineeList();
            return true;
        } catch (Exception ex) {
            Log.v("Serialization Read Error : ", ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    private String arrayToString(int[] array) {
        String s = "";

        for(int i=0; i<array.length; i++) {
            s = s + array[i];
        }

        return s;
    }
}
