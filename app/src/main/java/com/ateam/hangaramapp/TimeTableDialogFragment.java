package com.ateam.hangaramapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.sql.Time;
import java.util.ArrayList;

/**
 * Created by Suhyun on 2016-01-02.
 */
public class TimeTableDialogFragment extends DialogFragment {

    String dayName[]={"월","화","수","목","금"};
    AutoCompleteTextView subjectName;
    EditText et_memo;
    String name, memo;
    String[] subjectlist;
    int day, column;
    public interface TimeTableDialogListener{
        //        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogPositiveClick(String value, String memo);
    }
    TimeTableDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (TimeTableDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
    public TimeTableDialogFragment() {
        super();
        day = column = 1;
    }
    public void setDate(int day, int column){
        this.day = day;
        this.column = column;
    }
    public void setSubjectList(ArrayList<String> a){
        subjectlist = new String[a.size()];
        for(int i=0;i<a.size();i++){
            subjectlist[i] = a.get(i);
        }
    }

    public void setCellInfo(String name, String memo){
        this.name = name;
        this.memo = memo;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.timetable_dialog, null);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, subjectlist);
        subjectName = (AutoCompleteTextView) view.findViewById(R.id.tv_tt_name);
        et_memo = (EditText) view.findViewById(R.id.tv_tt_memo);

        subjectName.setText(name);
        et_memo.setText(memo);
        subjectName.setThreshold(0);
        subjectName.setAdapter(adapter);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        String value = subjectName.getText().toString();
                        String Memo = et_memo.getText().toString();
                        mListener.onDialogPositiveClick(value, Memo);

                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TimeTableDialogFragment.this.getDialog().cancel();
                    }
                })
                .setTitle(dayName[day-1]+"요일 "+column+"교시 과목 설정")
        ;
//        return super.onCreateDialog(savedInstanceState);
        return builder.create();
    }
    @Override
    public void onStart() {
        super.onStart();
    }
}
