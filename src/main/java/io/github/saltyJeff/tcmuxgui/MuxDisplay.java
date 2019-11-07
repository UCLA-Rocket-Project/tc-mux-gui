package io.github.saltyJeff.tcmuxgui;

import javax.swing.*;
import java.awt.*;

public class MuxDisplay extends JLabel {
    final int WIDTH = 200;
    final int HEIGHT = 50;
    final Dimension DIM = new Dimension(WIDTH, HEIGHT);
    public MuxDisplay() {
        setMinimumSize(DIM);
        setPreferredSize(DIM);
        setMaximumSize(DIM);
    }
}
