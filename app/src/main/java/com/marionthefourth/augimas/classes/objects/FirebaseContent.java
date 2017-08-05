package com.marionthefourth.augimas.classes.objects;

public abstract class FirebaseContent extends FirebaseObject {
    private String teamUID;
//    Class Getters & Setters
    public final String getTeamUID() { return teamUID; }
    public final void setTeamUID(final String teamUID) { this.teamUID = teamUID; }
}
