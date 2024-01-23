package com.example.umatchapp.adapter;

import static android.provider.Settings.System.getString;
import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.umatchapp.AccountSingleton;
import com.example.umatchapp.MainActivity;
import com.example.umatchapp.R;
import com.example.umatchapp.model.UserModel;
import com.example.umatchapp.ui.match.MatchFragment;
import com.example.umatchapp.ui.match.ShowMatchingDialogFragment;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.Viewholder> {

    private final Context context;
    private final String userID;
    private final String selectedSubject;
    private final ArrayList<UserModel> userModelArrayList;

    GoogleSignInAccount account = AccountSingleton.getInstance().getGoogleAccount();
    private static final String TAG = MainActivity.class.getSimpleName();

    // Constructor
    public MatchAdapter(Context context, ArrayList<UserModel> userModelArrayList, String userID, String selectedSubject) {
        this.context = context;
        this.userModelArrayList = userModelArrayList;
        this.userID = userID;
        this.selectedSubject = selectedSubject;
    }

    @NonNull
    @Override
    public MatchAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggested_match_card, parent, false);
        return new Viewholder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        UserModel model = userModelArrayList.get(position);

        String imageUrl = model.getUser_image(); // Assuming the image URL is stored in the model

        Glide.with(context)
                .load(imageUrl)
                .into(holder.user_image);
        holder.user_name.setText(model.getUser_name());
        holder.user_major.setText(model.getUser_major());
        holder.user_campus.setText(model.getUser_campus());
        holder.user_gender.setText(model.getUser_gender());
        holder.user_nationality.setText(model.getUser_nationality());
        holder.user_description.setText(model.getUser_description());

        Log.d("Test", model.getUser_subjects().toString());
//        ArrayList itemList = new ArrayList<>(Arrays.asList("item1", "item2", "item3"));
//        Log.d("Test", itemList.toString());

        if (holder.user_subjects != null && model.getUser_subjects() != null) {
            // Loop through the user_subjects ArrayList and concatenate the elements
            StringBuilder subjectsText = new StringBuilder();
            for (Object subject : model.getUser_subjects()) {
                if (!subject.toString().equals("Not select")){
                    subjectsText.append(subject.toString()).append(", ");
                }
            }
            // Remove the trailing comma and space
            if (subjectsText.length() > 2) {
                subjectsText.setLength(subjectsText.length() - 2);
            }
            // Set the concatenated subjects text to the TextView
            holder.user_subjects.setText(subjectsText.toString());
        }

        holder.profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.expandableSection.getVisibility() == View.VISIBLE) {
                    holder.profile_btn.setText(context.getString(R.string.view_profile_details));
                    holder.expandableSection.setVisibility(View.GONE);
                } else {
                    holder.profile_btn.setText(context.getString(R.string.hide_profile_details));
                    holder.expandableSection.setVisibility(View.VISIBLE);
                }

            }

        });

        holder.request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "Sending Request", Toast.LENGTH_SHORT).show();

                //Name of Array key - the value to add - the account to add it to, V
                updateFirestoreField("Requests_Sent", model.getUser_id(), account.getId(), v);
                updateFirestoreField("Requests_Received", account.getId(), model.getUser_id(), v);

            }
        });
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Log.d("Match Adapter", "Clicked");
//            }
//
//        });
    }

    private void updateFirestoreField(String key, String value, String accountID, View v) {
        CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("users");
        Query queryCurrentUser = usersCollection.whereEqualTo("User_ID", accountID);
        queryCurrentUser.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        Log.d("TAG", "selected subject:" + selectedSubject);
                        Log.d("TAG", "value:" + value);

                        Object existingRequests = document.get(key);
                        if (existingRequests instanceof Map) {
                            // Existing field is a Map
                            Map<String, Object> existingMap = (Map<String, Object>) existingRequests;
                            List<String> listOfUsersRequests = (List<String>) existingMap.get(selectedSubject);

                            if (listOfUsersRequests == null) {
                                // Create a new empty List<String> and add the value
                                listOfUsersRequests = new ArrayList<>();
                                listOfUsersRequests.add(value);
                            } else if (!listOfUsersRequests.contains(value)) {
                                // Add the value to the existing array if it doesn't already exist
                                listOfUsersRequests.add(value);
                            } else {
                                // Value already exists in the array
                                Toast.makeText(v.getContext(), "Request Already Sent before", Toast.LENGTH_SHORT).show();
                                Log.d("Dialog request: ", "Request Already Sent before");
                                return; // Exit the method since no further updates are needed
                            }

                            existingMap.put(selectedSubject, listOfUsersRequests);

                            document.getReference().update(key, existingMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(v.getContext(), "Request Sent", Toast.LENGTH_SHORT).show();
                                            Log.d("Dialog request: ", "Request Sent");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(v.getContext(), "Request Failed", Toast.LENGTH_SHORT).show();
                                            Log.d("Dialog request: ", "Request Failed");
                                        }
                                    });
                        } else {
                            // Requests_Sent/Requests_Received does not exist, create a new map
                            Map<String, Object> newMap = new HashMap<>(); newMap = new HashMap<>();
                            List<String> listOfUsersRequests = new ArrayList<>();
                            listOfUsersRequests.add(value);
                            newMap.put(selectedSubject, listOfUsersRequests);

                            document.getReference().update(key, newMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(v.getContext(), "Request Sent", Toast.LENGTH_SHORT).show();
                                            Log.d("Dialog request: ", "Request Sent");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(v.getContext(), "Request Failed", Toast.LENGTH_SHORT).show();
                                            Log.d("Dialog request: ", "Request Failed");
                                        }
                                    });
                        }





