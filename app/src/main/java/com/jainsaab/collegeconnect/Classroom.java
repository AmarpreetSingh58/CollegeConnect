package com.jainsaab.collegeconnect;

public class Classroom {
    int mColorCode;
    String mClassName;
    int mNoOfStudents;

    Classroom() {
    }

    public Classroom(int colorCode, String className, int noOfStudents) {
        mColorCode = colorCode;
        mClassName = className;
        mNoOfStudents = noOfStudents;
    }

    public int getmColorCode() {
        return mColorCode;
    }

    public void setmColorCode(int colorCode) {
        mColorCode = colorCode;
    }

    public String getmClassName() {
        return mClassName;
    }

    public void setmClassName(String className) {
        mClassName = className;
    }

    public int getmNoOfStudents() {
        return mNoOfStudents;
    }

    public void setmNoOfStudents(int noOfStudents) {
        mNoOfStudents = noOfStudents;
    }
}
