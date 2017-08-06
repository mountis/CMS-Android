package com.marionthefourth.augimas.classes.objects.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.ENTITY_STATUS;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.ENTITY_TYPE;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Fields.FULL_NAME;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Fields.USERNAME;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.UIDs.UID;

public final class Team extends FirebaseEntity {
//    Team Constructors
    public Team() {}
    public Team(final Parcel in) {
        final Team team = (Team) in.readSerializable();
        setUID(team.getUID());
        setName(team.getName());
        setType(team.getType());
        setStatus(team.getStatus());
        setUsername(team.getUsername());
    }
    public Team(final DataSnapshot teamReference) {
        this();
        if (teamReference.hasChild(Constants.Strings.UIDs.UID)) {
            setUID(teamReference.child(Constants.Strings.UIDs.UID).getValue().toString());
        }

        if (teamReference.hasChild(Constants.Strings.Fields.TEAM_NAME)) {
            setName(teamReference.child(Constants.Strings.Fields.TEAM_NAME).getValue().toString());
        }

        if (teamReference.hasChild(Constants.Strings.Fields.USERNAME)) {
            setUsername(teamReference.child(Constants.Strings.Fields.USERNAME).getValue().toString());
        }

        if (teamReference.hasChild(Constants.Strings.Fields.ENTITY_TYPE)) {
            setType(EntityType.getType(teamReference.child(Constants.Strings.Fields.ENTITY_TYPE).getValue().toString()));
        }

        if (teamReference.hasChild(Constants.Strings.Fields.ENTITY_STATUS)) {
            setStatus(EntityStatus.getStatus(teamReference.child(Constants.Strings.Fields.ENTITY_STATUS).getValue().toString()));
        }
    }
    public Team(final String name, final String username) {
        this();
        setName(name);
        setUsername(username);
    }
    public Team(final User user) {
        user.setTeamUID(getUID());
    }
    public Team(final String name, final String username, EntityType teamType) {
        this(name,username);
        setType(teamType);
    }
    public Team(final String name, final String uid, final ArrayList<User> users) {
        this();
        setName(name);
        setUID(uid);
        for (int i = 0; i < users.size();i++) {
            users.get(i).setTeamUID(uid);
        }
    }
    public Team(final String name, final String username, final User user, final EntityRole memberRole, final EntityStatus memberStatus) {
        this();
        setName(name);
        setUsername(username);
        addUser(user, memberRole, memberStatus);
    }
    public static ArrayList<Team> toArrayList(DataSnapshot teamReferences) {
        final ArrayList<Team> teams = new ArrayList<>();
        for(DataSnapshot teamReference:teamReferences.getChildren()) {
            teams.add(new Team(teamReference));
        }
        return teams;
    }
    public static ArrayList<Team> toFilteredArrayList(DataSnapshot userReferences,String field,String content) {
        return Team.toFilteredArrayList(Team.toArrayList(userReferences),field,content);
    }

    public static ArrayList<Team> toFilteredArrayList(ArrayList<Team> teams,String field, String content) {
        final ArrayList<Team> filteredTeamList = new ArrayList<>();
        for (final Team teamItem:teams) {
            switch (field) {
                case Constants.Strings.UIDs.TEAM_UID:
                    if (!teamItem.getUID().equals(content)) filteredTeamList.add(teamItem);
                    break;
                case Constants.Strings.UIDs.CHAT_UID:
                    if (!teamItem.equals(content)) filteredTeamList.add(teamItem);
                    break;
                case Constants.Strings.Fields.ENTITY_ROLE:
                    if (teamItem.getRole().toString().equals(content)) filteredTeamList.add(teamItem);
                    break;
                case Constants.Strings.Fields.ENTITY_TYPE:
                    if (teamItem.getType().toString().equals(content)) filteredTeamList.add(teamItem);
                    break;
                default:
                    break;
            }
        }
        return filteredTeamList;
    }

    public static ArrayList<Team> toFilteredArrayList(ArrayList<Team> teams,String field, ArrayList<String> content) {
        final ArrayList<Team> filteredTeamList = new ArrayList<>();
        for (int i = 0; i < content.size(); i++) {
            for (final Team teamItem:teams) {
                switch (field) {
                    case Constants.Strings.UIDs.TEAM_UID:
                    case Constants.Strings.UIDs.CHAT_UID:
                        if (teamItem.getUID().equals(content.get(i))) filteredTeamList.add(teamItem);
                        break;
                    case Constants.Strings.Fields.ENTITY_ROLE:
                        if (teamItem.getRole().toString().equals(content.get(i))) filteredTeamList.add(teamItem);
                        break;
                    case Constants.Strings.Fields.ENTITY_TYPE:
                        if (teamItem.getType().toString().equals(content.get(i))) filteredTeamList.add(teamItem);
                        break;
                    default:
                        break;
                }
            }
        }

        return filteredTeamList;
    }
//    Functional Methods
    public boolean removeUser(User user) {
        if (user.getTeamUID().equals(this.getUID())) {
            user.setTeamUID("");
            user.setType(EntityType.DEFAULT);
            user.setRole(EntityRole.NONE);
            user.setStatus(EntityStatus.DEFAULT);
            return true;
        }
        return false;
    }

    public void addUser(final User accountToAdd, final EntityRole memberRole, final EntityStatus memberStatus) {
        accountToAdd.setTeamUID(getUID());
        accountToAdd.setRole(memberRole);
        accountToAdd.setStatus(memberStatus);
        accountToAdd.setType(getType());
    }
//    Other Methods
    @Override
    public String description() {
        return null;
    }
    @Override
    public Map<String, String> toMap() {
        final HashMap<String, String> result = new HashMap<>();
        if (getUID() != null) {
            result.put(Constants.Strings.UIDs.UID, getUID());
        }

        result.put(Constants.Strings.Fields.ENTITY_TYPE, String.valueOf(getType().toInt(true)));
        result.put(Constants.Strings.Fields.ENTITY_STATUS, String.valueOf(getStatus().toInt(true)));

        if (!getName().equals(null)) {
            result.put(Constants.Strings.Fields.TEAM_NAME,getName().toString());
        }

        if (!getUsername().equals(null)) {
            result.put(Constants.Strings.Fields.USERNAME,getUsername().toString());
        }

        return result;
    }
    @Override
    public String getField(final int index) {
        switch (index) {
            case UID:
                getUID();
                break;
            case USERNAME:
                return getUsername();
            case FULL_NAME:
                return getName();
            case ENTITY_TYPE:
                return getType().toString();
            case ENTITY_STATUS:
                return getStatus().toString();
            default:
                return null;

        }
        return null;
    }
    public static ArrayList<String> getField(ArrayList<Team> teams, int field) {
        ArrayList<String> content = new ArrayList<>();
        for (int i = 0; i < teams.size(); i++) {
            content.add(teams.get(i).getField(field));
        }
        return content;
    }
//    Parcel Details
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }
        public Team[] newArray(int size) {
            return new Team[size];
        }
    };
}