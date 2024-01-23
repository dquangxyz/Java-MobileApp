package com.example.umatchapp.ui.contacts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umatchapp.AccountSingleton;
import com.example.umatchapp.MainActivity;
import com.example.umatchapp.R;
import com.example.umatchapp.adapter.NotiAdapter;
import com.example.umatchapp.model.SharedViewModel;
import com.example.umatchapp.adapter.ContactAdapter;
import com.example.umatchapp.databinding.FragmentContactsBinding;
import com.example.umatchapp.model.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


public class ContactsFragment extends Fragment {

    private FragmentContactsBinding binding;
    private SharedViewModel sharedViewModel;


    FirebaseFirestore db= FirebaseFirestore.getInstance();
    GoogleSignInAccount account = AccountSingleton.getInstance().getGoogleAccount();


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ContactsViewModel ContactsViewModel = new ViewModelProvider(this).get(ContactsViewModel.class);

        binding = FragmentContactsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        GoogleSignInAccount googleAccount = AccountSingleton.getInstance().getGoogleAccount();
        CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("users");

        // Set section names (according to the subject that the user registered)
        TextView textViewContactS1 = root.findViewById(R.id.textView_contact_s1);
        TextView textViewContactS2 = root.findViewById(R.id.textView_contact_s2);
        TextView textViewContactS3 = root.findViewById(R.id.textView_contact_s3);
        TextView textViewContactS4 = root.findViewById(R.id.textView_contact_s4);

        AtomicReference<String> subject1 = new AtomicReference<>("");
        AtomicReference<String> subject2 = new AtomicReference<>("");
        AtomicReference<String> subject3 = new AtomicReference<>("");
        AtomicReference<String> subject4 = new AtomicReference<>("");

        MainActivity mainActivity = (MainActivity) requireActivity();
        sharedViewModel = mainActivity.getSharedViewModel();
        sharedViewModel.getSharedData().observe(getViewLifecycleOwner(), data -> {
            List<String> userSubjects = data.getUser_subjects();
            textViewContactS1.setText(userSubjects.size() >= 1 ? userSubjects.get(0) : "");
            textViewContactS2.setText(userSubjects.size() >= 2 ? userSubjects.get(1) : "");
            textViewContactS3.setText(userSubjects.size() >= 3 ? userSubjects.get(2) : "");
            textViewContactS4.setText(userSubjects.size() >= 4 ? userSubjects.get(3) : "");
            subject1.set(userSubjects.size() >= 1 ? userSubjects.get(0) : "");
            subject2.set(userSubjects.size() >= 2 ? userSubjects.get(1) : "");
            subject3.set(userSubjects.size() >= 3 ? userSubjects.get(2) : "");
            subject4.set(userSubjects.size() >= 4 ? userSubjects.get(3) : "");
        });


        RecyclerView listContactRV1 = root.findViewById(R.id.listUsers1);
        RecyclerView listContactRV2 = root.findViewById(R.id.listUsers2);
        RecyclerView listContactRV3 = root.findViewById(R.id.listUsers3);
        RecyclerView listContactRV4 = root.findViewById(R.id.listUsers4);

        // DUMMY DATA
        ArrayList<UserModel> userModelArrayList1 = new ArrayList<UserModel>();
        ArrayList<UserModel> userModelArrayList2 = new ArrayList<UserModel>();
        ArrayList<UserModel> userModelArrayList3 = new ArrayList<UserModel>();
        ArrayList<UserModel> userModelArrayList4 = new ArrayList<UserModel>();

