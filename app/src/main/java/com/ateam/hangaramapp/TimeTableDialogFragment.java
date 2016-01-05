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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;

/**
 * Created by Suhyun on 2016-01-02.
 */
public class TimeTableDialogFragment extends DialogFragment {

    String dayName[]={"월","화","수","목","금"};
    AutoCompleteTextView subjectName;
    EditText et_memo;
    String[] subjectlist;
    public interface TimeTableDialogListener{
        void onDialogPositiveClick(ArrayList<cellInfo> cellinfos, int array_pos,  boolean isUpdate);
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
    }
    ArrayList<cellInfo> cellinfos;
    int array_pos;
    public void setSubjectList(){

        ArrayList<String> subjects = new ArrayList<String>();

        for(int i=0;i<cellinfos.size();i++){
            boolean flag = false;

            for(int j=0;j<subjects.size();j++){
                if(cellinfos.get(i).getName().equals(subjects.get(j))){
                    flag = true;
                }
            }
            if(flag == false){
                subjects.add(cellinfos.get(i).getName());
            }
        }

        subjectlist = new String[subjects.size()];
        for(int i=0;i<subjects.size();i++){
            subjectlist[i] = subjects.get(i);
        }
    }

    public void setCellInfo(ArrayList<cellInfo> cellinfos, int array_pos){
        this.cellinfos = cellinfos;
        this.array_pos = array_pos;
        setSubjectList();
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

        subjectName.setText(cellinfos.get(array_pos).getName());
        et_memo.setText(cellinfos.get(array_pos).getMemo());
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
                        Log.i("info", "확인 버튼 눌림! " + value + " | " + Memo);

                        int day = cellinfos.get(array_pos).getDay();
                        int column = cellinfos.get(array_pos).getColumn();

                        cellinfos.set(array_pos,new cellInfo(value, day, column, Memo));

/*                        cellinfos.get(array_pos).setMemo(value);
                        cellinfos.get(array_pos).setMemo(Memo);
  */


                        for(int i=0;i<cellinfos.size();i++){
                            Log.i("info", i+" | cellinfos log : "+cellinfos.get(i).getName());
                        }

                        mListener.onDialogPositiveClick(cellinfos, array_pos, (value != "" || Memo !="")?true:false);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TimeTableDialogFragment.this.getDialog().cancel();
                    }
                })
                .setTitle(dayName[cellinfos.get(array_pos).getDay() - 1] + "요일 " + cellinfos.get(array_pos).getColumn() + "교시 과목 설정")
        ;
//        return super.onCreateDialog(savedInstanceState);
        return builder.create();
    }
    @Override
    public void onStart() {
        super.onStart();
    }
}
