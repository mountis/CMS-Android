package com.marionthefourth.augimas.classes.objects.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.ArrayMap;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.communication.Chat;
import com.marionthefourth.augimas.classes.objects.communication.Message;
import com.marionthefourth.augimas.classes.objects.content.RecentActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class User extends FirebaseEntity {
    private String email    = "";
    private String teamUID  = "";
    @Exclude
    private String password = "";
//    User Constructors
    public User() {}
    public User(final Parcel in) {
        final User user = (User) in.readSerializable();
        setUID(user.getUID());
        setEmail(user.getEmail());
        setTeamUID(user.getTeamUID());
        setPassword(user.getPassword());
        setUsername(user.getUsername());
        setRole(user.getRole());
        setStatus(user.getStatus());
    }
    public User(final String username) {
        this();
        setUsername(username);
    }
    public User(final ArrayList<String> fields) {
        setEmail(fields.get(3));
        setPassword(fields.get(1));
        setUsername(fields.get(0));
        setName(fields.get(4));
    }
    public User(final DataSnapshot userSnapshot) {
        this();
        if (userSnapshot.hasChild(Constants.Strings.Fields.USERNAME)) {
            setUsername(userSnapshot.child(Constants.Strings.Fields.USERNAME).getValue().toString());
        }
        if (userSnapshot.hasChild(Constants.Strings.Fields.EMAIL)) {
            setEmail(userSnapshot.child(Constants.Strings.Fields.EMAIL).getValue().toString());
        }
        if (userSnapshot.hasChild(Constants.Strings.Fields.FULL_NAME)) {
            setName(userSnapshot.child(Constants.Strings.Fields.FULL_NAME).getValue().toString());
        }
        if (userSnapshot.hasChild(Constants.Strings.UIDs.UID)) {
            setUID(userSnapshot.child(Constants.Strings.UIDs.UID).getValue().toString());
        }
        if (userSnapshot.hasChild(Constants.Strings.UIDs.TEAM_UID)) {
            setTeamUID(userSnapshot.child(Constants.Strings.UIDs.TEAM_UID).getValue().toString());
        }
        if (userSnapshot.hasChild(Constants.Strings.Fields.ENTITY_TYPE)) {
            setType(EntityType.getType(userSnapshot.child(Constants.Strings.Fields.ENTITY_TYPE).getValue().toString()));
        }
        if (userSnapshot.hasChild(Constants.Strings.Fields.ENTITY_ROLE)) {
            setRole(EntityRole.getRole(userSnapshot.child(Constants.Strings.Fields.ENTITY_ROLE).getValue().toString()));
        }
        if (userSnapshot.hasChild(Constants.Strings.Fields.ENTITY_STATUS)) {
            setStatus(EntityStatus.getStatus(userSnapshot.child(Constants.Strings.Fields.ENTITY_STATUS).getValue().toString()));
        }
    }
    public User(final String username, final FirebaseEntity.EntityRole role) {
        this(username);
        setRole(role);
    }
    public User(final String firstname, final String lastname, final FirebaseEntity.EntityRole role) {
        setName(firstname + " " + lastname);
        setRole(role);
    }
    public User(final String username, final String password) {
        this(username);
        setPassword(password);
    }
    public User(final String username, final String email, final String fullname, final String uid) {
        setUID(uid);
        setName(fullname);
        setEmail(email);
        setUsername(username);
    }
    public boolean hasSeen(final RecentActivity recentActivity) {
        for(String seenUID:recentActivity.getSeenUIDs()) {
            if (seenUID.equals(getUID())) return true;
        }

        return false;
    }
    public static ArrayList<User> toArrayList(DataSnapshot userReferences) {
        final ArrayList<User> users = new ArrayList<>();
        for(DataSnapshot userReference:userReferences.getChildren()) {
            users.add(new User(userReference));
        }
        return users;
    }
    public static ArrayList<User> toFilteredArrayList(DataSnapshot userReferences,String field,String content) {
        return User.toFilteredArrayList(User.toArrayList(userReferences),field,content);
    }

    public static ArrayList<User> toFilteredArrayList(ArrayList<User> users,String field, String content) {
        final ArrayList<User> filteredUserList = new ArrayList<>();
        for (final User userItem:users) {
            switch (field) {
                case Constants.Strings.UIDs.TEAM_UID:
                    if (userItem.getTeamUID().equals(content)) filteredUserList.add(userItem);
                    break;
                case Constants.Strings.Fields.ENTITY_ROLE:
                    if (userItem.getRole().toString().equals(content)) filteredUserList.add(userItem);
                    break;
                default:
                    break;
            }
        }
        return filteredUserList;
    }

    public static ArrayMap<EntityRole,ArrayList<User>> toRoleFilteredArrayMap(final ArrayList<User> users) {
        final ArrayList<User> clonedUsers = users;
        final ArrayMap<EntityRole,ArrayList<User>> userArrayMap = new ArrayMap<>();
        for(final EntityRole role:EntityRole.getAllRoles()) {
            userArrayMap.put(role,new ArrayList<User>());
            for(final User user:clonedUsers) {
                if (user.getRole().equals(role)) {
                    userArrayMap.get(role).add(user);
//                    clonedUsers.remove(user);
                }
            }
        }
        return userArrayMap;
    }

    public static ArrayMap<EntityType,ArrayList<User>> toTypeFilteredArrayMap(final ArrayList<User> users) {
        final ArrayList<User> clonedUsers = users;
        final ArrayMap<EntityType,ArrayList<User>> userArrayMap = new ArrayMap<>();
        for(final EntityType type:EntityType.getAllTypes()) {
            userArrayMap.put(type,new ArrayList<User>());
            for(final User user:users) {
                if (user.getType().equals(type)) {
                    userArrayMap.get(type).add(user);
                    clonedUsers.remove(user);
                }
            }
        }
        return userArrayMap;
    }

    public static ArrayMap<EntityStatus,ArrayList<User>> toStatusFilteredArrayMap(final ArrayList<User> users) {
        final ArrayList<User> clonedUsers = users;
        final ArrayMap<EntityStatus,ArrayList<User>> userArrayMap = new ArrayMap<>();
        for(final EntityStatus type:EntityStatus.getAllStatii()) {
            userArrayMap.put(type,new ArrayList<User>());
            for(final User user:users) {
                if (user.getStatus().equals(type)) {
                    userArrayMap.get(type).add(user);
                    clonedUsers.remove(user);
                }
            }
        }
        return userArrayMap;
    }
//    Functional Methods
    @Override
    public Map<String, String> toMap() {
    final HashMap<String, String> result = new HashMap<>();
    if (getUID() != null) {
        result.put(Constants.Strings.UIDs.UID, getUID());
    }
    if (getUsername() != null) {
        result.put(Constants.Strings.Fields.USERNAME, getUsername());
    }
    if (getEmail() != null) {
        result.put(Constants.Strings.Fields.EMAIL, getEmail());
    }
    if (getName() != null) {
        result.put(Constants.Strings.Fields.FULL_NAME, getName());
    }
    if (getTeamUID() != null) {
        result.put(Constants.Strings.UIDs.TEAM_UID, getTeamUID());
    }
    result.put(Constants.Strings.Fields.ENTITY_ROLE, String.valueOf(getRole().toInt(true)));
    result.put(Constants.Strings.Fields.ENTITY_STATUS, String.valueOf(getStatus().toInt(true)));
    result.put(Constants.Strings.Fields.ENTITY_TYPE, String.valueOf(getType().toInt(true)));

    return result;
}
    public boolean isInChat(Chat chat) {
    for (int i = 0; i < chat.getTeamUIDs().size(); i++) {
        if (chat.getTeamUIDs().get(i).equals(getTeamUID())) {
            return true;
        }
    }

    return false;
}
    public boolean isInTeam(Team team) {
        return getTeamUID().equals(team.getUID());
    }
    public boolean usernameOrEmailMatches(final String usernameOrEmail) {
        return (getUsername().equals(usernameOrEmail) || getEmail().equals(usernameOrEmail));
    }
    public static User getMessageSender(ArrayList<User> users, Message message) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).sentMessage(message)) {
                return users.get(i);
            }
        }

        return null;
    }
    public boolean sentMessage(Message message) {
        return message.getSenderUID().equals(getUID());
    }
