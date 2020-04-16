package com.example.koirapaivakirja;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import java.text.ParseException;


public class Laakitys extends AppCompatActivity {

    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;
    Button mMed;
    EditText mNotes, mDate, mTime, mDog, mDose, mMedType, mUnit;

    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    public Calendar c = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laakitys);
        Toolbar mainToolbar = findViewById(R.id.medicateToolbar);
        setSupportActionBar(mainToolbar);


        mNotes = findViewById(R.id.medNotes);
        mDate = findViewById(R.id.medDate);
        mTime = findViewById(R.id.medTime);
        mDog = findViewById(R.id.medDog);
        mDose = findViewById(R.id.medAmount);
        mMed = findViewById(R.id.medBtn);
        mMedType = findViewById(R.id.medName);
        mUnit = findViewById(R.id.medUnit);


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
                            Toast.makeText(Laakitys.this, "Aseta päivämäärä", Toast.LENGTH_SHORT).show();
                        } else if (mTime.getText().toString().equals("")) {
                            Toast.makeText(Laakitys.this, "Aseta ajankohta", Toast.LENGTH_SHORT).show();
                        } else if (mUnit.getText().toString().equals("")) {
                            Toast.makeText(Laakitys.this, "Aseta annoksen yksikkö", Toast.LENGTH_SHORT).show();
                        } else if (mMedType.getText().toString().equals("")) {
                            Toast.makeText(Laakitys.this, "Aseta lääkkeen nimi", Toast.LENGTH_SHORT).show();
                        } else if (mDog.getText().toString().equals("")) {
                            Toast.makeText(Laakitys.this, "Valitse koiran nimi", Toast.LENGTH_SHORT).show();
                        } else if (mDose.getText().toString().equals("")) {
                            Toast.makeText(Laakitys.this, "Valitse annos", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(Laakitys.this,"Lääkityksen muistutus lisätty", Toast.LENGTH_SHORT).show();

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
                        mDate.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year);
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


                        mTime.setText(hourOfDay + ":" + minute);

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
}
