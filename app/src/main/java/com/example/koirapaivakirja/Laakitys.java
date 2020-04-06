package com.example.koirapaivakirja;

import android.os.Bundle;
import android.os.ParcelFormatException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.versionedparcelable.VersionedParcel;

import java.text.ParseException;

public class Laakitys extends AppCompatActivity {

    Button mMed;
    EditText mNotes, mDate, mTime, mDog, mDose, mMedType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laakitys);
        mNotes = findViewById(R.id.medNotes);
        mDate = findViewById(R.id.medDate);
        mTime = findViewById(R.id.medTime);
        mDog = findViewById(R.id.medDog);
        mDose = findViewById(R.id.medAmount);
        mMed = findViewById(R.id.medBtn);
        mMedType = findViewById(R.id.medName);

        mMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.medBtn:
                        if (mDate.getText().toString().equals("")) {
                            Toast.makeText(Laakitys.this, "Aseta päivämäärä", Toast.LENGTH_SHORT).show();
                        } else if (mTime.getText().toString().equals("")) {
                            Toast.makeText(Laakitys.this, "Aseta ajankohta", Toast.LENGTH_SHORT).show();
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

        private void saveMedTime() throws ParseException{
            Toast.makeText(Laakitys.this,"Lääkityksen muistutus lisätty", Toast.LENGTH_SHORT).show();

            String pMedType= mMedType.getText().toString();
            String pDate= mDate.getText().toString();
            String pTime= mTime.getText().toString();
            String pDog= mDog.getText().toString();
            String pDose= mDose.getText().toString();

            mNotes.setText("Lääkkeen nimi: " + pMedType + "\nPäivämäärä: " + pDate + "\nAjankohta: " + pTime + "\nKoiran nimi: "  + pDog + "\nAnnos: " + pDose);

        }





}
