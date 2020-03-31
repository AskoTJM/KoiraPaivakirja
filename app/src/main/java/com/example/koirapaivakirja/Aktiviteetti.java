package com.example.koirapaivakirja;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;


public class Aktiviteetti extends AppCompatActivity {

    EditText mWorkout, mStart, mCurrent, mDuration, mDate;
    CheckBox mOut, mPlay;
    Button mSave;
    Handler handler;
    String activity, pNotes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aktiviteetti);
        mWorkout = findViewById(R.id.editText);
        mStart = findViewById(R.id.activityTimeStart);
        mCurrent = findViewById(R.id.activityElapsedTime);
        mDuration = findViewById(R.id.activityTimeStop);
        mDate = findViewById(R.id.activityDateStart);
        mOut = findViewById(R.id.checkOut);
        mPlay = findViewById(R.id.checkPlay);
        final Button mSave = (Button) findViewById(R.id.saveButton);


        handler = new Handler();
        mSave.setOnClickListener(new View.OnClickListener() {
            //Antaa tallentaa lenkin tiedot vain jos toinen aktiviteettityypeistä on valittuna
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.saveButton:
                        if (mOut.isChecked()) {
                            activity = "Outside";
                        }
                        if (mPlay.isChecked()) {
                            activity = "Play";
                        }
                        if ((mOut.isChecked() == true) && (mPlay.isChecked() == true)||(mOut.isChecked() == false) && (mPlay.isChecked() == false)) {

                            Toast.makeText(Aktiviteetti.this,
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
            Toast.makeText(Aktiviteetti.this, "Aktiviteetti lisätty", Toast.LENGTH_SHORT).show();

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

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("dogs/rKJvTSFsozBr0V5JAyvQ/activitiesDB").add(Aktiviteetti);


        }



}
