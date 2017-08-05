package com.marionthefourth.augimas.classes.objects.notifications;

import android.os.Bundle;
import android.os.Parcel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseContent;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.FirebaseObject;
import com.marionthefourth.augimas.classes.objects.communication.Chat;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.DEFAULT_ID;
import static com.marionthefourth.augimas.classes.objects.notifications.Notification.NotificationObjectType.BRANDING_ELEMENT;

public final class Notification extends FirebaseContent {
    @Exclude
    private FirebaseObject subject;
    @Exclude
    private FirebaseObject object;
    private String objectUID;
    private String subjectUID;
    private String receiverUID;
    private ArrayList<String> receiverUIDs = new ArrayList<>();
    private NotificationVerbType verbType = NotificationVerbType.DEFAULT;
    private NotificationObjectType objectType = NotificationObjectType.DEFAULT;
    private NotificationSubjectType subjectType = NotificationSubjectType.DEFAULT;
//    Class Enums
    public enum NotificationSubjectType {
        MEMBER, TEAM, DEFAULT;

        @Override
        public String toString() {
            return super.toString();
        }

        public String toMapStyleString() {
            return String.valueOf(this.toInt(true));
        }

        public int toInt(boolean mapStyle) {
            if (mapStyle) {
                switch (this) {
                    case TEAM:      return Constants.Ints.NotificationTypes.Subjects.IDs.MEMBER;
                    case MEMBER:    return Constants.Ints.NotificationTypes.Subjects.IDs.TEAM;
                    default:        return DEFAULT_ID;
                }
            } else {
                switch (this) {
                    case TEAM:      return Constants.Ints.NotificationTypes.Subjects.Indices.TEAM;
                    case MEMBER:    return Constants.Ints.NotificationTypes.Subjects.Indices.MEMBER;
                    default:        return DEFAULT_ID;
                }
            }
        }

        public static NotificationSubjectType getType(int type) {
            switch (type) {
                case Constants.Ints.NotificationTypes.Subjects.IDs.MEMBER:
                case Constants.Ints.NotificationTypes.Subjects.Indices.MEMBER:
                    return MEMBER;
                case Constants.Ints.NotificationTypes.Subjects.IDs.TEAM:
                case Constants.Ints.NotificationTypes.Subjects.Indices.TEAM:
                    return TEAM;
                default: return DEFAULT;
            }
        }

        public static NotificationSubjectType getType(String type) {
            for (int i = 0; i < getNumberOfNotificationSubjectTypes(); i++) {
                if (type.equals(getType(i).toString()) || type.equals(getType(i).toMapStyleString())) {
                    return getType(i);
                }
            }

            return DEFAULT;
        }

        public static ArrayList<NotificationSubjectType> getAllNotificationSubjectTypes() {
            final ArrayList<NotificationSubjectType> notificationSubjectTypes = new ArrayList<>();
            for (int i = 0 ; i < getNumberOfNotificationSubjectTypes(); i++) {
                notificationSubjectTypes.add(getType(i));
            }

            return notificationSubjectTypes;
        }

        public static int getNumberOfNotificationSubjectTypes() {
            return 3;
        }

    }
    public enum NotificationVerbType {
        ADD, APPROVE, AWAIT, CREATE ,DISAPPROVE, INVITE, JOIN, LEFT, RECEIVE,
        REQUEST, REQUEST_ACCESS, REQUEST_APPROVAL, REQUEST_JOIN, UPDATE, DEFAULT, BLOCK;

        @Override
        public String toString() {
            return super.toString();
        }

        public String toMapStyleString() {
            return String.valueOf(this.toInt(true));
        }

