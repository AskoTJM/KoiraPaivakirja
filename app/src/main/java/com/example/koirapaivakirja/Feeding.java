package com.example.koirapaivakirja;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Feeding extends AppCompatActivity {


    EditText ruokiDate;
    EditText ruokiTime;
    EditText foodType;
    EditText foodAmount;
    EditText foodAdditional;
    ImageView feedDogImage;
    TextView feedNickName;
    private static final String FOOD_ADDITIONAL = "feedingNote";
    private static final String FOOD_TYPE = "food_type";
    private static final String FOOD_AMOUNT = "food_amount";
    private static final String FOOD_UNIT = "food_amountUnit";
    private static final String CURRENT_TIME = "current_time";
    private static final String CURRENT_DATE = "current_date";

    //String fdAdditional;
    //String fdType;
    //String fdAmount;
    String currentDate;
    String currentTime;
    //Button feedButton;
    //Button getFoodButton;

    private GestureDetector gdt;

    private static final int MIN_SWIPING_DISTANCE = 50;
    private static final int THRESHOLD_VELOCITY = 50;
    private static final int ERROR_DOGS = -2;
    private static final int NEW_DOG = -1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeding);
        Toolbar Toolbar = findViewById(R.id.feedToolbar);
        setSupportActionBar(Toolbar);



        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        currentTime = format.format(calendar.getTime());
        currentDate = DateFormat.getDateInstance().format(calendar.getTime());

        feedNickName = findViewById(R.id.feedNickName);
        foodAdditional = findViewById(R.id.feedNote);
        ruokiDate = findViewById(R.id.feedDate);
        ruokiTime = findViewById(R.id.feedTime);
        foodType = findViewById(R.id.feedFood);
        foodAmount = findViewById(R.id.feedAmount);
        //feedButton = findViewById(R.id.feedBtn);
        //getFoodButton = findViewById(R.id.getButton);
        feedDogImage = findViewById(R.id.feedDogImage);
        ruokiTime.setText(currentTime);
        ruokiDate.setText(currentDate);



        gdt = new GestureDetector(new Feeding.GestureListener());

        feedDogImage.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                gdt.onTouchEvent(event);
                // ... Respond to touch events
                return true;

            }
        });

      //  getNameOfTheChosenDog();
      //  getProfilePicture();
    }

    public void addFeeding(View v) throws ParseException {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("DogPref", 0); // 0 - for private mode
        String dogChosen = pref.getString("dog"+pref.getInt("dogChosenNumber",ERROR_DOGS),null);
        String dogPath = "dogs/"+dogChosen+"/feedingDB";

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference feedingRef = db.collection(dogPath);

        if (foodType.getText().toString().trim().equals("")) {
            Toast.makeText(Feeding.this, "Lisää ruoan nimi", Toast.LENGTH_SHORT).show();
        }
        else if (foodAmount.getText().toString().trim().equals("")) {
            Toast.makeText(Feeding.this, "Lisää ruoan määrä", Toast.LENGTH_SHORT).show();
        }
        else if (ruokiDate.getText().toString().trim().equals("")) {
            Toast.makeText(Feeding.this, "Lisää ruokinnan päivämäärä", Toast.LENGTH_SHORT).show();
        }
        else if (ruokiTime.getText().toString().trim().equals("")) {
            Toast.makeText(Feeding.this, "Lisää ruokinnan aika!", Toast.LENGTH_SHORT).show();
        }
        else {
            String current_date = ruokiDate.getText().toString();
            String current_time = ruokiTime.getText().toString();
            String food_type = foodType.getText().toString();
            String food_amount = foodAmount.getText().toString();
            String food_amountUnit = "g";
            String feedingNote = foodAdditional.getText().toString();
            String feedUid = pref.getString("uid",null);

            Map<String, Object> feedData = new HashMap<>();
            feedData.put(CURRENT_DATE, current_date);
            feedData.put(CURRENT_TIME, current_time);
            feedData.put(FOOD_AMOUNT, food_amount);
            feedData.put(FOOD_UNIT, food_amountUnit);
            feedData.put(FOOD_TYPE, food_type);
            feedData.put(FOOD_ADDITIONAL, feedingNote);
            feedData.put("uid", feedUid);
            feedData.put("timeStamp", Timestamp.now());



            feedingRef.add(feedData);
            Toast.makeText(Feeding.this, "Ruokinta onnistunut!", Toast.LENGTH_SHORT).show();
        }
    }

    public void getFeeding(){
        Intent intent = new Intent(this, OldFeedings.class);
        startActivity(intent);
    }

    // Voi hakea vain yhden ruokinnan tiedon DataBasesta, ei käytännöllinen.
   /* public void getFeeding(View v){
        db.collection("dogs/rKJvTSFsozBr0V5JAyvQ/feedingDB").document("Ruokinta").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String food_amount = documentSnapshot.getString(FOOD_AMOUNT);
                            String food_type = documentSnapshot.getString(FOOD_TYPE);
                            String food_additional = documentSnapshot.getString(FOOD_ADDITIONAL);
                            String current_time = documentSnapshot.getString(CURRENT_TIME);
                            String current_date = documentSnapshot.getString(CURRENT_DATE);

                            foodType.setText(food_type);
                            foodAmount.setText(food_amount);
                            foodAdditional.setText(food_additional);
                            ruokiTime.setText(current_time);
                            ruokiDate.setText(current_date);
                            Toast.makeText(Ruokinta.this, "Viimeisimmän ruokinnan haku onnistunut!", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    } */

    public void pvmButton(View v) throws ParseException {
        final android.icu.util.Calendar c = android.icu.util.Calendar.getInstance();
        int feedYear = c.get(android.icu.util.Calendar.YEAR);
        int feedMonth = c.get(android.icu.util.Calendar.MONTH);
        int feedDay = c.get(android.icu.util.Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int fYear,
                                          int fmonthOfYear, int fdayOfMonth) {
                        String setString = (fdayOfMonth + "." + (fmonthOfYear + 1) + "." + fYear);
                        ruokiDate.setText(setString);
                    }
                }, feedYear, feedMonth, feedDay);
        datePickerDialog.show();

    }

   public void timeButton(View v) {
        final Calendar c = Calendar.getInstance();
        int feedHour = c.get(Calendar.HOUR_OF_DAY);
        int feedMinute = c.get(Calendar.MINUTE);



        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if(hourOfDay<10 && minute>10){
                            ruokiTime.setText("0" + hourOfDay + ":" + minute);
                        }

                        else if(minute<10 && hourOfDay>10) {
                            ruokiTime.setText(hourOfDay + ":" + "0" + minute);
                        }

                        else if(minute<10 && hourOfDay<10)
                        {
                            ruokiTime.setText("0" + hourOfDay + ":" + "0" + minute);
                        }
                        else {
                            ruokiTime.setText(hourOfDay + ":" + minute);
                        }
                    }
                }, feedHour, feedMinute, true);
        timePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case (R.id.feedToolbarGetFeedings):
                Intent intentOldFeedings = new Intent(this, OldFeedings.class);
                startActivity(intentOldFeedings);
                break;
        }
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
                        feedDogImage.setImageBitmap(bitmap);

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
        feedNickName.setText(tempDogString);
    }

    @Override
    public void onResume(){
        super.onResume();
        getNameOfTheChosenDog();
        getProfilePicture();

    }
}
