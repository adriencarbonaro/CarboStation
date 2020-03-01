package com.carbostation;

import android.util.Log;

public class TempManager {
    private String TAG="TempManager";
    /* Private Attributes */
    private int _temp_int;
    private int _temp_out;

    /* Constructor */
    public TempManager(int temp_int, int temp_out) {
        _temp_int = temp_int;
        _temp_out = temp_out;
        Log.d(TAG, String.valueOf(_temp_int) + " " + String.valueOf(_temp_out));
    }

    /* Getters */
    public int getTempInt() { return _temp_int; }

    public int getTempOut() { return _temp_out; }

    public int[] getTemp() {
        int[] temp_array = new int[2];
        temp_array[0] = _temp_int;
        temp_array[1] = _temp_out;
        return temp_array;
    }

    /* Setters */
    public void setTempInt(int temp_int) { _temp_int = temp_int; }

    public void setTempOut(int temp_out) { _temp_out = temp_out; }
}
