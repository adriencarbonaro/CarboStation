package com.carbostation.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<String> mText_header;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText_header = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
        mText_header.setValue("Adrien");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getText_header() {
        return mText_header;
    }
}