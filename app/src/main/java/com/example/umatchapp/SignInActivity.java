package com.example.umatchapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;

import com.example.umatchapp.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> signInResultLauncher;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = MainActivity.class.getSimpleName();
    public FirebaseFirestore db;
    public GoogleSignInAccount googleAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        setContentView(R.layout.sign_in);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // SignIn
        configureGoogleSignIn();

        // Firestore database
        db = FirebaseFirestore.getInstance();

        // Buttons
        configureSignInButton();
    }

    // helper function to add newUser
    private void addNewUser(GoogleSignInAccount account) {
        DocumentReference docRef = db.collection("users").document(Objects.requireNonNull(account.getEmail()));
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    // if document don't exist, set a new document
                    // if document already exist don't need do anything
                    Map<String, Object> user = new HashMap<>();
                    user.put("e-mail", account.getEmail());
                    user.put("account name", account.getDisplayName());

                    // add document
                    // document name is user's email
                    db.collection("users").document(Objects.requireNonNull(account.getEmail())).set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });
                }
            }
        });


    }


    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void configureSignInButton() {
        SignInButton signInButton = findViewById(R.id.signin_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        Log.d(TAG, "Attempting to login");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            // check task
            if(task.isSuccessful()){
                Log.d(TAG, "task is successful");
            }
            else{
                Log.d(TAG, "task is fail");
            }

            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                googleAccount = account;
                handleSignInResult(account);
                //firebaseAuthWithGoogle(account.getIdToken(), account); // Authenticate with Firebase

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                handleSignInResult(null);
            }
        }
    }


    // check google account exist
    // add user information to firestore database and updateUI
    private void handleSignInResult(GoogleSignInAccount account) {
        Log.d(TAG, "handling SignInResult");
        if (account != null) {
            addNewUser(account);
            AccountSingleton.getInstance().setGoogleAccount(account);
            checkProfileExist(true);
        } else {
            Log.d(TAG, "account == null");
            checkProfileExist(false);
        }
        Log.d(TAG, "handle finished");
    }

    // This method is used to check if the user data already exists in the database,
    // if not, jump to the registration page,
    // if it does, jump to the main screen.
    private void checkProfileExist(boolean isSignedIn){
        if (isSignedIn){

            DocumentReference docRef = db.collection("users").document(Objects.requireNonNull(googleAccount.getEmail()));
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Check if the "Phone" field exists
                            // if "Phone" not exists, means this account hasn't signUp
                            // go to the sign_up activity
                            if (!Objects.requireNonNull(document.getData()).containsKey("Phone")) {
                                Log.d(TAG, "Don't exist profile, go to signUpActivity");
                                Intent activityIntent = new Intent(getApplicationContext(), SignUpActivity.class);
                                startActivity(activityIntent);
                            }
                            else {
                                String userID = document.getString("User_ID");
                                String userName = document.getString("F_Name") + " " + document.getString("L_Name");
                                String  image_url = document.getString("image_url");
                                String  e_mail = document.getString("Latrobe_Email");
                                String  phone = document.getString("Phone");
                                String  major = document.getString("Major");
                                String  campus = document.getString("Campus");
                                String  gender = document.getString("Gender");
                                String  nationality = document.getString("Nationality");
                                String  description = document.getString("Description");
                                Double  score = document.getDouble("User_Score");
//                               // Retrieve subjects array
                                ArrayList<String> subjects = (ArrayList<String>) document.get("Subjects");


                                UserModel userData = new UserModel(userID, userName,e_mail, phone, major, campus, subjects,nationality, gender, description, score, image_url );

                                Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);
                                activityIntent.putExtra("userData", userData);
                                startActivity(activityIntent);
                            }
                        }
                        // if account is already signed Up(check firestore database)
                        // directly go to the home page
                        else {
                            Log.d(TAG, "document don't exist");
                        }
                    } else {
                        Log.d(TAG, "get failed with task is not successful", task.getException());
                    }
                }
            });
        }
    }

}