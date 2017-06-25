package com.anyasoft.es.surveyapp.details;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.anyasoft.es.surveyapp.R;
import com.anyasoft.es.surveyapp.logger.L;
import com.anyasoft.es.surveyapp.question.QuestionModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PersonDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PersonDetailsFragment extends Fragment implements View.OnClickListener {
    private Button btnReset, btnSubmit;
    private TextInputLayout txtInputName;
    private TextInputLayout txtInputRelativeName;
    private TextInputLayout txtInputAge;
    private TextInputLayout txtInputCast;
    private TextInputLayout txtInputCorpName;
    private TextInputLayout txtInputWardNum;
    private TextInputLayout txtInputWardName;
    private TextInputLayout txtInputReligion;
    private TextInputLayout txtInputSubcaste;
    private TextInputLayout txtInputProfession;
    private TextInputLayout txtInputPlace;
    private TextInputLayout txtInputNumOfChildren;
    private AppCompatSpinner spnGender;

    private OnFragmentInteractionListener mListener;

    public PersonDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.content_person_details, container, false);
        txtInputAge = (TextInputLayout) view.findViewById(R.id.textLayoutAge);
        txtInputCast = (TextInputLayout) view.findViewById(R.id.textLayoutCaste);
        txtInputName = (TextInputLayout) view.findViewById(R.id.textLayoutName);
        txtInputCorpName = (TextInputLayout) view.findViewById(R.id.textLayoutMunicipality);
        txtInputWardName = (TextInputLayout) view.findViewById(R.id.textLayoutWardName);
        txtInputWardNum = (TextInputLayout) view.findViewById(R.id.textLayoutWardNum);
        txtInputReligion = (TextInputLayout) view.findViewById(R.id.textLayoutReligion);
        txtInputSubcaste = (TextInputLayout) view.findViewById(R.id.textLayoutSubcaste);
        txtInputProfession = (TextInputLayout) view.findViewById(R.id.textLayoutprofession);
        txtInputPlace = (TextInputLayout) view.findViewById(R.id.textLayoutplace);
        txtInputRelativeName = (TextInputLayout) view.findViewById(R.id.textLayoutRelativeName);
        txtInputNumOfChildren = (TextInputLayout) view.findViewById(R.id.textLayoutNumOfChildren);
        spnGender = (AppCompatSpinner) view.findViewById(R.id.spnGender);
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnReset = (Button) view.findViewById(R.id.btnReset);
        btnSubmit.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onSubmit();
        }//if()...
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (v == btnReset) {
            mListener.gotoBack();
            /*txtInputName.getEditText().setText("");
            txtInputCast.getEditText().setText("");
            txtInputAge.getEditText().setText("");
            txtInputNumOfChildren.getEditText().setText("");
            txtInputWardName.getEditText().setText("");
            txtInputWardName.getEditText().toString();
            txtInputCorpName.getEditText().setText("");

            txtInputWardNum.getEditText().setText("");

            // String txtInputWardName.getEditText().getText().toString();;
            txtInputReligion.getEditText().setText("");

            txtInputSubcaste.getEditText().setText("");

            txtInputProfession.getEditText().setText("");

            txtInputPlace.getEditText().setText("");

            txtInputNumOfChildren.getEditText().setText("");

            spnGender.setSelection(0);*/

        }//
        if (v == btnSubmit) {
            sendRequest();

        }//
    }
    private void sendRequest() {
        String name = txtInputName.getEditText().getText().toString();
        String cast = txtInputCast.getEditText().getText().toString();
        String age = txtInputAge.getEditText().getText().toString();
        String wardName = txtInputWardName.getEditText().getText().toString();
        String corpName = txtInputCorpName.getEditText().getText().toString();

        String wardNum = txtInputWardNum.getEditText().getText().toString();

        // String txtInputWardName.getEditText().getText().toString();;
        String religion = txtInputReligion.getEditText().getText().toString();

        String subCaste = txtInputSubcaste.getEditText().getText().toString();

        String profession = txtInputProfession.getEditText().getText().toString();

        String place = txtInputPlace.getEditText().getText().toString();
        String relativeName = txtInputRelativeName.getEditText().getText().toString();

        String numOfChildren = txtInputNumOfChildren.getEditText().getText().toString();

        String gender = spnGender.getSelectedItem().toString();
        if (name == null || name.equals("")) {
            txtInputName.setError("Can't be left blank");
            return;
        }//
        if (cast == null || cast.equals("")) {
            txtInputCast.setError("Can't be left blank");
            return;
        }//
        if (age == null || age.equals("")) {
            txtInputCast.setError("Can't be left blank");
            return;
        }//
        //int position =  QuestionModel.questionList.size()-1;
       // QuestionModel model =  QuestionModel.questionList.get(position);
        JSONObject jsonDetails =  new JSONObject();
        L.d("WardName = " + wardName);
        try {
            jsonDetails.put("Name",name);
            jsonDetails.put("cast",cast);
            jsonDetails.put("age",age);
            jsonDetails.put("wardName",wardName+"");
            jsonDetails.put("corpName",corpName+"");
            jsonDetails.put("religion",religion+"");
            jsonDetails.put("wardNum",wardNum+"");
            jsonDetails.put("subCaste",subCaste+"");
            jsonDetails.put("profession",profession+"");
            jsonDetails.put("place",place+"");
            jsonDetails.put("numOfChildren",numOfChildren+"");
            jsonDetails.put("gender",gender+"");
            jsonDetails.put("fName",relativeName+"");

        } catch (JSONException e) {
            e.printStackTrace();
            L.e("sendRequest()::Creating json "+e.getLocalizedMessage());
        }//catch()....
        //model.setAnswer("Attached");

        QuestionModel.details = jsonDetails.toString();
        L.d("PersonDeatailActivity:: SendRequest():: answer"+ jsonDetails.toString());
        onButtonPressed();
    }//

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
        void onMarked(boolean marked);

        void gotoBack();

        void onSubmit();
    }
}