        public int toInt(boolean mapStyle) {
            if (mapStyle) {
                switch(this) {

                    case ADD:               return Constants.Ints.NotificationTypes.Verbs.IDs.ADD;
                    case APPROVE:           return Constants.Ints.NotificationTypes.Verbs.IDs.APPROVE;
                    case BLOCK:           return Constants.Ints.NotificationTypes.Verbs.IDs.BLOCK;
                    case AWAIT:             return Constants.Ints.NotificationTypes.Verbs.IDs.AWAIT;
                    case CREATE:            return Constants.Ints.NotificationTypes.Verbs.IDs.CREATE;
                    case DISAPPROVE:        return Constants.Ints.NotificationTypes.Verbs.IDs.DISAPPROVE;
                    case INVITE:            return Constants.Ints.NotificationTypes.Verbs.IDs.INVITE;
                    case JOIN:              return Constants.Ints.NotificationTypes.Verbs.IDs.JOIN;
                    case LEFT:              return Constants.Ints.NotificationTypes.Verbs.IDs.LEFT;
                    case RECEIVE:           return Constants.Ints.NotificationTypes.Verbs.IDs.RECEIVE;
                    case REQUEST:           return Constants.Ints.NotificationTypes.Verbs.IDs.REQUEST;
                    case REQUEST_ACCESS:    return Constants.Ints.NotificationTypes.Verbs.IDs.REQUEST_ACCESS;
                    case REQUEST_APPROVAL:  return Constants.Ints.NotificationTypes.Verbs.IDs.REQUEST_APPROVAL;
                    case REQUEST_JOIN:      return Constants.Ints.NotificationTypes.Verbs.IDs.REQUEST_JOIN;
                    case UPDATE:            return Constants.Ints.NotificationTypes.Verbs.IDs.UPDATE;
                    case DEFAULT:
                    default:                return DEFAULT_ID;
                }
            } else {
                switch(this) {
                    case ADD:               return Constants.Ints.NotificationTypes.Verbs.Indices.ADD;
                    case APPROVE:           return Constants.Ints.NotificationTypes.Verbs.Indices.APPROVE;
                    case BLOCK:           return Constants.Ints.NotificationTypes.Verbs.Indices.BLOCK;
                    case AWAIT:             return Constants.Ints.NotificationTypes.Verbs.Indices.AWAIT;
                    case CREATE:            return Constants.Ints.NotificationTypes.Verbs.Indices.CREATE;
                    case DISAPPROVE:        return Constants.Ints.NotificationTypes.Verbs.Indices.DISAPPROVE;
                    case INVITE:            return Constants.Ints.NotificationTypes.Verbs.Indices.INVITE;
                    case JOIN:              return Constants.Ints.NotificationTypes.Verbs.Indices.JOIN;
                    case LEFT:              return Constants.Ints.NotificationTypes.Verbs.Indices.LEFT;
                    case RECEIVE:           return Constants.Ints.NotificationTypes.Verbs.Indices.RECEIVE;
                    case REQUEST:           return Constants.Ints.NotificationTypes.Verbs.Indices.REQUEST;
                    case REQUEST_ACCESS:    return Constants.Ints.NotificationTypes.Verbs.Indices.REQUEST_ACCESS;
                    case REQUEST_APPROVAL:  return Constants.Ints.NotificationTypes.Verbs.Indices.REQUEST_APPROVAL;
                    case REQUEST_JOIN:      return Constants.Ints.NotificationTypes.Verbs.Indices.REQUEST_JOIN;
                    case UPDATE:            return Constants.Ints.NotificationTypes.Verbs.Indices.UPDATE;
                    case DEFAULT:
                    default:                return DEFAULT_ID;
                }
            }
        }

