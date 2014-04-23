package com.example.app;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.widget.RelativeLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;

import java.util.ArrayList;
import java.util.List;


public class GraphActivity extends Activity {
    private static final int SERIES_NR = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        final GraphicalView gv = createIntent();
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.graph_layout);
        rl.addView(gv);
    }

    public GraphicalView createIntent() {
        String[] titles = new String[] { "Completed", "Missed","Scheduled" };
        List<double[]> values = new ArrayList<double[]>();
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
        values.add(new double[] { 1, 2, 3, 2, 2, 3, 1}); // completed
        values.add(new double[] { 3, 1, 2, 3, 1, 2, 1}); // missed
        values.add(new double[] { 2, 3, 1, 2, 3, 3, 2}); // scheduled
        int[] colors = new int[] { Color.parseColor("#00C8EC"), Color.parseColor("#CC0000"),Color.parseColor("#99CC00") };
        XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
        renderer.setOrientation(Orientation.HORIZONTAL);
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
        for (int i = 0; i < length; i++) {
            SimpleSeriesRenderer seriesRenderer = renderer.getSeriesRendererAt(i);
            seriesRenderer.setDisplayChartValues(true);
        }


        final GraphicalView grfv = ChartFactory.getBarChartView(GraphActivity.this, buildBarDataset(titles, values), renderer, BarChart.Type.DEFAULT);
        return grfv;
    }
    protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setBarSpacing(1);

        renderer.setMarginsColor(Color.parseColor("#565656"));
        renderer.setXLabelsColor(Color.WHITE);
        renderer.setYLabelsColor(0,Color.WHITE);

        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.parseColor("#565656"));

        int length = colors.length;
        for (int i = 0; i < length; i++) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(colors[i]);
            r.setChartValuesSpacing(15);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }
    protected XYMultipleSeriesDataset buildBarDataset(String[] titles, List<double[]> values) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        int length = titles.length;
        for (int i = 0; i < length; i++) {
            CategorySeries series = new CategorySeries(titles[i]);
            double[] v = values.get(i);
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
        renderer.setYLabelsAlign(Align.RIGHT);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setMargins(new int[] { 10, 65, 10, 15 });
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
    }

}
