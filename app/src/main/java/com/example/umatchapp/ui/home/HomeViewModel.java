package com.example.umatchapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
//        mText.setValue("Hi Joe Johnson");
    }

    public void setFullName(String fullName) {
        mText.setValue("Welcome \n" + fullName);
    }

    public LiveData<String> getText() {
        return mText;
    }
}