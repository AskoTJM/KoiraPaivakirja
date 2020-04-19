package com.example.koirapaivakirja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    EditText mEmail, mPassword;
    Button mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = findViewById(R.id.loginEmailField);
        mPassword = findViewById(R.id.loginPasswordField);
        mSignInButton = findViewById(R.id.loginSignInButton);
        firebaseAuth = FirebaseAuth.getInstance();



        SharedPreferences pref = getApplicationContext().getSharedPreferences("DogPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();


        if (firebaseAuth.getCurrentUser() != null){

            editor.putString("uid",firebaseAuth.getUid());
            editor.commit();
            String uID = firebaseAuth.getUid();

            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                    .build();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.setFirestoreSettings(settings);

            startActivity(new Intent(getApplicationContext(), Frontpage.class));
            finish();
            //jos kirjautunut jo sisään, menee suoraan etusivulle
        }

            mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Sähköposti vaaditaan");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Salasana vaaditaan");
                    return;
                }
                //kirjaa valmiin käyttäjän sisään.
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  //kirjaa valmiin käyttäjän sisään
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Kirjautuminen onnistui", Toast.LENGTH_SHORT).show();
                            //jos kirjautuminen onnistui valmiilla käyttäjällä
                            startActivity(new Intent(getApplicationContext(), Frontpage.class));
                        }else{
                            Toast.makeText(MainActivity.this, "Virheellinen käyttäjä tai salasana", Toast.LENGTH_SHORT).show();
                            //jos käyttäjää ei olemassa tai väärät tunnukset
                        }
                    }
                });
            }
        });

    }
    public void goToRegister(View view) {
        Intent intentActivity = new Intent(this, RegisteringUser.class);
        startActivity(intentActivity);
    }
    public void goToMainActivity(View view) {
        Intent intentActivity = new Intent(this, MainActivity.class);
        startActivity(intentActivity);
    }
}
