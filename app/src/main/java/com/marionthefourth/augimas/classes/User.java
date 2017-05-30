package com.marionthefourth.augimas.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.marionthefourth.augimas.classes.User.MemberRole.DEFAULT;

public final class User extends FirebaseObject {
    private String email    = "";
    private String fullname = "";
    private String username = "";
    private String teamUID  = "";
    private MemberRole memberRole = DEFAULT;
    @Exclude
    private String password = "";

    public enum MemberRole {
        OWNER,ADMIN, EDITOR, CHATTER, VIEWER,DEFAULT;
    }

    public static String getMemberRoleString(MemberRole role) {
        switch (role) {
            case OWNER:
                return "Owner";
            case ADMIN:
                return "Admin";
            case EDITOR:
                return "Editor";
            case CHATTER:
                return "Chatter";
            case VIEWER:
                return "Viewer";
            default:
                return null;
        }
    }

    public static ArrayList<String> getAllMemberRoles() {
        ArrayList<String> roles = new ArrayList<>();
        roles.add("Owner");
        roles.add("Admin");
        roles.add("Editor");
        roles.add("Chatter");
        roles.add("Viewer");


        return roles;
    }

    public User() {}

    public User(String username) {
        this();
        setUsername(username);
    }

    public User(String username, MemberRole role) {
        this(username);
        setMemberRole(role);
    }

    public User(String firstname, String lastname, MemberRole role) {
        setFullname(firstname + " " + lastname);
        setMemberRole(role);
    }

    public User(String username, String password) {
        this(username);
        setPassword(password);
    }

    public User(String username, String email, String fullname, String uid) {
        setUID(uid);
        setFullname(fullname);
        setEmail(email);
        setUsername(username);
    }

    public User(ArrayList<String> fields) {
        setFullname(fields.get(4));
        setEmail(fields.get(3));
        setPassword(fields.get(1));
        setUsername(fields.get(0));
    }

    public User(DataSnapshot userSnapshot) {
        this();
        if (userSnapshot.hasChild(Constants.Strings.USERNAME)) {
            setUsername(userSnapshot.child(Constants.Strings.USERNAME).getValue().toString());
        }
        if (userSnapshot.hasChild(Constants.Strings.EMAIL)) {
            setEmail(userSnapshot.child(Constants.Strings.EMAIL).getValue().toString());
        }
        if (userSnapshot.hasChild(Constants.Strings.FULL_NAME)) {
            setFullname(userSnapshot.child(Constants.Strings.FULL_NAME).getValue().toString());
        }
        if (userSnapshot.hasChild(Constants.Strings.UID)) {
            setUID(userSnapshot.child(Constants.Strings.UID).getValue().toString());
        }
        if (userSnapshot.hasChild(Constants.Strings.TEAM_UID)) {
            setUsername(userSnapshot.child(Constants.Strings.TEAM_UID).getValue().toString());
        }
        if (userSnapshot.hasChild(Constants.Strings.TEAM_ROLE)) {

        }
    }

    public User(Parcel in) {
        User user = (User) in.readSerializable();
        setUID(user.getUID());
        setEmail(user.getEmail());
        setPassword(user.getPassword());
        setUsername(user.getUsername());
        setTeamUID(user.getTeamUID());
    }

    public MemberRole getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(MemberRole memberRole) {
        this.memberRole = memberRole;
    }

    public String getUID() { return uid; }
    public String getEmail() { return email; }
    public String getFullname() {
        return fullname;
    }
    public String getPassword() { return password; }
    public String getUsername() { return username; }
    public String getTeamUID() { return teamUID; }
    public void setUID(String uid) { this.uid = uid; }
    public void setEmail(String email) { this.email = email; }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setTeamUID(String teamUID) { this.teamUID = teamUID; }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        User comparedFirebaseUser = (User)o;
        return comparedFirebaseUser.getEmail().equals(this.getEmail())
                &&
                comparedFirebaseUser.getUID().equals(this.getUID())
                &&
                comparedFirebaseUser.getPassword().equals(this.getPassword())
                &&
                comparedFirebaseUser.getUsername().equals(this.getUsername())
                &&
                comparedFirebaseUser.getFullname().equals(this.getFullname());

    }

    @Override
    public String getField(int index) {
        switch (index) {
            case Constants.Ints.USERNAME: return getUsername();
            case Constants.Ints.EMAIL: return getEmail();
            case Constants.Ints.PASSWORD: return getPassword();
            case Constants.Ints.FULL_NAME: return getFullname();
            case Constants.Ints.UID: return getUID();
            case Constants.Ints.TEAM_UID: return getTeamUID();
            default: return null;
        }
    }

    public boolean isInTeam(Team team) {
        for (String uid:team.getUserUIDs()) {
            if (getUID().equals(uid)) {
                return true;
            }
        }

        return false;
    }

    public boolean isInChat(Chat chat) {
        for (String uid:chat.getMembersUIDs()) {
            if (getUID().equals(uid)) {
                return true;
            }
        }

        return false;
    }

    public boolean sentMessage(Message message) {
        return message.getSenderUID().equals(getUID());
    }

    public static User getMessageSender(ArrayList<User> users, Message message) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).sentMessage(message)) {
                return users.get(i);
            }
        }

        return null;
    }

    @Exclude
    @Override
    public Map<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();
        if (getUID() != null) {
            result.put(Constants.Strings.UID, getUID());
        }
        if (getUsername() != null) {
            result.put(Constants.Strings.USERNAME, getUsername());
        }
        if (getEmail() != null) {
            result.put(Constants.Strings.EMAIL, getEmail());
        }
        if (getFullname() != null) {
            result.put(Constants.Strings.FULL_NAME, getFullname());
        }

        if (getTeamUID() != null) {
            result.put(Constants.Strings.TEAM_UID, getTeamUID());
        }
        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public static ArrayList<String> getField(ArrayList<User> users, int position) {
        ArrayList<String> field = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            field.add(users.get(i).getField(position));
        }
        return field;
    }

    public static String getTitleFromUsernames(final ArrayList<User> users) {

        if (users.size() == 1) {
            // If there is 1 User, display as [ Username(1) ]
            return users.get(0).getUsername();
        } else if (users.size() == 2) {
            // If there are 2 Users, display as [ Username(1) and Username(2) ]
            return users.get(0).getUsername() + " and " + users.get(1).getUsername();
        } else if (users.size() == 3) {
            // If there are 3 Users, display as [ Username(1), Username(2) and Username(3) ]
            return users.get(0).getUsername() + ", " + users.get(1).getUsername() + " and " + users.get(2).getUsername();
        } else if (users.size() > 3) {
            // If there are more than 3 Users, display as [ Username(1), Username(2) and X others ]
            return users.get(0).getUsername() + ", " + users.get(1).getUsername() + " and " + (users.size() - 2) + " others";
        }

        return null;
    }

    @Override
    public String description() {
        return getUsername() + " " + getPassword() + " " + getEmail();
    }
}
