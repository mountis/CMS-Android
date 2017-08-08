package com.marionthefourth.augimas.classes.objects;

import com.marionthefourth.augimas.classes.constants.Constants;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.DEFAULT_ID;

public abstract class FirebaseEntity extends FirebaseObject {
    private String name = "";
    private String username = "";
    private EntityRole entityRole = EntityRole.DEFAULT;
    private EntityType entityType = EntityType.DEFAULT;
    private EntityStatus entityStatus = EntityStatus.DEFAULT;
//    Class Enums
    public enum EntityType {
    HOST, CLIENT, DEFAULT;

        public String toMapStyleString() {
            return String.valueOf(this.toInt(true));
        }

        public int toInt(boolean mapStyle) {
            if (mapStyle) {
                switch (this) {
                    case HOST: return Constants.Ints.EntityTypes.IDs.HOST;
                    case CLIENT: return Constants.Ints.EntityTypes.IDs.CLIENT;
                    default: return DEFAULT_ID;
                }
            } else {
                switch (this) {
                    case HOST: return Constants.Ints.EntityTypes.Indices.HOST;
                    case CLIENT: return Constants.Ints.EntityTypes.Indices.CLIENT;
                    default: return DEFAULT_ID;
                }
            }
        }

        public static EntityType getType(String type) {
            for (int i = 0; i < getNumberOfTypes(); i++) {
                if (type.equals(getType(i).toString()) || type.equals(getType(i).toMapStyleString())) {
                    return getType(i);
                }
            }

            return DEFAULT;
        }

        public static EntityType getType(int type) {
            switch (type) {
                case Constants.Ints.EntityTypes.IDs.HOST:
                case Constants.Ints.EntityTypes.Indices.HOST:
                    return HOST;
                case Constants.Ints.EntityTypes.IDs.CLIENT:
                case Constants.Ints.EntityTypes.Indices.CLIENT:
                    return CLIENT;
                default:
                    return DEFAULT;
            }
        }

        public static ArrayList<EntityType> getAllTypes() {
            final ArrayList<EntityType> types = new ArrayList<>();
            for (int i = 0; i < getNumberOfTypes(); i++) {
                types.add(getType(i));
            }

            return types;
        }

        public static int getNumberOfTypes() {
            return 3;
        }
    }
    public enum EntityRole {
        OWNER, ADMIN, EDITOR, CHATTER, VIEWER, NONE, DEFAULT;

        @Override
        public String toString() {
            return super.toString();
        }

        public String toMapStyleString() {
            return String.valueOf(this.toInt(true));
        }

        public int toInt(boolean mapStyle) {
            if (mapStyle) {
                if (this == OWNER) return Constants.Ints.EntityRoles.IDs.OWNER;
                if (this == ADMIN) return Constants.Ints.EntityRoles.IDs.ADMIN;
                if (this == EDITOR) return Constants.Ints.EntityRoles.IDs.EDITOR;
                if (this == CHATTER) return Constants.Ints.EntityRoles.IDs.CHATTER;
                if (this == VIEWER) return Constants.Ints.EntityRoles.IDs.VIEWER;
                if (this == NONE) return Constants.Ints.EntityRoles.IDs.NONE;
                return Constants.Ints.EntityRoles.IDs.DEFAULT;
            } else {
                if (this == OWNER) return Constants.Ints.EntityRoles.Indices.OWNER;
                if (this == ADMIN) return Constants.Ints.EntityRoles.Indices.ADMIN;
                if (this == EDITOR) return Constants.Ints.EntityRoles.Indices.EDITOR;
                if (this == CHATTER) return Constants.Ints.EntityRoles.Indices.CHATTER;
                if (this == VIEWER) return Constants.Ints.EntityRoles.Indices.VIEWER;
                if (this == NONE) return Constants.Ints.EntityRoles.Indices.NONE;
                return Constants.Ints.EntityRoles.Indices.DEFAULT;
            }
        }

        public static EntityRole getRole(final String role) {
            for (int i = 0; i < getNumberOfRoles(); i++) {
                if (role.equals(getRole(i).toString()) || role.equals(getRole(i).toMapStyleString())) {
                    return getRole(i);
                }
            }
            return null;
        }

        public static ArrayList<EntityRole> getAllRoles() {
            final ArrayList<EntityRole> memberRoles = new ArrayList<>();
            for (int i = 0; i < getNumberOfRoles(); i++) {
                memberRoles.add(getRole(i));
            }

            return memberRoles;
        }

