package com.example.umatchapp.ui.profile;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umatchapp.AccountSingleton;
import com.example.umatchapp.MainActivity;
import com.example.umatchapp.R;
import com.example.umatchapp.adapter.MatchAdapter;
import com.example.umatchapp.databinding.FragmentOtherProfileBinding;
import com.example.umatchapp.model.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
//Permissions


public class OtherUserProfileFragment  extends DialogFragment {
    final String[] PERMISSIONS = {android.Manifest.permission.CALL_PHONE};

    private FragmentOtherProfileBinding binding;
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    GoogleSignInAccount account = AccountSingleton.getInstance().getGoogleAccount();
    private static final String TAG = MainActivity.class.getSimpleName();
    private String userID;
    private String buddyPhone;


    public OtherUserProfileFragment(String userID) {
        this.userID = userID;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_other_profile, null);

//        binding = FragmentOtherProfileBinding.inflate(inflater, null, false);
//        View rootView = binding.getRoot();

//        binding = FragmentOtherProfileBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//        Bundle bundle = getArguments();
//        String userID = null;

//        if (bundle != null) {
//            userID = bundle.getString("userId");
//            // Use the received data as needed
//        }
        // Fetch user profile data from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("users");
        Query queryCurrentUser = usersCollection.whereEqualTo("User_ID", userID);
        queryCurrentUser.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //ask user for outstanding permissions
                    askPermission();
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        String userName = document.getString("F_Name") + " " + document.getString("L_Name");
                        String image_url = document.getString("image_url");
                        String e_mail = document.getString("Latrobe_Email");
                        String phone = document.getString("Phone");
                        String major = document.getString("Major");
                        String campus = document.getString("Campus");
                        String gender = document.getString("Gender");
                        String nationality = document.getString("Nationality");
                        String description = document.getString("Description");
                        ArrayList<String> subjects = (ArrayList<String>) document.get("Subjects");
                        buddyPhone = phone;

                        CircleImageView profileImageView = dialogView.findViewById(R.id.profile_image_user);
                        if (image_url != null && !image_url.isEmpty()) {
                            if (image_url.startsWith("http://") || image_url.startsWith("https://")) {
                                Glide.with(requireContext()).load(image_url).into(profileImageView);
                            }
                        }
                        TextView usernameTextView = dialogView.findViewById(R.id.username);
                        usernameTextView.setText(userName);
                        TextView emailTextView = dialogView.findViewById(R.id.e_mail_content);
                        emailTextView.setText(e_mail);
                        TextView phoneTextView = dialogView.findViewById(R.id.phone_content);
                        phoneTextView.setText(phone);
                        TextView majorTextView = dialogView.findViewById(R.id.major_content);
                        majorTextView.setText(major);
                        TextView campusTextView = dialogView.findViewById(R.id.campus_content);
                        campusTextView.setText(campus);
                        TextView genderTextView = dialogView.findViewById(R.id.gender_content);
                        genderTextView.setText(gender);
                        TextView nationalityTextView = dialogView.findViewById(R.id.nationality_content);
                        nationalityTextView.setText(nationality);
                        TextView descriptionTextView = dialogView.findViewById(R.id.description_content);
                        descriptionTextView.setText(description);

                        TextView subjectsTextView = dialogView.findViewById(R.id.subjectList_content);
                        if (subjects != null) {
                            // Loop through the user_subjects ArrayList and concatenate the elements
                            StringBuilder subjectsText = new StringBuilder();
                            for (Object subject : subjects) {
                                if (!subject.toString().equals("Not select")){
                                    subjectsText.append(subject.toString()).append(", ");
                                }
                            }
                            // Remove the trailing comma and space
                            if (subjectsText.length() > 2) {
                                subjectsText.setLength(subjectsText.length() - 2);
                            }
                            // Set the concatenated subjects text to the TextView
                           subjectsTextView.setText(subjectsText.toString());
                        }
                    }
                }
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        ImageButton whatsappButton = dialogView.findViewById(R.id.btnCall);

        whatsappButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!hasPermission()) {
                    Log.d(TAG, "Permission denied");
                    Toast.makeText(getContext(), "Insufficient permissions to use this feature", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if the CALL_PHONE permission is granted
                    // Create the URI for the WhatsApp phone call
                    Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + buddyPhone);

                    // Create an Intent with the ACTION_VIEW action and the WhatsApp URI
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                    // Start the activity and pause the app
                    startActivity(intent);
                }

            }
        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                OtherUserProfileFragment.this.getDialog().cancel();
            }
        });
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }


    private boolean hasPermission(){
        boolean permissionStatus = true;
        for (String permission :PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted: " + permission);
            } else {
                Log.d(TAG, "Permission not granted: " + permission);
                permissionStatus = false;
            }
        }
        return permissionStatus;
    }

    private void askPermission(){
        if( !hasPermission()){
            Log.d(TAG, "Launching Multiple contract permission launcher for All required permissions");
            multiplePermissionActivityResultLauncher.launch(PERMISSIONS);
        } else {
            Log.d(TAG, "All permissions are already granted");
        }
    }

    private final ActivityResultLauncher<String[]> multiplePermissionActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                Log.d(TAG, "Launcher result: " + isGranted.toString());
                if (isGranted.containsValue(false)) {
                    Log.d(TAG, "At Least one of the permissions was not granted, please enable permissions to ensure app functionality");
                }
            });

}



