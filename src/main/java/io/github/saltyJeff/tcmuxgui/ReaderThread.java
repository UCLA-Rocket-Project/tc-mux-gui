package io.github.saltyJeff.tcmuxgui;

import com.fazecast.jSerialComm.SerialPort;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ReaderThread extends Thread {
    double[] readings = new double[8];
    private SerialPort port;
    private Scanner input;
    private PrintWriter output;
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
        inst.executing = !inst.executing;
        System.out.println("READER THREAD RUNNING: "+inst.executing);
        return inst.executing;
    }
    private ReaderThread() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down worker");
            inst.executing = false;
            if(port != null) {
                input.close();
                port.closePort();
                port = null;
            }
            if(output != null) {
                output.close();
            }
        }));
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
            }
            catch(Exception e) {
                if(!(e instanceof NoSuchElementException)) {
                    System.err.println(e);
                    e.printStackTrace();
                }
            }
        }
        if(port != null) {
            input.close();
            port.closePort();
            port = null;
        }
        if(output != null) {
            output.close();
        }
    }

    public void setPort(SerialPort newPort) {
        if(port != null) {
            input.close();
            port.closePort();
        }
        System.out.println("Opening port: "+newPort.getSystemPortName());
        port = newPort;
        port.setComPortParameters(115200, 8, 1, 0);
        port.openPort();
        input = new Scanner(port.getInputStream());
        setUnits(units);
    }
    public void setFile(File newFile) {
        try {
            if(output != null) {
                output.close();
            }
            if(!newFile.exists()) {
                newFile.createNewFile();
            }
            output = new PrintWriter(new FileWriter(newFile));
            output.println("tc0,tc1,tc2,tc3,tc4,tc5,tc6,tc7,dt");
            System.out.println("Writing to file: "+newFile.getAbsolutePath());
        }
        catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
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
