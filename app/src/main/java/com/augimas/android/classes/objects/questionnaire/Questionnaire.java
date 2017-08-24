package com.augimas.android.classes.objects.questionnaire;

import android.os.Parcel;
import android.os.Parcelable;

import com.augimas.android.classes.constants.Constants;
import com.augimas.android.classes.objects.FirebaseContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 7/21/17.
 */

public final class Questionnaire extends FirebaseContent {

    ArrayList<ArrayList<String>> pageData = new ArrayList<>();

    public Questionnaire(final Parcel in) {
        final Questionnaire user = (Questionnaire) in.readSerializable();
        setUID(user.getUID());
        setTeamUID(user.getTeamUID());
    }

    @Override
    public String getField(int index) {
        return null;
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public Map<String, String> toMap() {
        final HashMap<String,String> result = new HashMap<>();

        for (int i = 0; i < pageData.size(); i++) {
            for (int j = 0; j < pageData.get(i).size(); j++) {
                result.put(Constants.Strings.Fields.CONTENTS,pageData.get(i).get(j));
            }
        }

        return null;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Questionnaire createFromParcel(Parcel in) {
            return new Questionnaire(in);
        }
        public Questionnaire[] newArray(int size) {
            return new Questionnaire[size];
        }
    };
}
