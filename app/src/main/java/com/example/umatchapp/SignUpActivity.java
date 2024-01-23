package com.example.umatchapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.umatchapp.model.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.example.umatchapp.api.UMatchApiResponse;
import com.example.umatchapp.api.UMatchApiService;
import com.google.common.util.concurrent.AtomicDouble;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.List;



import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 200;
    final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private ActivityResultLauncher<Intent> captureImageLauncher;

    CircleImageView profileImage;
    Bitmap imageBitmap;
    String profileImageSrc;
    private static final String TAG = MainActivity.class.getSimpleName();
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    GoogleSignInAccount account = AccountSingleton.getInstance().getGoogleAccount();

    private GoogleSignInClient mGoogleSignInClient;

    // for api endpoint use
    private static final String BASE_URL_UMATCH = "https://us-central1-aiplatform.googleapis.com/";
    private static final String BEARER_TOKEN = "ya29.a0AfB_byBlMxkafyj4ijMg3_n8PAy_WQM1Fm6vuQqCIshOR_fP3uZtLeziA4zrz6nlKtg3okidGlGs1Mf_gBl6auRgVdOa8iFIMvDvXgu472dtr3yIpL0K0CTCTmlCaFG5NI2hJnXm8fVMvVjVw-che5RqMHQnfBfcwr_jb0sknb6q6rJf8QpdfO2OgorKQnnHu1cQBRo3sk_3n-FuwIMvKABoxHWyIJBIpx_vwHRUqr2Z2p2Uxo14NlpqsSfFTOpiSDuo1yQ3n3BeBUh_2S2MbL3QLQMvTiXWFi0xF8pdD67wUsZ0bU1oqil3hFTiRtZYOeWtV1wTrQ8RNSsoBD78zfOhu7JfjHM1oKTLL_nvLut1CE3ZcUBH7FMfnrJVpNEpu4TmxIbN0XuqGRSTOsHLHFefkzA0X70aCgYKAcISARISFQGOcNnC0tHZLJAz87miJt6FTIJ_cg0422";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_2);
        updateProfileData();


        // select major
        Spinner spinner_major = findViewById(R.id.spinner_major);
        ArrayAdapter<CharSequence> adapter_major = ArrayAdapter.createFromResource(this,
                R.array.spinner_options_major, android.R.layout.simple_spinner_item);
        adapter_major.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_major.setAdapter(adapter_major);

        spinner_major.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
//                Toast.makeText(SignUpActivity.this, "Select: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // select campus
        Spinner spinner_campus = findViewById(R.id.spinner_campus);
        ArrayAdapter<CharSequence> adapter_campus = ArrayAdapter.createFromResource(this,
                R.array.spinner_options_campus, android.R.layout.simple_spinner_item);
        adapter_campus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_campus.setAdapter(adapter_campus);

        spinner_campus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
//                Toast.makeText(SignUpActivity.this, "Select: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // select gender
        Spinner spinner_gender = findViewById(R.id.spinner_gender);
        ArrayAdapter<CharSequence> adapter_gender = ArrayAdapter.createFromResource(this,
                R.array.spinner_options_gender, android.R.layout.simple_spinner_item);
        adapter_gender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gender.setAdapter(adapter_gender);

        spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
//                Toast.makeText(SignUpActivity.this, "Select: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // select nationality
        Spinner spinner_nationality = findViewById(R.id.spinner_nationality);
        ArrayAdapter<CharSequence> adapter_nationality = ArrayAdapter.createFromResource(this,
                R.array.spinner_options_nationality, android.R.layout.simple_spinner_item);
        adapter_nationality.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_nationality.setAdapter(adapter_nationality);

        spinner_nationality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
//                Toast.makeText(SignUpActivity.this, "Select: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // select subject1
        Spinner spinner_subject1 = findViewById(R.id.spinner_subject1);
        ArrayAdapter<CharSequence> adapter_subject1 = ArrayAdapter.createFromResource(this,
                R.array.spinner_options_subjects, android.R.layout.simple_spinner_item);
        adapter_subject1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_subject1.setAdapter(adapter_subject1);

        spinner_subject1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
