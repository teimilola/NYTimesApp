package com.example.temilola.nytimes;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by temilola on 6/22/16.
 */
public class FilterDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    CheckBox cbArts;
    CheckBox cbFashion;
    CheckBox cbSports;
    Spinner sort;
    EditText etDate;
    Button btnSave;
    private SearchFilters mFilters;
    //String spinnerValue;


    public static FilterDialogFragment newInstance(SearchFilters filters) {
        FilterDialogFragment frag = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("filters", filters);
        frag.setArguments(args);
        return frag;
    }

    public FilterDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    // 1. Defines the listener interface with a method
    //    passing back filters as result.
    public interface OnFilterSearchListener {
        void onUpdateFilters(SearchFilters filters);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_menu, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        cbArts = (CheckBox) view.findViewById(R.id.cbArts);
        cbFashion= (CheckBox)view.findViewById(R.id.cbFashion);
        cbSports= (CheckBox)view.findViewById(R.id.cbSports);
        sort= (Spinner)view.findViewById(R.id.spinnerSort);
        etDate= (EditText)view.findViewById(R.id.etDate);
        btnSave= (Button)view.findViewById(R.id.btnSave);
        //Toast.makeText(getContext(),"You entered " + spinnerValue, Toast.LENGTH_SHORT).show();
        // Store the filters to a member variable
        mFilters = (SearchFilters) getArguments().getSerializable("filters");
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        mFilters.spinnerValue= sort.getSelectedItem().toString();

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Date Clicked!", Toast.LENGTH_SHORT).show();
            //showDatePickerDialog(v);
                /*DatePickerBuilder dpb = new DatePickerBuilder()
                        .setFragmentManager(getChildFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment)
                        .setTargetFragment(FilterDialogFragment.this);
                dpb.show();*/
                showDatePickerDialog(v);
            }
        });

        CompoundButton.OnCheckedChangeListener checkListen = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mFilters.news_desk= new ArrayList<>();
                    mFilters.news_desk.add("\"" + buttonView.getText().toString() + "\"");
                }
            }
        };

        cbArts.setOnCheckedChangeListener(checkListen);
        cbFashion.setOnCheckedChangeListener(checkListen);
        cbSports.setOnCheckedChangeListener(checkListen);

        // 2. Attach a callback when the button is pressed
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the mFilters based on the input views
                // ...
                // Return filters back to activity through the implemented listener
                OnFilterSearchListener listener = (OnFilterSearchListener) getActivity();
                listener.onUpdateFilters(mFilters);
                // Close the dialog to return back to the parent activity
                dismiss();

            }
        });

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.setTargetFragment(this,300);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        etDate.setText( String.valueOf(monthOfYear) + "/" + String.valueOf(dayOfMonth)+ "/"+ String.valueOf(year) );
        String month;
        if(monthOfYear < 10){
            month = "0" + String.valueOf(monthOfYear) ;
        }
        else {
            month= String.valueOf(monthOfYear);
        }
        mFilters.begin_date= String.valueOf(year)+String.valueOf(dayOfMonth)+month;
    }


}
