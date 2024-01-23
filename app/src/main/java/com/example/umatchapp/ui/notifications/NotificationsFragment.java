    package com.example.umatchapp.ui.notifications;

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
    import com.example.umatchapp.R;
    import com.example.umatchapp.adapter.NotiAdapter;
    import com.example.umatchapp.databinding.FragmentNotificationsBinding;
    import com.example.umatchapp.model.UserModel;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Map;

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

    public class NotificationsFragment extends Fragment {

        private FragmentNotificationsBinding binding;
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        GoogleSignInAccount account = AccountSingleton.getInstance().getGoogleAccount();

        int emptyCounter = 0;
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            NotificationsViewModel notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

            binding = FragmentNotificationsBinding.inflate(inflater, container, false);
            View root = binding.getRoot();

            final TextView textView = binding.newRequestToggle;
    //        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

            RecyclerView listNotiRV1 = root.findViewById(R.id.listUsers1);


            // DUMMY DATA
            ArrayList<UserModel> userModelArrayList1 = new ArrayList<UserModel>();
            // ArrayList<UserModel> userModelArrayList2 = new ArrayList<UserModel>();

            // Query data from Firebase
            GoogleSignInAccount googleAccount = AccountSingleton.getInstance().getGoogleAccount();
            String currentUserId = googleAccount.getId();
            CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("users");
            Map<String, Object> requestsReceived;

            // search current user's data check if it exist any request
            DocumentReference currentUserDocument = db.collection("users").document(account.getEmail());
            currentUserDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> requestsReceived = (Map<String, Object>) document.get("Requests_Received");
                            if (requestsReceived != null) {
                                emptyCounter = 0;
                                binding.emptyList.setVisibility(View.GONE);
                                for (Map.Entry<String, Object> entry : requestsReceived.entrySet()) {
                                    String subjectCode = entry.getKey();  // subjectCode name String
                                    List<String> userIDs = (List<String>) entry.getValue();
                                    if (userIDs != null && userIDs.size() != 0) {
                                        for (String otherUserID : userIDs) {
                                            Query queryOtherUser = usersCollection.whereEqualTo("User_ID", otherUserID);
                                            queryOtherUser.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        QuerySnapshot querySnapshot = task.getResult();
                                                        if (querySnapshot != null) {
                                                            for (QueryDocumentSnapshot document : querySnapshot) {
                                                                String otherUserFirstName = document.getString("F_Name");
                                                                if (otherUserFirstName == null) {
                                                                    otherUserFirstName = "";
                                                                }
                                                                String otherUserLastName = document.getString("L_Name");
                                                                if (otherUserLastName == null) {
                                                                    otherUserLastName = "";
                                                                }
                                                                String otherUserCampus = document.getString("Campus");
                                                                if (otherUserCampus == null) {
                                                                    otherUserCampus = "";
                                                                }
                                                                String otherUserFullName = otherUserFirstName + " " + otherUserLastName;

    //                                                            double otherUserScore;
    //                                                            if (document.contains("User_Score")) {
    //                                                                otherUserScore = document.getDouble("User_Score");
    //                                                            } else {
    //                                                                otherUserScore = 0;
    //                                                            }

                                                                String otherUserImageUrl = document.getString("image_url");
                                                                if (otherUserImageUrl == null) {
                                                                    otherUserImageUrl = "";
                                                                }
                                                                userModelArrayList1.add(new UserModel(otherUserFullName, otherUserImageUrl, subjectCode, otherUserCampus, otherUserID ));
                                                            }


                                                        }
                                                    } else {
                                                        Log.d("TAG", "Error getting documents: " + task.getException());
                                                    }
                                                    // we are initializing our adapter class and passing our arraylist to it.
                                                    NotiAdapter userAdapter1 = new NotiAdapter(getActivity(), userModelArrayList1);
                                                    // NotiAdapter userAdapter2 = new NotiAdapter(getActivity(), userModelArrayList2);

                                                    // below line is for setting a layout manager for our recycler view.
                                                    // here we are creating vertical list so we will provide orientation as vertical
                                                    LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                                                    LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

                                                    // in below two lines we are setting layoutmanager and adapter to our recycler view.
                                                    listNotiRV1.setLayoutManager(linearLayoutManager1);
                                                    listNotiRV1.setAdapter(userAdapter1);

                                                }
                                            });

                                        }
                                    } else {
                                        emptyCounter++;
                                    }
                                }
                                if (emptyCounter == requestsReceived.size()){
                                    binding.emptyList.setVisibility(View.VISIBLE);
                                    Log.d("NotificationFragment", "Empty array");
                                }
                            }
                        } else {
                            Log.d("TAG: ", "No such document");
                        }
                    } else {
                        Log.d("TAG: ", "Fail task in searching current user's request data");
                    }
                }
            });

            // set button "new request" updateLayout
            TextView toggleBtn = binding.newRequestToggle;
            toggleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RelativeLayout relativeLayout1 = binding.relativeLayout1;
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


            return root;
        }

    @Override
        public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }
    }