//                        if(existingArray.contains(value)){
//                            Toast.makeText(v.getContext(), "Request Already Sent before", Toast.LENGTH_SHORT).show();
//                            Log.d("Dialog request: ", "Request Already Sent before");
//                        } else{
//                            existingArray.add(value);
//                        }
//
//                        document.getReference().update(key, existingArray)
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        Toast.makeText(v.getContext(), "Request Sent", Toast.LENGTH_SHORT).show();
//                                        Log.d("Dialog request: ", "Request Sent");
//
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Toast.makeText(v.getContext(), "Request Failed", Toast.LENGTH_SHORT).show();
//                                        Log.d("Dialog request: ", "Request Failed");
//
//                                    }
//                                });

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return userModelArrayList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class Viewholder extends RecyclerView.ViewHolder {
        // Item with value to be retrieved from the database

        private ImageView user_image;
        private TextView user_name;
        private TextView user_major;
        private TextView user_campus;
        private TextView user_gender;
        private TextView user_nationality;
        private TextView user_subjects;
        private TextView user_description;

        private Button profile_btn;
        private Button request_btn;
//        private final TextView user_score;
        private ConstraintLayout expandableSection;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            user_image = itemView.findViewById(R.id.userImage);
            user_name = itemView.findViewById(R.id.userName);
            user_major = itemView.findViewById(R.id.major_content);
            user_campus = itemView.findViewById(R.id.campus_content);
            user_gender = itemView.findViewById(R.id.gender_content);
            user_nationality = itemView.findViewById(R.id.nationality_content);
            user_subjects = itemView.findViewById(R.id.subjects_content);
            user_description = itemView.findViewById(R.id.description_content);

            profile_btn = itemView.findViewById(R.id.view_profile);
            request_btn = itemView.findViewById(R.id.send_request);
            expandableSection = itemView.findViewById(R.id.expandableSection);
//            itemView.findViewById(R.id.view_profile).setOnClickListener(this);
//            view_profile_btn = itemView.findViewById(R.id.view_profile);
//            send_request_btn = itemView.findViewById(R.id.send_request);
//            user_score = itemView.findViewById(R.id.userScore);
        }



    }

}
