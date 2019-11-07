package io.github.saltyJeff.tcmuxgui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class MainFrame extends JFrame implements ActionListener {
    MuxDisplayTable muxDisplays;
    Timer timer;
    public MainFrame() throws IOException {
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        add(new ConfigPanel());
        muxDisplays = new MuxDisplayTable();
        add(muxDisplays);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        timer = new Timer(250, this);
        timer.start();
        pack();
        setResizable(false);
        setTitle("TC-Mux");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        pack();
        muxDisplays.updateDisplays(ReaderThread.get().readings);
        timer.restart();
    }
}
