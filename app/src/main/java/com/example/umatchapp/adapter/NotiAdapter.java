package com.example.umatchapp.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.umatchapp.AccountSingleton;
import com.example.umatchapp.MainActivity;
import com.example.umatchapp.R;
import com.example.umatchapp.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import com.example.umatchapp.FirestoreHelper;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotiAdapter extends RecyclerView.Adapter<NotiAdapter.Viewholder> {

    private final Context context;
    private final ArrayList<UserModel> userModelArrayList;
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    String googleEmail = AccountSingleton.getInstance().getGoogleAccount().getEmail();
    String currentUser_ID = AccountSingleton.getInstance().getGoogleAccount().getId();
    DocumentReference currentUserDocument = db.collection("users").document(googleEmail);
    private static final String TAG = MainActivity.class.getSimpleName();

    // Constructor
    public NotiAdapter(Context context, ArrayList<UserModel> userModelArrayList) {
        this.context = context;
        this.userModelArrayList = userModelArrayList;
    }

    @NonNull
    @Override
    public NotiAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.noti_card_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotiAdapter.Viewholder holder, @SuppressLint("RecyclerView") int position) {
        // to set data to textview and imageview of each card layout
        UserModel model = userModelArrayList.get(position);
        holder.user_name.setText(model.getUser_name());
        holder.user_campus.setText("I'm in " + model.getUser_campus());
        holder.target_subject.setText("I'm interesting in " + model.getTarget_subject());
//        holder.user_score.setText("" + model.getUser_score());
        String imageUrl = model.getUser_image(); // Assuming the image URL is stored in the model

        Glide.with(context)
                .load(imageUrl)
                .into(holder.user_image);

        holder.yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // click yes： delete Requests_Receive from current user's field
                List<String> list = new ArrayList<>();
                list.add(model.getUser_id());
                FirestoreHelper.removeArrayFromMapField(
                        "users",
                        currentUser_ID,
                        "Requests_Received",
                        model.getTarget_subject(),
                        list);

                // delete Requests_Sent from sender's field
                List<String> list2 = new ArrayList<>();
                list2.add(currentUser_ID);
                FirestoreHelper.removeArrayFromMapField(
                        "users",
                        model.getUser_id(),
                        "Requests_Sent",
                        model.getTarget_subject(),
                        list2);

                // add Requests_Accepted in current user's field
                List<String> list3 = new ArrayList<>();
                list3.add(model.getUser_id());
                FirestoreHelper.addArrayToMapField(
                        "users",
                        currentUser_ID,
                        "Requests_Accepted",
                        model.getTarget_subject(),
                        list3);

                // add Requests_Accepted in sender's field
                List<String> list4 = new ArrayList<>();
                list4.add(currentUser_ID);
                FirestoreHelper.addArrayToMapField(
                        "users",
                        model.getUser_id(),
                        "Requests_Accepted",
                        model.getTarget_subject(),
                        list4);

                // after updating Firestore, remove the item from the data model list
                userModelArrayList.remove(position);

                // then notify the adapter that an item is removed
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, userModelArrayList.size());

                // create a snackbar message
                Snackbar.make(v, "Successfully added friend!", Snackbar.LENGTH_LONG).show();

                // create a Dialog
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.notification_dialog);
                // close bottom
                Button dialogButton = dialog.findViewById(R.id.close_button);

                // two user image in Dialog
                CircleImageView user_image1 = dialog.findViewById(R.id.dialog_image_user1);
                CircleImageView user_image2 = dialog.findViewById(R.id.dialog_image_user2);
                // set image
                currentUserDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            String user1_image_url = document.getString("image_url");
                            Glide.with(context)
                                    .load(user1_image_url)
                                    .into(user_image1);
                        }
                    }
                });
                Glide.with(context)
                        .load(model.getUser_image())
                        .into(user_image2);

                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                // show the custom dialog
                dialog.show();
            }

        });
        holder.noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // click no： delete Requests_Receive from current user's field
                List<String> list = new ArrayList<>();
                list.add(model.getUser_id());
                FirestoreHelper.removeArrayFromMapField(
                        "users",
                        currentUser_ID,
                        "Requests_Received",
                        model.getTarget_subject(),
                        list);

                // delete Requests_Sent from sender's field
                List<String> list2 = new ArrayList<>();
                list2.add(currentUser_ID);
                FirestoreHelper.removeArrayFromMapField(
                        "users",
                        model.getUser_id(),
                        "Requests_Sent",
                        model.getTarget_subject(),
                        list2);

                // after updating Firestore, remove the item from the data model list
                userModelArrayList.remove(position);

                // create a snackbar message
                Snackbar.make(v, "Rejected request", Snackbar.LENGTH_LONG).show();

                // then notify the adapter that an item is removed
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, userModelArrayList.size());


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
        private final ImageView user_image;
        private final TextView user_name;
        private final TextView user_campus;
        private final TextView target_subject;
        private Button yesButton;
        private Button noButton;


        public Viewholder(@NonNull View itemView) {
            super(itemView);
            user_image = itemView.findViewById(R.id.userImage);
            user_name = itemView.findViewById(R.id.userName);
            target_subject = itemView.findViewById(R.id.targetSubject);
            user_campus = itemView.findViewById(R.id.userCampus);
            yesButton = itemView.findViewById(R.id.btnYes);
            noButton = itemView.findViewById(R.id.btnNo);
        }
    }
}
