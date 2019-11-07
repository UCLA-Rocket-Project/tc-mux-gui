package io.github.saltyJeff.tcmuxgui;

import com.fazecast.jSerialComm.SerialPort;
import li.flor.nativejfilechooser.NativeJFileChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConfigPanel extends JPanel {
    File currentFile;
    JTextArea filePathLabel;
    public ConfigPanel() throws IOException {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(new SelectPortDropdown(SerialPort.getCommPorts()));
        add(new SelectFileButton());
        filePathLabel = new JTextArea();
        filePathLabel.setEditable(false);
        filePathLabel.setColumns(15);
        filePathLabel.setLineWrap(true);
        filePathLabel.setWrapStyleWord(true);
        filePathLabel.setMaximumSize(new Dimension(250, 120));
        add(filePathLabel);
        add(new StartStopButton());
        add(new SelectUnitDropdown());

        String homePath = System.getProperty("user.home");
        File home = new File(homePath);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy-HH-mm-ss");
        String dateStr = dateFormat.format(new Date());
        setCurrentFile(new File(home, dateStr+".csv"));
        setBorder(new EmptyBorder(10,10,10,10));
        revalidate();
        setAlignmentX(Component.CENTER_ALIGNMENT);
    }
    void setCurrentFile(File file) {
        currentFile = file;
        filePathLabel.setText(file.getAbsolutePath());
        ReaderThread.get().setFile(file);
        revalidate();
    }
    class SelectPortDropdown extends JComboBox {
        SelectPortDropdown(SerialPort[] ports) {
            super(ports);
            if(ports.length > 0) {
                setSelectedIndex(0);
                changePort();
            }
            addActionListener(e -> changePort());
        }
        void changePort() {
            SerialPort port = (SerialPort) getSelectedItem();
            ReaderThread.get().setPort(port);
        }
    }
    class SelectUnitDropdown extends JComboBox {
        SelectUnitDropdown () {
            super(ReadUnits.class.getEnumConstants());
            setSelectedIndex(0);
            changeUnits();
            addActionListener(e -> changeUnits());
        }
        void changeUnits() {
            ReadUnits units = (ReadUnits) getSelectedItem();
            ReaderThread.get().setUnits(units);
        }
    }
    class SelectFileButton extends JButton {
        final JFileChooser fc = new NativeJFileChooser();
        SelectFileButton() {
            setLabel("Change File");
            addActionListener(e -> openChooser());
            setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        void openChooser() {
            //set it to be a save dialog
            fc.setDialogType(JFileChooser.SAVE_DIALOG);
            //set a default filename (this is where you default extension first comes in)
            fc.setSelectedFile(new File("tcmux.csv"));
            //Set an extension filter, so the user sees other XML files
            fc.setFileFilter(new FileNameExtensionFilter("CSV file","csv"));
            int returnVal = fc.showOpenDialog(ConfigPanel.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                ConfigPanel.this.setCurrentFile(file);
            }
        }
    }
    class StartStopButton extends JButton {
        StartStopButton () {
            setLabel("Start Recording");
            addActionListener(e -> toggleReader());
            setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        void toggleReader () {
            String newLabel = ReaderThread.toggle() ? "STOP" : "Start Recording";
            setLabel(newLabel);
        }
    }
}
