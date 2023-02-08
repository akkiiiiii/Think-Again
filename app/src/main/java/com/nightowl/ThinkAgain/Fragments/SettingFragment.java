package com.nightowl.ThinkAgain.Fragments;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nightowl.ThinkAgain.Database.SharedPref;
import com.nightowl.ThinkAgain.R;


public class SettingFragment extends BottomSheetDialogFragment {

    private Spinner reminderSpinner;
    private AppCompatSpinner timerAppCompatSpinner;

    private SharedPref pref;

    private final int THIRTY_SEC = 30000;
    private final int ONE_MIN = 60000;
    private final int ONE_MIN_THIRTY_SEC = 90000;
    private final int TWO_MIN = 120000;
    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_setting, container, false);


        String[] Reminder = {"Timer","Click Counter"};
        String[] Timer = {"30 Sec","1 Min","1:30 Min","2 Min"};


        pref = new SharedPref(requireContext(),requireActivity());

        reminderSpinner = view.findViewById(R.id.reminderSpinner);
        timerAppCompatSpinner = (AppCompatSpinner) view.findViewById(R.id.timer_appCompatSpinner);


        ArrayAdapter ad
                = new ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Reminder);

        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);

        reminderSpinner.setAdapter(ad);


        ArrayAdapter ad2
                = new ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Timer);

        ad2.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);


        timerAppCompatSpinner.setAdapter(ad2);

        if (pref.GET_Time() == THIRTY_SEC){
            timerAppCompatSpinner.setSelection(0);
        }else if (pref.GET_Time() == ONE_MIN){
            timerAppCompatSpinner.setSelection(1);
        }else if (pref.GET_Time() == ONE_MIN_THIRTY_SEC){
            timerAppCompatSpinner.setSelection(2);
        }else if (pref.GET_Time() == TWO_MIN){
            timerAppCompatSpinner.setSelection(3);
        }

        if (pref.GET_Reminder() == 0){
            reminderSpinner.setSelection(0);
        } else if (pref.GET_Reminder() == 1) {
            reminderSpinner.setSelection(1);
        }

        SelectTime();

        return view;
    }

    private void SelectTime(){
        timerAppCompatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position){

                    case 0:
                        pref.AddTime(THIRTY_SEC);
                        break;

                    case 1:
                        pref.AddTime(ONE_MIN);
                        break;

                    case 2:
                        pref.AddTime(ONE_MIN_THIRTY_SEC);
                        break;

                    case 3:
                        pref.AddTime(TWO_MIN);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        reminderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position){

                    case 0:
                        pref.AddReminder(0);
                        break;

                    case 1:
                        pref.AddReminder(1);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}