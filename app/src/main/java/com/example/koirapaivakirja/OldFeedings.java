package com.example.koirapaivakirja;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class OldFeedings extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference feedingRef = db.collection("dogs/rKJvTSFsozBr0V5JAyvQ/feedingDB");
    private TextView feedingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oldfeedings);
        feedingData = findViewById(R.id.feedingList);

        loadNotes();

    }

    public void loadNotes() {
        feedingRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            FeedingData feedingData = documentSnapshot.toObject(FeedingData.class);
                            feedingData.setDocumentId(documentSnapshot.getId());

                            // String documentId = feedingData.getDocumentId();
                            String current_date = feedingData.getCurrent_date();
                            String current_time = feedingData.getCurrent_time();
                            String food_type = feedingData.getFood_type();
                            String food_amount = feedingData.getFood_amount();

                            data +=
                                    "\nRuokinnan pvm: " + current_date + "\nRuokinta aika: " + current_time +
                                    "\nRuoan tyyppi: " + food_type + "\nRuoan määrä: " + food_amount + "g" + "\n\n";
                        }

                        feedingData.setTextColor(Color.BLACK);
                        feedingData.setMovementMethod(new ScrollingMovementMethod());
                        feedingData.setText(data);
                    }
                });
    }
}
