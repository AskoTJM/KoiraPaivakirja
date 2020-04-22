package com.example.koirapaivakirja;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
    TextView activitiesNickName;
    ImageView activitiesDogImage;
    CheckBox mOut, mPlay;
    Button mSave;
    Handler handler;
    String activity, pNotes;

    private GestureDetector gdt;


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
        activitiesNickName = findViewById(R.id.activityNickName);

        final Button mSave = findViewById(R.id.activitySaveButton);

        Toolbar mainToolbar = findViewById(R.id.activityToolbar);
        setSupportActionBar(mainToolbar);

        gdt = new GestureDetector(new Activities.GestureListener());

        activitiesDogImage.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                gdt.onTouchEvent(event);
                // ... Respond to touch events
                return true;

            }
        });

        getProfilePicture();

        handler = new Handler();
        mSave.setOnClickListener(new View.OnClickListener() {
            //Antaa tallentaa lenkin tiedot vain jos toinen aktiviteettityypeistä on valittuna
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.activitySaveButton:
                        if (mOut.isChecked()) {
                            activity = "Walk";
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
            Aktiviteetti.put("uid", pref.getString("uid",null));

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String chosenDog = pref.getString("dog"+pref.getInt("dogChosenNumber", ERROR_DOGS),null);
            String dogPath = "dogs/"+chosenDog+"/activitiesDB";
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

    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("DogPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        @Override
        public void onLongPress(MotionEvent event) {

        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if (e1.getX() - e2.getX() > MIN_SWIPING_DISTANCE && Math.abs(velocityX) > THRESHOLD_VELOCITY) {
                Toast.makeText(getApplicationContext(), "You have swiped left side", Toast.LENGTH_SHORT).show();
                if (pref.getInt("dogChosenNumber",ERROR_DOGS) == 0) {

                    editor.putInt("dogChosenNumber",(pref.getInt("numberOfDogs",ERROR_DOGS) -1));
                    editor.commit();
                } else {
                    int i = pref.getInt("dogChosenNumber",ERROR_DOGS);
                    i--;
                    editor.putInt("dogChosenNumber",i);
                    editor.commit();

                }
                //refreshDogsFromPref(pref);
                getNameOfTheChosenDog();
                getProfilePicture();

                return false;
            } else if (e2.getX() - e1.getX() > MIN_SWIPING_DISTANCE && Math.abs(velocityX) > THRESHOLD_VELOCITY) {
                Toast.makeText(getApplicationContext(), "You have swiped right side", Toast.LENGTH_SHORT).show();
                if (pref.getInt("dogChosenNumber",ERROR_DOGS) == (pref.getInt("numberOfDogs",ERROR_DOGS) -1)) {
                    editor.putInt("dogChosenNumber",0);
                    editor.commit();
                } else {
                    int i = pref.getInt("dogChosenNumber",ERROR_DOGS);
                    i++;
                    editor.putInt("dogChosenNumber",i);
                    editor.commit();
                }
                getNameOfTheChosenDog();
                getProfilePicture();
                return false;
            }
            return false;
        }

    }

    private void getNameOfTheChosenDog(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("DogPref", 0); // 0 - for private mode

        String tempDogString = pref.getString("dog"+pref.getInt("dogChosenNumber", ERROR_DOGS)+"nickname",null);
        activitiesNickName.setText(tempDogString);
    }

    @Override
    public void onResume(){
        super.onResume();
        getNameOfTheChosenDog();
        getProfilePicture();

    }
}
