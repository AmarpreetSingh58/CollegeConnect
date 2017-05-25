package com.jainsaab.collegeconnect.teacher;

public class Teacher {

    private String mUserName;
    private String mEmailId;
    private String mCollegeName;

    public Teacher() {
    }

    public Teacher(String username, String emailId, String colleeName) {
        mUserName = username;
        mEmailId = emailId;
        mCollegeName = colleeName;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getEmailId() {
        return mEmailId;
    }

    public void setEmailId(String emailId) {
        mEmailId = emailId;
    }

    public String getCollegeName() {
        return mCollegeName;
    }

    public void setCollegeName(String collegeName) {
        mCollegeName = collegeName;
    }

}
