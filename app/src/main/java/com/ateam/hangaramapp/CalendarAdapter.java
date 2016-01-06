package com.ateam.hangaramapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
//import android.widget.TextView;

class Calendar_cellInfo{
    int type;
    // type = 1; mHeader
    // type = 2; Cell

    int month, year,day;
    int cnt;

    String name;

    Calendar_cellInfo(int year, int month, int cnt){
        this.year = year;
        this.month = month;
        this.cnt = cnt;
        type = 1;
        Log.i("info","짜잔 헤더!"+year+month);
    }
    Calendar_cellInfo(String name, int date){
        this.name = name;
        this.year = date/10000;
        this.month = (date%10000)/100;
        this.day = date%100;
        type = 2;
        Log.i("info","짜잔! "+name+""+year+""+month+""+day+""+type+""+getDate());
    }
    public String getName(){return name;}
    public int getDate(){return year*10000+month*100+day;}
    public int getType(){return type;}
    public int getCnt(){return cnt;}
}

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    private ArrayList<Calendar_cellInfo> callist;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mheader_mindi;
        public TextView mheader_cntindi;

        public TextView scell_dayindi;
        public TextView scell_textview;
        public ViewHolder(View v) {
            super(v);
            mheader_mindi = (TextView) v.findViewById(R.id.mheader_mindi);
            mheader_cntindi = (TextView) v.findViewById(R.id.mheader_cntindi);
            scell_dayindi = (TextView) v.findViewById(R.id.scell_dayindi);
            scell_textview = (TextView) v.findViewById(R.id.scell_textview);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CalendarAdapter(ArrayList<Calendar_cellInfo> callist) {
        this.callist= callist;
    }

    @Override
    public int getItemViewType(int position) {
        return callist.get(position).getType();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        ViewHolder vh;

        if(viewType == 1) { // mheader
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.calendar_mheader, parent, false);
            vh = new ViewHolder(v);
            // set the view's size, margins, paddings and layout parameters
        }
        else { // mheader
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.calendar_scell, parent, false);
            vh = new ViewHolder(v);
            // set the view's size, margins, paddings and layout parameters
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Log.i("info", "position : " + position+ callist.get(position).getName());
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(callist.get(position).getType()==1){
            int date = callist.get(position).getDate();
            int year = date/10000;
            int month = (date%10000)/100;
            int cnt = callist.get(position).getCnt();
            holder.mheader_mindi.setText(year+"년 "+month+"월");
            holder.mheader_cntindi.setText(cnt+"개의 일정이 있습니다.");
        }
        else{
            Log.i("info",callist.get(position).getDate()%100+"일");
            holder.scell_dayindi.setText(callist.get(position).getDate()%100+"일");
            holder.scell_textview.setText(callist.get(position).getName());
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return callist.size();
    }
}