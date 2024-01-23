package com.example.umatchapp.ui.match;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umatchapp.AccountSingleton;
import com.example.umatchapp.R;
import com.example.umatchapp.adapter.MatchAdapter;
import com.example.umatchapp.model.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ShowMatchingDialogFragment extends DialogFragment {
    private final ArrayList<UserModel> userModelArrayList1 = new ArrayList<UserModel>();
    private String selectedCampus;
    private String selectedSubjectCode;
    double currentUserScore = 30;
    public ShowMatchingDialogFragment(String selectedCampus, String selectedSubjectCode) {
        this.selectedCampus = selectedCampus;
        this.selectedSubjectCode = selectedSubjectCode;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_match, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.matchList);


        // Query data from Firebase
        GoogleSignInAccount googleAccount = AccountSingleton.getInstance().getGoogleAccount();
        String currentUserId = googleAccount.getId();
        String currentUserEmail = googleAccount.getEmail();
        CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("users");

        Log.d("TESTING", selectedSubjectCode);
        Log.d("TESTING", selectedCampus);

        Query queryCurrentUser = usersCollection.whereEqualTo("User_ID", currentUserId);
        queryCurrentUser.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        if(document.getDouble("User_Score") != null){
                             currentUserScore = document.getDouble("User_Score");
                        }



                        // Perform the subsequent query using currentUserScore
                        Query query = usersCollection
                                .whereGreaterThanOrEqualTo("User_Score", currentUserScore * 0.9)
                                .whereLessThanOrEqualTo("User_Score", currentUserScore * 1.1)
                                .whereEqualTo("Campus", selectedCampus)
                                .whereArrayContains("Subjects", selectedSubjectCode);
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null) {
                                        for (QueryDocumentSnapshot document : querySnapshot) {
                                            if (!Objects.equals(document.getString("User_ID"), currentUserId)){
                                                String otherUserId = document.getString("User_ID");

                                                String otherUserFirstName = document.getString("F_Name");
                                                if (otherUserFirstName == null) {
                                                    otherUserFirstName = "";
                                                }
                                                String otherUserLastName = document.getString("L_Name");
                                                if (otherUserLastName == null) {
                                                    otherUserLastName = "";
                                                }
                                                String otherUserFullName = otherUserFirstName + " " + otherUserLastName;

                                                String otherUserEmail = document.getString("Latrobe_Email");
                                                if (otherUserEmail == null) {
                                                    otherUserEmail = "";
                                                }

                                                String otherUserPhone = document.getString("Phone");
                                                if (otherUserPhone == null) {
                                                    otherUserPhone = "";
                                                }

                                                String otherUserMajor = document.getString("Major");
                                                if (otherUserMajor == null) {
                                                    otherUserMajor = "";
                                                }

                                                String otherUserCampus = document.getString("Campus");
                                                if (otherUserCampus == null) {
                                                    otherUserCampus = "";
                                                }

                                                ArrayList<String> otherUserSubjects = (ArrayList<String>) document.get("Subjects");
                                                if (otherUserSubjects == null) {
                                                    otherUserSubjects = new ArrayList<>();
                                                }

                                                String otherUserNationality = document.getString("Nationality");
                                                if (otherUserNationality == null) {
                                                    otherUserNationality = "";
                                                }

                                                String otherUserGender = document.getString("Gender");
                                                if (otherUserGender == null) {
                                                    otherUserGender = "";
                                                }

                                                String otherUserDescription = document.getString("Description");
                                                if (otherUserDescription == null) {
                                                    otherUserDescription = "";
                                                }

                                                double otherUserScore;
                                                if (document.contains("User_Score")) {
                                                    otherUserScore = document.getDouble("User_Score");
                                                } else {
                                                    otherUserScore = 0;
                                                }

                                                String otherUserImage = document.getString("image_url");
                                                if (otherUserImage == null) {
                                                    otherUserImage = "";
                                                }

                                                Log.d("TAG", "(to be matched with) User Name: " + otherUserFullName + ", User image: " + otherUserImage);
                                                userModelArrayList1.add(new UserModel(otherUserId, otherUserFullName, otherUserEmail,otherUserPhone, otherUserMajor, otherUserCampus, otherUserSubjects, otherUserNationality, otherUserGender, otherUserDescription, otherUserScore, otherUserImage));
                                            }
                                        }
                                    }
                                    Log.d("TAG", "Current User Score: " + Double.toString(currentUserScore));


                                    // setup recycler view after querying
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false)); //linearLayoutManager1
                                    if (!userModelArrayList1.isEmpty()) {
                                        MatchAdapter userAdapter1 = new MatchAdapter(getActivity().getApplicationContext(), userModelArrayList1, currentUserId, selectedSubjectCode);
                                        recyclerView.setAdapter(userAdapter1);
                                    } else {
                                        Log.d("Recycle Adapter: ", "Empty List");
                                    }
                                } else {
                                    Log.d("TAG", "Error getting documents: " + task.getException());
                                }
                            }
                        });
                    } else {
                        Log.d("TAG", "No matching user found.");
                    }
                } else {
                    Log.d("TAG", "Error getting documents: " + task.getException());
                }
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(dialogView) // pass the already inflated dialogView

                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ShowMatchingDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}