        public static NotificationVerbType getType(int type) {
            switch (type) {
                case Constants.Ints.NotificationTypes.Verbs.IDs.ADD:
                case Constants.Ints.NotificationTypes.Verbs.Indices.ADD:
                    return ADD;
                case Constants.Ints.NotificationTypes.Verbs.IDs.APPROVE:
                case Constants.Ints.NotificationTypes.Verbs.Indices.APPROVE:
                    return APPROVE;
                case Constants.Ints.NotificationTypes.Verbs.IDs.AWAIT:
                case Constants.Ints.NotificationTypes.Verbs.Indices.AWAIT:
                    return AWAIT;
                case Constants.Ints.NotificationTypes.Verbs.IDs.BLOCK:
                case Constants.Ints.NotificationTypes.Verbs.Indices.BLOCK:
                    return BLOCK;
                case Constants.Ints.NotificationTypes.Verbs.IDs.CREATE:
                case Constants.Ints.NotificationTypes.Verbs.Indices.CREATE:
                    return CREATE;
                case Constants.Ints.NotificationTypes.Verbs.IDs.DISAPPROVE:
                case Constants.Ints.NotificationTypes.Verbs.Indices.DISAPPROVE:
                    return DISAPPROVE;
                case Constants.Ints.NotificationTypes.Verbs.IDs.INVITE:
                case Constants.Ints.NotificationTypes.Verbs.Indices.INVITE:
                    return INVITE;
                case Constants.Ints.NotificationTypes.Verbs.IDs.JOIN:
                case Constants.Ints.NotificationTypes.Verbs.Indices.JOIN:
                    return JOIN;
                case Constants.Ints.NotificationTypes.Verbs.IDs.LEFT:
                case Constants.Ints.NotificationTypes.Verbs.Indices.LEFT:
                    return LEFT;
                case Constants.Ints.NotificationTypes.Verbs.IDs.RECEIVE:
                case Constants.Ints.NotificationTypes.Verbs.Indices.RECEIVE:
                    return RECEIVE;
                case Constants.Ints.NotificationTypes.Verbs.IDs.REQUEST:
                case Constants.Ints.NotificationTypes.Verbs.Indices.REQUEST:
                    return REQUEST;
                case Constants.Ints.NotificationTypes.Verbs.IDs.UPDATE:
                case Constants.Ints.NotificationTypes.Verbs.Indices.UPDATE:
                    return UPDATE;
                default: return DEFAULT;
            }
        }

        public static NotificationVerbType getType(String type) {
            for (int i = 0; i < getNumberOfNotificationVerbTypes(); i++) {
                if (type.equals(getType(i).toString()) || type.equals(getType(i).toMapStyleString())) {
                    return getType(i);
                }
            }

            return DEFAULT;
        }

        public static ArrayList<NotificationVerbType> getAllNotificationVerbTypes() {
            final ArrayList<NotificationVerbType> notificationVerbTypes = new ArrayList<>();
            for (int i = 0 ; i < getNumberOfNotificationVerbTypes(); i++) {
                notificationVerbTypes.add(getType(i));
            }

            return notificationVerbTypes;
        }

        public static int getNumberOfNotificationVerbTypes() {
            return 15;
        }

    }
    public enum NotificationObjectType {
        BRANDING_ELEMENT, CHAT, MEMBER, TEAM, DEFAULT;

        @Override
        public String toString() {
            return super.toString();
        }

        public String toMapStyleString() {
            return String.valueOf(this.toInt(true));
        }

        public int toInt(boolean mapStyle) {
            if (mapStyle) {
                switch (this){
                    case BRANDING_ELEMENT:  return Constants.Ints.NotificationTypes.Objects.IDs.BRANDING_ELEMENT;
                    case CHAT:              return Constants.Ints.NotificationTypes.Objects.IDs.CHAT;
                    case MEMBER:            return Constants.Ints.NotificationTypes.Objects.IDs.MEMBER;
                    case TEAM:              return Constants.Ints.NotificationTypes.Objects.IDs.TEAM;
                    default:                return DEFAULT_ID;
                }
            } else {
                switch (this){
                    case BRANDING_ELEMENT:  return Constants.Ints.NotificationTypes.Objects.Indices.BRANDING_ELEMENT;
                    case CHAT:              return Constants.Ints.NotificationTypes.Objects.Indices.CHAT;
                    case MEMBER:            return Constants.Ints.NotificationTypes.Objects.Indices.MEMBER;
                    case TEAM:              return Constants.Ints.NotificationTypes.Objects.Indices.TEAM;
                    default:                return DEFAULT_ID;
                }
            }
        }

