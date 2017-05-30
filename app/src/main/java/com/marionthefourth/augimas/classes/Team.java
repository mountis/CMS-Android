package com.marionthefourth.augimas.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class Team extends FirebaseObject {

    private ArrayList<String> userUIDs = new ArrayList<>();
    private String name = "";
    private String username = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Team() {}

    public Team(String name, String username) {
        this();
        setName(name);
        setUsername(username);
    }

    public Team(User firebaseUser) {
        firebaseUser.setTeamUID(getUID());
        getUserUIDs().add(firebaseUser.getUID());
    }

    public Team(String name, String uid, ArrayList<User> users) {
        this();
        setName(name);
        setUID(uid);
        for (int i = 0; i < users.size();i++) {
            userUIDs.add(users.get(i).getUID());
            users.get(i).setTeamUID(uid);
        }
    }

    public Team(ArrayList<String> userUIDs) {
        this.userUIDs = userUIDs;
    }

    public Team(DataSnapshot jointAccountReference) {
        this();
        for (int i = 0; i < jointAccountReference.getChildrenCount()-1;i++) {
            if (jointAccountReference.hasChild(Constants.Strings.USER_UID+String.valueOf(i))) {
                userUIDs.add(jointAccountReference.child(Constants.Strings.USER_UID+String.valueOf(i)).getValue().toString());
            }
        }

        if (jointAccountReference.hasChild(Constants.Strings.UID)) {
            setUID(jointAccountReference.child(Constants.Strings.UID).getValue().toString());
        }
    }

    public boolean removeUser(User user) {
        for (int i = 0; i < getUserUIDs().size();i++) {
            if (getUserUIDs().get(i).equals(user.getUID())) {
                getUserUIDs().remove(i);
                user.setTeamUID("");
                return true;
            }
        }

        return false;
    }

    public Team(Parcel in) {
        Team team = (Team) in.readSerializable();
        setUID(team.getUID());
        setName(team.getName());
        setUsername(team.getUsername());
        setUserUIDs(team.getUserUIDs());
    }

    public ArrayList<String> getUserUIDs() { return userUIDs; }
    public void setUserUIDs(ArrayList<String> userUIDs) { this.userUIDs = userUIDs; }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        public Team[] newArray(int size) {
            return new Team[size];
        }
    };

    @Override
    public String getField(int index) {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {}

    @Override
    public Map<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();
        if (getUID() != null) {
            result.put(Constants.Strings.UID, getUID());
        }
        if (getUserUIDs() != null) {
            for (int i = 0; i < getUserUIDs().size();i++) {
                result.put(Constants.Strings.USER_UID+String.valueOf(i),getUserUIDs().get(i));
            }
        }
        return result;
    }

    @Override
    public String description() {
        return null;
    }


    public boolean hasNoUsers() {
        return getUserUIDs().size() == 0;
    }

    public boolean addUser(User accountToAdd) {
        for (int i = 0; i < userUIDs.size(); i++) {
            if (userUIDs.get(i).equals(accountToAdd.getUID())) {
                getUserUIDs().add(accountToAdd.getUID());
                accountToAdd.setTeamUID(getUID());
            }
        }

        return false;
    }
}
