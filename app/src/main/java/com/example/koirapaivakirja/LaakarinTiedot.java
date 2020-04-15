package com.example.koirapaivakirja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LaakarinTiedot extends AppCompatActivity {

    Button button;
    Button button2;
    EditText editText;
    EditText editText3;
    EditText editText4;
    EditText editText5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laakarin_tiedot);
        button = findViewById(R.id.Tallenna);
        button2 = findViewById(R.id.Peruuta);
        editText = findViewById(R.id.Nimi);
        editText3 = findViewById(R.id.Sposti);
        editText4 = findViewById(R.id.Puhnro);
        editText5 = findViewById(R.id.Address);
    }
    public void goBack(View view){
        Intent intent = new Intent(this, Etusivu.class);
        startActivity(intent);

    }
    public void tallennaTiedot(View view){
        Intent intent = new Intent(this, Etusivu.class);
        Context context = getApplicationContext();
        CharSequence text = "Tiedot tallennettu";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        startActivity(intent);
    }
}
