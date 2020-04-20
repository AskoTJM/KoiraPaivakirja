package com.example.koirapaivakirja;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;


public class Activities extends AppCompatActivity {

    private static final int MIN_SWIPING_DISTANCE = 50;
    private static final int THRESHOLD_VELOCITY = 50;
    private static final int ERROR_DOGS = -2;
    private static final int NEW_DOG = -1;

    EditText mWorkout, mStart, mCurrent, mDuration, mDate;
    ImageView activitiesDogImage;
    CheckBox mOut, mPlay;
    Button mSave;
    Handler handler;
    String activity, pNotes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);
        mWorkout = findViewById(R.id.activityNote);
        mStart = findViewById(R.id.activityTimeStart);
        mCurrent = findViewById(R.id.activityElapsedTime);
        mDuration = findViewById(R.id.activityTimeStop);
        mDate = findViewById(R.id.activityDateStart);
        mOut = findViewById(R.id.checkOut);
        mPlay = findViewById(R.id.checkPlay);
        activitiesDogImage = findViewById(R.id.activityDogImage);
        final Button mSave = findViewById(R.id.activitySaveButton);

        Toolbar mainToolbar = findViewById(R.id.activityToolbar);
        setSupportActionBar(mainToolbar);

        getProfilePicture();

        handler = new Handler();
        mSave.setOnClickListener(new View.OnClickListener() {
            //Antaa tallentaa lenkin tiedot vain jos toinen aktiviteettityypeistä on valittuna
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.activitySaveButton:
                        if (mOut.isChecked()) {
                            activity = "Outside";
                        }
                        if (mPlay.isChecked()) {
                            activity = "Play";
                        }
                        if ((mOut.isChecked() == true) && (mPlay.isChecked() == true)||(mOut.isChecked() == false) && (mPlay.isChecked() == false)) {

                            Toast.makeText(Activities.this,
                                    "Choose one activity type", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                pNotes = mWorkout.getText().toString();
                                saveData();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }


                }
            }
        });
    }

        private void saveData() throws ParseException {
            Toast.makeText(Activities.this, "Aktiviteetti lisätty", Toast.LENGTH_SHORT).show();

            Map<String, Object> Aktiviteetti = new HashMap<>();

            String pStart = mStart.getText().toString();
            String pDuration = mCurrent.getText().toString();
            String pEnd = mDuration.getText().toString();

            if(pNotes!= null) {
                Aktiviteetti.put("Notes", pNotes);
            }

            Aktiviteetti.put("Start", pStart);
            Aktiviteetti.put("Activity", activity);
            Aktiviteetti.put("Duration", pDuration + " hours");
            Aktiviteetti.put("End", pEnd);
            Aktiviteetti.put("Date", Timestamp.now());

            SharedPreferences pref = getApplicationContext().getSharedPreferences("DogPref", 0); // 0 - for private mode
            //SharedPreferences.Editor editor = pref.edit();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String chosenDog = pref.getString("dog"+pref.getInt("dogChosenNumber", ERROR_DOGS),null);
            String dogPath = "dogs"+chosenDog+"activitiesDB";
            db.collection(dogPath).add(Aktiviteetti);


        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);


        return false;
    }

    private void getProfilePicture(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("DogPref", 0); // 0 - for private mode
        String dogChosen = pref.getString("dog"+(pref.getInt("dogChosenNumber", ERROR_DOGS)),null);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        assert dogChosen != null;
        StorageReference imageRef = storage.getReference()
                .child(dogChosen).child("profilepic.webp");

        imageRef.getBytes(1024*1024)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0  ,bytes.length);
                        activitiesDogImage.setImageBitmap(bitmap);

                    }
                });
    }

    @Override
    public void onResume(){
        super.onResume();

        getProfilePicture();

    }
}
