package com.example.koirapaivakirja;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

public class DogClass {
    private int dIDNumber;
    private Timestamp dBirth;
    private String dKennelName;
    private String dNickName;
    private String dReg;

    public int getdIDNumber() {
        return dIDNumber;
    }

    public Timestamp getdBirth() {
        return dBirth;
    }

    public String getdKennelName() {
        return dKennelName;
    }

    public String getdNickName() {
        return dNickName;
    }

    public String getdReg() {
        return dReg;
    }
}
/*
        mInfoIDNumber.setEnabled(false);
        mInfoBirth.setEnabled(false);
        mInfoKennelName.setEnabled(false);
        mInfoName.setEnabled(false);
        mInfoReg.setEnabled(false);

*/