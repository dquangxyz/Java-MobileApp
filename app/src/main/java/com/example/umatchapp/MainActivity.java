package com.example.umatchapp;

import android.os.Bundle;
import android.util.Log;

import com.example.umatchapp.model.SharedViewModel;
import com.example.umatchapp.model.UserModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.example.umatchapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SharedViewModel sharedViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(this.getViewModelStore(), ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(SharedViewModel.class);

//        String fullName = getIntent().getStringExtra("Full Name");
//        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
//        homeViewModel.setFullName(fullName);

        UserModel userData = (UserModel) getIntent().getSerializableExtra("userData");
        Log.d("Main activity intent: ", "UserData" + userData);
        sharedViewModel.setSharedData(userData);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
    public SharedViewModel getSharedViewModel() {
        return sharedViewModel;
    }
}