package com.example.koirapaivakirja;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.ViewDebug;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class toolbox {
    String timeString;

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

    public static Timestamp TimeStamp4Time(String dateInString,String timeInString) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.mm.yyyy", Locale.getDefault());
        //String dateInString = date;
        SimpleDateFormat formatter2 = new SimpleDateFormat("hh:mm:ssSS");
        //Date dateTemp = formatter.parse(dateInString);
        Calendar calendar = Calendar.getInstance();

        //if(dateTemp != null) {
        //    Timestamp timeStamp = new Timestamp(getTimeInMillis(day,month,year,hour,minute));
        //    return timeStamp;
        //}
        return null;
    }

    public static long getTimeInMillis(int day, int month, int year,int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        return calendar.getTimeInMillis();
    }
/*
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
        }
    }
*/
    /*
     public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new toolbox.TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
    to layout file
    android:onClick="showTimePickerDialog"
     */

    /*
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        String timeString;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            timeString = Integer.toString(day) +"."+Integer.toString(month)+"."+Integer.toString(year);

            // Do something with the date chosen by the user
        }

    }

    public final String getTimeString()
    {
        return timeString;
    }

     */
/*
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new toolbox.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    To layout file
    android:onClick="showDatePickerDialog"

     */

    /*
    <EditText
        android:id="@+id/infoBirth"
        android:layout_width="110dp"
        android:layout_height="55dp"
        android:layout_marginStart="16dp"
        android:enabled="false"
        android:fontFamily="@font/share"
        android:gravity="center_horizontal|center_vertical"
        android:inputType="date"
        android:textSize="18sp"
        app:layout_constraintStart_toEndOf="@+id/infoName"
        app:layout_constraintTop_toTopOf="@+id/infoName"
        android:onClick="showDatePickerDialog"
        />

     */
}
