package com.example.koirapaivakirja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Aktiviteetti extends AppCompatActivity {

    EditText mWorkout, mStart, mCurrent, mDuration, mDate;
    CheckBox mOut, mPlay;
    ToggleButton mButton;
    Button mSave;
    Handler handler;
    String loppu="";
    int count;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    int Seconds, Minutes, Hours ;
    



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
        mSave = findViewById(R.id.saveButton);
        handler = new Handler();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
        final String paivaMaara = sdf.format(new Date());
        final String aika = sdf1.format(new Date());
        mDate.setText(paivaMaara);
        mStart.setText(aika);



        mButton = findViewById(R.id.activityBtn);
        mButton.setChecked(false);
        mButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton mButton, boolean isChecked) {
                if(!mOut.isChecked()&&!mPlay.isChecked()){
                    Toast.makeText(Aktiviteetti.this, "Valitse liikunnan tyyppi", Toast.LENGTH_SHORT).show();
                    mButton.setChecked(false);
                }
                else if(isChecked) {
                    StartTime = SystemClock.uptimeMillis();
                    SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");
                    String uusiAika = sdf3.format(new Date());

                    mStart.setText(uusiAika);
                    handler.postDelayed(runnable, 0);
                } else {
                    TimeBuff +=0;
                    handler.removeCallbacks(runnable);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
                    final String loppu = sdf2.format(new Date());
                    mDuration.setText(loppu);
                    mWorkout.setText("aloitus: " + aika + "\nLoppu: " + loppu +"\nPVM: " + paivaMaara + "\nKesto: " + Hours + ":" + String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds));

                }
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            private static final String TAG="";

            @Override
            public void onClick(View v) {
                Toast.makeText(Aktiviteetti.this, "toimii", Toast.LENGTH_SHORT).show();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference activitiesDB = db.collection("dogs/rKJvTSFsozBr0V5JAyvQ/activitiesDB");
                count++;
                Map<String, Object> Aktiviteetti = new HashMap<>();
                Aktiviteetti.put("Aloitus", aika);
                Aktiviteetti.put("Kesto",Hours + ":" + String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds));
                Aktiviteetti.put("Lopetus",loppu);
                Aktiviteetti.put("Päivämäärä", paivaMaara);

                activitiesDB.document("lenkki" + count).set(Aktiviteetti);
                



            }
        });
    }

    public Runnable runnable = new Runnable() {

        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            Hours = Minutes / 60;

            mCurrent.setText("" + Hours + ":"
                    + String.format("%02d", Minutes) + ":"
                    + String.format("%02d", Seconds));

            handler.postDelayed(this, 0);
        }
    };


}
