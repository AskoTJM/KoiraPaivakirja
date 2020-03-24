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
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    EditText mEmail, mPassword;
    Button mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = findViewById(R.id.sahkoposti);
        mPassword = findViewById(R.id.salasana);
        mRegisterButton = findViewById(R.id.registerButton);
        firebaseAuth = FirebaseAuth.getInstance();

            if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),Etusivu.class));
            finish();
            //jos kirjautunut jo sisään, menee suoraan etusivulle
        }

            mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password required");
                    return;
                }
                //rekisteröi käyttäjän firebaseen
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "user created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),Etusivu.class));
                        }else{
                            Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
    }
}
