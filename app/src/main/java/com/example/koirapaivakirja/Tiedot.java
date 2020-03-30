package com.example.koirapaivakirja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Bundle;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Tiedot extends AppCompatActivity {

    EditText mInfoName, mInfoNote, mInfoIDNumber, mInfoReg, mInfoBirth, mInfoKennelName, mInfoWeight;
    ImageView mInfoImageView;
    // Not in use anymore String infoMode = "Info"; // Modes = Info, Edit, Add
    Handler handler;
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiedot);

        mInfoIDNumber = findViewById(R.id.infoIDNum);
        mInfoBirth = findViewById(R.id.infoBirth);
        mInfoKennelName = findViewById(R.id.infoKennelName);
        mInfoName = findViewById(R.id.infoName);
        mInfoReg = findViewById(R.id.infoReg);
        mInfoNote = findViewById(R.id.infoNote);
        mInfoWeight = findViewById(R.id.infoWeight);

        mInfoImageView = findViewById(R.id.infoDogImage);

        handler = new Handler();

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

      // mInfoImageView.setOnClickListener();
      //  infoMode = "Edit";
      //  toggleEditMode("Edit");
        getDataFromFireStore();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case (R.id.infoToolbarAdd):
                toggleEditMode("Add");
                Toast.makeText(this, "Add selected", Toast.LENGTH_LONG).show();
                return true;
            case (R.id.infoToolbarRemove):
               // Toast.makeText(this, "Remove selected", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Save to DB selected", Toast.LENGTH_LONG).show();
                try {
                    putDataToFireStore();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
            /*case (R.id.infoToolbarSave):

                return true;
            */

        }
        return false;
    }

    private void getDataFromFireStore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("dogs").document("rKJvTSFsozBr0V5JAyvQ");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map dokumentti;
                        dokumentti = document.getData();
                        if(dokumentti != null) {
                            mInfoName.setText((CharSequence) dokumentti.get("nickname"));
                            mInfoKennelName.setText((CharSequence) dokumentti.get("kennelname"));
                            mInfoReg.setText((CharSequence) dokumentti.get("regnumber"));
                            mInfoIDNumber.setText((CharSequence) dokumentti.get("microChipID").toString());

                            Date bDate = document.getDate("birthdate");
                            if (bDate != null) {
                                String sbDate = sdf.format(bDate);
                                mInfoBirth.setText(sbDate);
                            }
                        }
                        // Log.d("Koera", "DocumentSnapshot data: " + document.toString()); // document.getData());
                    } else {
                        Log.d("Koera", "No such document");
                    }
                } else {
                   // Log.d(this, "get failed with ", task.getException());
                }
            }
        });

    }

    private void putDataToFireStore() throws ParseException {

        Map<String, Object> dogData = new HashMap<>();
        String pNickName= String.valueOf(mInfoName.getText());
        String pKennelname = String.valueOf(mInfoKennelName.getText());
        String pRegNum = String.valueOf(mInfoReg.getText());
        String temp = mInfoIDNumber.getText().toString();
        long pMicroChipID = Long.parseLong(temp);
        //String dateTemp = mInfoBirth.getText().toString();
        //Date dateIntTemp = Date,(dateTemp);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault());
        String dateInString = mInfoBirth.getText().toString();
        Date dateTemp = formatter.parse(dateInString);
        if(dateTemp != null) {
            Timestamp pBirthDate = new Timestamp(dateTemp);
            dogData.put("birthdate", pBirthDate);
        }

        dogData.put("nickname", pNickName);
        dogData.put("kennelname", pKennelname);
        dogData.put("regnumber", pRegNum);
        dogData.put("microChipID", pMicroChipID);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("dogs").add(dogData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Log.d("Koira", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Koira", "Error adding document", e);
                    }
                }); /**/

        // newDog.set(dogData);

    };
// Muuttaa EditTextit joko muokattavaan tai vain luettavaan tilaan. Add tyhjentää vanhan tekstin
    private void toggleEditMode(String infoMode2){
        switch(infoMode2){
            case "Info":
                mInfoIDNumber.setEnabled(false);
                mInfoBirth.setEnabled(false);
                mInfoKennelName.setEnabled(false);
                mInfoName.setEnabled(false);
                mInfoReg.setEnabled(false);
                mInfoNote.setEnabled(false);
                mInfoWeight.setEnabled(false);
                break;
            case "Edit":
                mInfoIDNumber.setEnabled(true);
                mInfoBirth.setEnabled(true);
                mInfoKennelName.setEnabled(true);
                mInfoName.setEnabled(true);
                mInfoReg.setEnabled(true);
                mInfoNote.setEnabled(true);
                mInfoWeight.setEnabled(true);
                break;
            case "Add":
                // En tiedä voiko tehdä näin mutta kokeillaan, toimi.
                toggleEditMode("Edit");
                mInfoIDNumber.setText("");
                mInfoBirth.setText("");
                mInfoKennelName.setText("");
                mInfoName.setText("");
                mInfoReg.setText("");
                mInfoNote.setText("");
                mInfoWeight.setText("");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + infoMode2);
        }
    }




}