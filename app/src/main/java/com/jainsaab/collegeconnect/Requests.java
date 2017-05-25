package com.jainsaab.collegeconnect;

public class Requests {
    private String mUserName;
    private String mClassName;
    private String mEmailId;
    private String mStatus;

    Requests() {
    }

    public Requests(String userName, String className, String emailId, String status) {
        mUserName = userName;
        mClassName = className;
        mEmailId = emailId;
        mStatus = status;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String userName) {
        mUserName = userName;
    }

    public String getmClassName() {
        return mClassName;
    }

    public void setmClassName(String className) {
        mClassName = className;
    }

    public String getmEmailId() {
        return mEmailId;
    }

    public void setmEmailId(String emailId) {
        mEmailId = emailId;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String status) {
        mStatus = status;
    }
}
