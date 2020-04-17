package com.example.koirapaivakirja;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;


public class Tiedot extends AppCompatActivity {

    EditText mInfoName, mInfoNote, mInfoIDNumber, mInfoReg, mInfoBirth, mInfoKennelName, mInfoWeight;
    ImageView mInfoImageView;
    Handler handler;
    Uri mImageUri;
    public Uri imguri;
    StorageReference mStorageRef;
    StorageReference Ref;
    private static final int PICK_IMAGE = 100;
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    Calendar cal;

    private GestureDetector gdt;
    private static final int MIN_SWIPING_DISTANCE = 50;
    private static final int THRESHOLD_VELOCITY = 50;
    private static final int ERROR_DOGS = -2;
    private static final int NEW_DOG = -1;
    // Temporary Dog switcher
    String dogChosen; // = "t4oHb1WKfnprJ82oa0Zj"; //"rKJvTSFsozBr0V5JAyvQ";
    ArrayList<String> dogDB = new ArrayList<>();
    int dogNumberInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiedot);

        Toolbar toolbar = findViewById(R.id.infoToolbar);
        setSupportActionBar(toolbar);
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");
        mInfoIDNumber = findViewById(R.id.infoIDNum);
        mInfoBirth = findViewById(R.id.infoBirth);
        mInfoKennelName = findViewById(R.id.infoKennelName);
        mInfoName = findViewById(R.id.infoName);
        mInfoReg = findViewById(R.id.infoReg);
        mInfoNote = findViewById(R.id.infoNote);
        mInfoWeight = findViewById(R.id.infoWeight);

        mInfoImageView = findViewById(R.id.infoDogImage);
        /*mInfoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if were in edit mode
                if(mInfoName.isEnabled())
                    openGallery();
                //Fileuploader();
            }
        });*/

        SharedPreferences pref = getApplicationContext().getSharedPreferences("DogPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        dogNumberInt = pref.getInt("dogChosenNumber", ERROR_DOGS);
        dogChosen = pref.getString("dog"+dogNumberInt,null);

        //String testToast = Integer.toString(pref.getInt("numberOfDogs", ERROR_DOGS ));
        //Toast.makeText(getApplicationContext(), dogGone, Toast.LENGTH_SHORT).show();


        int numberOfDogs = pref.getInt("numberOfDogs",ERROR_DOGS);
        if(numberOfDogs > 0) {

            int i = 0;
            while(i < numberOfDogs) {
                String dogNum = "dog"+i;
                dogDB.add(i,pref.getString(dogNum,null));
                i++;
            }
        }else{
            Toast.makeText(getApplicationContext(), "You have no dogs. :'(", Toast.LENGTH_SHORT).show();
        }


        gdt = new GestureDetector(new GestureListener());
        mInfoImageView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                gdt.onTouchEvent(event);
                // ... Respond to touch events
                return true;

            }
        });
        handler = new Handler();
        // Find the toolbar view inside the activity layout



        fetchDogDataFromFireStore(pref.getString("dog"+pref.getInt("dogChosenNumber",ERROR_DOGS),null));

        // Probably Useless but at least we can be sure were in info mode.
        toggleEditMode("Info");
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("DogPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        @Override
        public void onLongPress(MotionEvent event) {
            if(mInfoName.isEnabled())
                openGallery();
            Log.d("KOERA", "onLongPress: " + event.toString());
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if (!mInfoName.isEnabled()) {
                if (e1.getX() - e2.getX() > MIN_SWIPING_DISTANCE && Math.abs(velocityX) > THRESHOLD_VELOCITY) {
                    Toast.makeText(getApplicationContext(), "You have swiped left side", Toast.LENGTH_SHORT).show();
                    if (pref.getInt("dogChosenNumber",ERROR_DOGS) == 0) {

                        editor.putInt("dogChosenNumber",(pref.getInt("numberOfDogs",ERROR_DOGS) -1));
                        editor.commit();
                    } else {
                        int i = pref.getInt("dogChosenNumber",-2);
                        i--;
                        editor.putInt("dogChosenNumber",i);
                        editor.commit();

                    }
                    dogChosen = dogDB.get(pref.getInt("dogChosenNumber",ERROR_DOGS));
                    fetchDogDataFromFireStore(pref.getString("dog"+pref.getInt("dogChosenNumber",ERROR_DOGS),null));

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
                    dogChosen = dogDB.get(pref.getInt("dogChosenNumber",ERROR_DOGS));
                    fetchDogDataFromFireStore(pref.getString("dog"+pref.getInt("dogChosenNumber",ERROR_DOGS),null));
                    /* Code that you want to do on swiping right side*/
                    return false;
                }
                return false;
            }return false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("DogPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        switch (item.getItemId()) {
            case (R.id.infoToolbarAdd):
                toggleEditMode("Add");
                editor.putInt("dogChosenNumber",NEW_DOG);
                editor.commit();
                //dogChosen = "newDog";
                Toast.makeText(this, "Add selected", Toast.LENGTH_LONG).show();
                return true;
            case (R.id.infoToolbarSave):
                Toast.makeText(this, "Save to DB selected", Toast.LENGTH_LONG).show();
                try {
                    throwDogDataToFireStore(pref);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // Switch back to info mode
                toggleEditMode("Info");
                return true;
            case (R.id.infoToolbarEdit):
                Toast.makeText(this, "Edit selected", Toast.LENGTH_LONG).show();
                toggleEditMode("Edit");
                return true;


        }
        return false;
    }

    private void fetchDogDataFromFireStore(String dogString){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("dogs").document(dogString);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map dokumentti;
                        dokumentti = document.getData();
                        if(dokumentti != null) {
                            mInfoName.setText((CharSequence) dokumentti.get("nickname"));
                            mInfoKennelName.setText((CharSequence) dokumentti.get("kennelname"));
                            mInfoReg.setText((CharSequence) dokumentti.get("regnumber"));
                            mInfoIDNumber.setText((CharSequence) dokumentti.get("microChipID").toString());

                            Date bDate = document.getDate("birthdate");
                            if (bDate != null) {
                                String sbDate = sdf.format(bDate);
                                mInfoBirth.setText(sbDate);
                            }
                        }
                        // Log.d("Koera", "DocumentSnapshot data: " + document.toString()); // document.getData());
                    } else {
                        Log.d("Koera", "No such document");
                    }
                } else {
                   // Log.d(this, "get failed with ", task.getException());
                }
            }
        });

    }

    private void throwDogDataToFireStore(final SharedPreferences dogPref) throws ParseException {

        String dogChosen2 = dogPref.getString("dog"+dogPref.getInt("dogChosenNumber",ERROR_DOGS), null);

        final Map<String, Object> dogData = new HashMap<>();
            String pNickName = String.valueOf(mInfoName.getText());
        if(!pNickName.isEmpty())
            dogData.put("nickname", pNickName);
            String pKennelname = String.valueOf(mInfoKennelName.getText());
        if(!pKennelname.isEmpty())
            dogData.put("kennelname", pKennelname);
            String pRegNum = String.valueOf(mInfoReg.getText());
        if(!pRegNum.isEmpty())
            dogData.put("regnumber", pRegNum);
            String temp = mInfoIDNumber.getText().toString();
        if(!temp.isEmpty()){
            long pMicroChipID = Long.parseLong(temp);
            dogData.put("microChipID", pMicroChipID);
        }

        /*
        SimpleDateFormat formatter = new SimpleDateFormat("dd.mm.yyyy", Locale.getDefault());
        String dateInString = mInfoBirth.getText().toString();
        Date dateTemp = formatter.parse(dateInString);
        if(dateTemp != null) {
            Timestamp pBirthDate = new Timestamp(dateTemp);
            dogData.put("birthdate", pBirthDate);
        }
        */
        String dateInString = mInfoBirth.getText().toString();
        dogData.put("birthdate", toolbox.TimeStamp4Date(dateInString));

        // Check if new dog or updating old one

        if(dogPref.getInt("dogChosenNumber",ERROR_DOGS) == NEW_DOG) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("dogs").add(dogData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //SharedPreferences pref = getApplicationContext().getSharedPreferences("DogPref", 0); // 0 - for private mode
                            //SharedPreferences.Editor editor = dogPref.edit();
                            Log.d("KOERA", "DocumentSnapshot written with ID: " + documentReference.getId());
                            String documentReferenceID = documentReference.getId();
                            /* Need to add Dog to current owners dogs. */
                            String uidString = dogPref.getString("uid",null);
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("userID").document(uidString).collection("dogs").document(documentReferenceID).set(dogData);
                                    //.addOnSuccessListener( (OnSuccessListener) (aVoid) {
                                    //    @Override
                                    //    public void onSuccess(DocumentReference documentReference) {
                                    //        Log.w("KOERA", "No error adding document");
                                    //    }
                                    //})
                                    //.addOnFailureListener(new OnFailureListener() {
                                    //    @Override
                                    //    public void onFailure(@NonNull Exception e) {
                                    //        Log.w("KOERA", "Error adding document", e);
                                    //    }
                                    //});
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("KOERA", "Error adding document", e);
                        }
                    });


        }else{
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            assert dogChosen2 != null;
            db.collection("dogs").document(dogChosen2).set(dogData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("KOERA", "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("KOERA", "Error writing document", e);
                        }
                    });
        }


    };
