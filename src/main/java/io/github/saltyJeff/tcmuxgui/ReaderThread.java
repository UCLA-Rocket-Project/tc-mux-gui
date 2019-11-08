package io.github.saltyJeff.tcmuxgui;

import com.fazecast.jSerialComm.SerialPort;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ReaderThread extends Thread {
    double[] readings = new double[8];
    int deltaMs = 0;
    private SerialPort port;
    private Scanner input;
    private PrintWriter output;
    private File file;
    private static ReaderThread inst = null;
    private ReadUnits units = ReadUnits.F;
    private boolean executing = false;

    public static ReaderThread get() {
        if(inst == null) {
            inst = new ReaderThread();
            inst.start();
        }
        return inst;
    }
    public static boolean toggle() {
        if(inst.executing == false) {
            inst.prepRecording();
        }
        inst.executing = !inst.executing;
        System.out.println("Recording: "+inst.executing);
        return inst.executing;
    }
    public boolean executing() {
        return executing;
    }
    private ReaderThread() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> stopRecording()));
    }
    @Override
    public void run () {
        while (currentThread().isAlive()) {
            if(!inst.executing) {
                continue;
            }
            if(input == null) {
                continue;
            }
            try {
                String nextLine = input.nextLine();
                System.out.println("LINE: " + nextLine);
                String[] tokens = nextLine.split("\t");
                if (tokens.length < 8) {
                    System.err.println("Received an incomplete frame: " + nextLine + ", ignoring");
                    continue;
                }
                for (int i = 0; i < tokens.length; i++) {
                    readings[i] = Double.parseDouble(tokens[i]);
                }
                if (output != null) {
                    output.print(String.join(",", tokens));
                }
                deltaMs = Integer.parseInt(tokens[tokens.length - 1]);
            }
            catch(Exception e) {
                if(!(e instanceof NoSuchElementException)) {
                    e.printStackTrace();
                }
            }
        }

    }
    void prepRecording() {
        if(port != null && input == null) {
            boolean opened = port.openPort();
            System.out.println(opened);
            input = new Scanner(port.getInputStream());
            System.out.println("Opening port: "+port.getSystemPortName());
        }
        if(file != null && output == null) {
            try {
                if(!file.exists()) {
                    file.createNewFile();
                }
                output = new PrintWriter(file);
                output.println("tc0,tc1,tc2,tc3,tc4,tc5,tc6,tc7,dt");
                output.flush();
                System.out.println("Writing to file: "+file.getAbsolutePath());
            }
            catch(Exception e) {
                System.err.println("ERROR OPENING FILE FOR RECORDING");
                e.printStackTrace();
            }
        }
    }
    void stopRecording () {
        if(input != null) {
            input.close();
            port.closePort();
            input = null;
            System.out.println("Closed port");
        }
        if(output != null) {
            output.flush();
            output.close();
            output = null;
            System.out.println("Closed file");
        }
    }
    public void setPort(SerialPort newPort) {
        stopRecording();
        port = newPort;
        port.setComPortParameters(115200, 8, 1, 0);
        setUnits(units);
    }
    public void setFile(File newFile) {
        stopRecording();
        file = newFile;
    }
    public void setUnits(ReadUnits newUnits) {
        this.units = newUnits;
        if(port != null) {
            port.writeBytes((units.toChar()+"a\n").getBytes(), 3);
        }
    }
    public ReadUnits units() {
        return units;
    }
}
