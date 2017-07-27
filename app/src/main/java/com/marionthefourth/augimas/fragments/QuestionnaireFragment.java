package com.marionthefourth.augimas.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marionthefourth.augimas.R;

/**
 * Created by MGR4 on 7/21/17.
 */

public final class QuestionnaireFragment extends Fragment {

    public static QuestionnaireFragment newInstance() {

        final Bundle args = new Bundle();
        
        final QuestionnaireFragment fragment = new QuestionnaireFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_questionnaire,container);

        setupView(getActivity(),view);

        return view;
    }

    private void setupView(final Activity activity, final View view) {

    }


}
