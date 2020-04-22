package com.example.koirapaivakirja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
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


    private TextView feedingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oldfeedings);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("DogPref", 0); // 0 - for private mode

        String tempDogString = pref.getString("dog"+pref.getInt("dogChosenNumber",-2),null);
        String tempDogPath = "dogs/"+tempDogString+"/feedingDB";

        feedingData = findViewById(R.id.feedingList);

        loadNotes(tempDogPath);

    }

    public void loadNotes(String tempDogPath) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference feedingRef = db.collection(tempDogPath);
        feedingRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            FeedingData feedingData = documentSnapshot.toObject(FeedingData.class);
                            feedingData.setDocumentId(documentSnapshot.getId());

                             //String documentId = feedingData.getDocumentId();
                            String current_date = feedingData.getCurrent_date();
                            String current_time = feedingData.getCurrent_time();
                            String food_type = feedingData.getFood_type();
                            String food_amount = feedingData.getFood_amount();
                            String feedingNote = feedingData.getFeedingNote();

                            data +=
                                    "\nRuokinnan pvm: " + current_date + "\nRuokinta aika: " + current_time +
                                    "\nRuoan tyyppi: " + food_type + "\nRuoan määrä: " + food_amount + "g" + "\nLisätietoja:" + feedingNote + "\n";
                        }
                        feedingData.setTextColor(Color.BLACK);
                        feedingData.setMovementMethod(new ScrollingMovementMethod());
                        feedingData.setText(data);
                    }
                });
    }
}
