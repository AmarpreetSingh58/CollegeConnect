package com.jainsaab.collegeconnect.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.jainsaab.collegeconnect.R;
import com.jainsaab.collegeconnect.signingin.RegisterAsActivity;

import static com.jainsaab.collegeconnect.MainActivity.FIRST_RUN;

public final class CustomIntro extends AppIntro {

    SharedPreferences mGetPrefs;
    SharedPreferences.Editor mPreferenceEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGetPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        mPreferenceEditor = mGetPrefs.edit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTheme(R.style.FullScreenTheme);
        }

        addSlide(AppIntroFragment.newInstance(getString(R.string.onboarding_log_in),
                getString(R.string.onboarding_teacher_log_in),
                R.drawable.log_in_teacher, getResources().getColor(R.color.colorOnboardingTeacherLogIn)));

        addSlide(AppIntroFragment.newInstance(getString(R.string.onboarding_log_in),
                getString(R.string.onboarding_student_log_in),
                R.drawable.log_in_students, getResources().getColor(R.color.colorOnboardingStudentLogIn)));

        addSlide(AppIntroFragment.newInstance(getString(R.string.onboarding_connect),
                getString(R.string.onboarding_connect_description),
                R.drawable.connect, getResources().getColor(R.color.colorOnboardingConnect)));

        addSlide(AppIntroFragment.newInstance(getString(R.string.onboarding_get_started),
                getString(R.string.onboarding_get_started_description),
                R.drawable.get_started, getResources().getColor(R.color.colorOnboardingGetStarted)));

        setImmersiveMode(true);
        setColorTransitionsEnabled(true);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        mPreferenceEditor.putInt(FIRST_RUN, 1);
        mPreferenceEditor.apply();
        startActivity(new Intent(this, RegisterAsActivity.class));
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        mPreferenceEditor.putInt(FIRST_RUN, 1);
        mPreferenceEditor.apply();
        startActivity(new Intent(this, RegisterAsActivity.class));
        finish();
    }
}
