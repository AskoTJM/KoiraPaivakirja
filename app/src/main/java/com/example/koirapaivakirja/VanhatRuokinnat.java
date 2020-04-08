package com.example.koirapaivakirja;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class VanhatRuokinnat extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference feedingRef = db.collection("dogs/rKJvTSFsozBr0V5JAyvQ/feedingDB");
    private TextView feedingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vanhat_ruokinnat);
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
                            RuokintaData ruokintaData = documentSnapshot.toObject(RuokintaData.class);
                            ruokintaData.setDocumentId(documentSnapshot.getId());

                            String documentId = ruokintaData.getDocumentId();
                            String current_date = ruokintaData.getCurrent_date();
                            String current_time = ruokintaData.getCurrent_time();
                            String food_type = ruokintaData.getFood_type();
                            String food_amount = ruokintaData.getFood_amount();

                            data += "ID: " + documentId
                                    + "\nRuokinnan pvm: " + current_date + "\nRuokinta aika: " + current_time +
                                    "\nRuoan tyyppi: " + food_type + "\nRuoan määrä: " + food_amount + "g" + "\n\n";
                        }

                        feedingData.setText(data);
                    }
                });
    }
}
