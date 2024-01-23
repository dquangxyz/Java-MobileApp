package com.example.umatchapp.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.umatchapp.AccountSingleton;
import com.example.umatchapp.MainActivity;
import com.example.umatchapp.R;
import com.example.umatchapp.model.SharedViewModel;
import com.example.umatchapp.databinding.FragmentHomeBinding;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class HomeFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private FragmentHomeBinding binding;

    FirebaseFirestore db= FirebaseFirestore.getInstance();
    GoogleSignInAccount account = AccountSingleton.getInstance().getGoogleAccount();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) requireActivity();
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button goMatchBtn =  root.findViewById(R.id.btnGoMatch);
        final TextView textView = binding.userNameGreeting;
        AtomicReference<String> fullName = new AtomicReference<>("");
        sharedViewModel = mainActivity.getSharedViewModel();

        sharedViewModel.getSharedData().observe(getViewLifecycleOwner(), data -> {
            Log.d("Shared Data Observer", "Received shared data: " + data.getUser_name());
            fullName.set(data.getUser_name());

            // Update the UI element with the received full name
            homeViewModel.setFullName(fullName.get());
            // search current user's data check if it exist any request
            DocumentReference currentUserDocument = db.collection("users").document(account.getEmail());
            CollectionReference usersCollection = db.collection("users");
            TextView studyBuddies = binding.numStudyBuddies;
            currentUserDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    int buddiesNum = 0;
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> requestsAccepted = (Map<String, Object>) document.get("Requests_Accepted");
                            if (requestsAccepted != null) {
                                for (Map.Entry<String, Object> entry : requestsAccepted.entrySet()) {
                                    List<String> userIDs = (List<String>) entry.getValue();
                                    buddiesNum += userIDs.size();
                                }


                                Log.d("TAG: ", "buddiesNum: " + buddiesNum);
//                                binding.numStudyBuddies.setText(String.valueOf(buddiesNum));
                            }
                            binding.numStudyBuddies.setText(String.valueOf(buddiesNum));
                        } else {
                            Log.d("TAG: ", "Fail task in searching current user's request data");
                        }
                    }
                }
            });

        });


        goMatchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an instance of the new fragment
                // Retrieve the NavController
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);

                // Simulate clicking the desired bottom navigation button
                navController.navigate(R.id.action_home_to_match);
            }
        });

        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);


        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}