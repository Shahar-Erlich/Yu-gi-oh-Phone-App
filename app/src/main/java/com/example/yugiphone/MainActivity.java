package com.example.yugiphone;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.apache.commons.math3.distribution.HypergeometricDistribution;
import org.apache.commons.math3.geometry.euclidean.twod.Line;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;

public class MainActivity extends AppCompatActivity {

    // Constants for deck size and max cards
    private static final int DECK_SIZE = 40;
    private static final int MAX_CARDS = 4;  // Max copies of a card in the deck (1 to 6)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        SeekBar seekBar = findViewById(R.id.seekBar);
        TextView sliderValue = findViewById(R.id.sliderValue);
        BarChart chart = findViewById(R.id.chart);
        LineChart chartEx1 = findViewById(R.id.Exact1);
        LineChart chartEx2 = findViewById(R.id.Exact2);
        LineChart chartEx3 = findViewById(R.id.Exact3);
        LineChart chartEx4 = findViewById(R.id.Exact4);
        updateChartAL(chart, 5);
        updateChart(chartEx1,5,1,Color.RED);
        updateChart(chartEx2,5,2,Color.MAGENTA);
        updateChart(chartEx3,5,3,Color.YELLOW);
        updateChart(chartEx4,5,4,Color.BLUE);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) progress = 1;
                sliderValue.setText("Hand size: " + progress);
                updateChartAL(chart, progress);
                updateChart(chartEx1,progress,1,Color.RED);
                updateChart(chartEx2,progress,2,Color.MAGENTA);
                updateChart(chartEx3,progress,3,Color.YELLOW);
                updateChart(chartEx4,progress,4,Color.BLUE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
    private double calculateProbabilityBar(int handSize, int numCopies) {
        // Create the hypergeometric distribution for the deck and the number of copies of a card
        HypergeometricDistribution hyper = new HypergeometricDistribution(DECK_SIZE, numCopies, handSize);

        // Calculate the probability of drawing 0 copies (not drawing any of the copies)
        double probNotDrawing = hyper.probability(0);

        // Return the probability of drawing at least 1 copy by subtracting the probability of drawing 0 copies
        return 1 - probNotDrawing;
    }
    private double calculateProbability(int handSize, int numCopies,int num) {
        // Create the hypergeometric distribution for the deck and the number of copies of a card
        HypergeometricDistribution hyper = new HypergeometricDistribution(DECK_SIZE, numCopies, handSize);

        // Calculate the probability of drawing 0 copies (not drawing any of the copies)
        double probNotDrawing = hyper.probability(num);

        // Return the probability of drawing at least 1 copy by subtracting the probability of drawing 0 copies
        return probNotDrawing;
    }

    private void setAxisBar(BarChart barChart) {
        // Set up X-Axis labels
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(-1f);  // Ensure the minimum value aligns with the first bar
        xAxis.setAxisMaximum(4f);   // Set maximum to 4 to cover the range from 0 to 3 (4 bars)
        xAxis.setTextSize(18f);
        xAxis.setGranularity(1f);  // Only 1 value between intervals
        xAxis.setDrawGridLines(false);

        // Set X-Axis labels corresponding to indices 1, 2, 3, 4
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"1", "2", "3", "4"}));  // X-Axis labels

        // Set up Y-Axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);  // Start at 0 on the Y-Axis (0% = 0)
        leftAxis.setAxisMaximum(1f);  // End at 100% on the Y-Axis (1.0 corresponds to 100%)
        leftAxis.setGranularity(0.1f);  // Set steps of 10% (0.1 = 10%, 0.2 = 20%, etc.)
        leftAxis.setLabelCount(11, true);

        // Adjust Y-Axis to show 10% intervals (formatted as percentages)
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int)(value * 100) + "%";  // Convert to percentage format
            }
        });
        leftAxis.setDrawGridLines(false);
        // To ensure the Y-Axis shows 10% steps and the labels fit within the desired range
        leftAxis.setSpaceTop(10f);  // Adds a little space at the top
        leftAxis.setSpaceBottom(10f);  // Adds a little space at the bottom

        // Disable right axis (Optional)
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Set chart description (Optional)
        barChart.getDescription().setEnabled(false);  // Disable description
        // Center the labels on the bars
        barChart.getBarData().setBarWidth(0.7f);  // Adjust bar width to ensure they're well spaced
        barChart.setFitBars(true);  // Fit bars to the chart
    }
    private void setAxisLine(LineChart lineChart) {
        // Set up X-Axis labels
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(-1f);  // Ensure the minimum value aligns with the first point
        xAxis.setAxisMaximum(4f);   // Set maximum to 4 to cover the range from 0 to 3 (4 points)
        xAxis.setTextSize(18f);
        xAxis.setGranularity(1f);  // Only 1 value between intervals
        xAxis.setDrawGridLines(false);

        // Set X-Axis labels corresponding to indices 1, 2, 3, 4
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"1", "2", "3", "4"}));  // X-Axis labels

        // Set up Y-Axis
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);  // Start at 0 on the Y-Axis (0% = 0)
        leftAxis.setAxisMaximum(1f);  // End at 100% on the Y-Axis (1.0 corresponds to 100%)
        leftAxis.setGranularity(0.1f);  // Set steps of 10% (0.1 = 10%, 0.2 = 20%, etc.)
        leftAxis.setLabelCount(11, true);

        // Adjust Y-Axis to show 10% intervals (formatted as percentages)
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int)(value * 100) + "%";  // Convert to percentage format
            }
        });
        leftAxis.setDrawGridLines(false);
        // To ensure the Y-Axis shows 10% steps and the labels fit within the desired range
        leftAxis.setSpaceTop(10f);  // Adds a little space at the top
        leftAxis.setSpaceBottom(10f);  // Adds a little space at the bottom

        // Disable right axis (Optional)
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Set chart description (Optional)
        lineChart.getDescription().setEnabled(false);  // Disable description

        // Set up the LineData and adjust line properties (Optional)
        LineData lineData = lineChart.getData();
        if (lineData != null) {
            LineDataSet dataSet = (LineDataSet) lineData.getDataSetByIndex(0);  // Assuming there's only one data set
            dataSet.setLineWidth(6f);  // Adjust the line width
            dataSet.setCircleRadius(4f);  // Adjust the circle size on data points
            dataSet.setDrawCircles(true);  // Enable circles for data points
            dataSet.setValueTextSize(10f);
           // dataSet.setDrawValues(false);  // Disable values on data points
        }

        // Fit the chart to the data
        lineChart.invalidate();  // Refresh the chart
    }



    private void updateChartAL(BarChart chart, int handSize) {
        // Create data points for the chart, representing probability of drawing 1...n copies from 40 cards
        List<BarEntry> entries = new ArrayList<>();

        // Loop over possible number of copies (from 1 to MAX_CARDS)
        for (int i = 1; i <= MAX_CARDS; i++) {
            double probability = calculateProbabilityBar(handSize, i);
            entries.add(new BarEntry(i - 1, (float) probability));
        }

        // Create a dataset for the bar chart
        BarDataSet dataSet = new BarDataSet(entries, "Drawing at least 1");
        dataSet.setValueTextSize(18f);
        BarData barData = new BarData(dataSet);
        chart.setData(barData);
        chart.setTouchEnabled(false);
        chart.setDrawGridBackground(false);
        barData.setValueFormatter(new PercentFormatter());

        // Set axis, legend and refresh the chart
        setAxisBar(chart);
        chart.getLegend().setEnabled(false);
        chart.invalidate(); // Refresh the chart
    }

    private void updateChart(LineChart chart, int handSize, int number,int Color) {
        List<Entry> entries = new ArrayList<>();

        for (int i = 1; i <= MAX_CARDS; i++) {
            double probability = calculateProbability(handSize, i, number);
            entries.add(new BarEntry(i - 1, (float) probability));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Drawing Exactly " + number);
        dataSet.setValueTextSize(18f);
        dataSet.setColor(Color);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.setDrawGridBackground(false);
        dataSet.setDrawValues(true);
        lineData.setValueFormatter(new PercentFormatter());

        // Set axis, legend and refresh the chart
        setAxisLine(chart);
        chart.getLegend().setEnabled(false);
        chart.invalidate(); // Refresh the chart
    }

    public class PercentFormatter extends ValueFormatter {
        private DecimalFormat df = new DecimalFormat("###,###,##0.00%");

        @Override
        public String getFormattedValue(float value) {
            return df.format(value);
        }
    }
}
