package com.example.koirapaivakirja;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class Etusivu extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etusivu);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("DogPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        toolbox.getDogsToPref(pref);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);

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
    public void goToMed(View view) {
        Intent intentLaakitys = new Intent(this, Laakitys.class);
        startActivity(intentLaakitys);
    }
    public void goToDoctor(View view){
        Intent intentDoctor = new Intent(this,LaakarinTiedot.class);
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

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();

                return true;

        }
        return false;
    }
}
