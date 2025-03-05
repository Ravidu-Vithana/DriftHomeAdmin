package com.ryvk.drifthomeadmin;

public class UserCard {
    private final boolean isDrinker;
    private final String nameText;
    private final String emailText;
    private boolean isBlocked;
    private final boolean isUserList;

    public UserCard (boolean isDrinker, String nameText,String emailText, boolean isBlocked){
        this.isDrinker = isDrinker;
        this.nameText = nameText;
        this.emailText = emailText;
        this.isBlocked = isBlocked;
        this.isUserList = true;
    }

    public boolean isDrinker() {
        return isDrinker;
    }

    public String getNameText() {
        return nameText;
    }

    public String getEmailText() {
        return emailText;
    }

    public boolean isBlocked() {return isBlocked;}

    public void setBlocked(boolean blocked) {isBlocked = blocked;}

    public boolean isUserList() {return isUserList;}
}
