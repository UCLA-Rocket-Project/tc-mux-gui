package io.github.saltyJeff.tcmuxgui;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.util.Arrays;

public class MuxChart {
    double[] timestamps = new double[50];
    double[][] muxData = new double[8][50];
    String[] series = new String[] {"Mux 0", "Mux 1", "Mux 2", "Mux 3", "Mux 4", "Mux 5", "Mux 6", "Mux 7"};
    XYChart chart;
    SwingWrapper<XYChart> frame;
    ReadUnits lastUnit = null;
    public MuxChart () {
        chart = QuickChart.getChart("Mux data", "Timestamp", "Mux", series, timestamps, muxData);
        frame = new SwingWrapper<XYChart>(chart);
        frame.displayChart();
        double[] empty = new double[] {1, 0, 0, 0, 0, 0, 0, 0};
        for(int i = 0; i < muxData[0].length; i++) {
            updateChart(empty, 10, ReadUnits.MV);
        }
    }
    public void updateChart(double[] newData, int deltaMs, ReadUnits units) {
        if(lastUnit != units) {
            lastUnit = units;
            chart.setYAxisTitle("Mux ("+units.toString()+")");
            // throw away all XY data (its useless)
            Arrays.fill(timestamps, 0);
            for(int i = 0; i < 8; i++) {
                Arrays.fill(muxData[i], 0);
            }
        }
        for(int i = 0; i < muxData[0].length - 1; i++) {
            for(int j = 0; j < muxData.length; j++) {
                muxData[j][i] = muxData[j][i+1];
            }
            timestamps[i] = timestamps[i+1];
        }
        timestamps[timestamps.length - 1] = timestamps[timestamps.length-2] + deltaMs / 1000.0;
        for(int i = 0; i < muxData.length; i++) {
            muxData[i][muxData[i].length - 1] = newData[i];
            chart.updateXYSeries("Mux "+i, timestamps, muxData[i], null);
        }
        frame.repaintChart();
    }
}