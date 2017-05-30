package com.marionthefourth.augimas.classes;

import android.os.Parcel;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class Chat extends FirebaseContent {
    private String nickname = "";
    private ArrayList<String> membersUIDs = new ArrayList<>();

    public Chat() {}

    public ArrayList<String> getMembersUIDs() {
        return membersUIDs;
    }

    public void setMembersUIDs(ArrayList<String> membersUIDs) {
        this.membersUIDs = membersUIDs;
    }

    public Chat(ArrayList<User> members) {
        setMembersUIDs(User.getField(members,Constants.Ints.UID));
    }

    public Chat(String nickname, ArrayList<User> members) {
        this(members);
        setNickname(nickname);
    }

    public Chat(String nickname) {
        this();
        setNickname(nickname);
    }

    public Chat(Parcel in) {
        Chat chat = (Chat)in.readSerializable();
        setUID(chat.getUID());
        setNickname(chat.getNickname());
        setMembersUIDs(chat.getMembersUIDs());
    }

    public Chat(DataSnapshot chatReference) {

        int count = 1;
        if (chatReference.hasChild(Constants.Strings.NICKNAME)) {
            setNickname(chatReference.child(Constants.Strings.NICKNAME).getValue().toString());
            count++;
        }

        if (chatReference.hasChild(Constants.Strings.UID)) {
            setUID(chatReference.child(Constants.Strings.UID).getValue().toString());
        }

        for (int i = 0; i < chatReference.getChildrenCount()-count;i++) {
            if (chatReference.hasChild(Constants.Strings.MEMBER_UIDS+String.valueOf(i))) {
                getMembersUIDs().add(chatReference.child(Constants.Strings.MEMBER_UIDS+String.valueOf(i)).getValue().toString());
            }
        }

    }

    @Override
    public String getField(int index) {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public Map<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();

        if (getMembersUIDs() != null) {
            for (int i = 0; i < getMembersUIDs().size();i++) {
                result.put(Constants.Strings.MEMBER_UIDS+String.valueOf(i),getMembersUIDs().get(i));
            }
        }

        if (!getNickname().equals("")) {
            result.put(Constants.Strings.NICKNAME, getNickname());
        }

        if (!getUID().equals("")) {
            result.put(Constants.Strings.UID, getUID());
        }

        return result;
    }

    public boolean addMember(User member) {
        for (int i = 0; i < getMembersUIDs().size(); i++) {
            if (getMembersUIDs().get(i).equals(member.getUID())) {
                return false;
            }
        }
        getMembersUIDs().add(member.getUID());
        return true;
    }

    public boolean removeMember(User member) {
        for (int i = 0; i < getMembersUIDs().size(); i++) {
            if (getMembersUIDs().get(i).equals(member.getUID())) {
                getMembersUIDs().remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) { dest.writeSerializable(this);}

    public static final Creator CREATOR = new Creator() {
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