        public static NotificationObjectType getType(int type) {
            switch (type) {
                case Constants.Ints.NotificationTypes.Objects.IDs.BRANDING_ELEMENT:
                case Constants.Ints.NotificationTypes.Objects.Indices.BRANDING_ELEMENT:
                    return BRANDING_ELEMENT;
                case Constants.Ints.NotificationTypes.Objects.IDs.CHAT:
                case Constants.Ints.NotificationTypes.Objects.Indices.CHAT:
                    return CHAT;
                case Constants.Ints.NotificationTypes.Objects.IDs.MEMBER:
                case Constants.Ints.NotificationTypes.Objects.Indices.MEMBER:
                    return MEMBER;
                case Constants.Ints.NotificationTypes.Objects.IDs.TEAM:
                case Constants.Ints.NotificationTypes.Objects.Indices.TEAM:
                    return TEAM;
                    default: return DEFAULT;
            }
        }

        public static NotificationObjectType getType(String type) {
            for (int i = 0; i < getNumberOfNotificationObjectTypes(); i++) {
                if (type.equals(getType(i).toString()) || type.equals(getType(i).toMapStyleString())) {
                    return getType(i);
                }
            }

            return DEFAULT;
        }

        public static ArrayList<NotificationObjectType> getAllNotificationObjectTypes() {
            final ArrayList<NotificationObjectType> notificationObjectTypes = new ArrayList<>();
            for (int i = 0 ; i < getNumberOfNotificationObjectTypes(); i++) {
                notificationObjectTypes.add(getType(i));
            }

            return notificationObjectTypes;
        }