//                Toast.makeText(SignUpActivity.this, "Select: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // select subject2
        Spinner spinner_subject2 = findViewById(R.id.spinner_subject2);
        ArrayAdapter<CharSequence> adapter_subject2 = ArrayAdapter.createFromResource(this,
                R.array.spinner_options_subjects, android.R.layout.simple_spinner_item);
        adapter_subject2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_subject2.setAdapter(adapter_subject2);

        spinner_subject2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
//                Toast.makeText(SignUpActivity.this, "Select: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // select subject3
        Spinner spinner_subject3 = findViewById(R.id.spinner_subject3);
        ArrayAdapter<CharSequence> adapter_subject3 = ArrayAdapter.createFromResource(this,
                R.array.spinner_options_subjects, android.R.layout.simple_spinner_item);
        adapter_subject3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_subject3.setAdapter(adapter_subject3);

        spinner_subject3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
//                Toast.makeText(SignUpActivity.this, "Select: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // select subject4
        Spinner spinner_subject4 = findViewById(R.id.spinner_subject4);
        ArrayAdapter<CharSequence> adapter_subject4 = ArrayAdapter.createFromResource(this,
                R.array.spinner_options_subjects, android.R.layout.simple_spinner_item);
        adapter_subject4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_subject4.setAdapter(adapter_subject4);

        spinner_subject4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