// Turns EditTexts into different modes, Info/Edit/Add
    private void toggleEditMode(String infoMode2){
        switch(infoMode2){
            case "Info":
                mInfoIDNumber.setEnabled(false);
                mInfoBirth.setEnabled(false);
                mInfoKennelName.setEnabled(false);
                mInfoName.setEnabled(false);
                mInfoReg.setEnabled(false);
                mInfoNote.setEnabled(false);
                mInfoWeight.setEnabled(false);
                break;
            case "Edit":
                mInfoIDNumber.setEnabled(true);
                mInfoBirth.setEnabled(true);
                mInfoKennelName.setEnabled(true);
                mInfoName.setEnabled(true);
                mInfoReg.setEnabled(true);
                mInfoNote.setEnabled(true);
                mInfoWeight.setEnabled(true);
                break;
            case "Add":
                toggleEditMode("Edit");
                mInfoIDNumber.setText("");
                mInfoBirth.setText("");
                mInfoKennelName.setText("");
                mInfoName.setText("");
                mInfoReg.setText("");
                mInfoNote.setText("");
                mInfoWeight.setText("");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + infoMode2);
        }
    }
/*
    public void showTimePickerDialog(View v) {
        s
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        txtTime.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }
*/
    public void showDatePickerDialog(View v) throws ParseException {
        //DialogFragment newFragment = new toolbox.DatePickerFragment().;
        //newFragment.show(getSupportFragmentManager(), "datePicker");
        final Calendar c = Calendar.getInstance();
        //SimpleDateFormat sdfD = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        //String parseDate = mInfoBirth.getText().toString();
        //cal.setTime(sdfD.parse(parseDate));// all done
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mInfoBirth.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
     //   mInfoBirth.setText((CharSequence) cal);

    }




    //alla olevilla funktioilla haetaan kuva galleriasta ja asetetaan se imageviewiin.
    private void openGallery(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }
    private String getExtension(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
    private void Fileuploader() {
        StorageReference Ref = mStorageRef.child(System.currentTimeMillis() + "." + getExtension(imguri));
        Ref.putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(Tiedot.this, "Image Uploaded succesfully", Toast.LENGTH_LONG);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    private void Filedownloader() throws IOException {
        File localFile = File.createTempFile("images", "jpg");
        Ref.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imguri=data.getData();
            mInfoImageView.setImageURI(imguri);
        }
    }

}