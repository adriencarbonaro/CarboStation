package com.carbostation.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mDashboard_title;
    private MutableLiveData<String> mDashboard_temp_int_value;
    private MutableLiveData<String> mDashboard_temp_out_value;

    public DashboardViewModel() {
        mDashboard_title          = new MutableLiveData<>();
        mDashboard_temp_int_value = new MutableLiveData<>();
        mDashboard_temp_out_value = new MutableLiveData<>();
    }

    /* Live Data notifications */
    public LiveData<String> getTitle() { return mDashboard_title; }
    public LiveData<String> getTempInt() { return mDashboard_temp_int_value; }
    public LiveData<String> getTempOut() { return mDashboard_temp_out_value; }

    /* String getters */
    public String getTextTitle() { return mDashboard_title.getValue(); }
    public String getTextTempInt() { return mDashboard_temp_int_value.getValue(); }
    public String getTextTempOut() { return mDashboard_temp_out_value.getValue(); }

    /* String setters */
    public void setTextTitle(String title) { this.mDashboard_title.setValue(title); }
    public void setTextTempInt(String value) { this.mDashboard_temp_int_value.setValue(value); }
    public void setTextTempOut(String value) { this.mDashboard_temp_out_value.setValue(value); }
}