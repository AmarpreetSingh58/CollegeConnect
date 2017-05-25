package com.jainsaab.collegeconnect.signingin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jainsaab.collegeconnect.R;
import com.jainsaab.collegeconnect.Utility;

import java.util.Arrays;

public class RegisterAsActivity extends AppCompatActivity {

    public static final int DEFAULT_RC_SIGN_IN = 100;
    public static final int TEACHER_RC_SIGN_IN = 101;
    public static final int STUDENT_RC_SIGN_IN = 102;
    public static final int COLLEGE_NOT_SELECTED_VALUE = 200;
    public static final int COLLEGE_SELECTED_VALUE = 201;
    public static final String USER_TYPE = "user_type";
    CardView mTeacherCard, mStudentCard;
    SharedPreferences mGetPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_as);

        mGetPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        mTeacherCard = (CardView) findViewById(R.id.teacher_card);
        mStudentCard = (CardView) findViewById(R.id.student_card);

        mTeacherCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignInActivity(R.style.TeacherSignInTheme, R.drawable.register_teacher);
            }
        });
        mStudentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignInActivity(R.style.StudentSignInTheme, R.drawable.register_student);
            }
        });

    }

    private void startSignInActivity(int theme, int logo) {
        if (Utility.isNetworkAvailable(getApplicationContext())) {
            if (logo == R.drawable.register_teacher) {
                startAuthenticationActivity(TEACHER_RC_SIGN_IN, theme, logo);
            } else {
                startAuthenticationActivity(STUDENT_RC_SIGN_IN, theme, logo);
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
        }

    }

    private void startAuthenticationActivity(int RC_SIGN_IN, int theme, int logo) {

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .setTheme(theme)
                        .setLogo(logo)
                        .build(),
                RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TEACHER_RC_SIGN_IN || requestCode == STUDENT_RC_SIGN_IN) {

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null && mGetPrefs.getInt(USER_TYPE, DEFAULT_RC_SIGN_IN) == DEFAULT_RC_SIGN_IN) {
                SharedPreferences.Editor preferenceEditor = mGetPrefs.edit();
                preferenceEditor.putInt(USER_TYPE, requestCode);
                preferenceEditor.apply();
                startActivity(new Intent(getApplicationContext(), SelectCollegeActivity.class));
                finish();
            }
        }
    }

}
