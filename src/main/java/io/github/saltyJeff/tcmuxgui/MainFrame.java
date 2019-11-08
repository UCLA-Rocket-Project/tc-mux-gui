package io.github.saltyJeff.tcmuxgui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class MainFrame extends JFrame implements ActionListener {
    MuxDisplayTable muxDisplays;
    Timer timer;
    MuxChart chart;
    public MainFrame() throws IOException {
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        add(new ConfigPanel());
        muxDisplays = new MuxDisplayTable();
        add(muxDisplays);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        timer = new Timer(50, this);
        pack();
        setResizable(false);
        setTitle("TC-Mux");

        chart = new MuxChart();
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        pack();
        ReaderThread rt = ReaderThread.get();
        if(rt.executing()) {
            muxDisplays.updateDisplays(rt.readings);
            chart.updateChart(rt.readings, rt.deltaMs, rt.units());
        }

        timer.restart();
    }
}
