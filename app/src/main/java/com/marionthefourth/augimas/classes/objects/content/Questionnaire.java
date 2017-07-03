package com.marionthefourth.augimas.classes.objects.content;

import android.os.Parcel;
import android.os.Parcelable;

import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class Questionnaire extends FirebaseContent {

    private String userUID = "";

    private ArrayList<String> questions = new ArrayList<>();

    public Questionnaire(Parcel in) {
        super();
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

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Questionnaire createFromParcel(Parcel in) {
            return new Questionnaire(in);
        }
        public Questionnaire[] newArray(int size) {
            return new Questionnaire[size];
        }
    };

    @Override
    public Map<String, String> toMap() {
        final HashMap<String, String> result = new HashMap<>();
        if (getUID() != null) {
            result.put(Constants.Strings.UIDs.UID, getUID());
        }
        if (!getTeamUID().equals(null)) {
            result.put(Constants.Strings.UIDs.TEAM_UID,getTeamUID());
        }


        return result;
    }
}
