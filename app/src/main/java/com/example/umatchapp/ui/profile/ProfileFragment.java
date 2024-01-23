package com.example.umatchapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.umatchapp.AccountSingleton;
import com.example.umatchapp.MainActivity;
import com.example.umatchapp.R;
import com.example.umatchapp.model.SharedViewModel;
import com.example.umatchapp.SignInActivity;
import com.example.umatchapp.SignUpActivity;
import com.example.umatchapp.databinding.FragmentProfileBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Objects;
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SharedViewModel sharedViewModel;
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    GoogleSignInAccount account = AccountSingleton.getInstance().getGoogleAccount();
    private static final String TAG = MainActivity.class.getSimpleName();

/*
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel ProfileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

    binding = FragmentProfileBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        final TextView textView = binding.textProfile;
        ProfileViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

 */

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel ProfileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        MainActivity mainActivity = (MainActivity) requireActivity();
        sharedViewModel = mainActivity.getSharedViewModel();
        //final TextView textView = binding.textProfile;

        // Fetch user profile data from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(Objects.requireNonNull(account.getEmail()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // get information
                                String userName = document.getString("F_Name") + " " + document.getString("L_Name");
                                String  image_url = document.getString("image_url");
                                String  e_mail = document.getString("Latrobe_Email");
                                String  phone = document.getString("Phone");
                                String  major = document.getString("Major");
                                String  campus = document.getString("Campus");
                                String  gender = document.getString("Gender");
                                String  nationality = document.getString("Nationality");
                                String  description = document.getString("Description");
//                               // Retrieve subjects array
                                ArrayList<String> subjects = (ArrayList<String>) document.get("Subjects");
//                                sharedViewModel.setSharedData(subjects);


                                StringBuilder subjectsText = new StringBuilder();
                                if (subjects != null) {
                                    for (String subject : subjects) {
                                        subjectsText.append(subject).append(", ");
                                    }
                                    // Remove the trailing comma and space
                                    subjectsText.setLength(subjectsText.length() - 2);
                                }
                                // set information
                                if (image_url != null) {
                                    Glide.with(requireContext())
                                            .load(image_url)
                                            .into(binding.profileImageUser);
                                }
                                binding.username.setText(userName);
                                binding.eMailContent.setText(e_mail);
                                binding.phoneContent.setText(phone);
                                binding.majorContent.setText(major);
                                binding.campusContent.setText(campus);
                                binding.genderContent.setText(gender);
                                binding.nationalityContent.setText(nationality);
                                binding.descriptionContent.setText(description);
                                binding.subjectListContent.setText(subjectsText);
                            }
                        } else {
                            Log.d(TAG,"Can't find user data");
                        }
                    }
                });

        // Edit button
        Button editBtn = binding.editBtn;
        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent activityIntent = new Intent(getActivity(), SignUpActivity.class);
                startActivity(activityIntent);
            }
        });

        // Sign out button
        Button signOutBtn = binding.signOutBtn;
        signOutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Sign out Firebase
                FirebaseAuth.getInstance().signOut();

                // Sign out from Google
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(),
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(getString(R.string.default_web_client_id))
                                .requestEmail()
                                .build());
                mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(),
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // Signed out successfully
                                Log.d(TAG, "Signed out from Google");
                            }
                        });

                // Back to the login activity
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
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