package com.example.umatchapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.umatchapp.R;
import com.example.umatchapp.model.UserModel;
import com.example.umatchapp.ui.profile.OtherUserProfileFragment;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.Viewholder> {

    private final Context context;
    private final ArrayList<UserModel> userModelArrayList;
    private FragmentManager fragmentManager;

    // Constructor
    public ContactAdapter(Context context, ArrayList<UserModel> userModelArrayList, String subjectCode, FragmentManager fragmentManager) {
        this.context = context;
        this.userModelArrayList = userModelArrayList;
        this.fragmentManager = fragmentManager;
    }



    @NonNull
    @Override
    public ContactAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_card_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        UserModel model = userModelArrayList.get(position);
        holder.user_name.setText(model.getUser_name());
        holder.user_matched_subject.setText(model.getTarget_subject());

        holder.view_details_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = model.getUser_id();
                // Create an instance of OtherUserProfileFragment
                OtherUserProfileFragment dialogFragment = new OtherUserProfileFragment(userID);
                dialogFragment.show(fragmentManager, null);

            }
        });


        String imageUrl = model.getUser_image();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                Glide.with(context).load(imageUrl).into(holder.user_image);
            }
        }
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
        private final TextView user_matched_subject;
        private Button view_details_btn;


        public Viewholder(@NonNull View itemView) {
            super(itemView);
            user_image = itemView.findViewById(R.id.userImage);
            user_name = itemView.findViewById(R.id.userName);
            user_matched_subject = itemView.findViewById(R.id.matchedSubject);
            view_details_btn = itemView.findViewById(R.id.otherProfile);
        }
    }
}