        // search current user's data check if it exist any request
        DocumentReference currentUserDocument = db.collection("users").document(account.getEmail());
        currentUserDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> requestsReceived = (Map<String, Object>) document.get("Requests_Accepted");
                        if (requestsReceived != null) {
                            for (Map.Entry<String, Object> entry : requestsReceived.entrySet()) {
                                String subjectCode = entry.getKey();  // subjectCode name String
                                List<String> userIDs = (List<String>) entry.getValue();
                                if (userIDs != null) {
                                    for (String otherUserID : userIDs) {
                                        Log.d("TAG: ", "UserID: " + otherUserID + ", subject: " + subjectCode);
                                        Query queryOtherUser = usersCollection.whereEqualTo("User_ID", otherUserID);
                                        queryOtherUser.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    QuerySnapshot querySnapshot = task.getResult();
                                                    if (querySnapshot != null) {
                                                        for (QueryDocumentSnapshot document : querySnapshot) {
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

                                                            String otherUserImageUrl = document.getString("image_url");
                                                            if (otherUserImageUrl == null) {
                                                                otherUserImageUrl = "";
                                                            }

                                                            if (Objects.equals(subjectCode, subject1.get())){
//                                                                userModelArrayList1.add(new UserModel(otherUserFullName, otherUserImageUrl, subjectCode, otherUserCampus ));
                                                                userModelArrayList1.add(new UserModel(otherUserId, otherUserFullName, otherUserEmail,otherUserPhone, otherUserMajor, otherUserCampus, otherUserSubjects, otherUserNationality, otherUserGender, otherUserDescription, otherUserScore, otherUserImageUrl));
                                                                
                                                            } else if (Objects.equals(subjectCode, subject2.get())) {
//                                                                userModelArrayList2.add(new UserModel(otherUserFullName, otherUserImageUrl, subjectCode, otherUserCampus ));
                                                                userModelArrayList2.add(new UserModel(otherUserId, otherUserFullName, otherUserEmail,otherUserPhone, otherUserMajor, otherUserCampus, otherUserSubjects, otherUserNationality, otherUserGender, otherUserDescription, otherUserScore, otherUserImageUrl));

                                                            } else if (Objects.equals(subjectCode, subject3.get())) {
//                                                                userModelArrayList3.add(new UserModel(otherUserFullName, otherUserImageUrl, subjectCode, otherUserCampus ));
                                                                userModelArrayList3.add(new UserModel(otherUserId, otherUserFullName, otherUserEmail,otherUserPhone, otherUserMajor, otherUserCampus, otherUserSubjects, otherUserNationality, otherUserGender, otherUserDescription, otherUserScore, otherUserImageUrl));

                                                            } else if (Objects.equals(subjectCode, subject4.get())) {
//                                                                userModelArrayList4.add(new UserModel(otherUserFullName, otherUserImageUrl, subjectCode, otherUserCampus ));
                                                                userModelArrayList4.add(new UserModel(otherUserId, otherUserFullName, otherUserEmail,otherUserPhone, otherUserMajor, otherUserCampus, otherUserSubjects, otherUserNationality, otherUserGender, otherUserDescription, otherUserScore, otherUserImageUrl));

                                                            }
                                                        }
                                                    }
                                                } else {
                                                    Log.d("TAG", "Error getting documents: " + task.getException());
                                                }
                                                // we are initializing our adapter class and passing our arraylist to it.
//                                                NotiAdapter userAdapter1 = new NotiAdapter(getActivity(), userModelArrayList1);
//                                                NotiAdapter userAdapter2 = new NotiAdapter(getActivity(), userModelArrayList2);
                                                ContactAdapter userAdapter1 = new ContactAdapter(getActivity(), userModelArrayList1, subjectCode, getChildFragmentManager());
                                                ContactAdapter userAdapter2 = new ContactAdapter(getActivity(), userModelArrayList2, subjectCode, getChildFragmentManager());
                                                ContactAdapter userAdapter3 = new ContactAdapter(getActivity(), userModelArrayList3, subjectCode, getChildFragmentManager());
                                                ContactAdapter userAdapter4 = new ContactAdapter(getActivity(), userModelArrayList4, subjectCode, getChildFragmentManager());

                                                listContactRV1.setAdapter(userAdapter1);
                                                // below line is for setting a layout manager for our recycler view.
                                                // here we are creating vertical list so we will provide orientation as vertical
                                                LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                                                LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                                                LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                                                LinearLayoutManager linearLayoutManager4 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

                                                listContactRV2.setAdapter(userAdapter2);
                                                // in below two lines we are setting layoutmanager and adapter to our recycler view.
                                                listContactRV1.setLayoutManager(linearLayoutManager1);

                                                listContactRV2.setLayoutManager(linearLayoutManager2);

                                                listContactRV3.setAdapter(userAdapter3);
                                                listContactRV3.setLayoutManager(linearLayoutManager3);

                                                listContactRV4.setAdapter(userAdapter4);
                                                listContactRV4.setLayoutManager(linearLayoutManager4);
                                            }
                                        });

                                    }
                                } else {
                                    Log.d("TAG", "Empty Array");
                                }
                            }

                        } else {
                        Log.d("TAG: ", "No such document");
                    }
                } else {
                        Log.d("TAG: ", "Fail task in searching current user's request data");
                    }
                }
            }
        });


        // set button "subject1Toggle" updateLayout
        TextView subject1Toggle = binding.textViewContactS1;
        subject1Toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout relativeLayout1 = binding.contactRelativeLayout1;
                ViewGroup.LayoutParams layoutParams1 = relativeLayout1.getLayoutParams();

                RecyclerView listUsers1 = binding.listUsers1;
                RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) listUsers1.getLayoutParams();

                if (layoutParams1.height != 0){
                    layoutParams1.height = 0;
                    layoutParams2.height = 0;
                }
                else{
                    layoutParams1.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    layoutParams2.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                }
                relativeLayout1.setLayoutParams(layoutParams1);
                listUsers1.setLayoutParams(layoutParams2);
            }
        });

        // set button "subject2Toggle" updateLayout
        TextView subject2Toggle = binding.textViewContactS2;
        subject2Toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout relativeLayout1 = binding.contactRelativeLayout2;
                ViewGroup.LayoutParams layoutParams1 = relativeLayout1.getLayoutParams();

                RecyclerView listUsers1 = binding.listUsers2;
                RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) listUsers1.getLayoutParams();

                if (layoutParams1.height != 0){
                    layoutParams1.height = 0;
                    layoutParams2.height = 0;
                }
                else{
                    layoutParams1.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    layoutParams2.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                }
                relativeLayout1.setLayoutParams(layoutParams1);
                listUsers1.setLayoutParams(layoutParams2);
            }
        });

        // set button "subject3Toggle" updateLayout
        TextView subject3Toggle = binding.textViewContactS3;
        subject3Toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout relativeLayout1 = binding.contactRelativeLayout3;
                ViewGroup.LayoutParams layoutParams1 = relativeLayout1.getLayoutParams();

                RecyclerView listUsers1 = binding.listUsers3;
                RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) listUsers1.getLayoutParams();

                if (layoutParams1.height != 0){
                    layoutParams1.height = 0;
                    layoutParams2.height = 0;
                }
                else{
                    layoutParams1.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    layoutParams2.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                }
                relativeLayout1.setLayoutParams(layoutParams1);
                listUsers1.setLayoutParams(layoutParams2);
            }
        });

        // set button "subject4Toggle" updateLayout
        TextView subject4Toggle = binding.textViewContactS4;
        subject4Toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout relativeLayout1 = binding.contactRelativeLayout4;
                ViewGroup.LayoutParams layoutParams1 = relativeLayout1.getLayoutParams();

                RecyclerView listUsers1 = binding.listUsers4;
                RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) listUsers1.getLayoutParams();

                if (layoutParams1.height != 0){
                    layoutParams1.height = 0;
                    layoutParams2.height = 0;
                }
                else{
                    layoutParams1.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    layoutParams2.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                }
                relativeLayout1.setLayoutParams(layoutParams1);
                listUsers1.setLayoutParams(layoutParams2);
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