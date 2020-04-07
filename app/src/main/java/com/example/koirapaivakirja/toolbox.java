package com.example.koirapaivakirja;

import android.icu.text.SimpleDateFormat;

import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class toolbox {

    public static Timestamp TimeStamp4Date(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.mm.yyyy", Locale.getDefault());
        String dateInString = date;
        Date dateTemp = formatter.parse(dateInString);
        if(dateTemp != null) {
            Timestamp timeStamp = new Timestamp(dateTemp);
            return timeStamp;
        }
    return null;
    }


}
