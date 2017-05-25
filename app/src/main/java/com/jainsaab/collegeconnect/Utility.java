package com.jainsaab.collegeconnect;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.firebase.ui.auth.AuthUI;

import static com.jainsaab.collegeconnect.signingin.RegisterAsActivity.COLLEGE_NOT_SELECTED_VALUE;
import static com.jainsaab.collegeconnect.signingin.RegisterAsActivity.DEFAULT_RC_SIGN_IN;
import static com.jainsaab.collegeconnect.signingin.RegisterAsActivity.USER_TYPE;
import static com.jainsaab.collegeconnect.signingin.SelectCollegeActivity.COLLEGE_SELECTED;

public class Utility {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void signedOutCleanUp(Activity activity, SharedPreferences sharedPreferences) {
        AuthUI.getInstance().signOut(activity);
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();
        preferenceEditor.putInt(USER_TYPE, DEFAULT_RC_SIGN_IN);
        preferenceEditor.putInt(COLLEGE_SELECTED, COLLEGE_NOT_SELECTED_VALUE);
        preferenceEditor.apply();
    }

    public static String escapeEmailAddress(String email) {
        email = email.toLowerCase();
        email = email.replaceAll("\\.", ",");
        return email;
    }

    public static String getFirstName(String userName) {
        String user[] = userName.split(" ", 2);
        return user[0];
    }
}