        public static int getNumberOfNotificationObjectTypes() {
            return 5;
        }
    }
//    Notification Constructors
    public Notification() { }
    public Notification(final Parcel in) {
        final Notification notification = (Notification) in.readSerializable();
        setUID(notification.getUID());
        setObject(notification.getObject());
        setSubject(notification.getSubject());
        setVerbType(notification.getVerbType());
        setReceiverUIDs(notification.getReceiverUIDs());
    }
    public Notification(final DataSnapshot nRef) {
        this();
        if (nRef.hasChild(Constants.Strings.UIDs.UID)) {
            setUID(nRef.child(Constants.Strings.UIDs.UID).getValue().toString());
        }
        if (nRef.hasChild(Constants.Strings.Fields.SUBJECT_TYPE)) {
            setSubjectType(NotificationSubjectType.getType(nRef.child(Constants.Strings.Fields.SUBJECT_TYPE).getValue().toString()));
        }
        if (nRef.hasChild(Constants.Strings.Fields.VERB_TYPE)) {
            setVerbType(NotificationVerbType.getType(nRef.child(Constants.Strings.Fields.VERB_TYPE).getValue().toString()));
        }
        if (nRef.hasChild(Constants.Strings.Fields.OBJECT_TYPE)) {
            setObjectType(NotificationObjectType.getType(nRef.child(Constants.Strings.Fields.OBJECT_TYPE).getValue().toString()));
        }
        if (nRef.hasChild(Constants.Strings.UIDs.SUBJECT_UID)) {
            setSubjectUID(nRef.child(Constants.Strings.UIDs.SUBJECT_UID).getValue().toString());
        }
        if (nRef.hasChild(Constants.Strings.UIDs.OBJECT_UID)) {
            setObjectUID(nRef.child(Constants.Strings.UIDs.OBJECT_UID).getValue().toString());
        }

        int index = 0;
        while (nRef.hasChild(Constants.Strings.UIDs.RECEIVER_UID + index)) {
            getReceiverUIDs().add(nRef.child(Constants.Strings.UIDs.RECEIVER_UID + index).getValue().toString());
            index++;
        }
    }
    public Notification(final FirebaseObject subject, final NotificationVerbType vType) {
        setVerbType(vType);
        setSubject(subject);
    }
    public Notification(final FirebaseObject subject, final FirebaseObject object, final NotificationVerbType vType) {
        this(subject,vType);
        setObject(object);
        setReceiverUIDs(subject,object,vType);
    }
    public Notification(final NotificationSubjectType sType, final NotificationVerbType vType, final NotificationObjectType oType){
        setVerbType(vType);
        setObjectType(oType);
        setSubjectType(sType);
    }
    public Notification(final FirebaseObject subject, final NotificationVerbType vType, final BrandingElement.ElementType brandingElementType) {
        this(subject,vType);
    }
//    Functional Methods
    public Bundle toBundle() {
    final Bundle bundle = new Bundle();
    String entityTeamUID;
    String navigationDirection;
    switch(getSubjectType()) {
        case MEMBER:
            entityTeamUID = ((User) getSubject()).getTeamUID();
            break;
        case TEAM:
        default:
            entityTeamUID = getSubject().getUID();
            break;
    }
    switch (getObjectType()) {
        case BRANDING_ELEMENT:
            navigationDirection = Constants.Strings.Fragments.BRANDING_ELEMENTS;
            break;
        case CHAT:
            navigationDirection = Constants.Strings.Fragments.CHAT;
            break;
        case MEMBER:
        case TEAM:
        default:
            navigationDirection = Constants.Strings.Fragments.TEAM_MANAGEMENT;
            break;

    }

    bundle.putString(Constants.Strings.Fields.MESSAGE,getMessage());
    bundle.putString(Constants.Strings.UIDs.TEAM_UID,entityTeamUID);
    bundle.putString(Constants.Strings.Fields.FRAGMENT,navigationDirection);

    return bundle;
}
    @Override
    public Map<String, String> toMap() {
        final HashMap<String, String> result = new HashMap<>();
        if (getUID() != null) {
            result.put(Constants.Strings.UIDs.UID, getUID());
        }
        if (getObject() != null) {
            result.put(Constants.Strings.UIDs.OBJECT_UID, getObjectUID());
        }
        if (getSubject() != null) {
            result.put(Constants.Strings.UIDs.SUBJECT_UID, getSubjectUID());
        }
        if (!getObjectType().equals(null)) {
            result.put(Constants.Strings.Fields.OBJECT_TYPE, getObjectType().toMapStyleString());
        }
        if (!getSubjectType().equals(null)) {
            result.put(Constants.Strings.Fields.SUBJECT_TYPE, getSubjectType().toMapStyleString());
        }
        if (!getVerbType().equals(null)) {
            result.put(Constants.Strings.Fields.VERB_TYPE, getVerbType().toMapStyleString());
        }
        if (!getReceiverUIDs().equals(null)) {
            for (int i = 0; i < getReceiverUIDs().size(); i++) {
                result.put(Constants.Strings.UIDs.RECEIVER_UID+i, getReceiverUIDs().get(i).toString());
            }
        }
        return result;
    }
    public boolean goesToUID(String uid) {
        for (int i = 0; i < getReceiverUIDs().size(); i++) {
            if (getReceiverUIDs().get(i).equals(uid)) {
                return true;
            }
        }

        return false;
    }
    //    Parcel Details
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator CREATOR = new Creator() {
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };
//    Other Methods
    @Override
    public String getField(int index) {
        return null;
    }
    @Override
    public String description() {
        return null;
    }
    //    Class Getters & Setters
    public String getMessage() {
        String subjectPart = "";
        String verbPart = "";
        String objectPart = "";

        if (getSubject() != null && getObject() != null) {
            switch (subjectType) {
                case MEMBER:
                case TEAM:
                    subjectPart = ((FirebaseEntity)getSubject()).getName();
                    break;
                case DEFAULT:
                    return "Unable to grab data!";
            }

            switch (verbType) {
                case ADD:
                    verbPart = "added";
                case APPROVE:
                    verbPart = "approved";
                    break;
                case AWAIT:
                    verbPart = "is waiting for access";
                    break;
                case CREATE:
                    verbPart = "created";
                    break;
                case DISAPPROVE:
                    verbPart = "disapproved";
                    break;
                case INVITE:
                    verbPart = "invited";
                    break;
                case JOIN:
                    verbPart = "joined";
                    break;
                case LEFT:
                    verbPart = verbType.toString();
                    break;
                case RECEIVE:
                    verbPart = "received";
                case REQUEST:
                    verbPart = "is requesting";
                    break;
                case REQUEST_ACCESS:
                    verbPart = "is requesting access to";
                    break;
                case REQUEST_APPROVAL:
                    break;
                case REQUEST_JOIN:
                    verbPart = "is requesting to join";
                    break;
                case UPDATE:
                    verbPart = "updated";
                    break;
                case DEFAULT:
                    return "Unable to grab data!";
                case BLOCK:
                    verbPart = "blocked";
                    break;
            }

            switch (objectType) {
                case BRANDING_ELEMENT:
                    objectPart = ((BrandingElement)getObject()).getType().toString();
                    break;
                case CHAT:
                    objectPart = "Chat";
                    break;
                case MEMBER:
                case TEAM:
                    objectPart = ((FirebaseEntity)getObject()).getName();
                    break;
                case DEFAULT:
                    return "Unable to grab data!";

            }

            return subjectPart.toString() + " " + verbPart.toString() + " " + objectPart.toString() + ".";
        } else {
            return "Unable to grab data!";

        }

    }
    private void setReceiverUIDs(FirebaseObject subject, FirebaseObject object, NotificationVerbType vType) {
        if (subject instanceof FirebaseEntity) {
            if (subject instanceof User) {
                if (object instanceof FirebaseEntity) {
                    if (object instanceof User) {
                    } else if (object instanceof Team) {
                        getReceiverUIDs().add(object.getUID());
                    }
                } else if (object instanceof FirebaseContent) {
                    if (object instanceof BrandingElement) {

                    }
                }
            } else  if (subject instanceof Team){
                if (object instanceof FirebaseEntity) {
                    if (object instanceof User) {
                    } else if (object instanceof Team) {
                        getReceiverUIDs().add(object.getUID());
                    }
                } else if (object instanceof FirebaseContent) {
                    if (object instanceof BrandingElement) {

                    }
                }
            }
        }

        if (object instanceof FirebaseEntity) {
            if (subject instanceof User) {

            } else  if (subject instanceof Team){
                getReceiverUIDs().add(subject.getUID());
            }
        } else if (object instanceof FirebaseContent) {

        }
    }
    public String getObjectUID() { return objectUID; }
    public void setObjectUID(final String objectUID) { this.objectUID = objectUID; }
    public FirebaseObject getObject() { return object; }
    public NotificationObjectType setObject(final FirebaseObject object) {
        this.object = object;
        setObjectUID(object.getUID());
        if (object instanceof Chat) {
            setObjectType(NotificationObjectType.CHAT);
        } else if (object instanceof BrandingElement) {
            setObjectType(BRANDING_ELEMENT);
        } else if (object instanceof User) {
            setObjectType(NotificationObjectType.MEMBER);
        } else if (object instanceof Team) {
            setObjectType(NotificationObjectType.TEAM);
        } else {
            return NotificationObjectType.DEFAULT;
        }

        return getObjectType();
    }
    public String getSubjectUID() { return subjectUID; }
    public void setSubjectUID(final String subjectUID) { this.subjectUID = subjectUID; }
    public FirebaseObject getSubject() { return subject; }
    public NotificationSubjectType setSubject(final FirebaseObject subject) {
        this.subject = subject;
        setSubjectUID(subject.getUID());
        if (subject instanceof User) {
            setSubjectType(NotificationSubjectType.MEMBER);
        } else if (subject instanceof Team) {
            setSubjectType(NotificationSubjectType.TEAM);
        } else {
            return NotificationSubjectType.DEFAULT;
        }

        return getSubjectType();
    }
    public NotificationVerbType getVerbType() { return verbType; }
    public void setVerbType(final NotificationVerbType verbType) { this.verbType = verbType; }
    public ArrayList<String> getReceiverUIDs() { return receiverUIDs; }
    public void setReceiverUIDs(final ArrayList<String> receiverUIDs) { this.receiverUIDs = receiverUIDs; }
    public NotificationObjectType getObjectType() { return objectType; }
    public void setObjectType(final NotificationObjectType objectType) { this.objectType = objectType; }
    public NotificationSubjectType getSubjectType() { return subjectType; }
    public void setSubjectType(final NotificationSubjectType subjectType) { this.subjectType = subjectType; }
}