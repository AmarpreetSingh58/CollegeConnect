package com.jainsaab.collegeconnect.teacher;

import com.jainsaab.collegeconnect.Requests;

public class StudentRequest {

    private Requests mRequests;
    private String mEmail;

    StudentRequest() {
    }

    public StudentRequest(Requests requests, String email) {
        mEmail = email;
        mRequests = requests;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String email) {
        mEmail = email;
    }

    public Requests getmRequests() {
        return mRequests;
    }

    public void setmRequests(Requests requests) {
        mRequests = requests;
    }
}
