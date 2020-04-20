package com.example.koirapaivakirja;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Frontpage extends AppCompatActivity {

    private static final int MIN_SWIPING_DISTANCE = 50;
    private static final int THRESHOLD_VELOCITY = 50;
    private static final int ERROR_DOGS = -2;
    private static final int NEW_DOG = -1;
    ImageView mainDogImage; //= findViewById(R.id.mainDogImage);
    TextView mainNickName;

    private GestureDetector gdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frontpage);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("DogPref", 0); // 0 - for private mode
        //SharedPreferences.Editor editor = pref.edit();
        toolbox.getDogsToPref(pref);
        toolbox.getDogDataToPref(pref);
        mainDogImage = findViewById(R.id.mainDogImage);
        mainNickName = findViewById(R.id.frontPageNickName);
        getNameOfTheChosenDog();
        Toolbar mainToolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);

        gdt = new GestureDetector(new Frontpage.GestureListener());

        mainDogImage.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                gdt.onTouchEvent(event);
                // ... Respond to touch events
                return true;

            }
        });

        getProfilePicture();
        /*
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
                        mainDogImage.setImageBitmap(bitmap);

                    }
                });
        */


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
                        mainDogImage.setImageBitmap(bitmap);

                    }
                });
    }

    public void goToFeed(View view) {
        Intent intentFeed = new Intent(this, Feeding.class);
        startActivity(intentFeed);
    }

    public void goToActivity(View view) {
        Intent intentActivity = new Intent(this, Activities.class);
        startActivity(intentActivity);
    }

    public void goToInfo(View view) {

    Intent intentInfo = new Intent(this, Info.class);
        startActivity(intentInfo);
    }
    public void goToMed(View view) {
        Intent intentLaakitys = new Intent(this, Medicate.class);
        startActivity(intentLaakitys);
    }
    public void goToDoctor(View view){
        Intent intentDoctor = new Intent(this, DoctorInformation.class);
        startActivity(intentDoctor);
    }

    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        //kirjaa nykyisen käyttäjän ulos
        startActivity(new Intent (getApplicationContext(), MainActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case (R.id.mainToolbarLogout):
                FirebaseAuth.getInstance().signOut();
                //kirjaa nykyisen käyttäjän ulos
                SharedPreferences pref = getApplicationContext().getSharedPreferences("DogPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();

                return true;

        }
        return false;
    }

    @Override
    public void onResume(){
        super.onResume();
        getNameOfTheChosenDog();
        getProfilePicture();

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
        mainNickName.setText(tempDogString);
    }
}
