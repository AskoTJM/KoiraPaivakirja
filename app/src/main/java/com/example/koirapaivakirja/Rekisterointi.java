package com.example.koirapaivakirja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class Rekisterointi extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText mEmail, mPassword;
    Button mRegisterButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekisterointi);

        mEmail = findViewById(R.id.sahkoposti);
        mPassword = findViewById(R.id.salasana);
        mRegisterButton = findViewById(R.id.registerButton);
        firebaseAuth = FirebaseAuth.getInstance();

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
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
                //rekisteröi käyttäjän firebaseen
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Rekisterointi.this, "Rekisteröinti onnistui", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),Etusivu.class));
                        }else{
                            Toast.makeText(Rekisterointi.this, "Virhe luodessa uutta käyttäjää", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
    }

    public void goToMainActivity(View view) {
        Intent intentActivity = new Intent(this, MainActivity.class);
        startActivity(intentActivity);
    }
}
