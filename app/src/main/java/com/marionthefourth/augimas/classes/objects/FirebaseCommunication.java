package com.marionthefourth.augimas.classes.objects;

import com.marionthefourth.augimas.classes.constants.Constants;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.DEFAULT_ID;

public abstract class FirebaseCommunication extends FirebaseObject {
    private CommunicationType communicationType = CommunicationType.DEFAULT;
//    Class Enums
    public enum CommunicationType {
        A, B, DEFAULT;

        // TYPE A - Admin x Admin | Client X Client (takes Admin/Client Name)
        // TYPE B - Admin x Client (depending on who the user is and what team they are in)

        // From the User's side the first tab is their Team and the second tab is the Other Team

        @Override
        public String toString() { return super.toString(); }

        public String toMapStyleString() {
            return String.valueOf(this.toInt(true));
        }

        public int toInt(boolean mapStyle) {
            if (mapStyle) {
                switch (this){
                    case A: return Constants.Ints.CommunicationTypes.IDs.A;
                    case B: return Constants.Ints.CommunicationTypes.IDs.B;
                    default: return DEFAULT_ID;
                }
            } else {
                switch (this){
                    case A: return Constants.Ints.CommunicationTypes.Indices.A;
                    case B: return Constants.Ints.CommunicationTypes.Indices.B;
                    default: return DEFAULT_ID;
                }
            }

        }

        public static CommunicationType getType(String type) {
            for (int i = 0; i < getNumberOfCommunicationTypes(); i++) {
                if (type.equals(getType(i).toString()) || type.equals(getType(i).toMapStyleString())) {
                    return getType(i);
                }
            }

            return DEFAULT;
        }

        public static CommunicationType getType(int type) {
            for (int i = 0; i < getNumberOfCommunicationTypes(); i++) {
                if (type == i || type == getType(i).toInt(true)) {
                    return getType(i);
                }
            }

            return DEFAULT;
        }

        public static ArrayList<CommunicationType> getAllCommunicationTypes() {
            final ArrayList<CommunicationType> communicationTypes = new ArrayList<>();
            for (int i = 0; i < getNumberOfCommunicationTypes(); i++) {
                communicationTypes.add(getType(i));
            }

            return communicationTypes;
        }

        public static int getNumberOfCommunicationTypes() {
            return 3;
        }
    }
//    Class Getters & Setters
    public CommunicationType getType() {
        return communicationType;
    }
    public void setType(CommunicationType communicationType) {  this.communicationType = communicationType; }
}