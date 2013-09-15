package com.example.poc_gps;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.LinearLayout;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		int num = 150;
		GraphViewData[] data = new GraphViewData[num];
		double v = 0;
		for (int i = 0; i < num; i++) {
			v += 0.2;
			data[i] = new GraphViewData(i, Math.sin(v));
		}

		// init example series data
		GraphViewSeries exampleSeries = new GraphViewSeries("serie1",
				new GraphViewSeriesStyle(Color.rgb(200, 50, 00), 1), data);

		GraphViewSeries exampleSeries2 = new GraphViewSeries("serie2",
				new GraphViewSeriesStyle(Color.rgb(50, 200, 00), 1),
				new GraphViewData[] { new GraphViewData(1, 4.0d),
						new GraphViewData(2, 3.0d), new GraphViewData(3, 2.0d),
						new GraphViewData(4, 1.0d), new GraphViewData(5, 3.5d),
						new GraphViewData(6, 4.5d)

				});

		/*
		 * 
		 * 
		 * 
		 * Line chart
		 */
		GraphView graphViewLine = new LineGraphView(this // context
				, "GraphViewDemo" // heading
		);

		// put the data on the chart
		graphViewLine.addSeries(exampleSeries);
		graphViewLine.addSeries(exampleSeries2);

		// legend
		graphViewLine.setShowLegend(true);
		graphViewLine.setLegendAlign(LegendAlign.BOTTOM);
		graphViewLine.setLegendWidth(200);

		// the x axe value to start and size of the window
		graphViewLine.setViewPort(1, 40);

		// enable scroll and zoom
		graphViewLine.setScrollable(true);
		graphViewLine.setScalable(true);

		// add the chart to its layout
		LinearLayout layoutLine = (LinearLayout) findViewById(R.id.linearLayoutLine);
		layoutLine.addView(graphViewLine);

		/*
		 * 
		 * 
		 * 
		 * Bar chart
		 */
		GraphView graphViewBar = new BarGraphView(this // context
				, "GraphViewDemo" // heading
		);
		// put the data on the chart
		graphViewBar.addSeries(exampleSeries2);

		// legend
		graphViewBar.setShowLegend(true);
		graphViewBar.setLegendAlign(LegendAlign.BOTTOM);
		graphViewBar.setLegendWidth(200);

		// the x axe value to start and size of the window
		graphViewBar.setViewPort(1, 40);

		// enable scroll and zoom
		graphViewBar.setScrollable(true);
		graphViewBar.setScalable(true);

		// add the chart to its layout
		LinearLayout layoutBar = (LinearLayout) findViewById(R.id.linearLayoutBar);
		layoutBar.addView(graphViewBar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

}
