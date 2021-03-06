package com.along.android.healthmanagement.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import com.along.android.healthmanagement.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by RitenVithlani on 2/21/17.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    private OnDietDateChangeListener dietDateChangeListener;
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


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String[] months = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        String text = months[month] + " " + dayOfMonth + ", " + year;

        Calendar calendar = new GregorianCalendar(year, month, dayOfMonth);

        if(null!= this.getArguments() && "startDate".equals(this.getArguments().getString("whichDate"))) {
            TextView txtView = (TextView) getActivity().findViewById(R.id.tvStartDate);
            txtView.setText(text);
            TextView txtViewMillis = (TextView) getActivity().findViewById(R.id.tvStartDateInMillis);
            txtViewMillis.setText(calendar.getTimeInMillis() + "");
        } else if(null!= this.getArguments() && "endDate".equals(this.getArguments().getString("whichDate"))) {
            TextView txtView = (TextView) getActivity().findViewById(R.id.tvEndDate);
            txtView.setText(text);
            TextView txtViewMillis = (TextView) getActivity().findViewById(R.id.tvEndDateInMillis);
            txtViewMillis.setText(calendar.getTimeInMillis() + "");
        } else if(null!= this.getArguments() && "startDateVitalSign".equals(this.getArguments().getString("whichDate"))) {
            TextView txtView = (TextView) getActivity().findViewById(R.id.tvStartDateVitalSign);
            txtView.setText(text);
            TextView txtViewMillis = (TextView) getActivity().findViewById(R.id.tvStartDateVitalSignInMillis);
            txtViewMillis.setText(calendar.getTimeInMillis() + "");
        } else if(null!= this.getArguments() && "endDateVitalSign".equals(this.getArguments().getString("whichDate"))) {
            TextView txtView = (TextView) getActivity().findViewById(R.id.tvEndDateVitalSign);
            txtView.setText(text);
            TextView txtViewMillis = (TextView) getActivity().findViewById(R.id.tvEndDateVitalSignInMillis);
            txtViewMillis.setText(calendar.getTimeInMillis() + "");
        } else if(null!= this.getArguments() && "Today".equals(this.getArguments().getString("whichDate"))) {
            TextView txtView = (TextView) getActivity().findViewById(R.id.tvStartDateDiet);
            txtView.setText(text);

            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.HOUR , 0);
            calendar.set(Calendar.MINUTE , 0);
            calendar.set(Calendar.SECOND , 0);

            TextView txtViewMillis = (TextView) getActivity().findViewById(R.id.tvStartDateInDiet);
            txtViewMillis.setText(calendar.getTimeInMillis() + "");

//            dietDateChangeListener.onDietDateChange(calendar);
            dietDateChangeListener.onDietDateChange(calendar);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            dietDateChangeListener = (OnDietDateChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnBarcodeDetectedListener");
        }
    }

    public interface OnDietDateChangeListener {
        void onDietDateChange(Calendar selectedDate);
//        void onDietDateChange(Long selectedDate);
    }
}
