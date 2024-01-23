package com.example.umatchapp.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.umatchapp.model.UserModel;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<UserModel> sharedData = new MutableLiveData<>();

    public void setSharedData(UserModel data) {
        sharedData.setValue(data);
    }


    public LiveData<UserModel> getSharedData() {
        return sharedData;
    }
}
