package com.example.koirapaivakirja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class Etusivu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etusivu);
    }

    public void goToFeed(View view) {
        Intent intentFeed = new Intent(this, Ruokinta.class);
        startActivity(intentFeed);
    }

    public void goToActivity(View view) {
        Intent intentActivity = new Intent(this, Aktiviteetti.class);
        startActivity(intentActivity);
    }

    public void goToInfo(View view) {

    Intent intentInfo = new Intent(this, Tiedot.class);
        startActivity(intentInfo);
    }
    public void goToReport(View view){

    }

    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        //kirjaa nykyisen käyttäjän ulos
        startActivity(new Intent (getApplicationContext(), MainActivity.class));
        finish();
    }
}
