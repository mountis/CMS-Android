package com.marionthefourth.augimas.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.helpers.FirebaseHelper.getCurrentUser;

public class QuestionnaireHomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_questionnaire_home,container);

        final Activity activity = getActivity();
        setupView(activity,view);

        return view;
    }

    private void setupView(final Activity activity, final View view) {

        final int[] INPUT_IDS = new int[] {
                R.id.input_age,
                R.id.input_sex,
                R.id.input_occupation
        };

        final ArrayList<TextInputEditText> inputs = new ArrayList<>();

        for (int i = 0; i < INPUT_IDS.length; i++) {
            inputs.add((TextInputEditText)view.findViewById(INPUT_IDS[i]));
        }

        final AppCompatButton beginQuestionnaireButton = (AppCompatButton)view.findViewById(R.id.button_begin_questionnaire);

        beginQuestionnaireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FirebaseHelper.getReference(activity,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (currentUser != null && !currentUser.getTeamUID().equals("")) {
                        if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.ADMIN) && currentUser.getType().equals(FirebaseEntity.EntityType.US)) {

                            final LinearLayoutCompat additionalSection = (LinearLayoutCompat)view.findViewById(R.id.questionnaire_additional_section);
                            additionalSection.setVisibility(View.VISIBLE);

                            final AppCompatButton viewQuestionnairesButton = (AppCompatButton)view.findViewById(R.id.button_view_questionnaires);

                            viewQuestionnairesButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    private void setupQuestionnaire(final Activity activity, final View view) {

    }


}
