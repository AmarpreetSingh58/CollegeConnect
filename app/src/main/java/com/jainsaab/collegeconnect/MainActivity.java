package com.jainsaab.collegeconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jainsaab.collegeconnect.onboarding.CustomIntro;
import com.jainsaab.collegeconnect.signingin.RegisterAsActivity;
import com.jainsaab.collegeconnect.signingin.SelectCollegeActivity;
import com.jainsaab.collegeconnect.student.StudentActivity;
import com.jainsaab.collegeconnect.teacher.TeacherActivity;

import static com.jainsaab.collegeconnect.signingin.RegisterAsActivity.COLLEGE_NOT_SELECTED_VALUE;
import static com.jainsaab.collegeconnect.signingin.RegisterAsActivity.COLLEGE_SELECTED_VALUE;
import static com.jainsaab.collegeconnect.signingin.RegisterAsActivity.DEFAULT_RC_SIGN_IN;
import static com.jainsaab.collegeconnect.signingin.SelectCollegeActivity.COLLEGE_SELECTED;

public class MainActivity extends AppCompatActivity {

    public static final String FIRST_RUN = "first_run";
    private static final String TAG = MainActivity.class.getSimpleName();
    SharedPreferences mGetPrefs;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGetPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        if (mGetPrefs.getInt(FIRST_RUN, 0) == 0) {
            // First run, launch onBoarding
            startActivity(new Intent(this, CustomIntro.class));
            finish();
        }

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null && mGetPrefs.getInt(FIRST_RUN, 0) != 0) {
            // Not signed in, launch the RegisterAs activity
            startActivity(new Intent(this, RegisterAsActivity.class));
            finish();
        }

        final int userType = mGetPrefs.getInt(RegisterAsActivity.USER_TYPE, DEFAULT_RC_SIGN_IN);
        int collegeSelectedFlag = mGetPrefs.getInt(COLLEGE_SELECTED, COLLEGE_NOT_SELECTED_VALUE);

        if (mFirebaseUser != null && collegeSelectedFlag == COLLEGE_NOT_SELECTED_VALUE) {
            startActivity(new Intent(getApplicationContext(), SelectCollegeActivity.class));
            finish();
        }

        if (mFirebaseUser != null && userType != DEFAULT_RC_SIGN_IN && collegeSelectedFlag == COLLEGE_SELECTED_VALUE) {
            if (userType == RegisterAsActivity.TEACHER_RC_SIGN_IN) {
                startActivity(new Intent(this, TeacherActivity.class));
            } else if (userType == RegisterAsActivity.STUDENT_RC_SIGN_IN) {
                startActivity(new Intent(this, StudentActivity.class));
            }
            finish();
        }

    }
}