//    Parcel Details
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }
        public User[] newArray(int size) {
            return new User[size];
        }
    };
//    Class Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(final String email) { this.email = email; }
    public String getTeamUID() { return teamUID; }
    public void setTeamUID(final String teamUID) { this.teamUID = teamUID; }
    public String getPassword() { return password; }
    public void setPassword(final String password) {
        this.password = password;
    }
//    Other Methods
    public boolean equals(final Object o) {
        final User comparedUser = (User)o;
        return comparedUser.getEmail().equals(this.getEmail())
                &&
                comparedUser.getUID().equals(this.getUID())
                &&
                comparedUser.getPassword().equals(this.getPassword())
                &&
                comparedUser.getUsername().equals(this.getUsername())
                &&
                comparedUser.getName().equals(this.getName())
                &&
                comparedUser.getTeamUID().equals(this.getTeamUID())
                &&
                comparedUser.getRole().equals(this.getRole());
    }
    @Override
    public String getField(final int index) {
        switch (index) {
            case Constants.Ints.Fields.USERNAME: return getUsername();
            case Constants.Ints.Fields.EMAIL: return getEmail();
            case Constants.Ints.Fields.PASSWORD: return getPassword();
            case Constants.Ints.Fields.FULL_NAME: return getName();
            case Constants.Ints.UIDs.UID: return getUID();
            case Constants.Ints.UIDs.TEAM_UID: return getTeamUID();
            case Constants.Ints.ENTITY_ROLE: return getRole().toString();
            case Constants.Ints.ENTITY_STATUS: return getStatus().toString();
            case Constants.Ints.ENTITY_TYPE: return getType().toString();
            default: return null;
        }
    }
    public static ArrayList<String> getField(ArrayList<User> users, int position) {
        ArrayList<String> field = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            field.add(users.get(i).getField(position));
        }
        return field;
    }
    @Override
    public String description() {
        return getUsername() + " " + getEmail();
    }
}