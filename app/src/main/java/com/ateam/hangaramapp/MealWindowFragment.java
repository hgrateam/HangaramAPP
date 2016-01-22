package com.ateam.hangaramapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MealWindowFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MealWindowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MealWindowFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LUNCH = "lunch";
    private static final String ARG_DINNER = "dinner";

    boolean allergy_flag;
    TextView lunchtv;
    TextView dinnertv;
    TextView datetv;
    TextView allergy_info;

    LinearLayout lbox, dbox;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String date;

    private OnFragmentInteractionListener mListener;


        // Required empty public constructor
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MealWindowFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MealWindowFragment newInstance(String param1, String param2) {
        MealWindowFragment fragment = new MealWindowFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LUNCH, param1);
        args.putString(ARG_DINNER, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void setparam(String p1, String p2, String date){
        mParam1 = p1;
        mParam2 = p2;
        this.date = date;
    }
    public void setAllergyflag(){
        allergy_flag = true;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_LUNCH);
            mParam2 = getArguments().getString(ARG_DINNER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.mealwindow, container, false);

        datetv = (TextView) layout.findViewById(R.id.mw_dayindi);
        lunchtv = (TextView) layout.findViewById(R.id.mw_lunch);
        dinnertv = (TextView) layout.findViewById(R.id.mw_dinner);
        allergy_info = (TextView) layout.findViewById(R.id.mw_allergy_info);

        lbox = (LinearLayout) layout.findViewById(R.id.mw_lbox);
        dbox = (LinearLayout) layout.findViewById(R.id.mw_dbox);

        lunchtv.setText(mParam1);
        dinnertv.setText(mParam2);
        datetv.setText(date);



        allergy_info.setVisibility((allergy_flag)? View.VISIBLE:View.GONE);
        lbox.setVisibility((mParam1.equals("") ? View.GONE : View.VISIBLE));
        dbox.setVisibility((mParam2.equals("")? View.GONE:View.VISIBLE));

        if(mParam1.equals("") && mParam2.equals("")){

            datetv.setVisibility(View.VISIBLE);
            datetv.setText(date);
        }
        else
            datetv.setVisibility((date.equals("")? View.GONE:View.VISIBLE));


        return layout;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
/*        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
  */  }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
