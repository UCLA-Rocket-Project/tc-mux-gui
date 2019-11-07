package io.github.saltyJeff.tcmuxgui;

public enum ReadUnits {
    MV,
    F,
    C;
    char toChar() {
        if(this.equals(ReadUnits.MV)) {
            return 'V';
        }
        else if(this.equals(ReadUnits.F)) {
            return 'F';
        }
        else if(this.equals(ReadUnits.C)) {
            return 'C';
        }
        throw new IllegalStateException("wakanda enum is dis");
    }
}
