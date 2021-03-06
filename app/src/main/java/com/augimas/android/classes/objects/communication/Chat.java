package com.augimas.android.classes.objects.communication;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.augimas.android.classes.constants.Constants;
import com.augimas.android.classes.objects.FirebaseCommunication;
import com.augimas.android.classes.objects.entities.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class Chat extends FirebaseCommunication {
    @Exclude
    private ArrayList<String> teamUIDs = new ArrayList<>();
//    Chat Constructors
    public Chat() {}
    public Chat(final Parcel in) {
        final Chat chat = (Chat)in.readSerializable();
        setUID(chat.getUID());
        setTeamUIDs(chat.getTeamUIDs());
    }
    public Chat(final Team team) {
        this();
        getTeamUIDs().add(team.getUID());
    }
    public Chat(final ArrayList<Team> teams) {
        this();
        setTeamUIDs(Team.getField(teams, Constants.Ints.UIDs.UID));
    }
    public Chat(final DataSnapshot chatReference) {
        int count = 1;

        if (chatReference.hasChild(Constants.Strings.UIDs.UID)) {
            setUID(chatReference.child(Constants.Strings.UIDs.UID).getValue().toString());
        }

        for (int i = 0; i < chatReference.getChildrenCount()-count;i++) {
            if (chatReference.hasChild(Constants.Strings.UIDs.TEAM_UIDS+ String.valueOf(i))) {
                getTeamUIDs().add(chatReference.child(Constants.Strings.UIDs.TEAM_UIDS+ String.valueOf(i)).getValue().toString());
            }
        }

        if (chatReference.hasChild(Constants.Strings.Fields.COMMUNICATION_TYPE)) {
            setType(CommunicationType.getType(chatReference.child(Constants.Strings.Fields.COMMUNICATION_TYPE).getValue().toString()));
        }

    }
    public Chat(final Team team, CommunicationType type) {
        this(team);
        setType(type);
    }
    public Chat(Team teamOne, Team teamTwo, CommunicationType type) {
        this(teamOne,type);
        getTeamUIDs().add(teamTwo.getUID());
    }
    public static ArrayList<Chat> toArrayList(DataSnapshot chatReferences) {
        final ArrayList<Chat> chats = new ArrayList<>();
        for(DataSnapshot chatReference:chatReferences.getChildren()) {
            chats.add(new Chat(chatReference));
        }
        return chats;
    }
//    Other Methods
    @Override
    public String getField(final int index) {
        return null;
    }
    @Override
    public String description() {
        return null;
    }
    @Override
    public Map<String, String> toMap() {
        final HashMap<String, String> result = new HashMap<>();

        if (getTeamUIDs() != null) {
            for (int i = 0; i < getTeamUIDs().size();i++) {
                result.put(Constants.Strings.UIDs.TEAM_UIDS+ String.valueOf(i),getTeamUIDs().get(i));
            }
        }

        if (!getUID().equals(null)) {
            result.put(Constants.Strings.UIDs.UID, getUID());
        }

        return result;
    }
//    Functional Methods
    public boolean hasTeam(String teamUID) {
        for (int i = 0; i < teamUIDs.size();i++) {
            if (teamUID.equals(teamUIDs.get(i))) {
                return true;
            }
        }

        return false;
    }
//    Class Getters & Setters
    public ArrayList<String> getTeamUIDs() {
        return teamUIDs;
    }
    public void setTeamUIDs(final ArrayList<String> teamUIDs) { this.teamUIDs = teamUIDs; }
//    Parcel Details
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };
}