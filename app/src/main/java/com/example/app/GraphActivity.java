package com.example.app;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.ValueDependentColor;


public class GraphActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        /* Color the bars blue */
        GraphViewSeries.GraphViewSeriesStyle blueStyle = new GraphViewSeries.GraphViewSeriesStyle();
        blueStyle.setValueDependentColor(new ValueDependentColor() {
            @Override
            public int get(GraphViewDataInterface data) {
                return Color.rgb(0, 200, 236);
            }
        });

        /* Color the bars red */
        GraphViewSeries.GraphViewSeriesStyle redStyle = new GraphViewSeries.GraphViewSeriesStyle();
        redStyle.setValueDependentColor(new ValueDependentColor() {
            @Override
            public int get(GraphViewDataInterface data) {
                return Color.rgb(200, 50, 0);
            }
        });

        /* Set title of chart by week */
        String title = "Workout History";
        int week = 0;
        switch(week) {
            case 0:
                title = "This Week";
                break;
            case 1:
                title = "Last Week";
                break;
            case 2:
                title = "Two Weeks Ago";
                break;
        }

        int num = 8;

        /* Data for completed workouts */
        GraphView.GraphViewData[] data = new GraphView.GraphViewData[4];
        data[0] = new GraphView.GraphViewData(0, 0.0d);
        data[1] = new GraphView.GraphViewData(1, 1.0d);
        data[2] = new GraphView.GraphViewData(3, 3.0d);
        data[3] = new GraphView.GraphViewData(6, 3.0d);
        GraphViewSeries completed = new GraphViewSeries("Completed", blueStyle, data);

        /* Data for missed workouts */
        data = new GraphView.GraphViewData[5];
        data[0] = new GraphView.GraphViewData(2, 2.0d);
        data[1] = new GraphView.GraphViewData(4, 1.0d);
        data[2] = new GraphView.GraphViewData(5, 2.0d);
        data[3] = new GraphView.GraphViewData(6, 3.0d);
        data[4] = new GraphView.GraphViewData(7, 1.0d);
        GraphViewSeries missed = new GraphViewSeries("Missed", redStyle, data);

        /* Graph Design */
        GraphView graphView = new BarGraphView(this, title);
//        graphView.addSeries(completed);
        graphView.addSeries(missed);
        graphView.addSeries(completed);
        graphView.setVerticalLabels(new String[]{"3", "2", "1", " "});
        graphView.setHorizontalLabels(new String[] {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"});

        /* Add graph to layout */
        LinearLayout layout = (LinearLayout) findViewById(R.id.graph_layout);
        layout.addView(graphView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.graph, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
