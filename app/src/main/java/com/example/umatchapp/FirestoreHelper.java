package com.example.umatchapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirestoreHelper {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static void addArrayToMapField(String collectionName, String userId, String mapFieldName, String arrayFieldName, List<String> arrayFieldValue) {
        db.collection(collectionName)
                .whereEqualTo("User_ID", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                String documentName = document.getId();

                                DocumentReference docRef = db.collection(collectionName).document(documentName);

                                WriteBatch batch = db.batch();

                                for (String item : arrayFieldValue) {
                                    batch.update(docRef, mapFieldName + "." + arrayFieldName, FieldValue.arrayUnion(item));
                                }

                                batch.commit().addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        Log.d("Firestore", "Array added to map field successfully");
                                    } else {
                                        Log.d("Firestore", "Error adding array to map field", task2.getException());
                                    }
                                });

                            } else {
                                Log.d("Firestore", "No document found for the provided user id");
                            }
                        } else {
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    public static void removeArrayFromMapField(String collectionName, String userId, String mapFieldName, String arrayFieldName, List<String> arrayFieldValue) {
        db.collection(collectionName)
                .whereEqualTo("User_ID", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                String documentName = document.getId();

                                DocumentReference docRef = db.collection(collectionName).document(documentName);

                                WriteBatch batch = db.batch();

                                for (String item : arrayFieldValue) {
                                    batch.update(docRef, mapFieldName + "." + arrayFieldName, FieldValue.arrayRemove(item));
                                }

                                batch.commit().addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        Log.d("Firestore", "Array removed from map field successfully");
                                    } else {
                                        Log.d("Firestore", "Error removing array from map field", task2.getException());
                                    }
                                });

                            } else {
                                Log.d("Firestore", "No document found for the provided user id");
                            }
                        } else {
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
