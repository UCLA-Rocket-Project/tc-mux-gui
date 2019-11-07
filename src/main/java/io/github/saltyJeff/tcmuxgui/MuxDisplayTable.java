package io.github.saltyJeff.tcmuxgui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MuxDisplayTable extends JPanel {
    GridBagLayout layout;
    List<MuxDisplay> displays = new ArrayList<>();
    Border border = BorderFactory.createLineBorder(Color.BLUE, 1);

    public MuxDisplayTable() {
        super();

        // set layout dimensions
        setBounds(0,0,500,1500);
        layout = new GridBagLayout();
        setLayout (layout);

        for(int i = 0; i < 8; i++) {
            addDisplay(i);
        }
        setBorder(new EmptyBorder(10,10,10,10));
    }
    void addDisplay(int i) {
        GridBagConstraints labelConst = new GridBagConstraints();
        labelConst.gridx = 0;
        labelConst.gridy = i;
        JLabel numLabel = new JLabel();
        numLabel.setText(i+" ");
        add(numLabel, labelConst);

        GridBagConstraints muxConst = new GridBagConstraints();
        muxConst.gridx = 1;
        muxConst.gridy = i;
        MuxDisplay muxDisplay = new MuxDisplay();
        muxDisplay.setText("UNDEFINED");
        muxDisplay.setBorder(border);
        add(muxDisplay, muxConst);
        displays.add(muxDisplay);
    }
    void updateDisplays(double[] data) {
        for(int i = 0; i < 8; i++) {
            String s = "UNDEF";
            switch(ReaderThread.get().units()) {
                case C:
                case F:
                    s = String.format(" %.2f", data[i]);
                    break;
                case MV:
                    s = String.format(" %04d", (int)Math.rint(data[i]));
                    break;
            }
            displays.get(i).setText(s);
        }
    }
}
