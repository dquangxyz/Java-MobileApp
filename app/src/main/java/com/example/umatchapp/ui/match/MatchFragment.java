package com.example.umatchapp.ui.match;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.umatchapp.MainActivity;
import com.example.umatchapp.R;
import com.example.umatchapp.model.SharedViewModel;
import com.example.umatchapp.databinding.FragmentMatchBinding;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class MatchFragment extends Fragment {
    Dialog dialog;
    private SharedViewModel sharedViewModel;
    private FragmentMatchBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MatchViewModel MatchViewModel =
                new ViewModelProvider(this).get(MatchViewModel.class);

        View view = inflater.inflate(R.layout.fragment_match, container, false);

        binding = FragmentMatchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView subjectCode = binding.subjectCodeForMatching;

        MainActivity mainActivity = (MainActivity) requireActivity();
//        AtomicReference<ArrayList> subjects = new AtomicReference<>();
        AtomicReference<String> userID = new AtomicReference<>();
        Spinner spinner = binding.subjectCodeForMatching;
        sharedViewModel = mainActivity.getSharedViewModel();
        sharedViewModel.getSharedData().observe(getViewLifecycleOwner(), data -> {
            Log.d("Shared Data Observer", "Received shared data: " + data.getUser_subjects());
//            subjects.set(data.getUser_subjects());
            ArrayList<String> subjectList = data.getUser_subjects();
            userID.set(data.getUser_id());
            Log.d("Shared Data Observer", "subjectList: " + subjectList.size());

            // Update the UI element with the received full name
            // Set up the spinner

            final ArrayAdapter<String>[] spinnerAdapter = new ArrayAdapter[]{new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, subjectList)};
            spinnerAdapter[0].setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter[0]);

            // Set up the spinner selection listener
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedSubject = parent.getItemAtPosition(position).toString();
                    Log.d("Selected Subject", selectedSubject);
                    // Do something with the selected subject
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Handle the case when nothing is selected
                }
            });

        });

//        MatchViewModel.getText().observe(getViewLifecycleOwner(), subjectCode::setText);

        Button matchBtn = root.findViewById(R.id.matchBtn);

        matchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // selection of subject
                String selectedSubjectCode = spinner.getSelectedItem().toString();

                // selection of campus
                RadioGroup radioGroup = root.findViewById(R.id.campusRadioGroupBtn);
                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                final AtomicReference<String> selectedCampus = new AtomicReference<>("");
                if (selectedRadioButtonId != -1) {
                    RadioButton selectedRadioButton = root.findViewById(selectedRadioButtonId);
                    selectedCampus.set(selectedRadioButton.getText().toString());
                }

                // Check if both campus and subject code are selected
                if (!selectedCampus.get().isEmpty() && !selectedSubjectCode.isEmpty()) {
                    // Pass the selectedCampus and selectedSubjectCode to the ShowMatchingDialogFragment constructor
                    ShowMatchingDialogFragment dialogFragment = new ShowMatchingDialogFragment(selectedCampus.get(), selectedSubjectCode);
                    dialogFragment.show(getChildFragmentManager(), null);
                } else {
                    Log.d("ERROR", "Something's wrong");
                }
            }
        });



        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}