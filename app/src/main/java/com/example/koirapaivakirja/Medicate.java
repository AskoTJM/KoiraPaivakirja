package com.example.koirapaivakirja;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class Medicate extends AppCompatActivity {

    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;
    Button mMed;
    EditText mNotes, mDate, mTime, mDog, mDose, mMedType, mUnit;
    ImageView mDogImage;
    TextView mNickName;

    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    public Calendar c = Calendar.getInstance();

    private GestureDetector gdt;

    private static final int MIN_SWIPING_DISTANCE = 50;
    private static final int THRESHOLD_VELOCITY = 50;
    private static final int ERROR_DOGS = -2;
    private static final int NEW_DOG = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicate);
        Toolbar mainToolbar = findViewById(R.id.medicateToolbar);
        setSupportActionBar(mainToolbar);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("DogPref", 0); // 0 - for private mode


        mNotes = findViewById(R.id.medNotes);
        mDate = findViewById(R.id.medDate);
        mTime = findViewById(R.id.medTime);
        mDog = findViewById(R.id.medDog);
        mDose = findViewById(R.id.medAmount);
        mMed = findViewById(R.id.medBtn);
        mMedType = findViewById(R.id.medName);
        mUnit = findViewById(R.id.medUnit);
        mDogImage = findViewById(R.id.medDogImage);
        mNickName = findViewById(R.id.medNickName);
        // Automatic
        // Set current time and date because that looks nice
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        mTime.setText(format.format(calendar.getTime()));
        mDate.setText(DateFormat.getDateInstance().format(calendar.getTime()));

   //     String previousMed = pref.getString("dog"+pref.getInt("dogChosenNumber",ERROR_DOGS)+"_previousMedication","");
   //     mMedType.setText(previousMed);
   //     int previousDose = pref.getInt("dog"+pref.getInt("dogChosenNumber",ERROR_DOGS)+"_previousMedicationDose",0);
   //     mDose.setRawInputType(previousDose);

        //getNameOfTheChosenDog();
        //getProfilePicture();


        gdt = new GestureDetector(new Medicate.GestureListener());

        mDogImage.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                gdt.onTouchEvent(event);
                // ... Respond to touch events
                return true;

            }
        });



        //hakee tämänhetkisen ajan
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        //Tallentaa ajatuksen lääkitykselle, jos kaikki kentät täytettynä
        mMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.medBtn:
                        if (mDate.getText().toString().equals("")) {
                            Toast.makeText(Medicate.this, "Aseta päivämäärä", Toast.LENGTH_SHORT).show();
                        } else if (mTime.getText().toString().equals("")) {
                            Toast.makeText(Medicate.this, "Aseta ajankohta", Toast.LENGTH_SHORT).show();
                        } else if (mUnit.getText().toString().equals("")) {
                            Toast.makeText(Medicate.this, "Aseta annoksen yksikkö", Toast.LENGTH_SHORT).show();
                        } else if (mMedType.getText().toString().equals("")) {
                            Toast.makeText(Medicate.this, "Aseta lääkkeen nimi", Toast.LENGTH_SHORT).show();
                        } else if (mDog.getText().toString().equals("")) {
                            Toast.makeText(Medicate.this, "Valitse koiran nimi", Toast.LENGTH_SHORT).show();
                        } else if (mDose.getText().toString().equals("")) {
                            Toast.makeText(Medicate.this, "Valitse annos", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                saveMedTime();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }


                }
            }
        });
    }

        public void saveMedTime() throws ParseException{
            Toast.makeText(Medicate.this,"Lääkityksen muistutus lisätty", Toast.LENGTH_SHORT).show();
            // Saving the given medication to SharedPreferences for next use.
            SharedPreferences pref = getApplicationContext().getSharedPreferences("DogPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("dog"+pref.getInt("dogChosenNumber",ERROR_DOGS)+"_previousMedication",mMedType.getText().toString());
            editor.putInt("dog"+pref.getInt("dogChosenNumber",ERROR_DOGS)+"_previousMedicationDose", Integer.parseInt(mDose.getText().toString()));
            editor.putString("dog"+pref.getInt("dogChosenNumber",ERROR_DOGS)+"_previousMedicationUnit",mUnit.getText().toString());
            editor.apply();

            //kenttiin asetetut arvot
            String pMedType= mMedType.getText().toString();
            String pDate= mDate.getText().toString();
            String pTime= mTime.getText().toString();
            String pDog= mDog.getText().toString();
            String pDose= mDose.getText().toString();
            String pUnit = mUnit.getText().toString();
            String pNotes = mNotes.getText().toString();

            //ilmoitukseen tuleva data. content perusilmoitukselle ja content1 laajennetulle ilmoitukselle
            scheduleNotification(getNotification( pDog+" lääkitys klo "+pTime,
                                                  "Koira: "  + pDog + "\nAika: " + pTime + " " + pDate + "\nlääke: " + pMedType + " "+ pDose + pUnit + " \n" + pNotes), 1);

            //dataa tekstikenttään testailua varten
            //mNotes.setText("Lääkkeen nimi: " + pMedType + "\nPäivämäärä: " + pDate + "\nAjankohta: " + pTime + "\nKoiran nimi: "  + pDog + "\nAnnos: " + pDose + pUnit);
        }


    public void showDatePickerDialog(View v) throws ParseException {

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String setString = (dayOfMonth + "." + (monthOfYear + 1) + "." + year);
                        mDate.setText(setString);
                        c.set(Calendar.YEAR,year);
                        c.set(Calendar.MONTH,monthOfYear);
                        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    public void timeButton(View v) {

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        if(hourOfDay<10 && minute>10){
                            mTime.setText("0"+hourOfDay + ":" + minute);
                        }

                        else if(minute<10 && hourOfDay>10){
                            mTime.setText(hourOfDay + ":" + "0"+ minute);
                        }

                        else if(minute<10 && hourOfDay<10){
                            mTime.setText("0"+hourOfDay + ":" + "0"+ minute);
                        }

                        else {
                            mTime.setText(hourOfDay + ":" + minute);
                        }


                        //String setString = (hourOfDay + ":" + minute);
                        //mTime.setText(setString);

                        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        c.set(Calendar.MINUTE,minute);



                    }
                },  mHour,mMinute,true);
        timePickerDialog.show();
    }


    public void scheduleNotification (Notification notification, int delay) {

        //logiikka ilmoituksen ajastukselle. delaylla ei käyttöä tässä versiossa.
        Intent notificationIntent = new Intent( this, AlertReceiver.class ) ;
        notificationIntent.putExtra(AlertReceiver. NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(AlertReceiver. NOTIFICATION , notification) ;

        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        long futureInMillis = SystemClock. elapsedRealtime () + delay;

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;

        //c.getTimeInMillis=kalenteriin/kelloon asetettu uusi aika
        alarmManager.setExact(AlarmManager. RTC_WAKEUP , c.getTimeInMillis(), pendingIntent);
        android.util.Log.i("Aika", " aika "+c.getTimeInMillis());
    }


    private Notification getNotification (String content, String content1) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        builder.setContentTitle( "Koiran lääkitys" ) ;
        builder.setContentText(content) ;
        builder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        builder.setAutoCancel( true ) ;
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setChannelId( NOTIFICATION_CHANNEL_ID );
        //notifikaation laajennettu tila:
        builder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(content1));
        return builder.build() ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_medicate, menu);
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
                        mDogImage.setImageBitmap(bitmap);

                    }
                });
    }

    private void getNameOfTheChosenDog(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("DogPref", 0); // 0 - for private mode

        String tempDogString = pref.getString("dog"+pref.getInt("dogChosenNumber", ERROR_DOGS)+"nickname",null);
        mDog.setText(tempDogString);
        mNickName.setText(tempDogString);

        String previousMed = pref.getString("dog"+pref.getInt("dogChosenNumber",ERROR_DOGS)+"_previousMedication","");
        mMedType.setText(previousMed);
        int previousDose = pref.getInt("dog"+pref.getInt("dogChosenNumber",ERROR_DOGS)+"_previousMedicationDose",0);
        mDose.setText(Integer.toString(previousDose));
        String previousMedicationUnit = pref.getString("dog"+pref.getInt("dogChosenNumber",ERROR_DOGS)+"_previousMedicationUnit","");
        mUnit.setText(previousMedicationUnit);
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

    @Override
    public void onResume(){
        super.onResume();
        getNameOfTheChosenDog();
        getProfilePicture();

    }
}
