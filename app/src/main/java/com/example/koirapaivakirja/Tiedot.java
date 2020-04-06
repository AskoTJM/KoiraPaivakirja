package com.example.koirapaivakirja;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Tiedot extends AppCompatActivity {

    EditText mInfoName, mInfoNote, mInfoIDNumber, mInfoReg, mInfoBirth, mInfoKennelName, mInfoWeight;
    ImageView mInfoImageView;
    // Not in use anymore String infoMode = "Info"; // Modes = Info, Edit, Add
    Handler handler;
    Uri mImageUri;
    public Uri imguri;
    StorageReference mStorageRef;
    StorageReference Ref;
    private static final int PICK_IMAGE = 100;
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    // Temporary Dog switcher
    String dogChosen = "t4oHb1WKfnprJ82oa0Zj"; //"rKJvTSFsozBr0V5JAyvQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiedot);
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");
        mInfoIDNumber = findViewById(R.id.infoIDNum);
        mInfoBirth = findViewById(R.id.infoBirth);
        mInfoKennelName = findViewById(R.id.infoKennelName);
        mInfoName = findViewById(R.id.infoName);
        mInfoReg = findViewById(R.id.infoReg);
        mInfoNote = findViewById(R.id.infoNote);
        mInfoWeight = findViewById(R.id.infoWeight);

        mInfoImageView = findViewById(R.id.infoDogImage);
        mInfoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mInfoName.isEnabled())
                    openGallery();
                //Fileuploader();
            }


        });

        handler = new Handler();

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

      // mInfoImageView.setOnClickListener();
      //  infoMode = "Edit";
      //  toggleEditMode("Edit");
        getDataFromFireStore();


        toggleEditMode("Info");
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
        switch (item.getItemId()) {
            case (R.id.infoToolbarAdd):
                toggleEditMode("Add");
                dogChosen = "newDog";
                Toast.makeText(this, "Add selected", Toast.LENGTH_LONG).show();
                return true;
            case (R.id.infoToolbarSave):
               // Toast.makeText(this, "Remove selected", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Save to DB selected", Toast.LENGTH_LONG).show();
                try {
                    putDataToFireStore();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                toggleEditMode("Info");
                return true;
            case (R.id.infoToolbarEdit):
                Toast.makeText(this, "Edit selected", Toast.LENGTH_LONG).show();
                toggleEditMode("Edit");

                return true;


        }
        return false;
    }

    private void getDataFromFireStore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("dogs").document(dogChosen);
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

    private void putDataToFireStore() throws ParseException {

        Map<String, Object> dogData = new HashMap<>();
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
        SimpleDateFormat formatter = new SimpleDateFormat("dd.mm.yyyy", Locale.getDefault());
        String dateInString = mInfoBirth.getText().toString();
        Date dateTemp = formatter.parse(dateInString);
        if(dateTemp != null) {
            Timestamp pBirthDate = new Timestamp(dateTemp);
            dogData.put("birthdate", pBirthDate);
        }



        // Check if new dog or updating old one
        if(dogChosen.equals("newDog")) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("dogs").add(dogData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            Log.d("Koera", "DocumentSnapshot written with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Koera", "Error adding document", e);
                        }
                    }); /**/
        }else{
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("dogs").document(dogChosen).set(dogData)
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
// Muuttaa EditTextit joko muokattavaan tai vain luettavaan tilaan. Add tyhjentää vanhan tekstin
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
                // En tiedä voiko tehdä näin mutta kokeillaan, toimi.
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