        public static EntityRole getRole(final int roleIndex) {
            switch (roleIndex) {
                case Constants.Ints.EntityRoles.IDs.OWNER:
                case Constants.Ints.EntityRoles.Indices.OWNER:
                    return OWNER;
                case Constants.Ints.EntityRoles.IDs.ADMIN:
                case Constants.Ints.EntityRoles.Indices.ADMIN:
                    return ADMIN;
                case Constants.Ints.EntityRoles.IDs.EDITOR:
                case Constants.Ints.EntityRoles.Indices.EDITOR:
                    return EDITOR;
                case Constants.Ints.EntityRoles.IDs.CHATTER:
                case Constants.Ints.EntityRoles.Indices.CHATTER:
                    return CHATTER;
                case Constants.Ints.EntityRoles.IDs.VIEWER:
                case Constants.Ints.EntityRoles.Indices.VIEWER:
                    return VIEWER;
                case Constants.Ints.EntityRoles.IDs.NONE:
                case Constants.Ints.EntityRoles.Indices.NONE:
                    return NONE;
                default: return DEFAULT;
            }
        }

        private static int getNumberOfRoles() {
            return 7;
        }

    }
    public enum EntityStatus {
        APPROVED,AWAITING,BLOCKED,DEFAULT;

        @Override
        public String toString() {
            return super.toString();
        }

        public String toVerb() {
            switch (this) {
                case APPROVED:
                    return "Approve";
                case BLOCKED:
                    return "Block";
                default:
                    return DEFAULT.toString();
            }
        }

        public String toMapStyleString() {
            return String.valueOf(this.toInt(true));
        }

        public int toInt(boolean mapStyle) {
            if (mapStyle) {
                switch (this) {
                    case APPROVED: return Constants.Ints.EntityStatii.IDs.APPROVED;
                    case AWAITING: return Constants.Ints.EntityStatii.IDs.AWAITING;
                    case BLOCKED: return Constants.Ints.EntityStatii.IDs.BLOCKED;
                    default: return DEFAULT_ID;
                }
            } else {
                switch (this) {
                    case APPROVED: return Constants.Ints.EntityStatii.Indices.APPROVED;
                    case AWAITING: return Constants.Ints.EntityStatii.Indices.AWAITING;
                    case BLOCKED: return Constants.Ints.EntityStatii.Indices.BLOCKED;
                    default: return DEFAULT_ID;
                }
            }

        }

        public static FirebaseEntity.EntityStatus getStatus(final String role) {

            for (int i = 0; i < getNumberOfEntityStatii(); i++) {
                if (role.equals(getStatus(i).toString()) || role.equals(getStatus(i).toMapStyleString()) || role.equals(getStatus(i).toVerb())) {
                    return getStatus(i);
                }
            }
            return null;
        }

        public static FirebaseEntity.EntityStatus getVerbStatus(final int statusIndex) {
            switch (statusIndex) {
                case 0:
                    return APPROVED;
                case 1:
                    return BLOCKED;
                default:
                        return DEFAULT;
            }
        }

        public static ArrayList<FirebaseEntity.EntityStatus> getAllStatii() {
            final ArrayList<FirebaseEntity.EntityStatus> entityStatii = new ArrayList<>();
            for (int i = 0; i < getNumberOfEntityStatii(); i++) {
                entityStatii.add(getStatus(i));
            }

            return entityStatii;
        }

        public static FirebaseEntity.EntityStatus getStatus(final int statusIndex) {
            switch (statusIndex) {
                case Constants.Ints.EntityStatii.IDs.APPROVED:
                case Constants.Ints.EntityStatii.Indices.APPROVED: return APPROVED;
                case Constants.Ints.EntityStatii.IDs.AWAITING:
                case Constants.Ints.EntityStatii.Indices.AWAITING: return AWAITING;
                case Constants.Ints.EntityStatii.IDs.BLOCKED:
                case Constants.Ints.EntityStatii.Indices.BLOCKED: return BLOCKED;
                default: return DEFAULT;
            }
        }

        private static int getNumberOfEntityStatii() {
            return 4;
        }
    }
//    Functional Methods
    public final boolean hasInclusiveAccess(EntityRole accessRole) {
        if (getRole() != null) {
            return getRole().toInt(false) >= accessRole.toInt(false);
        } else {
            return false;
        }
    }
    public final boolean hasExclusiveAccess(EntityRole accessRole) {
        if (getRole() != null) {
            return getRole().toInt(false) > accessRole.toInt(false);
        } else {
            return false;
        }
    }
//    Class Getters & Setters
    public final String getName() { return name; }
    public final void setName(final String name) { this.name = name; }
    public final EntityRole getRole() { return entityRole; }
    public final void setRole(final EntityRole entityRole) { this.entityRole = entityRole; }
    public final EntityType getType() { return entityType; }
    public final void setType(final EntityType entityType) { this.entityType = entityType; }
    public final String getUsername() { return username; }
    public final void setUsername(final String username) { this.username = username; }
    public final EntityStatus getStatus() { return entityStatus; }
    public final void setStatus(final EntityStatus entityStatus) { this.entityStatus = entityStatus; }
}