//                Toast.makeText(SignUpActivity.this, "Select: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        Button finishBtn = findViewById(R.id.finish_btn);
        EditText firstName = findViewById(R.id.first_name_edit);
        EditText lastName = findViewById(R.id.Last_name_edit);
        EditText email = findViewById(R.id.e_mail_edit);
        EditText phone = findViewById(R.id.phone_edit);
        EditText description = findViewById(R.id.user_description);


        // umatch endpoint api
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody requestBody = RequestBody.create(mediaType, String.valueOf(description));

        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(BASE_URL_UMATCH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UMatchApiService umatchApiService = retrofit2.create(UMatchApiService.class);
        finishBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Retrieve the form field values
                String firstName_s = firstName.getText().toString().trim();
                String lastName_s = lastName.getText().toString().trim();
                String email_s = email.getText().toString().trim();
                String phone_s = phone.getText().toString().trim();
                String major = spinner_major.getSelectedItem().toString().trim();
                String campus = spinner_campus.getSelectedItem().toString().trim();
                String gender = spinner_gender.getSelectedItem().toString().trim();
                String nationality = spinner_nationality.getSelectedItem().toString().trim();
                String subject1 = spinner_subject1.getSelectedItem().toString().trim();
                String subject2 = spinner_subject2.getSelectedItem().toString().trim();
                String subject3 = spinner_subject3.getSelectedItem().toString().trim();
                String subject4 = spinner_subject4.getSelectedItem().toString().trim();
                String description_s = description.getText().toString().trim();
                ArrayList<String> subjects = new ArrayList<>();
                if (!subject1.equals("Not select")) {
                    subjects.add(subject1);
                }

                if (!subject2.equals("Not select")) {
                    subjects.add(subject2);
                }

                if (!subject3.equals("Not select")) {
                    subjects.add(subject3);
                }

                if (!subject4.equals("Not select")) {
                    subjects.add(subject4);
                }

                // Form validation: have to fill in the description in order to continue
                if (description_s.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in the description field.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Form validation: at lease 1 subject has to be added
                if (subjects.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please select at least one subject", Toast.LENGTH_SHORT).show();
                    return;
                }

                final AtomicDouble userScore = new AtomicDouble(0);
                String uniqueID = account.getId();

                // Initialize empty field RequestsSent and RequestReceived
                Map<String, Object> requestsSent = new HashMap<>();
                requestsSent.put(subject1, new ArrayList<>());
                requestsSent.put(subject2, new ArrayList<>());
                requestsSent.put(subject3, new ArrayList<>());
                requestsSent.put(subject4, new ArrayList<>());

                Map<String, Object> requestsReceived = new HashMap<>();
                requestsReceived.put(subject1, new ArrayList<>());
                requestsReceived.put(subject2, new ArrayList<>());
                requestsReceived.put(subject3, new ArrayList<>());
                requestsReceived.put(subject4, new ArrayList<>());

//                String profileImgSrc = imageBitmap.toString().trim();
                // Log the form field values
                Log.d("TAG", "F Name: " + firstName_s);
                Log.d("TAG", "L Name: " + lastName_s);
                Log.d("TAG", "Email: " + email_s);
                Log.d("TAG", "Phone: " + phone_s);
                Log.d("TAG", "major: " + major);
                Log.d("TAG", "campus: " + campus);
                Log.d("TAG", "nationality: " + nationality);
                Log.d("TAG", "gender: " + gender);
                Log.d("TAG", "subject1: " + subject1);
                Log.d("TAG", "subject2: " + subject2);
                Log.d("TAG", "subject3: " + subject3);
                Log.d("TAG", "subject4: " + subject4);
                Log.d("TAG", "description: " + description_s);
                Log.d("TAG", "Profile Src: " + profileImageSrc);
                Log.d("===================", "ID: " + uniqueID);

                // add field to database
                updateFirestoreField("User_ID", uniqueID);
                updateFirestoreField("F_Name", firstName_s);
                updateFirestoreField("L_Name", lastName_s);
                updateFirestoreField("Latrobe_Email", email_s);
                updateFirestoreField("Phone", phone_s);
                updateFirestoreField("Major", major);
                updateFirestoreField("Campus", campus);
                updateFirestoreField("Nationality", nationality);
                updateFirestoreField("Gender", gender);
                updateFirestoreField("Subjects", subjects);
                updateFirestoreField("Description", description_s);
                // updateFirestoreField("Requests_Sent", requestsSent);
                // updateFirestoreField("Requests_Received", requestsReceived);


                // call api endpoint
                String json = "{\"instances\": {\"mimeType\": \"text/plain\", \"content\": \"" + description_s + "\"}}";
                String authHeader = "Bearer " + BEARER_TOKEN;
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
                Call<UMatchApiResponse> call = umatchApiService.predict(authHeader,"602982173359", "8837771109970477056",body);
                call.enqueue(new Callback<UMatchApiResponse>() {
                    @Override
                    public void onResponse(Call<UMatchApiResponse> call, Response<UMatchApiResponse> response) {
                        UMatchApiResponse umatchResponse = response.body();
                        if (umatchResponse != null) {
                            List<UMatchApiResponse.Prediction> predictions = umatchResponse.getPredictions();
                            List<Float> confidences = predictions.get(0).getConfidences();
                            List<String> classifications = predictions.get(0).getDisplayNames();
                            StringBuilder sb = new StringBuilder();
                            double score = 0;
                            for (int i=0; i<confidences.size(); i++){
                                String line = classifications.get(i) + ": " + Float.toString(confidences.get(i)*100) + "%";
                                sb.append(line).append("\n");
                                switch (classifications.get(i)){
                                    case "team work":
                                        score += 0.4*(confidences.get(i)*100);
                                        break;
                                    case "hard-working":
                                        score += 0.3*(confidences.get(i)*100);
                                        break;
                                    case "motivated":
                                        score += 0.2*(confidences.get(i)*100);
                                        break;
                                    case "good communication":
                                        score += 0.1*(confidences.get(i)*100);
                                        break;
                                    default:
                                        score += 0;
                                        break;
                                }
                            }
                            userScore.set(score);
                            updateFirestoreField("User_Score", userScore.get());
                            Log.d("TAG", "Result from api: " + sb.toString());
                            Log.d("TAG", "User score: " + userScore.get());
                        } else {
                            Log.d("api", "null response");
                            // Check if "User_Score" field exists in the database
                            DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(account.getEmail()));
                            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null && document.exists()) {
                                            // User document exists, check if "User_Score" field exists
                                            if (!document.contains("User_Score")) {
                                                // "User_Score" field does not exist, set it to default value of 10
                                                updateFirestoreField("User_Score", 0);
                                            }
                                        }
                                        Log.d("TAG", "User score: " + document.getDouble("User_Score"));

                                    } else {
                                        Log.d("api", "Firestore error: " + task.getException().getMessage());
                                    }
                                }
                            });
                        }
                    }
                    @Override
                    public void onFailure(Call<UMatchApiResponse> call, Throwable t) {
                        //personalResultTextView.setText("Error: " + t.getMessage());
                        Log.d("TAG", "API error: " + t.getMessage());
                    }
                });

                // Create a UserModel object
                String fullName = firstName_s + " " + lastName_s;
                UserModel userData = new UserModel(uniqueID, fullName,email_s, phone_s, major, campus, subjects,nationality, gender, description_s, userScore.get(), profileImageSrc );

                Intent activity2Intent = new Intent(getApplicationContext(), MainActivity.class);
                activity2Intent.putExtra("userData", userData);
                startActivity(activity2Intent);
            }
        });


        // Select profileImage
        profileImage = findViewById(R.id.profile_image_user);

        final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Bundle extras = result.getData().getExtras();
                        imageBitmap = (Bitmap) extras.get("data");
                        profileImage.setImageBitmap(imageBitmap);
                        Log.d("TAG", "profileImage: " + profileImage.toString());

                    }
                });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });
    }

    private boolean hasPermission(){
        boolean permissionStatus = true;
        for (String permission :PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(SignUpActivity.this, permission) == PackageManager.PERMISSION_GRANTED) {
            } else {
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

    // this function is triggered when
    // the Select Image Button is clicked
    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    Log.d("TAG", "Selected image URI: " + selectedImageUri.toString());
                    profileImageSrc = selectedImageUri.toString();
                    profileImage.setImageURI(selectedImageUri);
                    signInAndUploadImage(selectedImageUri);
                }
            }
        }
    }

    // this function is a helper function to update FirestoreField
    private void updateFirestoreField(String key, Object value) {
        DocumentReference docRef = db.collection("users").document(Objects.requireNonNull(account.getEmail()));
        Log.d(TAG,"------------------------------\nupdating " + key);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Check if the field exists

                        // If the field doesn't exist, add it
                        Map<String, Object> updates = new HashMap<>();
                        updates.put(key, value);
                        docRef.update(updates)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    // Helper function to uploadImageToFirebaseStorage
    private void uploadImageToFirebaseStorage(Uri selectedImageUri) {
        // Create a reference to Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a unique identifier for the image
        String imageId = UUID.randomUUID().toString();

        // Create a reference to the specific image location in Firebase Storage
        StorageReference imageRef = storageRef.child("images/" + imageId);

        // Upload the image to Firebase Storage
        UploadTask uploadTask = imageRef.putFile(selectedImageUri);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    // Get the download URL of the image
                    imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUrl = task.getResult();
                                saveImageLinkToFirestore(downloadUrl.toString());
                            } else {
                                Log.e("Error", "Error getting download URL", task.getException());
                            }
                        }
                    });
                } else {
                    Log.e("Error", "Error uploading image", task.getException());
                }
            }
        });
    }

    // saveImageLink
    private void saveImageLinkToFirestore(String imageUrl) {
        // Prepare the data to save
        Map<String, Object> userData = new HashMap<>();
        userData.put("image_url", imageUrl);

        // Save the data to Firestore
        db.collection("users")
                .document(Objects.requireNonNull(account.getEmail()))
                .update(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firestore", "Image URL saved successfully");
                        } else {
                            Log.e("Firestore", "Error saving image URL", task.getException());
                        }
                    }
                });
    }

    private void signInAndUploadImage(Uri selectedImageUri) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Auth", "Signed in anonymously");
                            uploadImageToFirebaseStorage(selectedImageUri);
                        } else {
                            Log.e("Auth", "Error Firebase signing in anonymously", task.getException());
                        }
                    }
                });
    }

    private void updateProfileData(){
        Log.d(TAG,"updating edit ProfileData");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(Objects.requireNonNull(account.getEmail()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() && document.contains("Campus")) {
                                // get information
                                String f_name = document.getString("F_Name");
                                String l_name = document.getString("L_Name");
                                String image_url = document.getString("image_url");
                                String e_mail = document.getString("Latrobe_Email");
                                String phone = document.getString("Phone");
                                String major = document.getString("Major");
                                String campus = document.getString("Campus");
                                String gender = document.getString("Gender");
                                String nationality = document.getString("Nationality");
                                String description = document.getString("Description");
                                List<String> subjectList = (List<String>) document.get("Subjects");
                                String subject1 = "";
                                String subject2 = "";
                                String subject3 = "";
                                String subject4 = "";
                                if (subjectList != null && subjectList.size() >= 1) {
                                    subject1 = subjectList.get(0);
                                }
                                if (subjectList != null && subjectList.size() >= 2) {
                                    subject2 = subjectList.get(1);
                                }
                                if (subjectList != null && subjectList.size() >= 3) {
                                    subject3 = subjectList.get(2);
                                }
                                if (subjectList != null && subjectList.size() >= 4) {
                                    subject4 = subjectList.get(3);
                                }

                                EditText first_name_edit = findViewById(R.id.first_name_edit);
                                EditText last_name_edit = findViewById(R.id.Last_name_edit);
                                EditText email_edit = findViewById(R.id.e_mail_edit);
                                EditText phone_edit = findViewById(R.id.phone_edit);
                                Spinner majorSpinner = findViewById(R.id.spinner_major);
                                Spinner campusSpinner = findViewById(R.id.spinner_campus);
                                Spinner genderSpinner = findViewById(R.id.spinner_gender);
                                Spinner nationalitySpinner = findViewById(R.id.spinner_nationality);
                                Spinner subject1Spinner = findViewById(R.id.spinner_subject1);
                                Spinner subject2Spinner = findViewById(R.id.spinner_subject2);
                                Spinner subject3Spinner = findViewById(R.id.spinner_subject3);
                                Spinner subject4Spinner = findViewById(R.id.spinner_subject4);
                                EditText user_description_edit = findViewById(R.id.user_description);
                                CircleImageView profileImage = findViewById(R.id.profile_image_user);

                                // set data
                                first_name_edit.setText(f_name);
                                last_name_edit.setText(l_name);
                                email_edit.setText(e_mail);
                                phone_edit.setText(phone);
                                user_description_edit.setText(description);
                                Glide.with(SignUpActivity.this)
                                        .load(image_url)
                                        .placeholder(R.drawable.baseline_person_24)
                                        .error(R.drawable.baseline_person_24)
                                        .into(profileImage);

                                switch (Objects.requireNonNull(major)){
                                    case "Not select":
                                        majorSpinner.setSelection(0);
                                        break;
                                    case "Software Engineering":
                                        majorSpinner.setSelection(1);
                                        break;
                                    case "Cyber Security":
                                        majorSpinner.setSelection(2);
                                        break;
                                    case "Data Science":
                                        majorSpinner.setSelection(3);
                                        break;
                                    case "Network Engineering":
                                        majorSpinner.setSelection(4);
                                        break;
                                    default:
                                        break;
                                }

                                switch (Objects.requireNonNull(campus)){
                                    case "Not select":
                                        campusSpinner.setSelection(0);
                                        break;
                                    case "City":
                                        campusSpinner.setSelection(1);
                                        break;
                                    case "Bundoora":
                                        campusSpinner.setSelection(2);
                                        break;
                                    case "Online":
                                        campusSpinner.setSelection(3);
                                        break;
                                    default:
                                        break;
                                }

                                switch (Objects.requireNonNull(gender)){
                                    case "Not select":
                                        genderSpinner.setSelection(0);
                                        break;
                                    case "Female":
                                        genderSpinner.setSelection(1);
                                        break;
                                    case "Male":
                                        genderSpinner.setSelection(2);
                                        break;
                                    case "Unspecified":
                                        genderSpinner.setSelection(3);
                                        break;
                                    case "Gender Diverse":
                                        genderSpinner.setSelection(4);
                                        break;
                                    default:
                                        break;
                                }

                                switch (Objects.requireNonNull(nationality)){
                                    case "Not select":
                                        nationalitySpinner.setSelection(0);
                                        break;
                                    case "Australian":
                                        nationalitySpinner.setSelection(1);
                                        break;
                                    case "Chinese":
                                        nationalitySpinner.setSelection(2);
                                        break;
                                    case "Indian":
                                        nationalitySpinner.setSelection(3);
                                        break;
                                    case "American":
                                        nationalitySpinner.setSelection(4);
                                        break;
                                    case "Vietnamese":
                                        nationalitySpinner.setSelection(5);
                                        break;
                                    case "Others":
                                        nationalitySpinner.setSelection(6);
                                        break;
                                    default:
                                        break;
                                }

                                switch (Objects.requireNonNull(subject1)){
                                    case "Not select":
                                        subject1Spinner.setSelection(0);
                                        break;
                                    case "CSE3MAD":
                                        subject1Spinner.setSelection(1);
                                        break;
                                    case "CSE3PSD":
                                        subject1Spinner.setSelection(2);
                                        break;
                                    case "CSE4DBF":
                                        subject1Spinner.setSelection(3);
                                        break;
                                    case "CSE4OOF":
                                        subject1Spinner.setSelection(4);
                                        break;
                                    case "CSE4IFU":
                                        subject1Spinner.setSelection(5);
                                        break;
                                    case "CSE4002":
                                        subject1Spinner.setSelection(6);
                                        break;
                                    case "CSE4IP":
                                        subject1Spinner.setSelection(7);
                                        break;
                                    case "CSE5ALG":
                                        subject1Spinner.setSelection(8);
                                        break;
                                    case "CSE5PM":
                                        subject1Spinner.setSelection(9);
                                        break;
                                    case "CSE5003":
                                        subject1Spinner.setSelection(10);
                                        break;
                                    default:
                                        break;
                                }

                                switch (Objects.requireNonNull(subject2)){
                                    case "Not select":
                                        subject2Spinner.setSelection(0);
                                        break;
                                    case "CSE3MAD":
                                        subject2Spinner.setSelection(1);
                                        break;
                                    case "CSE3PSD":
                                        subject2Spinner.setSelection(2);
                                        break;
                                    case "CSE4DBF":
                                        subject2Spinner.setSelection(3);
                                        break;
                                    case "CSE4OOF":
                                        subject2Spinner.setSelection(4);
                                        break;
                                    case "CSE4IFU":
                                        subject2Spinner.setSelection(5);
                                        break;
                                    case "CSE4002":
                                        subject2Spinner.setSelection(6);
                                        break;
                                    case "CSE4IP":
                                        subject2Spinner.setSelection(7);
                                        break;
                                    case "CSE5ALG":
                                        subject2Spinner.setSelection(8);
                                        break;
                                    case "CSE5PM":
                                        subject2Spinner.setSelection(9);
                                        break;
                                    case "CSE5003":
                                        subject2Spinner.setSelection(10);
                                        break;
                                    default:
                                        break;
                                }

                                switch (Objects.requireNonNull(subject3)){
                                    case "Not select":
                                        subject3Spinner.setSelection(0);
                                        break;
                                    case "CSE3MAD":
                                        subject3Spinner.setSelection(1);
                                        break;
                                    case "CSE3PSD":
                                        subject3Spinner.setSelection(2);
                                        break;
                                    case "CSE4DBF":
                                        subject3Spinner.setSelection(3);
                                        break;
                                    case "CSE4OOF":
                                        subject3Spinner.setSelection(4);
                                        break;
                                    case "CSE4IFU":
                                        subject3Spinner.setSelection(5);
                                        break;
                                    case "CSE4002":
                                        subject3Spinner.setSelection(6);
                                        break;
                                    case "CSE4IP":
                                        subject3Spinner.setSelection(7);
                                        break;
                                    case "CSE5ALG":
                                        subject3Spinner.setSelection(8);
                                        break;
                                    case "CSE5PM":
                                        subject3Spinner.setSelection(9);
                                        break;
                                    case "CSE5003":
                                        subject3Spinner.setSelection(10);
                                        break;
                                    default:
                                        break;
                                }

                                switch (Objects.requireNonNull(subject4)){
                                    case "Not select":
                                        subject4Spinner.setSelection(0);
                                        break;
                                    case "CSE3MAD":
                                        subject4Spinner.setSelection(1);
                                        break;
                                    case "CSE3PSD":
                                        subject4Spinner.setSelection(2);
                                        break;
                                    case "CSE4DBF":
                                        subject4Spinner.setSelection(3);
                                        break;
                                    case "CSE4OOF":
                                        subject4Spinner.setSelection(4);
                                        break;
                                    case "CSE4IFU":
                                        subject4Spinner.setSelection(5);
                                        break;
                                    case "CSE4002":
                                        subject4Spinner.setSelection(6);
                                        break;
                                    case "CSE4IP":
                                        subject4Spinner.setSelection(7);
                                        break;
                                    case "CSE5ALG":
                                        subject4Spinner.setSelection(8);
                                        break;
                                    case "CSE5PM":
                                        subject4Spinner.setSelection(9);
                                        break;
                                    case "CSE5003":
                                        subject4Spinner.setSelection(10);
                                        break;
                                    default:
                                        break;
                                }

                            }
                        } else {
                            Log.d(TAG,"Can't find user data");
                        }
                    }
                });
    }
}

